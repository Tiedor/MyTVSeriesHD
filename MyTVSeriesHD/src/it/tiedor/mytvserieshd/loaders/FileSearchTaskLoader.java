package it.tiedor.mytvserieshd.loaders;

import it.tiedor.mytvserieshd.MyConstants;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

public class FileSearchTaskLoader extends AsyncTaskLoader<Object> {

	private 				String 		regexString;
	private 				Long 		beanId;
	private 				String[] 	array;
	private 	final 		String 		LOG_TAG 	= MyConstants.LOG_TAG + " - " + getClass().getSimpleName();

	public FileSearchTaskLoader(Context context, String regexString, Long beanId) {
		super(context);
		this.regexString = regexString;
		this.beanId = beanId != 0 ? beanId : null;
	}

	@Override
	public String[] loadInBackground() {
		Log.d(LOG_TAG, "FileSearchTaskLoader - loadInBackground");

		String uri = "file://";
		String videoUri = "";
		String subUri = "";

		Log.d(LOG_TAG, "FileSearchTaskLoader - loadInBackground - Cerco "+regexString);

		try {

			String folder_main = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("folder_main", "/sdcard/Movies");
			String folder_download = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("folder_download", "/sdcard/Download");
			Collection<File> files = (FileUtils.listFiles(Environment.getExternalStorageDirectory(),
					new RegexFileFilter(regexString),
					TrueFileFilter.TRUE));
			try{
				files.addAll(FileUtils.listFiles(new File(folder_main),
						new RegexFileFilter(regexString),
						TrueFileFilter.TRUE));
			}catch(Exception e){}

			try{
				files.addAll(FileUtils.listFiles(new File(folder_download),
						new RegexFileFilter(regexString),
						TrueFileFilter.TRUE));
			}catch(Exception e){}

			for(File file : files){
				Log.d(LOG_TAG, "File trovato: "+file.getAbsolutePath());
				if(file.getName().endsWith(".srt"))
					subUri = uri+file.getAbsolutePath();
				else if(file.getName().toLowerCase().endsWith(".mp4") || file.getName().toLowerCase().endsWith(".avi")
						|| file.getName().toLowerCase().endsWith(".mkv") || file.getName().toLowerCase().endsWith(".m4v"))
					videoUri = uri+file.getAbsolutePath();
				else if(file.getName().toLowerCase().endsWith(".zip") || file.getName().toLowerCase().endsWith(".rar"))
					FileUtils.deleteQuietly(file);
			}

			this.array = new String[]{videoUri, subUri, regexString};

			return array;
		}catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage(), e);
			return null;
		}
	}

	public Long getBeanId() {
		return beanId;
	}

}
