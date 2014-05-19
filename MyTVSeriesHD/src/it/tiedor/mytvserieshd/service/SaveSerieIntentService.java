package it.tiedor.mytvserieshd.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import it.tiedor.mydbhelper.DatabaseHelper;
import it.tiedor.mydbhelper.persistence.Season;
import it.tiedor.mydbhelper.persistence.Serie;
import it.tiedor.mythetvdb.TheTVDBApi;
import it.tiedor.mythetvdb.persistence.Banner;
import it.tiedor.mythetvdb.persistence.Episode;
import it.tiedor.mytvserieshd.MyConstants;
import it.tiedor.mytvserieshd.receiver.MyResultReceiver;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class SaveSerieIntentService extends IntentService {

	public static final String ACTION_SAVE_NEW_SERIE = "it.tiedor.mytvserieshd.action.SaveNewSerie";

	public static final String EXTRA_PARAM1 = "it.tiedor.mytvserieshd.extra.PARAM1";

	private final 		String LOG_TAG 		= MyConstants.LOG_TAG + " - " + getClass().getSimpleName();
	
	public SaveSerieIntentService() {
		super("SaveSerieIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (intent != null) {
			final String action = intent.getAction();
			if (ACTION_SAVE_NEW_SERIE.equals(action)) {
				final Serie param1 = (Serie) intent.getSerializableExtra(EXTRA_PARAM1);
				try {
					handleActionSaveNewSerie(param1);
				} catch (Exception e) {
					Log.e(LOG_TAG, "Errore nel salvare la serie", e);
				}
			}
		}
	}

	/**
	 * Handle action SaveNewSerie in the provided background thread with the provided
	 * parameters.
	 * @throws Exception 
	 */
	private void handleActionSaveNewSerie(Serie serie) throws Exception{
		
		TheTVDBApi api = new TheTVDBApi();
		
		DatabaseHelper helper = new DatabaseHelper(this);
		
		publishResults(0, "Sto salvando "+serie.getShowName()+" sul tuo dispositivo");
		
		helper.getSerieRuntimeDao().createOrUpdate(serie);
		
		ArrayList<Episode> episodeList = new ArrayList<Episode>(api.getEpisodes(serie.getShowID()));
		
		Log.d(LOG_TAG, "La serie ha "+episodeList.size()+" elementi");
		
		Map<String, Banner> banners = api.getSeasonBanners(serie.getShowID());
		
		for(Episode episode : episodeList){
			
			if(episode.getSeason().equals("0") || episode.getNumber().equals("0"))
				continue;					
			
			publishResults(1, "Inserisco l'episodio "+episode.getSeason()+"X"+episode.getNumber()+" - "+episode.getName());
			
			Log.d(LOG_TAG, "Inserisco l'episodio "+episode.getSeason()+"X"+episode.getNumber());
			
			Date airDate;
			try{
				airDate = new SimpleDateFormat("yyyy-MM-dd").parse(episode.getFirstAired());
			}catch(Exception e){
				airDate = null;
			}
			
			Season season;
			
			try{
				Log.d(LOG_TAG, "Cerco la stagione ["+episode.getSeason()+"] per la serie ["+serie.getShowID()+"]");
				season = helper.getSeason(Integer.parseInt(episode.getSeason()), serie.getShowID());
				if(season == null)
					throw new Exception();
				if(!season.isToFollow() && airDate.after(Calendar.getInstance().getTime()))
					helper.setSeasonFavourite(season.getSeasonId());
				Log.d(LOG_TAG, "Stagione trovata");
			}catch(Exception e ){
				Log.d(LOG_TAG, "Stagione non trovata, la creo nuova");
				String bannerPath = "";
				try{
					bannerPath = banners.get(episode.getSeason()).getBannerPath();
				}catch(Exception e1){}
				season = new Season(Integer.parseInt(episode.getSeason()), bannerPath, serie);
				if(!season.isToFollow() && airDate.after(Calendar.getInstance().getTime()))
					season.setToFollow(true);
			}
			
			it.tiedor.mydbhelper.persistence.Episode ep = 
					new it.tiedor.mydbhelper.persistence.Episode(
							episode.getId(),
							episode.getName(), 
							Integer.parseInt(episode.getNumber()), 
							season, 
							episode.getFilename(),
							"",
							airDate
							);
			
			helper.getEpisodeDao().createOrUpdate(ep);

		}
		
		publishResults(2, "Hai aggiunto "+serie.getShowName()+" alle tue serie preferite");
	}
	
	private void publishResults(int actionCode, String result) {
	    Intent intent = new Intent(MyResultReceiver.ACTION_RESP);
	    intent.putExtra(MyResultReceiver.CODE, actionCode);
	    intent.putExtra(MyResultReceiver.RESULT, result);
	    sendBroadcast(intent);
	  }
}
