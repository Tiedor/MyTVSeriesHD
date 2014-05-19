package it.tiedor.mytvserieshd;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.lamerman.FileDialog;

public class MySettingsActivity extends PreferenceActivity {

	private static final SimpleDateFormat 	sdf = new SimpleDateFormat(MyConstants.SIMPLE_DATE_FORMAT, Locale.getDefault());
	private static final String 			LOG_TAG 	= MyConstants.LOG_TAG + " - " + MySettingsActivity.class.getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(getResources().getBoolean(R.bool.portrait_only)){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle(getResources().getString(R.string.menu_settings));
	}

	@Override
	public void onBuildHeaders(List<Header> target) {
		loadHeadersFromResource(R.xml.preferences, target);
	}

	/*@Override
	protected boolean isValidFragment(String fragmentName) {
	    return PrefsFragmentGeneral.class.getName().equals(fragmentName) || PrefsFragmentItasa.class.getName().equals(fragmentName);
	}*/

	public static class PrefsFragmentGeneral extends PreferenceFragment implements OnSharedPreferenceChangeListener{

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			PreferenceManager.setDefaultValues(getActivity(), R.xml.prefsfragmentgeneral, false);

			addPreferencesFromResource(R.xml.prefsfragmentgeneral);

			PreferenceScreen folder = (PreferenceScreen) findPreference("folder_main");

			folder.setOnPreferenceClickListener(new OnPreferenceClickListener() {

				@Override
				public boolean onPreferenceClick(Preference preference) {
					Intent intent = new Intent(getActivity(), FileDialog.class);
					intent.putExtra(FileDialog.START_PATH, "/");

					//can user select directories or not
					intent.putExtra(FileDialog.CAN_SELECT_DIR, true);

					//alternatively you can set file filter
					//intent.putExtra(FileDialog.FORMAT_FILTER, new String[] { "png" });

					startActivityForResult(intent, 0);
					return true;
				}
			});
			
			PreferenceScreen folderView = (PreferenceScreen) findPreference("folder_view");

			folderView.setOnPreferenceClickListener(new OnPreferenceClickListener() {

				@Override
				public boolean onPreferenceClick(Preference preference) {
					Intent intent = new Intent(getActivity(), FileDialog.class);
					intent.putExtra(FileDialog.START_PATH, "/");

					//can user select directories or not
					intent.putExtra(FileDialog.CAN_SELECT_DIR, true);

					//alternatively you can set file filter
					//intent.putExtra(FileDialog.FORMAT_FILTER, new String[] { "png" });

					startActivityForResult(intent, 1);
					return true;
				}
			});
			
			PreferenceScreen folderDownload = (PreferenceScreen) findPreference("folder_download");

			folderDownload.setOnPreferenceClickListener(new OnPreferenceClickListener() {

				@Override
				public boolean onPreferenceClick(Preference preference) {
					Intent intent = new Intent(getActivity(), FileDialog.class);
					intent.putExtra(FileDialog.START_PATH, "/");

					//can user select directories or not
					intent.putExtra(FileDialog.CAN_SELECT_DIR, true);

					//alternatively you can set file filter
					//intent.putExtra(FileDialog.FORMAT_FILTER, new String[] { "png" });

					startActivityForResult(intent, 2);
					return true;
				}
			});
			
			String filePath = PreferenceManager
					.getDefaultSharedPreferences(getActivity())
					.getString("folder_main", null);
			if(filePath != null)
				getPreferenceScreen().findPreference("folder_main").setSummary("Hai selezionato "+filePath);
			
			String filePathView = PreferenceManager
					.getDefaultSharedPreferences(getActivity())
					.getString("folder_view", null);
			if(filePath != null)
				getPreferenceScreen().findPreference("folder_view").setSummary("Hai selezionato "+filePathView);
			
			String filePathDownload = PreferenceManager
					.getDefaultSharedPreferences(getActivity())
					.getString("folder_download", null);
			if(filePath != null)
				getPreferenceScreen().findPreference("folder_download").setSummary("Hai selezionato "+filePathDownload);

			String kickass = PreferenceManager
					.getDefaultSharedPreferences(getActivity())
					.getString(MyConstants.TORRENT, null);

			if(kickass!=null && !kickass.equals("")){
				String summary = getPreferenceScreen().findPreference(MyConstants.TORRENT).getSummary().toString()
						.replaceAll("\\[.*\\]", "")+" ["+kickass+"]";
				getPreferenceScreen()
				.findPreference(MyConstants.TORRENT)
				.setSummary(summary);

			}

			try{

			}catch (Exception e) {
				Log.e(LOG_TAG, "Errore", e);
			}
		}

