package it.tiedor.mytvserieshd.utils;

import it.tiedor.mydbhelper.persistence.Episode;
import it.tiedor.mytvserieshd.MyConstants;
import it.tiedor.mytvserieshd.exception.MyMovingFileException;
import it.tiedor.mytvserieshd.exception.MyNoRecordsFoundException;

import java.io.File;

import android.app.Activity;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class FileUtils {

	private static final 	String LOG_TAG = MyConstants.LOG_TAG + " - " + FileUtils.class.getSimpleName();
	private static 			String folder_main;
	private static 			String folder_view;

	public static String moveVideoFile(String[] arg1, Activity ctx) throws MyNoRecordsFoundException{

		String videoUri = ((String[])arg1)[0];
		String regex = ((String[])arg1)[2];

		if(videoUri.equals(""))
			throw new MyNoRecordsFoundException("Nessun video trovato per questo episodio");
		else
			Log.d(LOG_TAG, "Video URI: "+videoUri);

		folder_main = PreferenceManager.getDefaultSharedPreferences(ctx).getString("folder_main", "/sdcard/Movies");

		File dir = new File(folder_main);

		if(!dir.exists())
			dir.mkdir();

		File file = new File(videoUri.replace("file://", ""));
		String[] regexArray = regex.replace("(.)?", ".").replace("..", ".").replace("(s)?(0)?", "s").replace("(e)?(0)?", "e").split("\\*");		

		String fileName = "";
		for(int i = 1; i<regexArray.length; i++){
			fileName+=regexArray[i];
		}

		File newFile = new File(dir, fileName+file.getName().substring(file.getName().lastIndexOf(".")+1));

		Log.d(LOG_TAG, "New File: "+newFile.getAbsolutePath()+" and file exists: "+file.exists());

		if(file.renameTo(newFile)){
			Toast.makeText(ctx, "Modifica effettuata con successo", Toast.LENGTH_LONG).show();
			Log.d(LOG_TAG, "Original File Directory: "+file.getParent()+", Movies Directory: "+dir.getAbsolutePath());
			if(!file.getParentFile().equals(dir)){
				if(org.apache.commons.io.FileUtils.deleteQuietly(file.getParentFile()))
					Log.d(LOG_TAG, "Cartella di download eliminata");
			}
			
			return newFile.getAbsolutePath();
			
		}else{
			Toast.makeText(ctx, "Impossibile effettuare la modifica", Toast.LENGTH_LONG).show();
		}
		
		return null;
	}

	public static String moveSubFile(String[] arg1, Activity ctx) throws Exception{

		String subUri = ((String[])arg1)[1];
		String regex = ((String[])arg1)[2];

		if(subUri.equals(""))
			throw new MyNoRecordsFoundException("Nessun sottotitolo trovato per questo episodio");
		else
			Log.d(LOG_TAG, "Sub URI: "+subUri);

		folder_main = PreferenceManager.getDefaultSharedPreferences(ctx).getString("folder_main", "/sdcard/Movies");

		File dir = new File(folder_main);

		if(!dir.exists())
			dir.mkdir();

		File file = new File(subUri.replace("file://", ""));
		String[] regexArray = regex.replace("(.)?", ".").replace("..", ".").replace("(s)?(0)?", "s").replace("(e)?(0)?", "e").split("\\*");     

		String fileName = "";
		for(int i = 1; i<regexArray.length; i++){
			fileName+=regexArray[i];
		}

		File newFile = new File(dir, fileName+file.getName().substring(file.getName().lastIndexOf(".")+1));

		Log.d(LOG_TAG, "New File: "+newFile.getAbsolutePath()+" and file exists: "+file.exists());

		if(!file.renameTo(newFile))
			throw new Exception();	

		try{
			dir = new File(PreferenceManager.getDefaultSharedPreferences(ctx).getString("folder_main", "/sdcard/Movies")+File.separator+"myItasaSubs");
			org.apache.commons.io.FileUtils.deleteDirectory(dir);
		}catch(Exception e){}
		
		return newFile.getAbsolutePath();
	}

	public static void moveViewVideoFile(Activity ctx) throws Exception{

		String videoUri = PreferenceManager.getDefaultSharedPreferences(ctx).getString(MyConstants.MX_EXTRA_VIDEO_LIST, null);
		String subUri 	= PreferenceManager.getDefaultSharedPreferences(ctx).getString(MyConstants.MX_PLAYER_EXTRA_SUBS, null);

		if(videoUri == null || subUri == null)
			throw new MyNoRecordsFoundException("Video o Sottotitoli non trovati");

		folder_main = PreferenceManager.getDefaultSharedPreferences(ctx).getString("folder_main", "/sdcard/Movies");
		folder_view = PreferenceManager.getDefaultSharedPreferences(ctx).getString("folder_view", "/sdcard/Movies/View");

		File dir = new File(folder_view);

		if(!dir.exists())
			dir.mkdir();

		for(String file : new String[]{videoUri, subUri}){
			file = file.replace("file://", "");
			File oldFile = new File(file); 
			File newFile = new File(dir, oldFile.getName());
			if(!oldFile.renameTo(newFile))
				throw new MyMovingFileException("Impossibile spostare il file "+oldFile.getPath()+" nella cartella "+newFile.getPath());
		}
	}
}
