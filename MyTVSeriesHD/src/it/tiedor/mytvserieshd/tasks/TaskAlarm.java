package it.tiedor.mytvserieshd.tasks;

import java.util.Calendar;

import it.tiedor.mytvserieshd.MyConstants;
import it.tiedor.mytvserieshd.R;
import it.tiedor.mytvserieshd.helper.NotificationHelper;
import it.tiedor.mytvserieshd.receiver.OnAlarmReceiver;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class TaskAlarm {

	public static final String ALARM_EXTRA ="edu.worcester.cs499summer2012.TaskAlarm";
	public static final int REPEATING_ALARM = 1;
	public static final int PROCRASTINATOR_ALARM =2;
	private final String LOG_TAG = MyConstants.LOG_TAG + " - " + getClass().getSimpleName();

	/**
	 * Cancel alarm using the task id, PendingIntent is created using the Task id
	 * @param context
	 * @param intent
	 */
	public void cancelAlarm(Context context, int id)
	{	
		//cancel regular alarms
		PendingIntent pi = getPendingIntent(context, id);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pi);
		pi.cancel();

		//cancel Reminder Alarm
		Intent intent =  new Intent(context, OnAlarmReceiver.class)
			.putExtra(MyConstants.EXTRA_TASK_ID, id)
			.putExtra(TaskAlarm.ALARM_EXTRA, MyConstants.REMINDER_TIME);
		pi = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.cancel(pi);
		pi.cancel();
		
		//cancel procrastinator Alarm
		intent =  new Intent(context, OnAlarmReceiver.class)
			.putExtra(MyConstants.EXTRA_TASK_ID, id)
			.putExtra(TaskAlarm.ALARM_EXTRA, MyConstants.ALARM_TIME);
		pi = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.cancel(pi);
		pi.cancel();
	}

	/**
	 * Set a One Time Alarm using the taskID
	 * @param context
	 * @param id id of task to retrieve task from SQLite database
	 */
	public void setAlarm(Context context, int id){
		
		Log.d(LOG_TAG, "Metto un alarm con con id ["+id+"]");
		
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		long triggerAtMillis = Calendar.getInstance().getTimeInMillis();
		switch (id) {
		case 1:
			long update_time = pref.getLong(MyConstants.LAST_UPDATE_DATE, Calendar.getInstance().getTimeInMillis())+pref.getLong(MyConstants.UPDATE_TIME, 1000*60*60*24*7);
			triggerAtMillis = update_time;
			break;

		case 0:
			long synch_time = pref.getLong(MyConstants.LAST_SYNC_DATE, Calendar.getInstance().getTimeInMillis())+pref.getLong(MyConstants.SYNCH_TIME, 1000*60*40);
			triggerAtMillis = synch_time;
			break;
		}
		
		AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		
		PendingIntent pi = getPendingIntent(context, id);
		
		am.cancel(pi);
		
		am.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi);
	}

	//get a PendingIntent 
	private PendingIntent getPendingIntent(Context context, int id) {
		Intent intent =  new Intent(context, OnAlarmReceiver.class)
			.putExtra(MyConstants.EXTRA_TASK_ID, id);
		return PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}
	
}
