package it.tiedor.mythetvdb.internal;

import it.tiedor.mythetvdb.MyConstants;
import it.tiedor.mythetvdb.persistence.Banner;
import it.tiedor.mythetvdb.persistence.Banners;
import it.tiedor.mythetvdb.persistence.Data;
import it.tiedor.mythetvdb.persistence.Episode;
import it.tiedor.mythetvdb.persistence.Serie;

import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.util.Log;

public class TheTVDBParser {

	private Serializer serializer;
	private final String LOG = MyConstants.LOG_TAG + this.getClass().getName();
	
	public TheTVDBParser() {
		this.serializer = new Persister();
	}
	
	private Data getDataFromUrl(String url) throws IllegalStateException, Exception{
		
		URI uri = URI.create(url);
		
		Log.d(LOG, "Mi connetto a: "+url);
		
		DefaultHttpClient mHttpClient = new DefaultHttpClient();
		HttpGet mHttpGet = new HttpGet(uri);
		HttpResponse mHttpResponse = mHttpClient.execute(mHttpGet);
		if (mHttpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			HttpEntity entity = mHttpResponse.getEntity();
			if ( entity != null) {        
				return this.serializer.read(Data.class, entity.getContent());
			}else
				throw new Exception("Impossibile collegarsi a TheTVDB");
		}else
			throw new Exception("Impossibile collegarsi a TheTVDB");
	}
	
	private Banners getBannersFromUrl(String url) throws IllegalStateException, Exception{
		
		URI uri = URI.create(url);
		
		Log.d(LOG, "Mi connetto a: "+url);
		
		DefaultHttpClient mHttpClient = new DefaultHttpClient();
		HttpGet mHttpGet = new HttpGet(uri);
		HttpResponse mHttpResponse = mHttpClient.execute(mHttpGet);
		if (mHttpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			HttpEntity entity = mHttpResponse.getEntity();
			if ( entity != null) {        
				return this.serializer.read(Banners.class, entity.getContent());
			}else
				throw new Exception("Impossibile collegarsi a TheTVDB");
		}else
			throw new Exception("Impossibile collegarsi a TheTVDB");
	}
	
	public ArrayList<Serie> searchSeries(String name) throws Exception{
		
		String uri = MyConstants.SITE_URI+MyConstants.SEARCH_URI+URLEncoder.encode(name.trim(), "UTF-8");
		
		return this.getDataFromUrl(uri).getSeries();
	}
	
	public Serie getSerie(String serieId) throws Exception{
		
		String uri = MyConstants.SITE_URI+MyConstants.GET_SERIE_URI.replace("{SERIEID}", serieId);
		
		return this.getDataFromUrl(uri).getSeries().get(0);
	}
	
	public Episode getEpisode(long episodeId) throws Exception{
		
		String uri = MyConstants.SITE_URI+MyConstants.GET_EPISODE_URI.replace("{EPISODEID}", Long.toString(episodeId));
		
		return this.getDataFromUrl(uri).getEpisodes().get(0);
	}
	
	public Data getSerieWithEpisodes(Long serieId) throws Exception{
		
		String uri = MyConstants.SITE_URI+MyConstants.GET_EPISODES_URI.replace("{SERIEID}", Long.toString(serieId));
		
		return this.getDataFromUrl(uri);
	}
	
	public ArrayList<Episode> getEpisodes(String serieId) throws Exception{
		
		String uri = MyConstants.SITE_URI+MyConstants.GET_EPISODES_URI.replace("{SERIEID}", serieId);
		
		return this.getDataFromUrl(uri).getEpisodes();
	}
	
	public ArrayList<Banner> getBanners(String serieId) throws Exception{
		
		String uri = MyConstants.SITE_URI+MyConstants.GET_BANNERS_URI.replace("{SERIEID}", serieId);
		
		return this.getBannersFromUrl(uri).getBanner();
	}
	
	public Data getDailyUpdates() throws Exception{
		
		String uri = MyConstants.SITE_URI+MyConstants.GET_DAILY_UPDATE_URI;
		
		return this.getDataFromUrl(uri);
	}
}
