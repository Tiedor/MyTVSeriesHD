package it.tiedor.mytvserieshd.service;

import java.sql.SQLException;
import java.util.List;

import it.tiedor.mydbhelper.DataBaseHelperFactory;
import it.tiedor.mydbhelper.DatabaseHelper;
import it.tiedor.mydbhelper.persistence.Episode;
import it.tiedor.mydbhelper.persistence.Season;
import it.tiedor.mydbhelper.persistence.Serie;
import it.tiedor.mytvserieshd.MyConstants;
import it.tiedor.mytvserieshd.fragment.MyOwnSeriesListFragment.MyResultReceiver;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MyDeleteSeriesIntentService extends IntentService {
	
	public static final String 	ACTION_DELETE_SERIE = "it.tiedor.mytvserieshd.action.DELETE_SERIE";

	public static final String 	SERIE_ID			= "it.tiedor.mytvserieshd.extra.SERIE_ID";
	public static final String 	SERIE_POSITION		= "it.tiedor.mytvserieshd.extra.SERIE_POSITION";
	
	private final 		String 	LOG_TAG 			= MyConstants.LOG_TAG + " - " + "MyOwnSeriesListFragment";

	public MyDeleteSeriesIntentService() {
		super("MyDeleteSeriesIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (intent != null) {
			final String action = intent.getAction();
			
			Intent broadcastIntent = new Intent();
			broadcastIntent.setAction(MyResultReceiver.ACTION_RESP);
			broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
			
			
			if (ACTION_DELETE_SERIE.equals(action)) {
				final Long param1 = intent.getLongExtra(SERIE_ID, -1);
				final int param2 = intent.getIntExtra(SERIE_POSITION, -1);
				try {
					
					if(param1 == -1)
						throw new Exception(); 
					
					handleActionDeleteSerie(param1);
					broadcastIntent.putExtra(MyResultReceiver.PARAM_OUT_MSG, 1);
					broadcastIntent.putExtra(SERIE_POSITION, param2);
				} catch (Exception e) {
					Log.e(LOG_TAG, "Errore nel cancellare la serie", e);
					broadcastIntent.putExtra(MyResultReceiver.PARAM_OUT_MSG, 0);
				}
			}
			
			sendBroadcast(broadcastIntent);
		}
	}

	/**
	 * Handle action Delete Serie in the provided background thread with the provided
	 * parameters.
	 * @throws SQLException 
	 */
	private void handleActionDeleteSerie(Long param1) throws SQLException {
		DatabaseHelper databaseHelper = DataBaseHelperFactory.getDatabaseHelper(this);
		
		Log.d(LOG_TAG, "Provo a cancellare la serie con id "+param1);
		
		Serie serie = databaseHelper.getSerieDao().queryForId(param1);
		
		List<Season> seasons = databaseHelper.getSeasons(serie.getShowID());
		
		for(Season season : seasons){
			List<Episode> episodes = databaseHelper.getSeasonsEpisodes(season.getSeasonId());
			databaseHelper.getEpisodeDao().delete(episodes);
			databaseHelper.getSeasonDao().delete(season);
		}
		
		databaseHelper.getSerieDao().delete(serie);
	}
}
