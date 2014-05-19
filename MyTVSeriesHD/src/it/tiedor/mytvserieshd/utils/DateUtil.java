package it.tiedor.mytvserieshd.utils;

import it.tiedor.mytvserieshd.MyConstants;
import it.tiedor.mytvserieshd.adapter.MyBucketEpisodeAdapter;

import java.util.Calendar;
import java.util.Date;

import android.util.Log;

public class DateUtil {

	private	static final String LOG_TAG 	= MyConstants.LOG_TAG + " - " + DateUtil.class.getSimpleName();
	
	public static boolean checkToday(Date airTimeDate){
		Calendar airTime = Calendar.getInstance();
		airTime.setTime(airTimeDate);
		Calendar today = Calendar.getInstance();
		
		//Log.d(LOG_TAG, "Oggi ["+today.get(Calendar.DAY_OF_YEAR)+"], AirTime ["+airTime.get(Calendar.DAY_OF_YEAR)+"]");
		
		return airTime.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) && airTime.get(Calendar.YEAR) == today.get(Calendar.YEAR);
	}
	
	public static boolean checkYesterday(Date airTimeDate){
		Calendar airTime = Calendar.getInstance();
		airTime.setTime(airTimeDate);
		Calendar yesterday = Calendar.getInstance();
		yesterday.add(Calendar.DATE, -1);
		
		//Log.d(LOG_TAG, "Ieri ["+yesterday.get(Calendar.DAY_OF_YEAR)+"], AirTime ["+airTime.get(Calendar.DAY_OF_YEAR)+"]");
		
		return airTime.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR) && airTime.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR);
	}
	
	public static boolean checkTomorrow(Date airTimeDate){
		Calendar airTime = Calendar.getInstance();
		airTime.setTime(airTimeDate);
		Calendar tomorrow = Calendar.getInstance();
		tomorrow.add(Calendar.DATE, +1);
		
		//Log.d(LOG_TAG, "Domani ["+tomorrow.get(Calendar.DAY_OF_YEAR)+"], AirTime ["+airTime.get(Calendar.DAY_OF_YEAR)+"]");
		
		return airTime.get(Calendar.DAY_OF_YEAR) == tomorrow.get(Calendar.DAY_OF_YEAR) && airTime.get(Calendar.YEAR) == tomorrow.get(Calendar.YEAR);
	}
}
