package it.tiedor.mytvserieshd.fragment;

import java.util.ArrayList;

import com.google.android.gms.ads.AdView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

import it.tiedor.mythetvdb.persistence.Serie;
import it.tiedor.mytvserieshd.MyConstants;
import it.tiedor.mytvserieshd.R;
import it.tiedor.mytvserieshd.adapter.MyBucketAdapter;
import it.tiedor.mytvserieshd.loaders.NewSerieAsyncTaskLoader;
import it.tiedor.mytvserieshd.receiver.MyResultReceiver;
import it.tiedor.mytvserieshd.service.SaveSerieIntentService;

import android.app.ProgressDialog;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.DialogInterface.OnCancelListener;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class SearchNewSeriesListFragment extends MyFragment implements LoaderCallbacks<ArrayList<Serie>>{


	private 					ProgressDialog 		progressDialog;
	private 					ListView 			listView;
	private 					ImageLoader 		loader;
	private						boolean				dataDownloaded 	= false;
	public 	final 	static 		int					CALLER = 2;
	private						IntentFilter		filter = new IntentFilter(MyResultReceiver.ACTION_RESP);
	private 					BroadcastReceiver 	resultReceiver = new BroadcastReceiver() {
						
		public static final String CODE = "it.tiedor.mytvserieshd.receiver.CODE";
		public static final String RESULT = "it.tiedor.mytvserieshd.receiver.RESULT";
		
		@Override
		public void onReceive(Context arg0, Intent arg1) {			
			int code = arg1.getIntExtra(CODE, -1);
			
			Log.d(LOG_TAG, "Sono nell'onReceive con CODE ["+code+"]");
			
			String result = arg1.getStringExtra(RESULT);
			
			if(code == 0){
				getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
				((RelativeLayout)getActivity().findViewById(R.id.relativeLayout)).setGravity(Gravity.CENTER);
				getActivity().findViewById(R.id.list_view).setVisibility(View.GONE);
				getActivity().findViewById(R.id.status).setVisibility(View.VISIBLE);
				
				((TextView)getActivity().findViewById(R.id.status_message)).setText(result);
			}else if (code == 1) {
				((TextView)getActivity().findViewById(R.id.status_message)).setText(result);
			}else if (code == 2) {
				getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
				Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
				MyFragmentManager.setFragment(0, getArguments(), getActivity(), getTag());
				try{
					if(MyConstants.isTwoPane)
						MyFragmentManager.removeFragment(getActivity(), R.id.serie_detail_container);
				}catch(Exception e){
					Log.e(LOG_TAG, "Impossibile aggiornare la slave", e);
				}
			}
			
		}
	};

	private final String 		LOG_TAG 	= MyConstants.LOG_TAG + " - " + "SearchNewSeriesListFragment";

	public SearchNewSeriesListFragment() {}

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

		getActivity().registerReceiver(resultReceiver, filter);
		
		return rootView;
	}

	@Override
	public void onResume() {
		loader.resume();
		getActivity().registerReceiver(resultReceiver, filter);
		super.onResume();
	}
	
	@Override
	public void onStop() {
		loader.stop();
		try{
			getActivity().unregisterReceiver(resultReceiver);
		}catch(Exception e){}
		super.onStop();
	}
	
	@Override
	public void onPause() {
		loader.stop();
		try{
			getActivity().unregisterReceiver(resultReceiver);
		}catch(Exception e){}
		super.onPause();
	}

	@Override
	public Loader<ArrayList<Serie>> onCreateLoader(int id, Bundle bundle) {

		progressDialog = ProgressDialog.show( getActivity(), "", "Loading...", false, false);

		switch (id) {
		default:
			final Loader<ArrayList<Serie>> l = new NewSerieAsyncTaskLoader(getActivity(), bundle);
			
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
			    progressDialog.setCancelable(true);
			    progressDialog.setOnCancelListener(new OnCancelListener() {
					
					@Override
					public void onCancel(DialogInterface dialog) {
						l.cancelLoad();
						MyFragmentManager.setFragment(0, getArguments(), getActivity(), getTag());
					}
				});
			}
			
			return l;
		}
	}

	@Override
	public void onLoadFinished(Loader<ArrayList<Serie>> arg0,
			ArrayList<Serie> arg1) {

		if(arg1 == null){
			if(progressDialog != null && progressDialog.isShowing())
				progressDialog.dismiss();
			return;
		}

		Log.d(LOG_TAG, "La lista è di "+arg1.size()+" elementi");

		MyBucketAdapter adap = new MyBucketAdapter(getActivity(), arg1, 2);
		adap.enableAutoMeasure(180);
		dataDownloaded = true;
		if(progressDialog != null && progressDialog.isShowing())
			progressDialog.dismiss();

		this.listView.setAdapter(adap);
	}

	@Override
	public void onLoaderReset(Loader<ArrayList<Serie>> arg0) {

	}

	@Override
	public void onBackPressed() {
		MyFragmentManager.setFragment(0, getArguments(), getActivity(), getTag());
	}
}
