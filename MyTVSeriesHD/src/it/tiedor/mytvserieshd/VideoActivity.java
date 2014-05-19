package it.tiedor.mytvserieshd;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.android.gms.internal.co;

import it.tiedor.mytvserieshd.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaPlayer.OnTimedTextListener;
import android.media.TimedText;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.TrackInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class VideoActivity extends Activity implements Callback{

	private final String LOG_TAG = MyConstants.LOG_TAG + " - " + getClass().getSimpleName();

	private String videoUri;
	private String subUri;
	private TextView subtitleView;
	private ImageView play;
	private RelativeLayout buttons;

	private Typeface italic;
	private Typeface bodoni_hand;
	private Typeface bold;
	private SeekBar progress;

	private MediaPlayer mediaPlayer;
	private boolean wasPlaying;
	
	int subtitleTrack = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_video);

		getActionBar().hide();

		play = (ImageView) findViewById(R.id.play);
		subtitleView = (TextView) findViewById(R.id.subtitle);
		SurfaceHolder holder = ((SurfaceView) findViewById(R.id.surfaceview)).getHolder();
		holder.addCallback(this);

		videoUri = getIntent().getStringExtra("videoUri");
		subUri = getIntent().getStringExtra("subUri");

		italic = Typeface.createFromAsset(getAssets(),
				"fonts/italic.ttf");

		bodoni_hand = Typeface.createFromAsset(getAssets(),
				"fonts/bodoni_hand.otf");

		bold = Typeface.createFromAsset(getAssets(),
				"fonts/acme.ttf");

		buttons = (RelativeLayout) findViewById(R.id.buttons);

		progress = (SeekBar) findViewById(R.id.seekBar);

	}

	private void playVideo(SurfaceHolder holder){
		final Uri myVideoUri = Uri.parse(videoUri); // initialize Uri here
		final Uri mySubUri = Uri.parse(subUri); // initialize Uri here
		mediaPlayer = MediaPlayer.create(this, myVideoUri);
		mediaPlayer.setDisplay(holder);
		mediaPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
		mediaPlayer.setOnErrorListener(new OnErrorListener() {

			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				Log.e(LOG_TAG, "Error what ["+what+"] extra ["+extra+"]");
				return false;
			}
		});

		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				Log.d(LOG_TAG, "Completato");
				Log.d(LOG_TAG, "Hai guardato fino a "+mp.getCurrentPosition()+" millisec");
				mp.release();						
			}
		});

		mediaPlayer.setOnPreparedListener(new OnPreparedListener() { 
			@Override
			public void onPrepared(MediaPlayer mp) {
				try {	        		
					mediaPlayer.addTimedTextSource(getApplicationContext(), mySubUri, MediaPlayer.MEDIA_MIMETYPE_TEXT_SUBRIP);
				} catch (Exception e) {
					Log.e(LOG_TAG, "Errore nell'aggiungere i sottotitoli", e);
				}

				for(TrackInfo ti : mediaPlayer.getTrackInfo()){
					if(TrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT == ti.getTrackType())
						mediaPlayer.selectTrack(subtitleTrack);
					else
						subtitleTrack++;
				}

				Log.d(LOG_TAG, "Starto");
				setResult(Activity.RESULT_OK);
				MediaObserver observer = new MediaObserver();
				mp.start();
				new Thread(observer).start();
			}
		});
		
		mediaPlayer.setOnTimedTextListener(new OnTimedTextListener() {

			@Override
			public void onTimedText(final MediaPlayer mp,final TimedText text) {
				new Handler().post(new Runnable() {

					@Override
					public void run() {
						if(!mp.isPlaying())
							return;

						String sottotitolo;
						
						if(text != null)
							sottotitolo = text.getText();
						else
							sottotitolo = "";

						subtitleView.setTypeface(bodoni_hand, Typeface.NORMAL);

						if(sottotitolo.contains("<i>")){
							sottotitolo = sottotitolo.replaceAll("<.*i>", "");
							subtitleView.setTypeface(italic, Typeface.NORMAL);
						}
						if(sottotitolo.contains("<b>")){
							sottotitolo = sottotitolo.replaceAll("<.*b>", "");
							subtitleView.setTypeface(bold, Typeface.NORMAL);
						}
						subtitleView.setText(sottotitolo);
					}
				});
			}
		});

		findViewById(R.id.surfaceview).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				buttons.setVisibility(View.VISIBLE);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						try{
							if(mediaPlayer != null && mediaPlayer.isPlaying()){
								buttons.setVisibility(View.GONE);
							}
						}catch(Exception e){}
					}
				}, 2500);
			}
		});

		play.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(mediaPlayer.isPlaying()){
					mediaPlayer.pause();
					play.setImageResource(R.drawable.play);
				}else{
					mediaPlayer.start();
					play.setImageResource(R.drawable.pause);
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							try{
								if(mediaPlayer != null && mediaPlayer.isPlaying()){
									buttons.setVisibility(View.GONE);
								}
							}catch(Exception e){}
						}
					}, 500);
				}
			}
		});

		progress.setMax(mediaPlayer.getDuration());

		progress.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if(fromUser){
					seekBar.setClickable(false);
					try{
						mediaPlayer.deselectTrack(subtitleTrack);
					}catch(Exception e){}
					subtitleView.setText("");
					mediaPlayer.seekTo(progress);
				}
			}
		});
		
		mediaPlayer.setOnSeekCompleteListener(new OnSeekCompleteListener() {
			
			@Override
			public void onSeekComplete(MediaPlayer mp) {
				mediaPlayer.selectTrack(subtitleTrack);
				progress.setClickable(true);
			}
		});
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		playVideo(holder);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {}


	private class MediaObserver implements Runnable {
		private AtomicBoolean stop = new AtomicBoolean(false);

		public void stop() {
			stop.set(true);
		}

		@Override
		public void run() {
			while (!stop.get()) {
				try{
					progress.setProgress(mediaPlayer.getCurrentPosition());
				}catch(Exception e){}
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {}
			}
		}
	}
}
