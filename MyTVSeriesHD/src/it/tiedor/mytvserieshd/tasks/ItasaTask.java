package it.tiedor.mytvserieshd.tasks;

import it.tiedor.mydbhelper.DataBaseHelperFactory;
import it.tiedor.mydbhelper.DatabaseHelper;
import it.tiedor.mydbhelper.persistence.Episode;
import it.tiedor.mydbhelper.persistence.Serie;
import it.tiedor.myitasa.ItasaSubs;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

public class ItasaTask extends AsyncTask<Episode, Integer, List<Episode>> {

	private long last_synch;
	private long next_synch;
	private SharedPreferences pref;
	private Context ctx;
	private DatabaseHelper helper;
	
	private final String LOG_TAG = "MyTVSeriesHD" + " - " + getClass().getSimpleName();
	
	public ItasaTask(Context ctx){
		this.ctx = ctx;
		this.helper = DataBaseHelperFactory.getDatabaseHelper(ctx);
	}

	@Override
	protected List<Episode> doInBackground(Episode... arg0) {
		
		List<Episode> result = new ArrayList<Episode>();
		for(Episode ep : arg0){
			try{
				Serie serie = ep.getSeason().getSerie(); 

				Log.d(LOG_TAG, "ItasaAsyncLoader - Episodio: "+serie.getShowName()+"  s"+ep.getSeason().getSeason()+" e"+ep.getEpisode());
				
				String showId;

				if(serie.getItasaId() == null ){
					String seriesName = serie.getShowName();
					seriesName = (seriesName.contains("(") ? seriesName.substring(0, seriesName.indexOf("(")) : seriesName).replace("!", "").trim();
					showId = ItasaSubs.searchShow(seriesName, seriesName);
					serie.setItasaId(Long.parseLong(showId));
					this.helper.updateSerie(serie);
				}else 
					showId = Long.toString(serie.getItasaId());
				
				Log.d(LOG_TAG, "ShowId: "+showId);
				
				
				String episodeId = ItasaSubs.searchEpisode(showId, this.createRegex(serie, ep));
				Log.d(LOG_TAG, "Trovato l'id di itasa per l'episodio "+serie.getShowName()+"  s"+ep.getSeason().getSeason()+" e"+ep.getEpisode()+" ---> "+episodeId);
				ep.setItasaSubUrl(episodeId);
				this.helper.updateEpisode(ep);
				result.add(ep);
			}catch(Exception e){
				Log.w(LOG_TAG, e.getMessage());
			}
		}
		
		return result;
	}
	
	private String createRegex(String seriesName, Episode episode){
		
		seriesName = (seriesName.contains("(") ? seriesName.substring(0, seriesName.indexOf("(")) : seriesName).trim();
		seriesName = seriesName.replace(".", "(.)?").replace("'", ".*");
		
		Integer epSeason = episode.getSeason().getSeason();
		Integer epNumber = episode.getEpisode();
				
		if(seriesName.toLowerCase(Locale.ITALY).contains("american dad") || seriesName.toLowerCase(Locale.ITALY).contains("american.dad")){
			epSeason = epSeason-1;
			seriesName = seriesName.replace("!", "");
		}
		
		Log.d(LOG_TAG, "EpisodeListActivity - createRegex - Series Name: "+seriesName);
		
		String regex = "(?i).*"+".*"+epSeason+"(x)?"+(epNumber.toString().length() <= 1  ? "0"+epNumber : epNumber)+"$";
		
		return regex;
	}
	
	private String createRegex(Serie serie, Episode episode){
		
		Log.i(LOG_TAG, "Creo la regex per la serie: ["+serie.getShowName()+"]");
		
		String seriesName = (serie.getShowName().contains("(") ? serie.getShowName().substring(0, serie.getShowName().indexOf("(")) : serie.getShowName()).trim();
		
		return this.createRegex(seriesName, episode);
	}
}
