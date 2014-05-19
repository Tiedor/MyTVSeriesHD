package it.tiedor.mytvserieshd;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.mediation.admob.AdMobExtras;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import it.tiedor.mydbhelper.persistence.Episode;
import it.tiedor.mytvserieshd.draw.MyActionBarDrawerToggle;
import it.tiedor.mytvserieshd.fragment.MyFragment;
import it.tiedor.mytvserieshd.fragment.MyFragmentManager;
import it.tiedor.mytvserieshd.fragment.MyOwnSeriesListFragment;
import it.tiedor.mytvserieshd.listener.SearchSeriesOnReturnListener;
import it.tiedor.mytvserieshd.receiver.MyResultReceiver;
import it.tiedor.mytvserieshd.tasks.TaskAlarm;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class MyTVSeriesHD extends Activity {

	private final String LOG_TAG = MyConstants.LOG_TAG + " - " + getClass().getSimpleName();

	private DrawerLayout 			myDrawerLayout;
	private LinearLayout 			myLinearDrawer;
	private ListView 				myDrawerList;
	private ActionBarDrawerToggle 	mDrawerToggle;
	private EditText 				search;
	private InterstitialAd 			interstitialAd;
	private MyOwnSeriesListFragment.MyLastVideoResultReceiver 		receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(LOG_TAG, "On Create");
		
		if(getResources().getBoolean(R.bool.portrait_only)){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		setContentView(R.layout.activity_main);

		Log.d(LOG_TAG, "On Create!!");

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		if (findViewById(R.id.serie_detail_container) != null)
			MyConstants.isTwoPane = true;
		else
			MyConstants.isTwoPane = false;

		myLinearDrawer = (LinearLayout) findViewById(R.id.linearDrower);

		myDrawerList = (ListView) findViewById(R.id.left_drawer);
		myDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, getResources().getStringArray(R.array.menu_array)));
		myDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		myDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerToggle = new MyActionBarDrawerToggle(this, myDrawerLayout);

		myDrawerLayout.setDrawerListener(mDrawerToggle);

		search = (EditText) myDrawerLayout.findViewById(R.id.searchText);
		search.setTextColor(Color.WHITE);
		search.setOnKeyListener(new SearchSeriesOnReturnListener(this, myDrawerLayout, myLinearDrawer));

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
		.threadPriority(Thread.NORM_PRIORITY - 2)
		.denyCacheImageMultipleSizesInMemory()
		.discCacheFileNameGenerator(new Md5FileNameGenerator())
		.tasksProcessingOrder(QueueProcessingType.LIFO)
		.writeDebugLogs() // Remove for release app
		.build();

		ImageLoader.getInstance().init(config);

		if(savedInstanceState == null)
			MyFragmentManager.setFragment(0, null, this, MyConstants.TAG_MAIN);

		interstitialAd = new InterstitialAd(this);
		interstitialAd.setAdUnitId("ca-app-pub-8662015702262160/7585826044");

		interstitialAd.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {
				interstitialAd.show();
			}
			/*@Override
			public void onAdFailedToLoad(int errorCode) {
				String message = String.format("onAdFailedToLoad (%s)", getErrorReason(errorCode));
				Log.d(LOG_TAG, message);
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
			}*/
		});

		AdRequest adRequest = new AdRequest.Builder()/*.addTestDevice("547A883F8D8E6ACB15F4CFD2756422B4")*/.build();
		interstitialAd.loadAd(adRequest);

		initService();		
	}
	
	@Override
	public View onCreateView(View parent, String name, Context context,
			AttributeSet attrs) {

		return super.onCreateView(parent, name, context, attrs);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Log.d(LOG_TAG, "Ho selezionato l'elemento ["+item.getItemId()+"] sulla Main Activity");

		return false;
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void selectItem(int position) {

		selectItem(position, null, null);
	}

	public void selectItem(int position, Bundle arguments, String tag) {

		Log.d(LOG_TAG, "Apro la position: ["+position+"]");

		myDrawerList.setItemChecked(position, true);
		
		if(position == 5){
			Intent i = new Intent(this, MySettingsActivity.class);
			startActivity(i);
			return;
		}

		if(tag == null){

			switch (position) {
			case 0:
				tag = MyConstants.TAG_MAIN;
				break;
			default:
				tag = MyConstants.TAG_SLAVE;
				break;
			}
		}
		
		MyFragmentManager.setFragment(position, null, this, tag);
		myDrawerLayout.closeDrawer(myLinearDrawer);
	}
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selectItem(position);
		}
	}

	private void initService(){
		TaskAlarm alarm = new TaskAlarm();
		alarm.cancelAlarm(this, 0);
		alarm.setAlarm(this, 0);
		alarm.cancelAlarm(this, 1);
		alarm.setAlarm(this, 1);
	}

	@Override
	public void onBackPressed() {
		if(MyConstants.isTwoPane)
			finish();
		else{
			MyFragment fragment = (MyFragment) getFragmentManager().findFragmentById(R.id.content_frame);
			fragment.onBackPressed();
		}
	}

	private String getErrorReason(int errorCode) {
		String errorReason = "";
		switch(errorCode) {
		case AdRequest.ERROR_CODE_INTERNAL_ERROR:
			errorReason = "Internal error";
			break;
		case AdRequest.ERROR_CODE_INVALID_REQUEST:
			errorReason = "Invalid request";
			break;
		case AdRequest.ERROR_CODE_NETWORK_ERROR:
			errorReason = "Network Error";
			break;
		case AdRequest.ERROR_CODE_NO_FILL:
			errorReason = "No fill";
			break;
		}
		return errorReason;
	}
}