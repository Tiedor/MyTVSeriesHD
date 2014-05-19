package it.tiedor.mytvserieshd.adapter;

import it.tiedor.mydbhelper.DataBaseHelperFactory;
import it.tiedor.mydbhelper.DatabaseHelper;
import it.tiedor.mydbhelper.persistence.Season;
import it.tiedor.mytvserieshd.MyConstants;
import it.tiedor.mytvserieshd.R;
import it.tiedor.mytvserieshd.fragment.MyFragmentManager;
import it.tiedor.mytvserieshd.loaders.MyFavouriteLoader;
import it.tiedor.mytvserieshd.loaders.core.FavouriteDisplayer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapBackgroundDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleLargerBitmapBackgroundDisplayer;
import com.scythe.bucket.BucketListAdapter;

public class MyBucketSeasonsAdapter extends BucketListAdapter<Season> {

	private 				ImageLoader 			loader;
	private 				MyFavouriteLoader 		favLoader;
	private 				DisplayImageOptions 	options;
	private 				ImageLoadingListener 	animateFirstListener;
	private					Typeface				forteTypeface;
	private 				DatabaseHelper 			helper;
	private final static 	String 					LOG_TAG 	= MyConstants.LOG_TAG + " - " + "MyBucketAdapter";

	public MyBucketSeasonsAdapter(Activity ctx, ArrayList<Season> elements,
			Integer bucketSize) {
		super(ctx, elements, bucketSize);
		
		this.helper = DataBaseHelperFactory.getDatabaseHelper(ctx);
		
		this.loader = ImageLoader.getInstance();
		this.favLoader = MyFavouriteLoader.getInstance(ctx, new FavouriteDisplayer());

		this.animateFirstListener = new AnimateFirstDisplayListener();

		options = new DisplayImageOptions.Builder()
			.resetViewBeforeLoading(true)
			.cacheInMemory(false)
			.cacheOnDisc(true)
			.displayer(new SimpleBitmapBackgroundDisplayer())
			//.showImageOnLoading(R.drawable.no_image_found)
			.imageScaleType(ImageScaleType.EXACTLY)
			.build();

		forteTypeface = Typeface.createFromAsset(ctx.getAssets(),
				"fonts/forte.ttf");
	}

	@Override
	protected View getBucketElement(int position, Season currentElement,
			View convertView) {

		final ViewHolder viewHolder;

		if(convertView == null){
			convertView = View.inflate(ctx, R.layout.season_poster, null);

			viewHolder = new ViewHolder();

			viewHolder.posterContainer = (RelativeLayout) convertView.findViewById(R.id.posterContainer);
			viewHolder.text = (TextView)convertView.findViewById(R.id.myImageViewText);
			viewHolder.poster = (ImageView) convertView.findViewById(R.id.poster);
			viewHolder.favourite = (ImageView) convertView.findViewById(R.id.favourite);
			viewHolder.relativeText = (RelativeLayout) convertView.findViewById(R.id.relativeText);

			viewHolder.text.setTypeface(this.forteTypeface);

			convertView.setTag(viewHolder);
		}else
			viewHolder = (ViewHolder) convertView.getTag();

		viewHolder.text.setText("Stagione "+currentElement.getSeason());

		if(currentElement.getSaesonImageUrl()!=null)
			loader.displayImage(currentElement.getSaesonImageUrl(), viewHolder.poster, options, animateFirstListener);
		else
			loader.displayImage("drawable://"+R.drawable.no_image_found, viewHolder.poster, options, animateFirstListener);
		
		//Log.d(LOG_TAG, "H ["+viewHolder.poster.getHeight()+"], W ["+viewHolder.poster.getWidth()+"], MH ["+viewHolder.poster.getMeasuredHeight()+"], MW ["+viewHolder.poster.getMeasuredWidth()+"]");
		//viewHolder.posterContainer.setLayoutParams(new FrameLayout.LayoutParams(viewHolder.poster.getMeasuredWidth(), viewHolder.poster.getMeasuredHeight()));
		final Season season = currentElement;
		
		if(currentElement.isToFollow())
			viewHolder.favourite.setImageResource(android.R.drawable.btn_star_big_on);
		else
			viewHolder.favourite.setImageResource(android.R.drawable.btn_star_big_off);
		
		viewHolder.relativeText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					if(helper.setSeasonFavourite(season.getSeasonId()))
						viewHolder.favourite.setImageResource(android.R.drawable.btn_star_big_on);
					else
						viewHolder.favourite.setImageResource(android.R.drawable.btn_star_big_off);
					
					if(MyConstants.isTwoPane)
						MyFragmentManager.reloadFragment(0, null, ((Activity)getContext()), MyConstants.TAG_MAIN);
				} catch (SQLException e) {
					Log.e(LOG_TAG, "Impossibile seguire la stagione "+season.getSeasonId(), e);
				}
				
			}
		});

		viewHolder.posterContainer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try{
					Bundle bundle = new Bundle();
					bundle.putLong(MyConstants.SERIES_ID_KEY, season.getSerie().getShowID());
					bundle.putLong(MyConstants.SEASON_ID_KEY, season.getSeasonId());
					
					MyFragmentManager.setFragment(103, bundle, ((Activity)getContext()), MyConstants.TAG_SLAVE);
				}catch(Exception e){
					Log.e(LOG_TAG, "Errore nel recuperare gli episodi", e);
				}
			}
		});
		
		return convertView;
	}

	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			RelativeLayout posterContainer = (RelativeLayout) view.getParent();
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					posterContainer.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}

	static class ViewHolder{
		TextView text;
		RelativeLayout relativeText;
		RelativeLayout posterContainer;
		ImageView poster;
		ImageView favourite;
	}
}