		@Override
		public void onActivityResult(int requestCode, int resultCode,
				Intent data) {

			if (resultCode == Activity.RESULT_OK) {

				if (requestCode == 0) {
					String filePath = data.getStringExtra(FileDialog.RESULT_PATH);
					PreferenceManager
						.getDefaultSharedPreferences(getActivity()).edit()
						.putString("folder_main", filePath)
						.commit();
					
					getPreferenceScreen().findPreference("folder_main").setSummary("Hai selezionato "+filePath);
				}else if (requestCode == 1) {
					String filePath = data.getStringExtra(FileDialog.RESULT_PATH);
					PreferenceManager
						.getDefaultSharedPreferences(getActivity()).edit()
						.putString("folder_view", filePath)
						.commit();
					
					getPreferenceScreen().findPreference("folder_view").setSummary("Hai selezionato "+filePath);
				}else if (requestCode == 2) {
					String filePath = data.getStringExtra(FileDialog.RESULT_PATH);
					PreferenceManager
						.getDefaultSharedPreferences(getActivity()).edit()
						.putString("folder_download", filePath)
						.commit();
					
					getPreferenceScreen().findPreference("folder_download").setSummary("Hai selezionato "+filePath);
				}
			}
		}

		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			Log.d(LOG_TAG, "OnSharedPreferenceChanged - "+key);
			if(key.equals(MyConstants.TORRENT)){
				String summary = getPreferenceScreen().findPreference(MyConstants.TORRENT).getSummary().toString()
						.replaceAll("\\[.*\\]", "")+" ["+sharedPreferences.getString(key, "http://TORRENT.net")+"]";

				getPreferenceScreen().findPreference(MyConstants.TORRENT).setSummary(summary);
			}
		}

		@Override
		public void onResume() {
			super.onResume();
			getPreferenceScreen().getSharedPreferences()
			.registerOnSharedPreferenceChangeListener(this);		    
		}

		@Override
		public void onPause() {
			super.onPause();
			getPreferenceScreen().getSharedPreferences()
			.unregisterOnSharedPreferenceChangeListener(this);
		}
	}

	public static class PrefsFragmentCloud extends PreferenceFragment implements OnSharedPreferenceChangeListener{
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			PreferenceManager.setDefaultValues(getActivity(), R.xml.prefsfragmentcloud, false);

			addPreferencesFromResource(R.xml.prefsfragmentcloud);
			try{

				String user = PreferenceManager
						.getDefaultSharedPreferences(getActivity())
						.getString(MyConstants.DROPBOX_USER, null);
				if(user!=null)            	
					getPreferenceScreen()
					.findPreference(MyConstants.DROPBOX_USER)
					.setSummary("Hai effettuato la login con l'account "+user);

				Long date = PreferenceManager
						.getDefaultSharedPreferences(getActivity())
						.getLong(MyConstants.DROPBOX_LAST_SYNC_DATE, 0);
				if(date>0)
					getPreferenceScreen()
					.findPreference(MyConstants.DROPBOX_UPLOAD)
					.setSummary("Ultimo upload effettuato: "+sdf.format(new Date(date)));
			}catch (Exception e) {
				Log.e(LOG_TAG, "Errore", e);
			}
		}

		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			Log.d(LOG_TAG, "OnSharedPreferenceChanged - "+key+"["+key.equals(MyConstants.TORRENT)+"]");
			if (key.equals(MyConstants.DROPBOX_USER)) {
				getPreferenceScreen().findPreference(MyConstants.DROPBOX_USER).setSummary("Hai effettuato la login con l'account "+sharedPreferences.getString(key, "foo@bar.com"));
			}else if(key.equals(MyConstants.DROPBOX_LAST_SYNC_DATE)){
				getPreferenceScreen().findPreference(MyConstants.DROPBOX_UPLOAD).setSummary("Ultimo upload effettuato: "+sdf.format(new Date(sharedPreferences.getLong(key, 0))));
			}
		}

		@Override
		public void onResume() {
			super.onResume();
			getPreferenceScreen().getSharedPreferences()
			.registerOnSharedPreferenceChangeListener(this);		    
		}

		@Override
		public void onPause() {
			super.onPause();
			//getPreferenceScreen().getSharedPreferences()
			//.unregisterOnSharedPreferenceChangeListener(this);
		}
	}

	public static class PrefsFragmentItasa extends PreferenceFragment implements OnSharedPreferenceChangeListener{
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			PreferenceManager.setDefaultValues(getActivity(), R.xml.prefsfragmentitasa, false);

			addPreferencesFromResource(R.xml.prefsfragmentitasa);
			try{

				String user = PreferenceManager
						.getDefaultSharedPreferences(getActivity())
						.getString(MyConstants.ITASA_USER, null);
				if(user!=null)            	
					getPreferenceScreen()
					.findPreference(MyConstants.ITASA_USER)
					.setSummary("Hai effettuato la login con l'account "+user);

			}catch (Exception e) {
				Log.e(LOG_TAG, "Errore", e);
			}
		}

		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			Log.d(LOG_TAG, "OnSharedPreferenceChanged - "+key+"["+key.equals(MyConstants.ITASA_USER)+"]");
			if (key.equals(MyConstants.ITASA_USER)) {
				getPreferenceScreen().findPreference(MyConstants.ITASA_USER).setSummary("Hai effettuato la login con l'account "+sharedPreferences.getString(key, "foo@bar.com"));
			}
		}

		@Override
		public void onResume() {
			super.onResume();

			String user = PreferenceManager
					.getDefaultSharedPreferences(getActivity())
					.getString(MyConstants.ITASA_USER, null);
			if(user!=null)            	
				getPreferenceScreen()
				.findPreference(MyConstants.ITASA_USER)
				.setSummary("Hai effettuato la login con l'account "+user);


			getPreferenceScreen().getSharedPreferences()
			.registerOnSharedPreferenceChangeListener(this);		    
		}

		@Override
		public void onPause() {
			super.onPause();
			//getPreferenceScreen().getSharedPreferences()
			//.unregisterOnSharedPreferenceChangeListener(this);
		}

		@Override
		public void onActivityResult(int requestCode, int resultCode,
				Intent data) {
			Log.d(LOG_TAG, "onActivityResult - ");
			String user = PreferenceManager
					.getDefaultSharedPreferences(getActivity())
					.getString(MyConstants.ITASA_USER, null);
			if(user!=null)            	
				getPreferenceScreen()
				.findPreference(MyConstants.ITASA_USER)
				.setSummary("Hai effettuato la login con l'account "+user);

			super.onActivityResult(requestCode, resultCode, data);
		}
	}
}