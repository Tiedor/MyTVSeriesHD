package it.tiedor.mytvserieshd.loaders;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import it.tiedor.myitasa.ItasaSubs;
import it.tiedor.mytvserieshd.MyConstants;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

public class SubFileSearchTaskLoader extends AsyncTaskLoader<Object> {

	private String regexString;
	private String href;
	private String[] array;
	private 	final 		String 		LOG_TAG 	= MyConstants.LOG_TAG + " - " + getClass().getSimpleName();
	
	public SubFileSearchTaskLoader(Context context, String regexString, String href) {
		super(context);
		this.regexString = regexString;
		this.href = href;
	}

	@Override
	public String[] loadInBackground() {
		Log.d(LOG_TAG, "SubFileSearchTaskLoader - loadInBackground");
		
		String uri = "file://";
		String subUri = "";
		
		Log.d(LOG_TAG, "SubFileSearchTaskLoader - loadInBackground - Cerco "+regexString);
		
		try {	
			
			File dir = new File(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("folder_main", "/sdcard/Movies")+File.separator+"myItasaSubs");
			if(!dir.exists() || !dir.isDirectory())
				dir.mkdir();
			
			String filename = ItasaSubs.downloadEpisode(getContext(), MyConstants.ITASA_EPISODE_SUB_URL+href, dir);
			
			File subDir = new File(filename);
			
			File[] files = subDir.listFiles();
			
			for(File file : files){
				Log.d(LOG_TAG, "File trovato: "+file.getAbsolutePath());
				if(file.getName().endsWith(".srt"))
					subUri = uri+file.getAbsolutePath();
				else if(file.getName().toLowerCase().endsWith(".zip") || file.getName().toLowerCase().endsWith(".rar"))
					FileUtils.deleteQuietly(file);
			}
			
			//subUri = uri+filename;
			
			this.array = new String[]{"", subUri, regexString};
			
			return array;
	    }catch (Exception e) {
	    	Log.e(LOG_TAG, e.getMessage(), e);
			return null;
		}
	}
}
