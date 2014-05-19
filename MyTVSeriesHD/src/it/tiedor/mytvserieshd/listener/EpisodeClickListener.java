package it.tiedor.mytvserieshd.listener;

import it.tiedor.mydbhelper.persistence.Episode;
import it.tiedor.mydbhelper.persistence.Serie;
import it.tiedor.mytvserieshd.MyConstants;
import it.tiedor.mytvserieshd.MyTVSeriesHD;
import it.tiedor.mytvserieshd.R;
import it.tiedor.mytvserieshd.fragment.EpisodeListFragment;

import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


public class EpisodeClickListener implements android.view.View.OnClickListener{

	private 				int 					method;
	private 				MyTVSeriesHD 			ctx;
	private 				Episode 				episode;
	private 				LoaderCallbacks<Object> episodeListFragment;
	private 				String 					torrent;
	private					int						position;
	private 	final 		String 					LOG_TAG 	= MyConstants.LOG_TAG + " - " + getClass().getSimpleName();

	public EpisodeClickListener(Activity ctx, int method, Episode episode, int position) {
		this.method = method;
		this.ctx = (MyTVSeriesHD) ctx;
		this.episode = episode;
		this.position = position;
		this.torrent = PreferenceManager.getDefaultSharedPreferences(ctx).getString(MyConstants.TORRENT, "http://katunblock.com");
		if(MyConstants.isTwoPane)
			this.episodeListFragment = (LoaderCallbacks<Object>) ctx.getFragmentManager().findFragmentById(R.id.serie_detail_container);
		else
			this.episodeListFragment = (LoaderCallbacks<Object>) ctx.getFragmentManager().findFragmentById(R.id.content_frame);
	}

	@Override
	public void onClick(View v) {

		Loader loader = null;

		PreferenceManager.getDefaultSharedPreferences(this.ctx).edit()
		.putLong(MyConstants.EPISODE_ID_PROCESSING, this.episode.getEpisodeId())
		.putInt(MyConstants.EPISODE_POSITION_PROCESSING, this.position)
		.commit();

		String seriesName = episode.getSeason().getSerie().getShowName().replace(".", " ");

		seriesName = (seriesName.contains("(") ? seriesName.substring(0, seriesName.indexOf("(")) : seriesName).trim();

		Bundle bundle = new Bundle();

		String regex;

		try{
			switch (method) {
			case 1:

				Intent intent = new Intent();

				intent.setData(Uri.parse("magnet://"));
			    PackageManager pm = this.ctx.getPackageManager();
			    List<ResolveInfo> activities = pm.queryIntentActivities(intent, 0);
			    if (activities == null || activities.size() <= 0) {
			        Toast.makeText(this.ctx, "Devi installare un client torrent per scaricare gli episodi", Toast.LENGTH_LONG).show();
			        return;
			    }				
				
			    int epSeason = episode.getSeason().getSeason();
			    
			    if(seriesName.toLowerCase(Locale.ITALY).contains("american dad") || seriesName.toLowerCase(Locale.ITALY).contains("american.dad")){
					epSeason = epSeason-1;
					seriesName = seriesName.replace("!", "");
				}
			    
				String term = seriesName + " s"+String.format("%02d", epSeason)+"e"+String.format("%02d", episode.getEpisode());

				bundle.putString(MyConstants.OTHER_URL, term+" torrent");
				
				term = URLEncoder.encode(term, "UTF-8");

				term = term.replace("+", "%20");

				bundle.putString(MyConstants.TORRENT_URL, torrent+"/usearch/"+term+"/");
				

				loader = ctx.getLoaderManager().restartLoader(method, bundle, this.episodeListFragment);

				break;

			case 2:

				regex = createRegex(seriesName, this.episode, true);

				bundle.putString(MyConstants.REGEX, regex);
				bundle.putLong("id", this.episode.getEpisodeId());

				Log.d(LOG_TAG, "Cerco il file: "+regex);

				loader = ctx.getLoaderManager().restartLoader(method, bundle, this.episodeListFragment);

				break;

			case 3:

				if(PreferenceManager.getDefaultSharedPreferences(ctx).getString(MyConstants.ITASA_USER, null) == null ||
						PreferenceManager.getDefaultSharedPreferences(ctx).getString(MyConstants.ITASA_PASSWORD, null) == null){
					Toast.makeText(ctx, "Effettuare la Login su Itasa dalle impostazioni", Toast.LENGTH_LONG).show();
					break;
				}
				
				regex = createRegex(seriesName, this.episode, true);

				bundle.putString(MyConstants.REGEX, regex);
				bundle.putString(MyConstants.ITASA_SUB_HREF, this.episode.getItasaSubUrl());

				Log.d(LOG_TAG, "Cerco il file: "+regex);

				loader = ctx.getLoaderManager().restartLoader(method, bundle, this.episodeListFragment);

				break;

			case 4:

				regex = createRegex(seriesName, this.episode, false);


				bundle.putString(MyConstants.REGEX, regex);
				bundle.putLong("id", this.episode.getEpisodeId());

				Log.d(LOG_TAG, "Cerco il file: "+regex);

				loader = ctx.getLoaderManager().restartLoader(method, bundle, this.episodeListFragment);

				break;

			default:
				break;
			}

			if(loader != null && loader.isStarted()){
				loader.forceLoad();
			}

		}catch(Exception e){
			Log.e(LOG_TAG, "Errore", e);
		}

	}

	private String createRegex(String seriesName, Episode episode, boolean full){

		seriesName = (seriesName.contains("(") ? seriesName.substring(0, seriesName.indexOf("(")) : seriesName).trim();
		seriesName = seriesName.replace(".", "(.)?").replace("'", ".*");

		Integer epSeason = episode.getSeason().getSeason();
		Integer epNumber = episode.getEpisode();

		if(seriesName.toLowerCase(Locale.ITALY).contains("american dad") || seriesName.toLowerCase(Locale.ITALY).contains("american.dad")){
			epSeason = epSeason-1;
			seriesName = seriesName.replace("!", "");
		}

		Log.d(LOG_TAG, "EpisodeListActivity - createRegex - Series Name: "+seriesName);

		String regex = "(?i).*"+seriesName.replace(" ", ".")+"." +
				(full ? "*" : "") +
				"(s)?(0)?"+epSeason+"(.)?(e)?(0)?"+epNumber+".*";

		return regex;
	}
}
