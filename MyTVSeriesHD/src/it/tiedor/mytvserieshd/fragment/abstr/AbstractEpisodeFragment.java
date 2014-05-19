package it.tiedor.mytvserieshd.fragment.abstr;

import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.Calendar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.mediation.admob.AdMobExtras;
import com.lamerman.FileDialog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;

import it.tiedor.mydbhelper.DataBaseHelperFactory;
import it.tiedor.mydbhelper.DatabaseHelper;
import it.tiedor.mydbhelper.persistence.Action;
import it.tiedor.mydbhelper.persistence.Episode;
import it.tiedor.mytvserieshd.MyConstants;
import it.tiedor.mytvserieshd.MyTVSeriesHD;
import it.tiedor.mytvserieshd.VideoActivity;
import it.tiedor.mytvserieshd.adapter.MyBucketEpisodeAdapter;
import it.tiedor.mytvserieshd.adapter.StickyGridHeadersSimpleArrayAdapter;
import it.tiedor.mytvserieshd.exception.MyNoRecordsFoundException;
import it.tiedor.mytvserieshd.fragment.MyFragment;
import it.tiedor.mytvserieshd.fragment.MyFragmentManager;
import it.tiedor.mytvserieshd.loaders.FileSearchTaskLoader;
import it.tiedor.mytvserieshd.loaders.MyOwnEpisodeAsyncTaskLoader;
import it.tiedor.mytvserieshd.loaders.SubFileSearchTaskLoader;
import it.tiedor.mytvserieshd.loaders.TorrentTaskDownLoader;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Adapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public abstract class AbstractEpisodeFragment extends MyFragment implements LoaderCallbacks<Object>{

	protected 					ProgressDialog 		progressDialog;
	protected 					View	 			listView;
	protected 					ImageLoader 		loader;
	protected 					String 				folder_main;
	protected 					boolean 			move_on_view;
	protected 					String 				folder_view;
	protected 					boolean 			move_on_download;
	protected	 				boolean 			move_on_sub;
	protected 	final 			String 				LOG_TAG 			= MyConstants.LOG_TAG + " - " + getClass().getSimpleName();
	protected 					AdView 				adView;

	protected					RelativeLayout		loading;
	protected					RelativeLayout		listLayout;
	protected					Adapter				adapter;

	@Override
	public abstract View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {		
		Log.d(LOG_TAG, "ContextMenu creating...");
		menu.setHeaderTitle("Menù Episodi");
		SubMenu editEpisodes = menu.addSubMenu(Menu.CATEGORY_SECONDARY, MyConstants.SUBMENU_EDIT_EPISODE, 0, "Imposta l'episodio come..");
		editEpisodes.add(Menu.CATEGORY_SECONDARY, MyConstants.MENU_NOT_DOWNLOADING, Menu.NONE, "..da scaricare");
		editEpisodes.add(Menu.CATEGORY_SECONDARY, MyConstants.MENU_DOWNLOADING, Menu.NONE, "..in download");
		editEpisodes.add(Menu.CATEGORY_SECONDARY, MyConstants.MENU_DOWNLOAD, Menu.NONE, "..scaricato");
		editEpisodes.add(Menu.CATEGORY_SECONDARY, MyConstants.MENU_DOWNLOAD_SUB, Menu.NONE, "..sottotitolato");
		editEpisodes.add(Menu.CATEGORY_SECONDARY, MyConstants.MENU_VIEW, Menu.NONE, "..visto");			
		menu.add(Menu.CATEGORY_SECONDARY, MyConstants.OPEN_DOWNLOAD_PAGE, 0, "Apri la pagina con i torrent");
		//menu.add(Menu.NONE, MyConstants.MENU_DELETE, Menu.NONE, "Elimina episodio");
		super.onCreateContextMenu(menu, v, menuInfo);    	
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if(item.getGroupId() != Menu.CATEGORY_SECONDARY)
			return false;

		DatabaseHelper databaseHelper = DataBaseHelperFactory.getDatabaseHelper(getActivity());

		Episode episode;

		long id = PreferenceManager.getDefaultSharedPreferences(getActivity()).getLong(MyConstants.EPISODE_ID_PROCESSING, -1);
		int position = PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt(MyConstants.EPISODE_POSITION_PROCESSING, -1);
		Log.d(LOG_TAG, "Ho selezionato l'elemento ["+item.getItemId()+"] sull'episodio ["+id+"] in posizione ["+position+"]");

		try{	    	
			switch (item.getItemId()){
			case MyConstants.MENU_NOT_DOWNLOADING:
				episode = databaseHelper.setEpisodeToDownload(id);
				update(episode, position);
				break;

			case MyConstants.MENU_VIEW:
				episode = databaseHelper.setEpisodeViewed(id);
				databaseHelper.insertNewAction(Calendar.getInstance().getTime(), Action.ENDVIEW, episode, "", "");
				update(episode, position);
				break;

			case MyConstants.MENU_DOWNLOAD:
				episode = databaseHelper.setEpisodeDownloaded(id);
				databaseHelper.insertNewAction(Calendar.getInstance().getTime(), Action.ENDDOWNLOAD, episode, "", "");
				update(episode, position);
				break;

			case MyConstants.MENU_DOWNLOADING:
				episode = databaseHelper.setEpisodeDownloading(id);
				databaseHelper.insertNewAction(Calendar.getInstance().getTime(), Action.STARTDOWNLOAD, episode, "", "");
				update(episode, position);
				break;

			case MyConstants.MENU_DOWNLOAD_SUB:
				episode = databaseHelper.setEpisodeSubbed(id);
				databaseHelper.insertNewAction(Calendar.getInstance().getTime(), Action.DOWNLOADSUB, episode, "", "");
				update(episode, position);
				break;

			case MyConstants.OPEN_DOWNLOAD_PAGE:
				episode = databaseHelper.getEpisodeDao().queryForId(id);
				String term = episode.getSeason().getSerie().getShowName().replace(".", " ") + " s"+String.format("%02d", episode.getSeason().getSeason())+"e"+String.format("%02d", episode.getEpisode());
				/*term = URLEncoder.encode(term, "UTF-8");
				term = term.replace("+", "%20");
				String url = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(MyConstants.TORRENT, "http://katunblock.com")+"/usearch/"+term+"/";
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));*/
				
				Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
				intent.putExtra(SearchManager.QUERY, term+" torrent");
				
				
				
				startActivityForResult(intent, 2);

				break;
			default:
				episode = null;
				break;
			}
		}catch(Exception e){
			return false;
		}

		return true;
	}

	@Override
	public abstract Loader<Object> onCreateLoader(int id, Bundle bundle);

	@Override
	public abstract void onLoadFinished(Loader<Object> arg0, Object arg1);

	@Override
	public void onBackPressed() {
		((MyTVSeriesHD)getActivity()).selectItem(0);
		//MyFragmentManager.setFragment(0, getArguments(), getActivity(), getTag());
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		Log.d(LOG_TAG, "Torno dalla requestCode ["+requestCode+"] con la resultCode ["+resultCode+"]");
		
		final DatabaseHelper databaseHelper = DataBaseHelperFactory.getDatabaseHelper(getActivity());

		final Long videoId = PreferenceManager.getDefaultSharedPreferences(getActivity()).getLong(MyConstants.EPISODE_ID_PROCESSING, -1);
		final Integer position = PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt(MyConstants.EPISODE_POSITION_PROCESSING, -1);

		switch (requestCode) {
		case 0:

				final Bundle b = new Bundle();

				b.putLong("id", videoId);

				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle("Modifica Episodio");
				builder.setMessage("Hai terminato di vedere l'episodio?");

				builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						try {							
							if(move_on_view)
								it.tiedor.mytvserieshd.utils.FileUtils.moveViewVideoFile(getActivity());

							Episode ep = databaseHelper.setEpisodeViewed(videoId);
							update(ep, position);

							Action action = databaseHelper.insertNewAction(
									Calendar.getInstance().getTime(), 
									Action.ENDVIEW, 
									ep, 
									PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(MyConstants.MX_EXTRA_VIDEO_LIST, null), 
									"");
									
						} catch (Exception e) {
							Log.e(LOG_TAG, e.getMessage());
						}
					}
				});
				builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Toast.makeText(getActivity(), "Non hai terminato di vedere l'episodio", Toast.LENGTH_LONG).show();
					}
				});

				AlertDialog dialog = builder.create();
				dialog.show();

			break;
		case 1:
			if (resultCode == Activity.RESULT_OK) {
				String filePath = data.getStringExtra(FileDialog.RESULT_PATH);

				try {
					String newfileName = it.tiedor.mytvserieshd.utils.FileUtils.moveVideoFile(new String[]{filePath, "", MyConstants.ACTUAL_REGEX}, getActivity());
					
					Episode episode = databaseHelper.setEpisodeDownloaded(videoId);
					update(episode, position);
					
					Action action = databaseHelper.insertNewAction(
							Calendar.getInstance().getTime(), 
							Action.ENDDOWNLOAD, 
							episode, 
							filePath,
							newfileName);
					
				} catch (MyNoRecordsFoundException e) {
					Log.e(LOG_TAG, e.getMessage(), e);
				} catch (SQLException e) {
					Log.e(LOG_TAG, e.getMessage(), e);
				}
			}
			break;
		default:
			Episode episode;
			try {
				episode = databaseHelper.getEpisodeDao().queryForId(videoId);
				update(episode, position);
			} catch (SQLException e) {
				Log.e(LOG_TAG, "Errore nel refresh della lista", e);
			}
			break;
		}


	}

	protected abstract void update(Episode episode, Integer position);

	@Override
	public void onStop() {
		if(adView != null)
			adView.destroy();
		loader.stop();
		super.onStop();
	}

	@Override
	public void onPause() {
		if(adView != null)
			adView.destroy();
		loader.pause();
		super.onPause();
	}

	@Override
	public void onResume() {
		if(adView != null){
			Bundle bundle = new Bundle();
			bundle.putString("color_bg", "245ABF");
			AdMobExtras extras = new AdMobExtras(bundle);
			AdRequest adRequest = new AdRequest.Builder().addNetworkExtras(extras).build();
			adView.loadAd(adRequest);
		}
		loader.resume();
		super.onResume();
	}

	protected boolean appInstalledOrNot(ComponentName uri) {
		PackageManager pm = getActivity().getPackageManager();
		boolean app_installed = false;
		try {
			pm.getActivityInfo(uri, PackageManager.GET_ACTIVITIES);
			app_installed = true;
		}
		catch (PackageManager.NameNotFoundException e) {
			app_installed = false;
		}
		return app_installed ;
	}
	
	protected void openTorrentFile(String arg1, long episodeId, DatabaseHelper databaseHelper){
		try{
			Intent intent;
			if(arg1.contains("magnet")){
				intent = new Intent(Intent.ACTION_VIEW, Uri.parse(arg1));
			}else{
				intent = new Intent(Intent.ACTION_WEB_SEARCH);
				intent.putExtra(SearchManager.QUERY, arg1); // query contains search string					
			}
				
			startActivityForResult(intent, 2);
			if(episodeId > 0){
				Episode episode = databaseHelper.setEpisodeDownloading(episodeId);
				Action action = databaseHelper.insertNewAction(
						Calendar.getInstance().getTime(), 
						Action.STARTDOWNLOAD, 
						databaseHelper.getEpisodeDao().queryForId(episodeId), 
						"", 
						"");
			}

		}catch (Exception e) {
			Log.e(LOG_TAG, "Errore", e);
		}
	}
	
	protected void moveVideoFile(String[] arg1, long episodeId, DatabaseHelper databaseHelper, int position){
		try {
			Log.d(LOG_TAG, "Il primo elemento è: "+arg1[0]);
			if(arg1[0] != null && !arg1[0].equals("")){
				String newfileName = it.tiedor.mytvserieshd.utils.FileUtils.moveVideoFile(arg1, getActivity());
				Episode episode = databaseHelper.setEpisodeDownloaded(episodeId);
				update(episode, position);
				
				Action action = databaseHelper.insertNewAction(
						Calendar.getInstance().getTime(), 
						Action.ENDDOWNLOAD, 
						episode, 
						arg1[0],
						newfileName);
				
			}else{
				
				Toast.makeText(getActivity(), "File non trovato, selezionarlo manualmente", Toast.LENGTH_LONG).show();
				
				Intent intent = new Intent(getActivity(), FileDialog.class);
				intent.putExtra(FileDialog.START_PATH, "/");

				intent.putExtra(FileDialog.CAN_SELECT_DIR, false);

				intent.putExtra(FileDialog.FORMAT_FILTER, new String[] { "mp4", "mkv", "mpeg", "m4v" });
				intent.putExtra(MyConstants.REGEX, arg1[2]);
				MyConstants.ACTUAL_REGEX = arg1[2];

				startActivityForResult(intent, 1);
			}
			getLoaderManager().destroyLoader(2);

		}catch (MyNoRecordsFoundException e) {
			Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
		}catch (Exception e) {
			Log.e(LOG_TAG, "Errore", e);
		}
	}
	
	protected void moveSubFile(String[] arg1, long episodeId, DatabaseHelper databaseHelper, int position){
		try {

			String newfileName = it.tiedor.mytvserieshd.utils.FileUtils.moveSubFile(((String[])arg1), getActivity());
			Toast.makeText(getActivity(), "Modifica effettuata con successo", Toast.LENGTH_LONG).show();

			Episode episode = databaseHelper.setEpisodeJustSubbed(episodeId);
			update(episode, position);
			
			Action action = databaseHelper.insertNewAction(
					Calendar.getInstance().getTime(), 
					Action.DOWNLOADSUB, 
					episode, 
					((String[])arg1)[1],
					newfileName);

		}catch (Exception e) {
			Toast.makeText(getActivity(), "Impossibile effettuare la modifica", Toast.LENGTH_LONG).show();
			Log.e(LOG_TAG, "Impossibile effettuare la modifica", e);
		}
	}
	
	protected void openVideoFile(String[] arg1, long episodeId, DatabaseHelper databaseHelper, int position){
		try {

			if(arg1.getClass() != String[].class || ((String[])arg1).length < 2){
				throw new MyNoRecordsFoundException("Non sono stati trovati il video o i sottotitoli per questo episodio");
			}

			String videoUri = ((String[])arg1)[0];
			String subUri = ((String[])arg1)[1];

			if(videoUri.equals(""))
				throw new MyNoRecordsFoundException("Non è stato trovato il file video per questo episodio");
			else if(subUri.equals(""))
				throw new MyNoRecordsFoundException("Non è stato trovato il file dei sottotitoli per questo episodio");

			PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
				.putString(MyConstants.MX_EXTRA_VIDEO_LIST, videoUri)
				.putString(MyConstants.MX_PLAYER_EXTRA_SUBS, subUri)
				.commit();
			Intent i;

			Action action = databaseHelper.insertNewAction(
					Calendar.getInstance().getTime(), 
					Action.STARTVIEW, 
					databaseHelper.getEpisodeDao().queryForId(episodeId), 
					videoUri,
					"");
			
			boolean useNativePlayer = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(MyConstants.USE_NATIVE_PLAYER, false);

			if(useNativePlayer){
				i = new Intent(getActivity(), VideoActivity.class);
				i.putExtra("videoUri", videoUri);
				i.putExtra("subUri", subUri);

			}else{
				/*i = new Intent(Intent.ACTION_VIEW);
				i.setDataAndType(Uri.parse(videoUri), "video/*" );*/
				if (!appInstalledOrNot(new ComponentName(MyConstants.MXVP, MyConstants.MXVP_PLAYBACK_CLASS))) {
					i = new Intent(Intent.ACTION_VIEW);
					i.setDataAndType(Uri.parse(videoUri), "video/*" );
				}else{
					i = new Intent();
					i.setPackage(MyConstants.MXVP);
					i.setDataAndType(Uri.parse(videoUri), "application/*" );
					i.putExtra(MyConstants.MX_PLAYER_EXTRA_DECODE_MODE, (byte)1 );
					i.putExtra(MyConstants.MX_PLAYER_EXTRA_SUBS, Uri.parse(subUri));
					i.putExtra(MyConstants.MX_EXTRA_VIDEO_LIST, new Uri[]{Uri.parse(videoUri)});
					i.putExtra(MyConstants.MX_PLAYER_EXTRA_RETURN_RESULT, true);
					String[] headers = new String[] { "User-Agent", "MX Player Caller App/1.0", "Extra-Header", "911" };
					i.putExtra(MyConstants.MX_EXTRA_HEADERS, headers );
					i.setClassName(MyConstants.MXVP, MyConstants.MXVP_PLAYBACK_CLASS);
				}
			}

			getLoaderManager().destroyLoader(1);

			startActivityForResult(i, 0);
		}catch (MyNoRecordsFoundException e) {
			Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
		}catch (Exception e) {
			Log.e(LOG_TAG, "Impossibile riprodurre l'episodio", e);
			Toast.makeText(getActivity(), "Impossibile riprodurre l'episodio", Toast.LENGTH_LONG).show();
		}
	}
}
