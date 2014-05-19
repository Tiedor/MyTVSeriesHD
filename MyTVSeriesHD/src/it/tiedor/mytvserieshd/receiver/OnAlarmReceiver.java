package it.tiedor.mytvserieshd.receiver;

import it.tiedor.mytvserieshd.MyConstants;
import it.tiedor.mytvserieshd.service.MySynchUpdateIntentService;
import it.tiedor.mytvserieshd.service.WakefulIntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class OnAlarmReceiver extends BroadcastReceiver {

	private final String LOG_TAG = MyConstants.LOG_TAG + " - " + getClass().getSimpleName();
	
	public void onReceive(Context context, Intent intent) {
		
		Log.d(LOG_TAG, "Ho ricevuto un alarm, faccio partire l'IntentService");
		
		if(intent != null){
			WakefulIntentService.acquireStaticLock(context); //acquire a partial WakeLock
			Intent msgIntent = new Intent(context, MySynchUpdateIntentService.class);
			switch (intent.getIntExtra(MyConstants.EXTRA_TASK_ID, -1)) {
			case 0:
		        msgIntent.setAction(MySynchUpdateIntentService.ACTION_SYNCH_SUB);
				break;

			case 1:
		        msgIntent.setAction(MySynchUpdateIntentService.ACTION_SYNCH_UPDATES);
				break;
			}
			context.startService(msgIntent); //start MySynchUpdateIntentService
		}
	}
	
}
