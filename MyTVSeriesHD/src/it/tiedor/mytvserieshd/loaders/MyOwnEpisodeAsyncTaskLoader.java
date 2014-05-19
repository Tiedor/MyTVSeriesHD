package it.tiedor.mytvserieshd.loaders;

import it.tiedor.mydbhelper.DataBaseHelperFactory;
import it.tiedor.mydbhelper.DatabaseHelper;
import it.tiedor.mydbhelper.persistence.Episode;
import it.tiedor.mydbhelper.persistence.Serie;
import it.tiedor.mytvserieshd.MyConstants;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class MyOwnEpisodeAsyncTaskLoader extends AsyncTaskLoader<Object> {

	private final String LOG_TAG = MyConstants.LOG_TAG + " - " + "MyOwnAsyncTaskLoader";
	private long serieId;
	private int filter;
	
	public MyOwnEpisodeAsyncTaskLoader(Context context, Bundle bundle) {
		super(context);
		this.serieId = bundle.getLong(MyConstants.SEASON_ID_KEY);
		this.filter = bundle.getInt(MyConstants.EPISODE_FILTER, 0);
	}
	
	@Override
	protected void onStartLoading() {
		Log.d(LOG_TAG, "onStartLoading");
		forceLoad();
		super.onStartLoading();
	}

	@Override
	public ArrayList<Episode> loadInBackground() {		
		try { 
						
			DatabaseHelper databaseHelper = DataBaseHelperFactory.getDatabaseHelper(getContext());
			
			ArrayList<Episode> episodes;
			
			switch (this.filter) {
			case 1:
				episodes = databaseHelper.getEpisodesToDownload();
				break;
			case 2: 
				episodes = databaseHelper.getEpisodesInDownload();
			break;
			case 3: 
				episodes = databaseHelper.getEpisodesToSub();
			break;
			case 4: 
				episodes = databaseHelper.getEpisodesToView();
			break;
			default:
				episodes = databaseHelper.getSeasonsEpisodes(this.serieId);
				break;
			}
			
			return episodes;
		} catch (Exception e) {
			Log.e(LOG_TAG, "Errore", e);
		}
		
		return null;
	}
		
	
}
