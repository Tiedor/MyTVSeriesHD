package it.tiedor.mytvserieshd.receiver;

import it.tiedor.mytvserieshd.MyConstants;
import it.tiedor.mytvserieshd.service.MySynchUpdateIntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class ResponseReceiver extends BroadcastReceiver {
	
	private final String LOG_TAG = MyConstants.LOG_TAG + " - " + getClass().getSimpleName();
	
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		Log.d(LOG_TAG, "Sono nell'onReceive");		
		
		Toast.makeText(arg0, arg1.getStringExtra(MySynchUpdateIntentService.EXTRA_PARAM1), Toast.LENGTH_LONG).show();
	}

}
