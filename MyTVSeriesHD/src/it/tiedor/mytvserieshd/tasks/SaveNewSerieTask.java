package it.tiedor.mytvserieshd.tasks;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import it.tiedor.mythetvdb.persistence.Banner;
import it.tiedor.mythetvdb.persistence.Episode;
import it.tiedor.mydbhelper.DataBaseHelperFactory;
import it.tiedor.mydbhelper.DatabaseHelper;
import it.tiedor.mydbhelper.persistence.Season;
import it.tiedor.mydbhelper.persistence.Serie;
import it.tiedor.mythetvdb.TheTVDBApi;
import it.tiedor.mytvserieshd.MyConstants;
import it.tiedor.mytvserieshd.fragment.MyFragmentManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

public class SaveNewSerieTask extends AsyncTask<Serie, String, Integer> {

	private Context ctx;
	private DatabaseHelper helper;
	private TheTVDBApi api;
	private ProgressDialog dialog;
	private final static 	String 	LOG_TAG = MyConstants.LOG_TAG + " - " + SaveNewSerieTask.class.getSimpleName();
	
	public SaveNewSerieTask(Context ctx, ProgressDialog dialog) {
		this.ctx = ctx;
		this.dialog = dialog;
		this.helper = DataBaseHelperFactory.getDatabaseHelper(this.ctx);
	}
	
	@Override
	protected void onPreExecute() {
		this.api = new TheTVDBApi();
		super.onPreExecute();
	}
	
	@Override
	protected Integer doInBackground(Serie... params) {
		int count = 0;
				
		for(Serie serie : params){
			publishProgress("Salvo sul la serie "+serie.getShowName());
			helper.getSerieRuntimeDao().createOrUpdate(serie);			
			try {
				publishProgress("Cerco gli episodi per la serie "+serie.getShowName());
				
				ArrayList<Episode> episodeList = new ArrayList<Episode>(this.api.getEpisodes(serie.getShowID()));
				
				Log.d(LOG_TAG, "La serie ha "+episodeList.size()+" elementi");
				
				Map<String, Banner> banners = this.api.getSeasonBanners(serie.getShowID());
				
				for(Episode episode : episodeList){
					
					publishProgress("Salvo l'episodio "+episode.getName()+" - "+serie.getShowID()+"[s"+episode.getSeason()+"e"+episode.getNumber()+"]");
					
					if(episode.getSeason().equals("0"))
						continue;					
					
					Log.d(LOG_TAG, "Inserisco l'episodio "+episode.getSeason()+"X"+episode.getNumber());
										
					Season season;
					
					try{
						Log.d(LOG_TAG, "Cerco la stagione ["+episode.getSeason()+"] per la serie ["+serie.getShowID()+"]");
						season = helper.getSeason(Integer.parseInt(episode.getSeason()), serie.getShowID());
						if(season == null)
							throw new Exception();
						Log.d(LOG_TAG, "Stagione trovata");
					}catch(Exception e ){
						Log.d(LOG_TAG, "Stagione non trovata, la creo nuova");
						String bannerPath = "";
						try{
							bannerPath = banners.get(episode.getSeason()).getBannerPath();
						}catch(Exception e1){}
						season = new Season(Integer.parseInt(episode.getSeason()), bannerPath, serie);
					}
					
					Date airDate;
					try{
						airDate = new SimpleDateFormat("yyyy-MM-dd").parse(episode.getFirstAired());
					}catch(Exception e){
						airDate = null;
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
					
					helper.getEpisodeDao().createIfNotExists(ep);
				}
				
				Season lastSeason = helper.getLastSeason(serie.getShowID());
				helper.setSeasonFavourite(lastSeason.getSeasonId());
			} catch (Exception e) {
				Log.e(LOG_TAG, "Errore nel cercare gli episodi della serie "+serie.getShowID(), e);
			}			
		}
		if(this.dialog != null)
			this.dialog.dismiss();
		return count;
	}
	
	@Override
	protected void onPostExecute(Integer result) {
		if(MyConstants.isTwoPane){
			MyFragmentManager.reloadFragment(0, null, ((Activity)ctx), MyConstants.TAG_MAIN);
		}
	}
}
