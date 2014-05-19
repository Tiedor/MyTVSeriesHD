package it.tiedor.mytvserieshd.fragment;

import java.util.ArrayList;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.mediation.admob.AdMobExtras;
import com.google.android.gms.internal.ad;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

import it.tiedor.mydbhelper.persistence.Season;
import it.tiedor.mytvserieshd.MyConstants;
import it.tiedor.mytvserieshd.R;
import it.tiedor.mytvserieshd.adapter.MyBucketAdapter;
import it.tiedor.mytvserieshd.adapter.MyBucketSeasonsAdapter;
import it.tiedor.mytvserieshd.loaders.NewSerieAsyncTaskLoader;
import it.tiedor.mytvserieshd.loaders.SeasonAsyncTaskLoader;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentManager.OnBackStackChangedListener;
import android.app.ProgressDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.DialogInterface;
import android.content.Loader;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;

public class SeasonListFragment extends MyFragment implements LoaderCallbacks<ArrayList<Season>>{
		
    
    private 					ProgressDialog 		progressDialog;
    private 					ListView 			listView;
    private 					ImageLoader 		loader;
    private						boolean				dataDownloaded 	= false;
    public 	final 	static 		int					CALLER = 2;
    
	private final String 		LOG_TAG 	= MyConstants.LOG_TAG + " - " + getClass().getSimpleName();
	
	public SeasonListFragment() {}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		super.onCreateView(inflater, container, savedInstanceState);
		
		View rootView = inflater.inflate(R.layout.series_list_container, container, false);
		
		this.listView = (ListView) rootView.findViewById(R.id.list_view);
		
		if(!dataDownloaded)
			getLoaderManager().initLoader(0, getArguments(), this);
		
		loader = ImageLoader.getInstance();
		
		PauseOnScrollListener listener = new PauseOnScrollListener(loader, false, true);
		this.listView.setOnScrollListener(listener);
				
		AdView adView = (AdView)rootView.findViewById(R.id.adView);
		if(adView != null)
			adView.setVisibility(View.GONE);
		
        return rootView;
	}
	
	@Override
	public void onStop() {
		loader.stop();
		super.onStop();
	}
	
	@Override
	public void onResume() {
		loader.resume();
		super.onResume();
	}
	
	@Override
	public Loader<ArrayList<Season>> onCreateLoader(int id, Bundle bundle) {
		
		progressDialog = ProgressDialog.show( getActivity(), "", "Loading...", false, false);
		
		switch (id) {
			default:
				final Loader<ArrayList<Season>> l = new SeasonAsyncTaskLoader(getActivity(), bundle);
				return l;
		}
	}

	@Override
	public void onLoadFinished(Loader<ArrayList<Season>> arg0,
			ArrayList<Season> arg1) {
				
		Log.d(LOG_TAG, "La lista è di "+arg1.size()+" elementi");
		
		MyBucketSeasonsAdapter adap = new MyBucketSeasonsAdapter(getActivity(), arg1, 2);
		
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
		
		dataDownloaded = true;
		if(progressDialog != null && progressDialog.isShowing())
			progressDialog.dismiss();
		
		this.listView.setAdapter(adap);
	}

	@Override
	public void onLoaderReset(Loader<ArrayList<Season>> arg0) {
		
	}

	@Override
	public void onBackPressed() {
		MyFragmentManager.setFragment(0, getArguments(), getActivity(), getTag());
	}
}
