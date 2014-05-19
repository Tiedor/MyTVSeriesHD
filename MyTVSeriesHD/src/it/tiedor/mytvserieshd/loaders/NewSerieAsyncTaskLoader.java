package it.tiedor.mytvserieshd.loaders;

import it.tiedor.mythetvdb.TheTVDBApi;
import it.tiedor.mythetvdb.persistence.Serie;
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

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class NewSerieAsyncTaskLoader extends AsyncTaskLoader<ArrayList<Serie>> {

	private final String LOG_TAG = MyConstants.LOG_TAG + " - " + "NewSerieAsyncTaskLoader";
	private Bundle bundle;
	private ArrayList<Serie> series;
	private String url;
	
	public NewSerieAsyncTaskLoader(Context context, Bundle bundle) {
		super(context);
		this.bundle = bundle;
	}
	
	@Override
	protected void onStartLoading() {
		Log.d(LOG_TAG, "onStartLoading");
		
		if(this.series!=null && this.url!=null && !this.url.equals(this.bundle.getString(MyConstants.SEARCH_SERIES_KEY))){
			deliverResult(series);
		}else{
			this.url = this.bundle.getString(MyConstants.SEARCH_SERIES_KEY);
			forceLoad();
		}
		super.onStartLoading();
	}

	@Override
	public ArrayList<Serie> loadInBackground() {
		
		ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE); 
		if (cm.getActiveNetworkInfo() == null) {
			return null; 
		}
		
		try { 
			TheTVDBApi api = new TheTVDBApi();
			this.series = api.searchSeries(url);
			for(Serie serie : series)
				Log.d(LOG_TAG, "Serie: id["+serie.getId()+"], poster["+serie.getPoster()+"]");
			return series;
		} catch (Exception e) {
			Log.e(LOG_TAG, "Errore", e);
		}
		
		return null;
	}
}
