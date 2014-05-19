package it.tiedor.mytvserieshd.adapter;

import it.tiedor.mythetvdb.persistence.Serie;
import it.tiedor.mytvserieshd.MyConstants;
import it.tiedor.mytvserieshd.R;
import it.tiedor.mytvserieshd.loaders.MyFavouriteLoader;
import it.tiedor.mytvserieshd.loaders.core.FavouriteDisplayer;
import it.tiedor.mytvserieshd.service.SaveSerieIntentService;
import it.tiedor.mytvserieshd.tasks.SaveNewSerieTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.scythe.bucket.BucketListAdapter;

public class MyBucketAdapter extends BucketListAdapter<Serie> {

	private 				ProgressDialog 			progressDialog;
	private 				ImageLoader 			loader;
	private 				MyFavouriteLoader 		favLoader;
	private 				DisplayImageOptions 	options;
	private 				ImageLoadingListener 	animateFirstListener;
	private					Typeface				forteTypeface;
	private final static 	String 					LOG_TAG 	= MyConstants.LOG_TAG + " - " + "MyBucketAdapter";

	public MyBucketAdapter(Activity ctx, ArrayList<Serie> elements,
			Integer bucketSize) {
		super(ctx, elements, bucketSize);
		this.loader = ImageLoader.getInstance();
		this.favLoader = MyFavouriteLoader.getInstance(ctx, new FavouriteDisplayer());

		this.animateFirstListener = new AnimateFirstDisplayListener();

		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.no_image_found)
		.showImageForEmptyUri(R.drawable.no_image_found)
		.showImageOnFail(R.drawable.no_image_found)
		.cacheInMemory(false)
		.cacheOnDisc(true)
		.displayer(new RoundedBitmapDisplayer(5))
		.build();

		forteTypeface = Typeface.createFromAsset(ctx.getAssets(),
				"fonts/forte.ttf");
	}

	@Override
	protected View getBucketElement(int position, Serie currentElement,
			View convertView) {

		final ViewHolder viewHolder;

		if(convertView == null){
			convertView = View.inflate(ctx, R.layout.image_poster, null);

			viewHolder = new ViewHolder();

			viewHolder.text = (TextView)convertView.findViewById(R.id.text);
			viewHolder.poster = (ImageView) convertView.findViewById(R.id.poster);
			viewHolder.favourite = (ImageView) convertView.findViewById(R.id.favourite);
			viewHolder.relativeText = (RelativeLayout) convertView.findViewById(R.id.relativeText);
			
			viewHolder.text.setTypeface(this.forteTypeface);

			convertView.setTag(viewHolder);
		}else
			viewHolder = (ViewHolder) convertView.getTag();


		viewHolder.text.setText(currentElement.getName());

		if(currentElement.getPosterURI()!=null)
			loader.displayImage(currentElement.getPosterURI(), viewHolder.poster, options, animateFirstListener);


		viewHolder.poster.setImageResource(R.drawable.no_image_found);

		final Serie s = currentElement;

		viewHolder.favourite.setVisibility(View.VISIBLE);

		viewHolder.relativeText.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//progressDialog = ProgressDialog.show( getContext(), "", "Loading...", false, false);
				try {
					it.tiedor.mydbhelper.persistence.Serie serie = new it.tiedor.mydbhelper.persistence.Serie(Long.valueOf(s.getId()), s.getName(), s.getBannerURI(), 
							s.getPosterURI(), s.getStatus(), s.getDayOfWeekAirs(), s.getTimeAirs(), 
							s.getOverview(), s.getNetwork(), new Date(s.getLastUpdated()));
					/*SaveNewSerieTask task = new SaveNewSerieTask(getContext(), progressDialog);
					task.execute(serie);*/
				
					startActionSaveNewSerie(getContext(), serie);
					
				} catch (Exception e) {
					Log.e(LOG_TAG, "Errore nel salvare la serie "+s.getName(), e);
					Toast.makeText(getContext(), "Si è verificato un errore", Toast.LENGTH_SHORT).show();
				}
			}
		});

		this.favLoader.displayFavourite(Long.parseLong(currentElement.getId()), viewHolder.favourite);

		return convertView;
	}
	
	/**
	 * Starts this service to perform action SaveNewSerie with the given parameters. If
	 * the service is already performing a task this action will be queued.
	 * 
	 * @see IntentService
	 */
	public static void startActionSaveNewSerie(Context context, it.tiedor.mydbhelper.persistence.Serie param1) {
		Intent intent = new Intent(context, SaveSerieIntentService.class);
		intent.setAction(SaveSerieIntentService.ACTION_SAVE_NEW_SERIE);
		intent.putExtra(SaveSerieIntentService.EXTRA_PARAM1, param1);
		context.startService(intent);
		
	}

	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
					Log.d(LOG_TAG, "Grandezza della ImageView: "+imageView.getMeasuredHeight()+", "+imageView.getMeasuredWidth());
				}
			}
		}
	}

	static class ViewHolder{
		TextView text;
		RelativeLayout relativeText;
		ImageView poster;
		ImageView favourite;
	}
}
