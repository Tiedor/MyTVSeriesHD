package it.tiedor.mytvserieshd.service;

import java.util.Calendar;
import java.util.List;

import it.tiedor.mydbhelper.DataBaseHelperFactory;
import it.tiedor.mydbhelper.DatabaseHelper;
import it.tiedor.mytvserieshd.MyConstants;
import it.tiedor.mytvserieshd.tasks.TaskAlarm;
import it.tiedor.mytvserieshd.utils.SynchUtils;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

public class MySynchUpdateIntentService extends WakefulIntentService{

	public static final 	String 			ACTION_SYNCH_SUB 		= "it.tiedor.mytvserieshd.action.SYNCH_SUB";
	public static final 	String 			ACTION_SYNCH_UPDATES 	= "it.tiedor.mytvserieshd.action.SYNCH_UPDATES";
	public static final 	String 			EXTRA_PARAM1 			= "it.tiedor.mytvserieshd.extra.PARAM1";
	private final 			String		 	LOG_TAG 				= MyConstants.LOG_TAG + " - " + getClass().getSimpleName();
	private static boolean isRunning = false;
	
	public MySynchUpdateIntentService() {
		super(MySynchUpdateIntentService.class.getSimpleName());
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		TaskAlarm alarm = new TaskAlarm();
		
		Log.d(LOG_TAG, "onHandleIntent - Start");
		
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE); 
		if (cm.getActiveNetworkInfo() == null || isRunning /*|| isRunning(this)*/) { 
			return; 
		}
		
		isRunning = true;

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

		long synch_time = pref.getLong(MyConstants.SYNCH_TIME, 1000*60*40);
		long update_time = pref.getLong(MyConstants.UPDATE_TIME, 1000*60*60*24*7);
		long last_synch = pref.getLong(MyConstants.LAST_SYNC_DATE, Calendar.getInstance().getTimeInMillis()-synch_time);
		long last_update = pref.getLong(MyConstants.LAST_UPDATE_DATE, Calendar.getInstance().getTimeInMillis()-update_time);
		int last_synch_update_s = pref.getInt(MyConstants.LAST_SYNC_UPDATE_SERIES, 0);

		DatabaseHelper myDBHelper = DataBaseHelperFactory.getDatabaseHelper(this);
		
		int actualS = myDBHelper.getAllSeriesCount();
		
		try {
			if (intent != null) {
				final String action = intent.getAction();
				if (ACTION_SYNCH_SUB.equals(action)) {
					if(Calendar.getInstance().getTimeInMillis()-last_synch>=synch_time || actualS > last_synch_update_s){
						Log.d(LOG_TAG, "Comincio il sub synch");
						SynchUtils.handleActionSynchSub(this);
						pref.edit().putLong(MyConstants.LAST_SYNC_DATE, Calendar.getInstance().getTimeInMillis()).commit();
						pref.edit().putInt(MyConstants.LAST_SYNC_UPDATE_SERIES, actualS).commit();
						alarm.cancelAlarm(this, 0);
						alarm.setAlarm(this, 0);	
					}else{
						Log.w(LOG_TAG, "Non c'è ancora bisogno di effettuare un sync dei sottotitoli");
					}
				}else if (ACTION_SYNCH_UPDATES.equals(action)) {
					if(Calendar.getInstance().getTimeInMillis()-last_update>=update_time || actualS > last_synch_update_s){
						Log.d(LOG_TAG, "Comincio l'update");
						SynchUtils.handleActionSynchUpdates(this);
						pref.edit().putLong(MyConstants.LAST_UPDATE_DATE, Calendar.getInstance().getTimeInMillis()).commit();
						pref.edit().putInt(MyConstants.LAST_SYNC_UPDATE_SERIES, actualS).commit();
						alarm.cancelAlarm(this, 1);
						alarm.setAlarm(this, 1);
					}else{
						Log.w(LOG_TAG, "Non c'è ancora bisogno di effettuare un update");
					}
				}
			}
	    }catch (Exception e) {
	    	Log.e(LOG_TAG, "Errore", e);
		}finally{
			isRunning = false;
		}

		Log.d(LOG_TAG, "onHandleIntent - End");
		
		super.onHandleIntent(intent);

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