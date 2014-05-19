package it.tiedor.mytvserieshd.loaders;

import it.tiedor.mydbhelper.DataBaseHelperFactory;
import it.tiedor.mydbhelper.DatabaseHelper;
import it.tiedor.mydbhelper.persistence.Episode;
import it.tiedor.mydbhelper.persistence.Season;
import it.tiedor.mydbhelper.persistence.Serie;
import it.tiedor.mytvserieshd.MyConstants;
import it.tiedor.mytvserieshd.adapter.headerlist.Header;
import it.tiedor.mytvserieshd.adapter.headerlist.Item;
import it.tiedor.mytvserieshd.adapter.headerlist.ListItem;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

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

public class MyFilteredSeasonAsyncTaskLoader extends AsyncTaskLoader<Object> {

	private final String LOG_TAG = MyConstants.LOG_TAG + " - " + getClass().getSimpleName();
	private long serieId;
	private int filter;
	
	private LinkedHashMap<Season, ArrayList<Episode>> result;
	
	public MyFilteredSeasonAsyncTaskLoader(Context context, Bundle bundle) {
		super(context);
		this.serieId = bundle.getLong(MyConstants.SEASON_ID_KEY);
		this.filter = bundle.getInt(MyConstants.EPISODE_FILTER, 0);
	}
	
	@Override
	protected void onStartLoading() {
		Log.d(LOG_TAG, "onStartLoading");
		if(result != null)
			deliverResult(result);
		else
			forceLoad();
		super.onStartLoading();
	}

	@Override
	public HashMap<Season, ArrayList<Episode>> loadInBackground() {		
		try { 
						
			DatabaseHelper databaseHelper = DataBaseHelperFactory.getDatabaseHelper(getContext());
			
			ArrayList<Season> seasons;
			
			switch (this.filter) {
			case 1:
				seasons = databaseHelper.getSeasonToDownload();
				break;
			case 2: 
				seasons = databaseHelper.getSeasonInDownload();
				break;
			case 3: 
				seasons = databaseHelper.getSeasonToSub();
				break;
			case 4: 
				seasons = databaseHelper.getSeasonToView();
				break;
			default:
				seasons = new ArrayList<Season>();
				break;
			}
			
			 result = new LinkedHashMap<Season, ArrayList<Episode>>();
			
			for(Season s : seasons){
				Log.i(LOG_TAG, "Analizzo la serie: "+s.getSerie().getShowName());
				ArrayList<Episode> episodes;
				switch (this.filter) {
				case 1:
					episodes = databaseHelper.getEpisodesToDownload(s.getSeasonId());
					break;
				case 2: 
					episodes = databaseHelper.getEpisodesInDownload(s.getSeasonId());
					break;
				case 3: 
					episodes = databaseHelper.getEpisodesToSub(s.getSeasonId());
					break;
				case 4: 
					episodes = databaseHelper.getEpisodesToView(s.getSeasonId());
					break;
				default:
					episodes = new ArrayList<Episode>();
					break;
				}
				result.put(s, episodes);
			}
			
			return result;
		} catch (Exception e) {
			Log.e(LOG_TAG, "Errore", e);
		}
		
		return null;
	}
		
	
}
