package it.tiedor.mytvserieshd.fragment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.mediation.admob.AdMobExtras;
import com.google.android.gms.internal.ac;
import com.lamerman.FileDialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;
import com.nostra13.universalimageloader.core.decode.MyImageDecoder;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;

import it.tiedor.mydbhelper.DataBaseHelperFactory;
import it.tiedor.mydbhelper.DatabaseHelper;
import it.tiedor.mydbhelper.persistence.Action;
import it.tiedor.mydbhelper.persistence.Episode;
import it.tiedor.mydbhelper.persistence.Serie;
import it.tiedor.mytvserieshd.MyConstants;
import it.tiedor.mytvserieshd.MyTVSeriesHD;
import it.tiedor.mytvserieshd.R;
import it.tiedor.mytvserieshd.adapter.MyListAdapter;
import it.tiedor.mytvserieshd.exception.MyNoRecordsFoundException;
import it.tiedor.mytvserieshd.loaders.MyOwnAsyncTaskLoader;
import it.tiedor.mytvserieshd.receiver.MyResultReceiver;
import it.tiedor.mytvserieshd.service.MyDeleteSeriesIntentService;
import it.tiedor.mytvserieshd.service.MyLastVideoIntentService;

import android.app.Activity;
import android.app.AlertDialog;

import android.app.ProgressDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;

