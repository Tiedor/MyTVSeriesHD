package it.tiedor.mytvserieshd.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.mediation.admob.AdMobExtras;
import com.google.android.gms.internal.ch;
import com.lamerman.FileDialog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;

import it.tiedor.mydbhelper.DataBaseHelperFactory;
import it.tiedor.mydbhelper.DatabaseHelper;
import it.tiedor.mydbhelper.persistence.Action;
import it.tiedor.mydbhelper.persistence.Episode;
import it.tiedor.mydbhelper.persistence.Season;
import it.tiedor.mytvserieshd.MyConstants;
import it.tiedor.mytvserieshd.R;
import it.tiedor.mytvserieshd.VideoActivity;

import it.tiedor.mytvserieshd.adapter.StickyGridHeadersSimpleArrayAdapter;

import it.tiedor.mytvserieshd.exception.MyNoRecordsFoundException;
import it.tiedor.mytvserieshd.fragment.abstr.AbstractEpisodeFragment;
import it.tiedor.mytvserieshd.listener.MultiChoiceModeListener;
import it.tiedor.mytvserieshd.loaders.FileSearchTaskLoader;
import it.tiedor.mytvserieshd.loaders.MyFilteredSeasonAsyncTaskLoader;
import it.tiedor.mytvserieshd.loaders.SubFileSearchTaskLoader;
import it.tiedor.mytvserieshd.loaders.TorrentTaskDownLoader;

import android.app.ProgressDialog;
import android.app.SearchManager;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.preference.PreferenceManager;

import android.util.Log;

import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class FilteredEpisodeListFragment extends AbstractEpisodeFragment{

	private MultiChoiceModeListener choiceModeListener;

	public FilteredEpisodeListFragment() {}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(LOG_TAG, "Sono nella oncreate del fragment");
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.d(LOG_TAG, "Sono nella onActivityCreated del fragment");
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onStart() {
		Log.d(LOG_TAG, "Sono nella onStart del fragment");
		super.onStart();
	}
	
	@Override
	public void onResume() {
		Log.d(LOG_TAG, "Sono nella onresume del fragment");
		super.onResume();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		Log.d(LOG_TAG, "Sono nella createView");
		
		View rootView = inflater.inflate(R.layout.filtered_episode_list, container, false);

		if(savedInstanceState != null){
			Log.d(LOG_TAG, "C'è il bundle Saved Instance");
			return rootView;		
		}
		
		this.listView = (StickyGridHeadersGridView) rootView.findViewById(android.R.id.list);		
		((StickyGridHeadersGridView)this.listView).setEmptyView(rootView.findViewById(android.R.id.empty));
		this.loading = (RelativeLayout) rootView.findViewById(R.id.loading);
		this.listLayout = (RelativeLayout) rootView.findViewById(R.id.listRelativeLayout);
		
		choiceModeListener = new MultiChoiceModeListener();
		choiceModeListener.setGrid(((StickyGridHeadersGridView)this.listView));
		((StickyGridHeadersGridView)this.listView).setMultiChoiceModeListener(choiceModeListener);		

		getLoaderManager().initLoader(0, getArguments(), this);

		loader = ImageLoader.getInstance();

		PauseOnScrollListener listener = new PauseOnScrollListener(loader, false, true);
		((StickyGridHeadersGridView)this.listView).setOnScrollListener(listener);

		registerForContextMenu(this.listView);

		move_on_view = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("move_on_view", false);

		adView = (AdView)rootView.findViewById(R.id.adView);
		if(!MyConstants.isTwoPane && (adView != null)){
			Bundle bundle = new Bundle();
			bundle.putString("color_bg", "245ABF");
			AdMobExtras extras = new AdMobExtras(bundle);
			AdRequest adRequest = new AdRequest.Builder().addNetworkExtras(extras).build();
			adView.loadAd(adRequest);
		}

		return rootView;
	}

	@Override
	public Loader<Object> onCreateLoader(int id, Bundle bundle) {

		Log.d(LOG_TAG, "Sono nella OnCreateLoader");
		
		progressDialog = ProgressDialog.show( getActivity(), "", "Loading...", false, false);

		final Loader<Object> l;

		switch (id) {
		case 0:
			l = new MyFilteredSeasonAsyncTaskLoader(getActivity(), bundle);
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

			this.loading.setVisibility(View.GONE);
			this.listLayout.setVisibility(View.VISIBLE);

			LinkedHashMap<Season, ArrayList<Episode>> items = (LinkedHashMap<Season, ArrayList<Episode>>) arg1;

			adapter = new StickyGridHeadersSimpleArrayAdapter(getActivity(), items, getArguments().getInt(MyConstants.EPISODE_FILTER, 0));
			((StickyGridHeadersSimpleArrayAdapter)adapter).setModeListener(choiceModeListener);
			((StickyGridHeadersSimpleArrayAdapter)adapter).setGridView(((StickyGridHeadersGridView)this.listView));
			((StickyGridHeadersGridView)this.listView).setAdapter((StickyGridHeadersSimpleArrayAdapter)adapter);

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

		Log.d(LOG_TAG, "Parte l'update");

		((StickyGridHeadersSimpleArrayAdapter)adapter).removeItem(position);
		((StickyGridHeadersSimpleArrayAdapter)adapter).notifyDataSetChanged();
		try{
			((StickyGridHeadersGridView)this.listView).setSelection(position);
		}catch(Exception e){}
		

		Log.d(LOG_TAG, "Update effettuato");
	}
}
