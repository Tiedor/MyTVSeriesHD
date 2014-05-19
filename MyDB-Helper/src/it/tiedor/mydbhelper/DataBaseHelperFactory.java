package it.tiedor.mydbhelper;

import android.content.Context;

public class DataBaseHelperFactory {

	private static DatabaseHelper helper;
	
	public static synchronized DatabaseHelper getDatabaseHelper(Context ctx){
		if(helper == null)
			helper = new DatabaseHelper(ctx);
		
		return helper;
	}
	
}
