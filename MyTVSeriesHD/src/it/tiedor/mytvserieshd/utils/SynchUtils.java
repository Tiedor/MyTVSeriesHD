package it.tiedor.mytvserieshd.utils;

import it.tiedor.mydbhelper.DataBaseHelperFactory;
import it.tiedor.mydbhelper.DatabaseHelper;
import it.tiedor.mydbhelper.persistence.Action;
import it.tiedor.mydbhelper.persistence.Season;
import it.tiedor.myitasa.ItasaSubs;
import it.tiedor.mythetvdb.TheTVDBApi;
import it.tiedor.mythetvdb.persistence.Banner;
import it.tiedor.mythetvdb.persistence.Data;
import it.tiedor.mythetvdb.persistence.Episode;
import it.tiedor.mythetvdb.persistence.Serie;
import it.tiedor.mytvserieshd.MyConstants;
import it.tiedor.mytvserieshd.MyTVSeriesHD;
import it.tiedor.mytvserieshd.R;
import it.tiedor.mytvserieshd.service.MySynchUpdateIntentService;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SynchUtils {

	private final static String LOG_TAG = MyConstants.LOG_TAG + " - " + SynchUtils.class.getSimpleName();
	
	public static void handleActionSynchSub(IntentService is) throws SQLException {
		
		DatabaseHelper myDBHelper = DataBaseHelperFactory.getDatabaseHelper(is);
		
		ArrayList<it.tiedor.mydbhelper.persistence.Episode> lista = myDBHelper.getEpisodesToSub4Task();

		List<it.tiedor.mydbhelper.persistence.Episode> result = new ArrayList<it.tiedor.mydbhelper.persistence.Episode>();
		for(it.tiedor.mydbhelper.persistence.Episode ep : lista){
			try{
				it.tiedor.mydbhelper.persistence.Serie serie = ep.getSeason().getSerie(); 

				Log.d(LOG_TAG, "ItasaAsyncLoader - Episodio: "+serie.getShowName()+"  s"+ep.getSeason().getSeason()+" e"+ep.getEpisode());

				String showId;

				if(serie.getItasaId() == null ){
					String seriesName = serie.getShowName();
					seriesName = (seriesName.contains("(") ? seriesName.substring(0, seriesName.indexOf("(")) : seriesName).replace("!", "").trim();
					showId = ItasaSubs.searchShow(seriesName, seriesName);
					serie.setItasaId(Long.parseLong(showId));
					myDBHelper.updateSerie(serie);
				}else 
					showId = Long.toString(serie.getItasaId());

				Log.d(LOG_TAG, "ShowId: "+showId);


				String episodeId = ItasaSubs.searchEpisode(showId, createRegex(serie, ep));
				Log.d(LOG_TAG, "Trovato l'id di itasa per l'episodio "+serie.getShowName()+"  s"+ep.getSeason().getSeason()+" e"+ep.getEpisode()+" ---> "+episodeId);
				ep.setItasaSubUrl(episodeId);
				myDBHelper.updateEpisode(ep);
				myDBHelper.insertNewAction(Calendar.getInstance().getTime(), 
						Action.FOUNDSUB, 
						ep, 
						"", 
						"");
				result.add(ep);
			}catch(Exception e){
				Log.w(LOG_TAG, e.getMessage());
			}
		}

		String notificationTitle;
		String notificationMessage;
		
		int total = myDBHelper.getEpisodesToSub().size();
		
		if( total  > 0 && result.size() > 0){
			notificationTitle = "Hai "+total+" sub da scaricare di cui "+result.size()+" nuovi";
			notificationMessage = "Guarda quali";
		}else{
			return;
		}
		
		Intent intent = new Intent(is, MyTVSeriesHD.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		//intent.putExtra(MyConstants.FILTER_TYPE, 4);
		PendingIntent pIntent = PendingIntent.getActivity(is, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

		Notification noti = new Notification.Builder(is)
			.setContentTitle(notificationTitle)
			.setContentText(notificationMessage)
			.setSmallIcon(R.drawable.ic_stat_new_episode)
			.setContentIntent(pIntent)
			.getNotification();

		noti.defaults = Notification.DEFAULT_ALL;
		noti.flags |= Notification.FLAG_AUTO_CANCEL;

		NotificationManager notificationManager = (NotificationManager) is.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(2, noti);

		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction(MySynchUpdateIntentService.ACTION_SYNCH_SUB);
		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		broadcastIntent.putExtra(MySynchUpdateIntentService.EXTRA_PARAM1, "Ho finito di effettuare il Synch: "+notificationTitle);
		is.sendBroadcast(broadcastIntent);
	}
	
	public static void handleActionSynchUpdates(IntentService is) throws SQLException {

		int updatedSeries = 0;
		int updatedEpisodes = 0;
		int updatedSeason = 0;
		
		TheTVDBApi api = new TheTVDBApi();
		DatabaseHelper myDBHelper = DataBaseHelperFactory.getDatabaseHelper(is);
		
		try {
			List<it.tiedor.mydbhelper.persistence.Serie> series = myDBHelper.getSerieDao().queryForAll();
			//series.add(myDBHelper.getSerieDao().queryForId(259669L));
			
			for(it.tiedor.mydbhelper.persistence.Serie dbSerie : series){
				
				Data data = api.getSerieWithEpisodes(dbSerie.getShowID());
				Serie tmPserie = data.getSeries().get(0);
				try{
					Log.d(LOG_TAG, "Cerco la serie con id ["+dbSerie.getShowID()+"]");
					
					if(true/*dbSerie.getLastUpdated() == null || dbSerie.getLastUpdated().getTime() < tmPserie.getLastUpdated()*/){
						dbSerie.setShowBannerUrl(tmPserie.getBannerURI());
						dbSerie.setShowPosterUrl(tmPserie.getPosterURI());
						dbSerie.setStatus(tmPserie.getStatus());
						dbSerie.setDayOfWeekAirs(tmPserie.getDayOfWeekAirs());
						dbSerie.setTimeAirs(tmPserie.getTimeAirs());
						dbSerie.setOverview(tmPserie.getOverview());
						dbSerie.setNetwork(tmPserie.getNetwork());
						dbSerie.setLastUpdated(new Date(tmPserie.getLastUpdated()));
						myDBHelper.updateSerie(dbSerie);

						updatedSeries++;
						Map<String, Banner> banners = api.getSeasonBanners(dbSerie.getShowID());

						for(Episode tmPepisode : data.getEpisodes()){
							Log.d(LOG_TAG, "Analizzo l'episodio ["+tmPepisode.getName()+"] ["+tmPepisode.getId()+"]");
							if(tmPepisode.getSeason().equals("0"))
								continue;
							
							Season season;
							
							it.tiedor.mydbhelper.persistence.Episode dbEpisode = myDBHelper.getEpisodeDao().queryForId(tmPepisode.getId());
							if(dbEpisode != null ){
								/*if(dbEpisode.getLastUpdate().after(new Date(tmPepisode.getLastUpdated())))
									continue;*/
								
								season = dbEpisode.getSeason();
								
								try{
									String bannerpath = banners.get(tmPepisode.getSeason()).getBannerPath();
									season.setSaesonImageUrl(bannerpath);
									myDBHelper.updateSeason(season);
								}catch(Exception e1){}
								
								try{
									dbEpisode.setAirTime(new SimpleDateFormat("yyyy-MM-dd", Locale.ITALY).parse(tmPepisode.getFirstAired()));
								}catch(Exception e2){}
								dbEpisode.setEpisodeImageUrl(tmPepisode.getFilename());
								dbEpisode.setEpisodeName(tmPepisode.getName());
								dbEpisode.setEpisode(Integer.parseInt(tmPepisode.getNumber()));
								dbEpisode.setLastUpdate(new Date(tmPepisode.getLastUpdated()));
								myDBHelper.updateEpisode(dbEpisode);
								updatedEpisodes++;
								Log.d(LOG_TAG, "Ho aggiornato l'episode ["+tmPepisode.getName()+"] con id ["+tmPepisode.getId()+"]");
							}else{

								try{
									Log.d(LOG_TAG, "Cerco la stagione ["+tmPepisode.getSeason()+"] per la serie ["+tmPserie.getId()+"]");
									season = myDBHelper.getSeason(Integer.parseInt(tmPepisode.getSeason()), dbSerie.getShowID());
									try{
										String bannerpath = banners.get(tmPepisode.getSeason()).getBannerPath();
										season.setSaesonImageUrl(bannerpath);
										myDBHelper.updateSeason(season);
									}catch(Exception e1){}
									if(season == null)
										throw new Exception();
									Log.d(LOG_TAG, "Stagione trovata");
								}catch(Exception e ){
									Log.d(LOG_TAG, "Stagione non trovata, la creo nuova");
									String bannerpath = "";
									try{
										bannerpath = banners.get(tmPepisode.getSeason()).getBannerPath();
									}catch(Exception e1){}
									season = new Season(
											Integer.parseInt(tmPepisode.getSeason()), 
											bannerpath, 
											dbSerie);
									
									season.setToFollow(true);
									
									updatedSeason++;
								}

								Date airDate;
								try{
									airDate = new SimpleDateFormat("yyyy-MM-dd").parse(tmPepisode.getFirstAired());
								}catch(Exception e){
									airDate = null;
								}
								dbEpisode = new it.tiedor.mydbhelper.persistence.Episode(tmPepisode.getId(),tmPepisode.getName(),Integer.parseInt(tmPepisode.getNumber()), 
										season,tmPepisode.getFilename(),"",airDate);

								myDBHelper.getEpisodeDao().createIfNotExists(dbEpisode);
								updatedEpisodes++;
							}
						}

						Log.d(LOG_TAG, "Ho aggiornato la serie ["+tmPserie.getName()+"] con id ["+tmPserie.getId()+"]");
					}
					
					Log.d(LOG_TAG, "Non ho aggiornato la serie ["+tmPserie.getName()+"] con id ["+tmPserie.getId()+"]");
				}catch(Exception e){
					Log.e(LOG_TAG, "Errore nella serie con id ["+tmPserie.getId()+"]", e);
				}
			}
		} catch (Exception e1) {
			Log.e(LOG_TAG, "Errore nel cercare gli update", e1);
		}
		
		/*ArrayList<it.tiedor.mydbhelper.persistence.Episode> episodeToUpdate = myDBHelper.getEpisodeToUpdate();
		
		for(it.tiedor.mydbhelper.persistence.Episode episode : episodeToUpdate){
			try{
				Episode tvdbEpisode = api.getEpisode(episode.getEpisodeId());
				if(tvdbEpisode != null && tvdbEpisode.getLastUpdated()>episode.getLastUpdate().getTime()){
					try{
						episode.setAirTime(new SimpleDateFormat("yyyy-MM-dd").parse(tvdbEpisode.getFirstAired()));
					}catch(Exception e2){}
					episode.setEpisodeImageUrl(tvdbEpisode.getFilename());
					episode.setEpisodeName(tvdbEpisode.getName());
					episode.setEpisode(Integer.parseInt(tvdbEpisode.getNumber()));
					episode.setLastUpdate(new Date(tvdbEpisode.getLastUpdated()));
					myDBHelper.updateEpisode(episode);
					updatedEpisodes++;
					Log.d(LOG_TAG, "Ho aggiornato l'episode ["+tvdbEpisode.getName()+"] con id ["+tvdbEpisode.getId()+"]");
				}
			}catch(Exception e){
				Log.e(LOG_TAG, "Errore nell'episode con id ["+episode.getEpisodeId()+"], ["+episode.getEpisodeName()+"]", e);
			}
		}
		
		String notificationTitle;
		
		if(updatedSeries + updatedEpisodes > 0){
			notificationTitle = "Ho aggiornato "+updatedSeries+" serie e "+updatedEpisodes+" episodi e "+updatedSeason+" stagioni";
		}else{
			return;
			//notificationTitle = "Non ho trovato nessun nuovo update";
		}
		
		Intent intent = new Intent(is, MyTVSeriesHD.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		PendingIntent pIntent = PendingIntent.getActivity(is, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

		Notification noti = new Notification.Builder(is)
		.setContentTitle(notificationTitle)
		.setSmallIcon(R.drawable.ic_stat_new_episode)
		.setContentIntent(pIntent)
		.getNotification();

		noti.defaults = Notification.DEFAULT_ALL;
		noti.flags |= Notification.FLAG_AUTO_CANCEL;

		NotificationManager notificationManager = (NotificationManager) is.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(1, noti);*/
	}
	
	private static String createRegex(String seriesName, it.tiedor.mydbhelper.persistence.Episode episode){

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

	private static String createRegex(it.tiedor.mydbhelper.persistence.Serie serie, it.tiedor.mydbhelper.persistence.Episode episode){

		Log.i(LOG_TAG, "Creo la regex per la serie: ["+serie.getShowName()+"]");

		String seriesName = (serie.getShowName().contains("(") ? serie.getShowName().substring(0, serie.getShowName().indexOf("(")) : serie.getShowName()).trim();

		return createRegex(seriesName, episode);
	}
	
}
