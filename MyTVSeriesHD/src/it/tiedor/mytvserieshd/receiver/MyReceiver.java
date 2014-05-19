package it.tiedor.mytvserieshd.receiver;

import it.tiedor.mydbhelper.DataBaseHelperFactory;
import it.tiedor.mydbhelper.DatabaseHelper;
import it.tiedor.mytvserieshd.MyConstants;
import it.tiedor.mytvserieshd.service.MySynchUpdateIntentService;
import it.tiedor.mytvserieshd.service.WakefulIntentService;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {

	private static boolean isRunning = false;

	private final String LOG_TAG = "MyTVSeriesHD" + " - " + getClass().getSimpleName();
	private DatabaseHelper myDBHelper;

	public MyReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); 
		if (cm.getActiveNetworkInfo() == null || isRunning /*|| isRunning(context)*/) { 
			return;
		}

		NetworkInfo mWifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		
		isRunning = true;

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

		long synch_time = pref.getLong(MyConstants.SYNCH_TIME, 1000*60*40);
		long update_time = pref.getLong(MyConstants.UPDATE_TIME, 1000*60*60*24*7);
		long last_synch = pref.getLong(MyConstants.LAST_SYNC_DATE, Calendar.getInstance().getTimeInMillis()-synch_time);
		long last_update = pref.getLong(MyConstants.LAST_UPDATE_DATE, Calendar.getInstance().getTimeInMillis()-update_time);
		int last_synch_update_s = pref.getInt(MyConstants.LAST_SYNC_UPDATE_SERIES, 0);

		myDBHelper = DataBaseHelperFactory.getDatabaseHelper(context);

		int actualS = myDBHelper.getAllSeriesCount();

		try {

			if(Calendar.getInstance().getTimeInMillis()-last_synch>=synch_time || actualS > last_synch_update_s){
				this.analyzeSubs(context, intent);
			}else{
				Log.w(LOG_TAG, "Non c'è ancora bisogno di effettuare un sync dei sottotitoli ["+(Calendar.getInstance().getTimeInMillis()-last_synch)+"]");
			}

			if(mWifi.isConnected() || Calendar.getInstance().getTimeInMillis()-last_update>=update_time || actualS > last_synch_update_s){
				this.analyzeUpdates(context, intent);
			}else{
				Log.w(LOG_TAG, "Non c'è ancora bisogno di effettuare un update ["+(Calendar.getInstance().getTimeInMillis()-last_update)+"]");
			}

		}catch (Exception e) {
			Log.e(LOG_TAG, "Errore", e);
		}finally{
			isRunning = false;
		}
	}

	private void analyzeSubs(Context context, Intent intent) throws SQLException, InterruptedException, ExecutionException{		
		Log.d(LOG_TAG, "Lancio un Sub Synch");
		WakefulIntentService.acquireStaticLock(context); //acquire a partial WakeLock
		Intent msgIntent = new Intent(context, MySynchUpdateIntentService.class);
		msgIntent.setAction(MySynchUpdateIntentService.ACTION_SYNCH_SUB);
		context.startService(msgIntent);

	}

	private void analyzeUpdates(Context context, Intent intent) throws Exception{		
		Log.d(LOG_TAG, "Lancio un Update");
		WakefulIntentService.acquireStaticLock(context); //acquire a partial WakeLock
		Intent msgIntent = new Intent(context, MySynchUpdateIntentService.class);
		msgIntent.setAction(MySynchUpdateIntentService.ACTION_SYNCH_UPDATES);
		context.startService(msgIntent);
	}

	public boolean isRunning(Context ctx) {
		ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

		for (RunningTaskInfo task : tasks) {
			Log.d(LOG_TAG, "Controllo che non sia attivo MyTVSeriesHD ["+task.baseActivity.getPackageName()+"]["+task.baseActivity.getClassName()+"]["+task.baseActivity.getShortClassName()+"]");
			if ("it.tiedor.mytvserieshd.MyTVSeriesHD".equalsIgnoreCase(task.baseActivity.getClassName())) 
				return true;                                  
		}

		return false;
	}
}
