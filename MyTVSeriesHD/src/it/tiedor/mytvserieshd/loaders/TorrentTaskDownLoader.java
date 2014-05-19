package it.tiedor.mytvserieshd.loaders;


import it.tiedor.mytvserieshd.MyConstants;
import it.tiedor.torrentdownloader.TorrentHelper;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

public class TorrentTaskDownLoader extends AsyncTaskLoader<Object> {

	private 				String url;
	private					String otherUrl;
	private 				String result;
	private 	final 		String LOG_TAG 	= MyConstants.LOG_TAG + " - " + getClass().getSimpleName();
	
	public TorrentTaskDownLoader(Context context, String url, String otherUrl) {
		super(context);
		this.url = url;
		this.otherUrl = otherUrl;
	}

	@Override
	public String loadInBackground() {
		
		Log.d(LOG_TAG, "SubFileSearchTaskLoader - loadInBackground");
				
		Log.d(LOG_TAG, "SubFileSearchTaskLoader - loadInBackground - Cerco "+url);
		
		try {	
			result = TorrentHelper.downloadTorrent(url);
	    }catch (Exception e) {
	    	Log.e(LOG_TAG, e.getMessage(), e);
	    	result = otherUrl;
		}
		
		return result;
	}
	
	
}
