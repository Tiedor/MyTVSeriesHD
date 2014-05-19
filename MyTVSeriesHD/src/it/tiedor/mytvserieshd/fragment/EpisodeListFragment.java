package it.tiedor.mytvserieshd.fragment;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.mediation.admob.AdMobExtras;
import com.google.android.gms.internal.in;
import com.lamerman.FileDialog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

import it.tiedor.mydbhelper.DataBaseHelperFactory;
import it.tiedor.mydbhelper.DatabaseHelper;
import it.tiedor.mydbhelper.persistence.Action;
import it.tiedor.mydbhelper.persistence.Episode;
import it.tiedor.mydbhelper.persistence.Serie;
import it.tiedor.mytvserieshd.MyConstants;
import it.tiedor.mytvserieshd.R;
import it.tiedor.mytvserieshd.VideoActivity;
import it.tiedor.mytvserieshd.adapter.MyBucketEpisodeAdapter;
import it.tiedor.mytvserieshd.exception.MyNoRecordsFoundException;
import it.tiedor.mytvserieshd.fragment.abstr.AbstractEpisodeFragment;
import it.tiedor.mytvserieshd.loaders.FileSearchTaskLoader;
import it.tiedor.mytvserieshd.loaders.MyOwnEpisodeAsyncTaskLoader;
import it.tiedor.mytvserieshd.loaders.SubFileSearchTaskLoader;
import it.tiedor.mytvserieshd.loaders.TorrentTaskDownLoader;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.DialogInterface.OnCancelListener;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class EpisodeListFragment extends AbstractEpisodeFragment implements LoaderCallbacks<Object>{

	public EpisodeListFragment() {}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.series_list_container, container, false);

		this.listView = rootView.findViewById(R.id.list_view);

		Log.d(LOG_TAG, "Ho aperto la serie ["+getArguments().getLong(MyConstants.SEASON_ID_KEY)+"]");
		getLoaderManager().initLoader(0, getArguments(), this);

		loader = ImageLoader.getInstance();

		PauseOnScrollListener listener = new PauseOnScrollListener(loader, false, true);
		((ListView)this.listView).setOnScrollListener(listener);

		move_on_view = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("move_on_view", false);
		folder_view = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("folder_view", "/view");

		move_on_download = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("move_on_download", false);
		move_on_sub = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("move_on_sub", false);

		folder_main = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("folder_main", "/sdcard/Movies");

		adView = (AdView)rootView.findViewById(R.id.adView);
		if(adView != null){
			Bundle bundle = new Bundle();
			bundle.putString("color_bg", "245ABF");
			AdMobExtras extras = new AdMobExtras(bundle);
			AdRequest adRequest = new AdRequest.Builder().addNetworkExtras(extras).build();
			adView.loadAd(adRequest);
		}

		return rootView;
	}

	@Override
	public void onLoadFinished(Loader<Object> arg0, Object arg1) {

		DatabaseHelper databaseHelper = DataBaseHelperFactory.getDatabaseHelper(getActivity());
		Long episodeId = PreferenceManager.getDefaultSharedPreferences(getActivity()).getLong(MyConstants.EPISODE_ID_PROCESSING, -1);
		Integer position = PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt(MyConstants.EPISODE_POSITION_PROCESSING, -1);

		switch (arg0.getId()) {
		case 1:
			openTorrentFile(((String)arg1), episodeId, databaseHelper);			
			break;
		case 2:				
			moveVideoFile(((String[])arg1), episodeId, databaseHelper, position);
			break;
		case 3:
			moveSubFile(((String[])arg1), episodeId, databaseHelper, position);
			break;
		case 4:
			openVideoFile(((String[])arg1), episodeId, databaseHelper, position);
			break;
		default:

			ArrayList<Episode> episodes = (ArrayList<Episode>) arg1;

			Log.d(LOG_TAG, "La lista è di "+episodes.size()+" elementi");

			int numberOfEpisodes = 2;
			if(MyConstants.isTwoPane)
				numberOfEpisodes = 3;

			MyBucketEpisodeAdapter adap = new MyBucketEpisodeAdapter(getActivity(), episodes, numberOfEpisodes);
			switch (getActivity().getResources().getDisplayMetrics().densityDpi) {
			case DisplayMetrics.DENSITY_XHIGH:
				adap.enableAutoMeasure(180);
				break;
			case DisplayMetrics.DENSITY_MEDIUM:
				adap.enableAutoMeasure(360);
				break;
			default:
				adap.enableAutoMeasure(180);
				break;
			}

			((ListView)this.listView).setAdapter(adap);
			registerForContextMenu(this.listView);

			break;
		}

		if(progressDialog != null && progressDialog.isShowing())
			progressDialog.dismiss();
	}

	@Override
	public void onLoaderReset(Loader<Object> arg0) {

	}

	@Override
	protected void update(Episode episode, Integer position) {    	
		MyBucketEpisodeAdapter adap = ((MyBucketEpisodeAdapter)((ListView)this.listView).getAdapter());
		adap.remove(adap.getItem(position));
		adap.insert(episode, position);
		((ListView)this.listView).setSelection(position);
		adap.notifyDataSetChanged();
	}

	@Override
	public Loader<Object> onCreateLoader(int id, Bundle bundle) {

		progressDialog = ProgressDialog.show( getActivity(), "", "Loading...", false, false);

		final Loader<Object> l;

		switch (id) {
		case 0:
			l = new MyOwnEpisodeAsyncTaskLoader(getActivity(), bundle);
			break;
		case 1:
			l = new TorrentTaskDownLoader(getActivity(), bundle.getString(MyConstants.TORRENT_URL), bundle.getString(MyConstants.OTHER_URL));
			break;
		case 3:
			l = new SubFileSearchTaskLoader(getActivity(), bundle.getString(MyConstants.REGEX),  bundle.getString(MyConstants.ITASA_SUB_HREF));
			break;
		default:
			l = new FileSearchTaskLoader(getActivity(), bundle.getString(MyConstants.REGEX), bundle.getLong("id", 0));		
			break;			
		}

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
		    progressDialog.setCancelable(true);
		    progressDialog.setOnCancelListener(new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					l.cancelLoad();					
				}
			});
		}

		return l;
	}
}