import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class MyOwnSeriesListFragment extends MyFragment implements LoaderCallbacks<ArrayList<Serie>>{


	private 					ProgressDialog 		progressDialog;
	private 					DisplayImageOptions options;
	private						RelativeLayout		lastVideo;
	private 					ListView 			listView;
	private 					ImageLoader 		loader;
	private						RelativeLayout		loading;
	private						RelativeLayout		listLayout;
	public 	final 	static 		int					CALLER = 2;
	private 					AdView 				adView;
	private						MyResultReceiver	receiver;
	private						MyLastVideoResultReceiver	lastVideoreceiver;
	private						int					position;
	private 					Typeface 			bodoni_hand;
	private final String 		LOG_TAG 	= MyConstants.LOG_TAG + " - " + "MyOwnSeriesListFragment";

	public MyOwnSeriesListFragment() {}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.series_list, null, false);

		this.listView = (ListView) rootView.findViewById(android.R.id.list);
		this.listView.setEmptyView(rootView.findViewById(android.R.id.empty));
		this.loading = (RelativeLayout) rootView.findViewById(R.id.loading);
		this.listLayout = (RelativeLayout) rootView.findViewById(R.id.listRelativeLayout);
		//this.lastVideo = (ImageView) rootView.findViewById(R.id.last_view);
		this.lastVideo = (RelativeLayout) inflater.inflate(R.layout.last_view, this.listView, false);

		IntentFilter filter = new IntentFilter(it.tiedor.mytvserieshd.receiver.MyResultReceiver.ACTION_RESP);
		filter.addCategory(Intent.CATEGORY_DEFAULT);
		lastVideoreceiver = new MyLastVideoResultReceiver();
		getActivity().registerReceiver(lastVideoreceiver, filter);

		this.listView.addHeaderView(lastVideo);

		getLoaderManager().initLoader(0, getArguments(), this);

		loader = ImageLoader.getInstance();

		ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(getActivity())
		.imageDecoder(new MyImageDecoder())
		.build();

		if(this.loader.isInited()){
			this.loader.destroy();
		}
		this.loader.init(configuration);

		options = new DisplayImageOptions.Builder()
		.cacheInMemory(false)
		.resetViewBeforeLoading(true)
		.displayer(new BitmapDisplayer() {
			@Override
			public Bitmap display(Bitmap bitmap, ImageView imageView, LoadedFrom loadedFrom) {
				imageView.setImageBitmap(bitmap);
				return bitmap;
			}
		})
		.imageScaleType(ImageScaleType.EXACTLY)
		.cacheOnDisc(false)
		.build();

		PauseOnScrollListener listener = new PauseOnScrollListener(loader, false, true);
		this.listView.setOnScrollListener(listener);

		adView = (AdView)rootView.findViewById(R.id.adView);
		if(adView != null){
			Bundle bundle = new Bundle();
			bundle.putString("color_bg", "245ABF");
			AdMobExtras extras = new AdMobExtras(bundle);
			AdRequest adRequest = new AdRequest.Builder().addNetworkExtras(extras).build();
			adView.loadAd(adRequest);
		}		

		if(getArguments() != null)
			position = getArguments().getInt(MyConstants.SERIES_POSITION);

		boolean b = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("v22", true);

		if(b){
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Attenzione!");
			builder.setMessage("E' necessario reimpostare le cartelle su Impostazioni>Configurazione. Ripetere questo messaggio in futuro?");

			builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					try {							
						PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("v22", false).commit();
					} catch (Exception e) {
						Log.e(LOG_TAG, e.getMessage());
					}
				}
			});
			builder.setNegativeButton("Si", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {

				}
			});

			AlertDialog dialog = builder.create();
			dialog.show();
		}

		bodoni_hand = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/bodoni_hand.otf");
		
		return rootView;
	}

	@Override
	public void onStop() {
		try{
			getActivity().unregisterReceiver(receiver);
		}catch(Exception e){}
		try{
			getActivity().unregisterReceiver(lastVideoreceiver);
		}catch(Exception e){}
		try{
			adView.destroy();
		}catch(Exception e){}
		try{
			loader.stop();
		}catch(Exception e){}
		super.onStop();
	}

	@Override
	public void onPause() {
		try{
			getActivity().unregisterReceiver(receiver);
		}catch(Exception e){}
		try{
			getActivity().unregisterReceiver(lastVideoreceiver);
		}catch(Exception e){}
		try{
			adView.destroy();
		}catch(Exception e){}
		try{
			loader.stop();
		}catch(Exception e){}
		super.onPause();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {		
		Log.d(LOG_TAG, "ContextMenu creating...");
		menu.setHeaderTitle("Menù Serie");
		menu.add(Menu.NONE, MyConstants.MENU_DELETE, Menu.NONE, "Elimina serie");
		super.onCreateContextMenu(menu, v, menuInfo);    	
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		if(item.getGroupId() != Menu.NONE)
			return false;

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

		long serieId = PreferenceManager.getDefaultSharedPreferences(getActivity()).getLong(MyConstants.SERIE_ID_PROCESSING, -1);

		Log.d(LOG_TAG, "Ho selezionato l'elemento ["+item.getItemId()+"] sulla serie ["+serieId+"] in posizione ["+info.position+"]");
		switch (item.getItemId()){
		case MyConstants.MENU_DELETE:
			progressDialog = ProgressDialog.show( getActivity(), "", "Deleting...", false, false);
			Intent intent = new Intent(getActivity(), MyDeleteSeriesIntentService.class);
			intent.setAction(MyDeleteSeriesIntentService.ACTION_DELETE_SERIE);
			intent.putExtra(MyDeleteSeriesIntentService.SERIE_ID, serieId);
			intent.putExtra(MyDeleteSeriesIntentService.SERIE_POSITION, info.position);
			IntentFilter filter = new IntentFilter(MyResultReceiver.ACTION_RESP);
			filter.addCategory(Intent.CATEGORY_DEFAULT);
			getActivity().registerReceiver(receiver = new MyResultReceiver(), filter);
			getActivity().startService(intent);
			break;
		}

		return true;
	}

	@Override
	public Loader<ArrayList<Serie>> onCreateLoader(int id, Bundle bundle) {

		switch (id) {
		default:
			final Loader<ArrayList<Serie>> l = new MyOwnAsyncTaskLoader(getActivity(), bundle);
			return l;
		}
	}

	@Override
	public void onLoadFinished(Loader<ArrayList<Serie>> arg0,
			ArrayList<Serie> arg1) {

		this.loading.setVisibility(View.GONE);
		this.listLayout.setVisibility(View.VISIBLE);

		Log.d(LOG_TAG, "La lista è di "+arg1.size()+" elementi");

		MyListAdapter adap = new MyListAdapter(getActivity(), R.layout.series_list_row, R.id.series_name, arg1);

		if(progressDialog != null && progressDialog.isShowing())
			progressDialog.dismiss();



		//this.listView.addHeaderView(lastVideo);

		this.listView.setAdapter(adap);
		registerForContextMenu(this.listView);

		this.listView.setSelection(position);

		Intent msgIntent = new Intent(getActivity(), MyLastVideoIntentService.class);
		getActivity().startService(msgIntent);
	}

	@Override
	public void onLoaderReset(Loader<ArrayList<Serie>> arg0) {

	}

	@Override
	public void onBackPressed() {
		getActivity().finish();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		Log.d(LOG_TAG, "Torno dalla requestCode ["+requestCode+"] con la resultCode ["+resultCode+"]");

		final DatabaseHelper databaseHelper = DataBaseHelperFactory.getDatabaseHelper(getActivity());

		final Long videoId = PreferenceManager.getDefaultSharedPreferences(getActivity()).getLong(MyConstants.EPISODE_ID_PROCESSING, -1);

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

						it.tiedor.mytvserieshd.utils.FileUtils.moveViewVideoFile(getActivity());

						Episode ep = databaseHelper.setEpisodeViewed(videoId);

						Action action = databaseHelper.insertNewAction(
								Calendar.getInstance().getTime(), 
								Action.ENDVIEW, 
								ep, 
								PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(MyConstants.MX_EXTRA_VIDEO_LIST, null), 
								"");

						((MyTVSeriesHD)getActivity()).selectItem(0);
						
					} catch (Exception e) {
						Log.e(LOG_TAG, e.getMessage(), e);
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
		default:
			break;
		}


	}

	public class MyResultReceiver extends BroadcastReceiver {

		public static final String ACTION_RESP = "ACTION_RESP";
		public static final String PARAM_OUT_MSG = "PARAM_OUT_MSG";

		@Override
		public void onReceive(Context context, Intent intent) {

			int param1 = intent.getIntExtra(PARAM_OUT_MSG, -1);
			int param2 = intent.getIntExtra(MyDeleteSeriesIntentService.SERIE_POSITION, -1);

			if(param1 < 1)
				Toast.makeText(getActivity(), "Non è stato possibilie cancellare la serie", Toast.LENGTH_LONG).show();
			else{
				((MyListAdapter)listView.getAdapter()).removeItem(param2);
				((MyListAdapter)listView.getAdapter()).notifyDataSetChanged();
				try{
					if(MyConstants.isTwoPane)
						MyFragmentManager.removeFragment(getActivity(), R.id.serie_detail_container);
				}catch(Exception e){
					Log.e(LOG_TAG, "Impossibile aggiornare la slave", e);
				}
			}

			if(progressDialog != null && progressDialog.isShowing())
				progressDialog.dismiss();
		}
	}

	public class MyLastVideoResultReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, final Intent intent) {
			int actioncode = intent.getIntExtra(it.tiedor.mytvserieshd.receiver.MyResultReceiver.CODE, -1);

			final String param1 = intent.getStringExtra(it.tiedor.mytvserieshd.receiver.MyResultReceiver.PARAM1);
			final String param2 = intent.getStringExtra(it.tiedor.mytvserieshd.receiver.MyResultReceiver.PARAM2);
			final long param3 = intent.getLongExtra(it.tiedor.mytvserieshd.receiver.MyResultReceiver.PARAM3, -1);
			if(actioncode == -1){
				getActivity().findViewById(R.id.last_relative).setVisibility(View.GONE);
				listView.removeHeaderView(lastVideo);
			}else if(actioncode == 0)
				try{
					((TextView)getActivity().findViewById(R.id.myImageViewText2)).setText(param1);
					((TextView)getActivity().findViewById(R.id.myImageViewText2)).setTypeface(bodoni_hand);
					loader.displayImage(param2, ((ImageView)getActivity().findViewById(R.id.last_view)), options);
				}catch(Exception e){
					Log.e(LOG_TAG, "Errore", e);
				}
			else if(actioncode == 1)
				try{

					if(param1 == null || param2 == null)
						return;

					getActivity().findViewById(R.id.progress_bar).setVisibility(View.GONE);
					getActivity().findViewById(R.id.image_icon).setVisibility(View.VISIBLE);
					getActivity().findViewById(R.id.image_icon).setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
							.putString(MyConstants.MX_EXTRA_VIDEO_LIST, param1)
							.putString(MyConstants.MX_PLAYER_EXTRA_SUBS, param2)
							.putLong(MyConstants.EPISODE_ID_PROCESSING, param3)
							.commit();
							Intent i;

							/*Action action = databaseHelper.insertNewAction(
								Calendar.getInstance().getTime(), 
								Action.STARTVIEW, 
								databaseHelper.getEpisodeDao().queryForId(param3), 
								param1,
								"");*/

							if (!appInstalledOrNot(new ComponentName(MyConstants.MXVP, MyConstants.MXVP_PLAYBACK_CLASS))) {
								i = new Intent(Intent.ACTION_VIEW);
								i.setDataAndType(Uri.parse(param1), "video/*" );
							}else{
								i = new Intent();
								i.setPackage(MyConstants.MXVP);
								i.setDataAndType(Uri.parse(param1), "application/*" );
								i.putExtra(MyConstants.MX_PLAYER_EXTRA_DECODE_MODE, (byte)1 );
								i.putExtra(MyConstants.MX_PLAYER_EXTRA_SUBS, Uri.parse(param2));
								i.putExtra(MyConstants.MX_EXTRA_VIDEO_LIST, new Uri[]{Uri.parse(param1)});
								i.putExtra(MyConstants.MX_PLAYER_EXTRA_RETURN_RESULT, true);
								String[] headers = new String[] { "User-Agent", "MX Player Caller App/1.0", "Extra-Header", "911" };
								i.putExtra(MyConstants.MX_EXTRA_HEADERS, headers );
								i.setClassName(MyConstants.MXVP, MyConstants.MXVP_PLAYBACK_CLASS);
							}
							
							startActivityForResult(i, 0);
						}
					});
				}catch(Exception e){
					Log.e(LOG_TAG, "Errore", e);
				}
		}
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

}
