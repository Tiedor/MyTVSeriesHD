package it.tiedor.mytvserieshd.helper;

import java.util.Calendar;
import java.util.GregorianCalendar;

import it.tiedor.mytvserieshd.MyConstants;
import it.tiedor.mytvserieshd.MyTVSeriesHD;
import it.tiedor.mytvserieshd.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.format.DateFormat;

public class NotificationHelper {
	/**
	 * Basic Text Notification for Task Butler, using NotificationCompat
	 * @param context 
	 * @param id id of task, call task.getID() and pass it to this parameter
	 *
	public void sendBasicNotification(Context context, Task task) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		boolean vibrate = prefs.getBoolean(MyConstants.VIBRATE_ON_ALARM, true);
		int alarmInterval;
		int alarmUnits;

		if (task.isHasFinalDateDue()) {
			alarmInterval = Integer.parseInt(prefs.getString(MyConstants.ALARM_TIME, MyConstants.DEFAULT_ALARM_TIME));
			alarmUnits = Calendar.MINUTE;
		} else {
			alarmInterval = Integer.parseInt(prefs.getString(MyConstants.REMINDER_TIME, MyConstants.DEFAULT_REMINDER_TIME));
			alarmUnits = Calendar.HOUR_OF_DAY;
		}

		Calendar next_reminder = GregorianCalendar.getInstance();
		next_reminder.add(alarmUnits, alarmInterval);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
		.setAutoCancel(true)
		.setContentIntent(getPendingIntent(context, task.getId()))
		.setContentInfo(MyConstants.PRIORITY_LABELS[task.getPriority()])
		.setContentTitle(task.getName())
		.setContentText(DateFormat.format("'Next reminder at' h:mmaa", next_reminder))
		.setDefaults(vibrate ? Notification.DEFAULT_ALL : Notification.DEFAULT_SOUND|Notification.DEFAULT_LIGHTS)
		.setSmallIcon(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? 
				R.drawable.ic_stat_new_episode : R.drawable.ic_stat_new_episode)
				.setTicker(task.getName())
				.setWhen(System.currentTimeMillis());
		@SuppressWarnings("deprecation")
		Notification notification = builder.getNotification();
		NotificationManager notificationManager = getNotificationManager(context);
		notificationManager.notify(task.getId(), notification);
	}
	/**
	 * Basic Text Notification with Ongoing flag enabled for Task Butler, using NotificationCompat
	 * @param context 
	 * @param id id of task, call task.getID() and pass it to this parameter
	 * @deprecated Use sendBasicNotification for all notifications
	 *
	public void sendPersistentNotification(Context context, Task task) {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
		.setContentText(task.getNotes())
		.setContentTitle(task.getName())
		.setSmallIcon(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? 
				R.drawable.ic_stat_new_episode : R.drawable.ic_stat_new_episode)
				.setAutoCancel(true)
				.setContentIntent(getPendingIntent(context,task.getId()))
				.setWhen(System.currentTimeMillis())
				.setOngoing(true)
				.setDefaults(Notification.DEFAULT_ALL);
		Notification notification = builder.getNotification();
		NotificationManager notificationManager = getNotificationManager(context);
		notificationManager.notify(task.getId(), notification);
	}
	//get a PendingIntent
	PendingIntent getPendingIntent(Context context, int id) {
		Intent intent =  new Intent(context, MyTVSeriesHD.class)
		.putExtra(MyConstants.EXTRA_TASK_ID, id);
		return PendingIntent.getActivity(context, id, intent, 0);
	}
	//get a NotificationManager
	NotificationManager getNotificationManager(Context context) {
		return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	/**
	 * Cancels an existing notification, if user modified the task. Make the
	 * actual call from TaskAlarm.cancelNotification(Context, int)
	 * @param context
	 * @param taskID
	 *
	public void cancelNotification(Context context, int taskID) {
		NotificationManager notificationManager = getNotificationManager(context);
		notificationManager.cancel(taskID);
	}

	*/
}
