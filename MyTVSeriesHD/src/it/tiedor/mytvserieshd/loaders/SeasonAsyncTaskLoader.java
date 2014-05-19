package it.tiedor.mytvserieshd.loaders;

import it.tiedor.mydbhelper.DataBaseHelperFactory;
import it.tiedor.mydbhelper.DatabaseHelper;
import it.tiedor.mydbhelper.persistence.Season;
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

public class SeasonAsyncTaskLoader extends AsyncTaskLoader<ArrayList<Season>> {

	private Long serieId;
	private final String LOG_TAG = MyConstants.LOG_TAG + " - " + "MyOwnAsyncTaskLoader";
	
	public SeasonAsyncTaskLoader(Context context, Bundle bundle) {
		super(context);
		this.serieId = bundle.getLong(MyConstants.SERIES_ID_KEY);
	}
	
	@Override
	protected void onStartLoading() {
		Log.d(LOG_TAG, "onStartLoading");
		forceLoad();
		super.onStartLoading();
	}

	@Override
	public ArrayList<Season> loadInBackground() {		
		try { 
						
			DatabaseHelper databaseHelper = DataBaseHelperFactory.getDatabaseHelper(getContext());
			
			ArrayList<Season> seasons = new ArrayList<Season>(databaseHelper.getSeasons(serieId.intValue()));
			
			return seasons;
		} catch (Exception e) {
			Log.e(LOG_TAG, "Errore", e);
		}
		
		return null;
	}
		
	
}
