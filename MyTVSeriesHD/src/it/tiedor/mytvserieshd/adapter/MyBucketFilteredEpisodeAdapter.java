package it.tiedor.mytvserieshd.adapter;

import it.tiedor.mydbhelper.DataBaseHelperFactory;
import it.tiedor.mydbhelper.DatabaseHelper;
import it.tiedor.mydbhelper.persistence.Episode;
import it.tiedor.mytvserieshd.MyConstants;
import it.tiedor.mytvserieshd.R;
import it.tiedor.mytvserieshd.fragment.MyFragmentManager;
import it.tiedor.mytvserieshd.listener.EpisodeClickListener;
import it.tiedor.mytvserieshd.loaders.MyFavouriteLoader;
import it.tiedor.mytvserieshd.loaders.core.FavouriteDisplayer;
import it.tiedor.mytvserieshd.utils.DateUtil;
import it.tiedor.torrentdownloader.TorrentHelper;

import java.net.URLEncoder;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Loader;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapBackgroundDisplayer;
import com.scythe.bucket.BucketListAdapter;

public class MyBucketFilteredEpisodeAdapter extends BucketListAdapter<Episode> {

	private 				ImageLoader 			loader;
	private 				DisplayImageOptions 	options;
	private 				ImageLoadingListener 	animateFirstListener;
	private					Typeface				forteTypeface;
	private 				DatabaseHelper 			helper;
	private	static	final 	String 					LOG_TAG 	= MyConstants.LOG_TAG + " - " + MyBucketFilteredEpisodeAdapter.class.getSimpleName();

	public MyBucketFilteredEpisodeAdapter(Activity ctx, ArrayList<Episode> elements,
			Integer bucketSize) {
		super(ctx, elements, bucketSize);
		
		this.helper = DataBaseHelperFactory.getDatabaseHelper(ctx);
		
		this.loader = ImageLoader.getInstance();

		this.animateFirstListener = new AnimateFirstDisplayListener();

		options = new DisplayImageOptions.Builder()
			.resetViewBeforeLoading(true)
			.cacheInMemory(false)
			.cacheOnDisc(true)
			.displayer(new SimpleBitmapBackgroundDisplayer())
			.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
			.build();

		forteTypeface = Typeface.createFromAsset(ctx.getAssets(),
				"fonts/forte.ttf");
		
	}

	@Override
	protected View getBucketElement(int position, Episode currentElement,
			View convertView) {

		final ViewHolder viewHolder;

		if(convertView == null){
			convertView = View.inflate(ctx, R.layout.episode_poster, null);

			viewHolder = new ViewHolder();

			viewHolder.posterContainer = (RelativeLayout) convertView.findViewById(R.id.posterContainer);
			viewHolder.text = (TextView)convertView.findViewById(R.id.myImageViewText);
			viewHolder.text2 = (TextView)convertView.findViewById(R.id.myImageViewText2);
			viewHolder.poster = (ImageView) convertView.findViewById(R.id.poster);
			viewHolder.relativeText = (RelativeLayout) convertView.findViewById(R.id.relativeText);
			viewHolder.relativeText2 = (RelativeLayout) convertView.findViewById(R.id.relativeText2);
			viewHolder.image_icon = (ImageView) convertView.findViewById(R.id.image_icon);
			
			viewHolder.text.setTypeface(this.forteTypeface);
			viewHolder.text2.setTypeface(this.forteTypeface);

			convertView.setTag(viewHolder);
		}else
			viewHolder = (ViewHolder) convertView.getTag();

		viewHolder.text.setText(currentElement.getSeason().getSerie().getShowName());
		
		String date;
		if(currentElement.getAirTime() == null)
			date = "Non pianificato";
		else if(DateUtil.checkToday(currentElement.getAirTime()))
			date = "Oggi";
		else if(DateUtil.checkYesterday(currentElement.getAirTime()))
			date = "Ieri";
		else if(DateUtil.checkTomorrow(currentElement.getAirTime()))
			date = "Domani";
		else
			date = new SimpleDateFormat("dd/MM/yy").format(currentElement.getAirTime());
		
		viewHolder.text2.setText(date+" ["+currentElement.getSeason().getSeason()+"X"+currentElement.getEpisode()+"]");

		if(currentElement.getEpisodeImageUrl()!=null)
			loader.displayImage(currentElement.getEpisodeImageUrl(), viewHolder.poster, options, animateFirstListener);		
		else{
			loader.displayImage("drawable://"+R.drawable.no_episode_found, viewHolder.poster, options, animateFirstListener);
		}
				
		if(currentElement.getAirTime() != null && currentElement.getAirTime().before(Calendar.getInstance().getTime())){
			viewHolder.image_icon.setVisibility(View.VISIBLE);
			if(!currentElement.isDownloading()){
				viewHolder.image_icon.setImageResource(R.drawable.download);
				viewHolder.image_icon.setOnClickListener(new EpisodeClickListener(ctx, 1, currentElement, position));
			}else if(!currentElement.isDownload()){
				viewHolder.image_icon.setImageResource(R.drawable.search_episode);
				viewHolder.image_icon.setOnClickListener(new EpisodeClickListener(ctx, 2, currentElement, position));
			}else if(!currentElement.isSub() && currentElement.getItasaSubUrl() != null && !currentElement.getItasaSubUrl().equals("")){
				viewHolder.image_icon.setImageResource(R.drawable.subba);
				viewHolder.image_icon.setOnClickListener(new EpisodeClickListener(ctx, 3, currentElement, position));
			}else if(!currentElement.isView() && currentElement.isSub() && currentElement.isDownload()){
				viewHolder.image_icon.setImageResource(R.drawable.play);
				viewHolder.image_icon.setOnClickListener(new EpisodeClickListener(ctx, 4, currentElement, position));
			}else
				viewHolder.image_icon.setVisibility(View.GONE);
		}else
			viewHolder.image_icon.setVisibility(View.GONE);
		
		final View view = convertView;
		final Episode episode = currentElement;
		final int finalPosition = position;
		
		convertView.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				
				PreferenceManager.getDefaultSharedPreferences(getContext()).edit()
					.putLong(MyConstants.EPISODE_ID_PROCESSING, episode.getEpisodeId())
					.putInt(MyConstants.EPISODE_POSITION_PROCESSING, finalPosition)
					.commit();
				
				((Activity)getContext()).openContextMenu(view);
				return true;
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
		TextView text2;
		RelativeLayout relativeText2;
		RelativeLayout posterContainer;
		ImageView poster;
		ImageView image_icon;
	}
}