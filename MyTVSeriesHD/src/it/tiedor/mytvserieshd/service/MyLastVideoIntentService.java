package it.tiedor.mytvserieshd.service;

import java.io.File;
import java.sql.SQLException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import it.tiedor.mydbhelper.DatabaseHelper;
import it.tiedor.mydbhelper.persistence.Episode;
import it.tiedor.mytvserieshd.MyConstants;
import it.tiedor.mytvserieshd.exception.MyDBException;
import it.tiedor.mytvserieshd.receiver.MyResultReceiver;
import it.tiedor.mytvserieshd.util.RegexBuilder;
import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class MyLastVideoIntentService extends IntentService {

	private final String LOG_TAG = MyConstants.LOG_TAG + " - " + getClass().getSimpleName();
	
	public MyLastVideoIntentService() {
		super("MyLastVideoIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (intent != null) {
			try {
				handleAction();
			} catch (SQLException e) {
				Log.e(LOG_TAG, "Errore nel caricamento dell'ultima puntata vista", e);
				publishResults(-1, null, "", "");
			}
		}
	}

	private void handleAction() throws SQLException {
		DatabaseHelper mydbhHelper = new DatabaseHelper(this);
				
		Episode episode;
		
		try{
			episode = mydbhHelper.getLastViewEpisode();
		}catch(Exception e){
			episode = null;
		}
		
		if(episode == null)
			episode = mydbhHelper.getFavouriteEpisodes();
		
		if(episode == null){
			publishResults(-1, null, "", "");
			return;
		}
		
		String name = episode.getSeason().getSerie().getShowName()+" - "+episode.getSeason().getSeason()+"X"+episode.getEpisode()+" - "+episode.getEpisodeName();
		
		publishResults(0, episode, name, episode.getEpisodeImageUrl());
		
		searchVideoFile(episode);
	}
	
	private void searchVideoFile(Episode episode){
		String uri = "file://";
		String videoUri = "";
		String subUri = "";
		String folder_main = PreferenceManager.getDefaultSharedPreferences(this).getString("folder_main", "/sdcard/Movies");
		
		Collection<File> files = (FileUtils.listFiles(new File(folder_main),
				new RegexFileFilter(RegexBuilder.createRegex(episode.getSeason().getSerie().getShowName(), episode, false)),
				TrueFileFilter.TRUE));
		
		for(File file : files){
			Log.d(LOG_TAG, "File trovato: "+file.getAbsolutePath());
			if(file.getName().endsWith(".srt"))
				subUri = uri+file.getAbsolutePath();
			else if(file.getName().toLowerCase().endsWith(".mp4") || file.getName().toLowerCase().endsWith(".avi")
					|| file.getName().toLowerCase().endsWith(".mkv") || file.getName().toLowerCase().endsWith(".m4v"))
				videoUri = uri+file.getAbsolutePath();
			else if(file.getName().toLowerCase().endsWith(".zip") || file.getName().toLowerCase().endsWith(".rar"))
				FileUtils.deleteQuietly(file);
		}

		publishResults(1, episode, videoUri, subUri);
	}
	
	private void publishResults(int actionCode, Episode episode, String episodeName, String episodeImage) {
	    Intent intent = new Intent(MyResultReceiver.ACTION_RESP);
	    intent.putExtra(MyResultReceiver.CODE, actionCode);
	    intent.putExtra(MyResultReceiver.PARAM1, episodeName);
	    intent.putExtra(MyResultReceiver.PARAM2, episodeImage);
	    if(episode != null)
	    	intent.putExtra(MyResultReceiver.PARAM3, episode.getEpisodeId());
	    sendBroadcast(intent);
	  }
}
