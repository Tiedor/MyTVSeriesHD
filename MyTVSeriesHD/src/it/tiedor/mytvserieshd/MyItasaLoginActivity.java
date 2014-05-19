package it.tiedor.mytvserieshd;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import it.tiedor.mydbhelper.DataBaseHelperFactory;
import it.tiedor.mydbhelper.DatabaseHelper;
import it.tiedor.mydbhelper.persistence.Season;
import it.tiedor.myitasa.ItasaSubs;
import it.tiedor.myitasa.MyITASAException;
import it.tiedor.mythetvdb.TheTVDBApi;
import it.tiedor.mythetvdb.persistence.Banner;
import it.tiedor.mythetvdb.persistence.Episode;
import it.tiedor.mythetvdb.persistence.Serie;
import it.tiedor.mytvserieshd.tasks.SaveNewSerieTask;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class MyItasaLoginActivity extends Activity {
	
	/**
	 * The default email to populate the email field with.
	 */
	public static final String 	EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

	private final String LOG_TAG 	= MyConstants.LOG_TAG + " - " + getClass().getSimpleName();
	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_my_itasa_login);
		setupActionBar();

		// Set up the login form.
		mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
		mEmailView = (EditText) findViewById(R.id.email);
		mEmailView.setText(mEmail);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Show the Up button in the action bar.
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.my_itasa_login, menu);
		return true;
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}
		
		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		getIntent().putExtra(EXTRA_EMAIL, mEmail);
		
		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} /*else if (!mEmail.contains("@")) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}*/

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
	
	protected void saveCredential(String success){
		PreferenceManager.getDefaultSharedPreferences(this).edit()
		.putString(MyConstants.ITASA_USER, mEmail)
		.putString(MyConstants.ITASA_PASSWORD, mPassword)
		.putString(MyConstants.ITASA_KEY, success)
		.commit();
		Toast.makeText(this, "Complimenti, hai effettuato la login", Toast.LENGTH_LONG).show();
		setResult(RESULT_OK);
		finish();
	}
	
	protected void myPublishProgress(String seriesName) {
		mLoginStatusMessageView.setText("Analizzo la serie "+seriesName);
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, String, String> {
		@Override
		protected String doInBackground(Void... params) {
			
			String authcode;
			
			try {
				authcode = ItasaSubs.login(mEmail, mPassword);
				
				publishProgress("Ricerco le tue serie su MyItasa");
				
				ArrayList<String[]> listaMyItasa = ItasaSubs.getFavouriteShows(authcode);
				
				TheTVDBApi api = new TheTVDBApi();
				
				ArrayList<it.tiedor.mydbhelper.persistence.Serie> mySeries = new ArrayList<it.tiedor.mydbhelper.persistence.Serie>();
				
				for(String[] values : listaMyItasa){
					
					publishProgress("Analizzo la serie "+values[1]);
					
					String thetvdbid = ItasaSubs.getTVDBIdFromShow(values[0]);
					
					Serie s = api.getSerie(thetvdbid);
					
					it.tiedor.mydbhelper.persistence.Serie dbSerie = new it.tiedor.mydbhelper.persistence.Serie(
							Long.valueOf(s.getId()), s.getName(), s.getBannerURI(), 
							s.getPosterURI(), s.getStatus(), s.getDayOfWeekAirs(), s.getTimeAirs(), 
							s.getOverview(), s.getNetwork(), new Date(s.getLastUpdated()));
					
					mySeries.add(dbSerie);
				}
				
				
				saveSeries(mySeries);
				
			} catch (MyITASAException e) {
				Log.e(LOG_TAG, e.getMessage(), e);
				return null;
			} catch (Exception e) {
				Log.e(LOG_TAG, e.getMessage(), e);
				return null;
			}
			
			return authcode;
		}
		
		private void saveSeries(ArrayList<it.tiedor.mydbhelper.persistence.Serie> params){
			
			TheTVDBApi api = new TheTVDBApi();
			DatabaseHelper helper = DataBaseHelperFactory.getDatabaseHelper(getApplication());
			
			for(it.tiedor.mydbhelper.persistence.Serie serie : params){
				publishProgress("Salvo sul la serie "+serie.getShowName());
				helper.getSerieRuntimeDao().createOrUpdate(serie);			
				try {
					publishProgress("Cerco gli episodi per la serie "+serie.getShowName());
					
					ArrayList<Episode> episodeList = new ArrayList<Episode>(api.getEpisodes(serie.getShowID()));
					
					Log.d(LOG_TAG, "La serie ha "+episodeList.size()+" elementi");
					
					Map<String, Banner> banners = api.getSeasonBanners(serie.getShowID());
					
					for(Episode episode : episodeList){
						
						publishProgress("Salvo l'episodio "+episode.getName()+" - "+serie.getShowName()+"[s"+episode.getSeason()+"e"+episode.getNumber()+"]");
						
						if(episode.getSeason().equals("0"))
							continue;					
						
						Log.d(LOG_TAG, "Inserisco l'episodio "+episode.getSeason()+"X"+episode.getNumber());
											
						Season season;
						
						try{
							Log.d(LOG_TAG, "Cerco la stagione ["+episode.getSeason()+"] per la serie ["+serie.getShowID()+"]");
							season = helper.getSeason(Integer.parseInt(episode.getSeason()), serie.getShowID());
							if(season == null)
								throw new Exception();
							Log.d(LOG_TAG, "Stagione trovata");
						}catch(Exception e ){
							Log.d(LOG_TAG, "Stagione non trovata, la creo nuova");
							String bannerPath = "";
							try{
								bannerPath = banners.get(episode.getSeason()).getBannerPath();
							}catch(Exception e1){}
							season = new Season(Integer.parseInt(episode.getSeason()), bannerPath, serie);
						}
						
						Date airDate;
						try{
							airDate = new SimpleDateFormat("yyyy-MM-dd").parse(episode.getFirstAired());
						}catch(Exception e){
							airDate = null;
						}
						
						it.tiedor.mydbhelper.persistence.Episode ep = 
								new it.tiedor.mydbhelper.persistence.Episode(
										episode.getId(),
										episode.getName(), 
										Integer.parseInt(episode.getNumber()), 
										season, 
										episode.getFilename(),
										"",
										airDate
										);
						
						helper.getEpisodeDao().createIfNotExists(ep);
					}
					
					Season lastSeason = helper.getLastSeason(serie.getShowID());
					helper.setSeasonFavourite(lastSeason.getSeasonId());
				} catch (Exception e) {
					Log.e(LOG_TAG, "Errore nel cercare gli episodi della serie "+serie.getShowID(), e);
				}			
			}
		}

		@Override
		protected void onPostExecute(final String success) {
			mAuthTask = null;
			showProgress(false);

			if (success != null) {
				saveCredential(success);
			} else {
				mPasswordView
						.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}
		}

		
		@Override
		protected void onProgressUpdate(String... values) {
			for(String value : values)
				mLoginStatusMessageView.setText(value);
			super.onProgressUpdate(values);
		}
		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
}
