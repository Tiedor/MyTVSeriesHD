package it.tiedor.mytvserieshd.receiver;

import it.tiedor.mytvserieshd.MyConstants;
import it.tiedor.mytvserieshd.R;
import it.tiedor.mytvserieshd.service.MySynchUpdateIntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MyResultReceiver extends BroadcastReceiver {

	private final String LOG_TAG = MyConstants.LOG_TAG + " - " + getClass().getSimpleName();
	
	public static final String ACTION_RESP = "it.tiedor.mytvserieshd.receiver.MESSAGE_PROCESSED";
	
	public static final String CODE = "it.tiedor.mytvserieshd.receiver.CODE";
	public static final String RESULT = "it.tiedor.mytvserieshd.receiver.RESULT";
	public static final String PARAM1 = "it.tiedor.mytvserieshd.receiver.PARAM1";
	public static final String PARAM2 = "it.tiedor.mytvserieshd.receiver.PARAM2";
	public static final String PARAM3 = "it.tiedor.mytvserieshd.receiver.PARAM3";
	
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		
		//Toast.makeText(arg0, arg1.getStringExtra(EPISODENAME), Toast.LENGTH_LONG).show();		
		
	}

}
