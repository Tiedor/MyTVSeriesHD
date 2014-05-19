package it.tiedor.mytvserieshd.adapter;

import it.tiedor.mydbhelper.DataBaseHelperFactory;
import it.tiedor.mydbhelper.DatabaseHelper;
import it.tiedor.mydbhelper.persistence.Episode;
import it.tiedor.mydbhelper.persistence.Serie;
import it.tiedor.mytvserieshd.MyConstants;
import it.tiedor.mytvserieshd.R;
import it.tiedor.mytvserieshd.fragment.MyFragmentManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.decode.MyImageDecoder;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapBackgroundDisplayer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.SyncStateContract.Helpers;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

public class MyEpisodeListAdapter extends ArrayAdapter<Episode> {

	private 				ImageLoader 			loader;
	private 				DisplayImageOptions 	options;
	private 				ImageLoadingListener 	animateFirstListener;
    private					Typeface				forteTypeface;
	private final static 	String 					LOG_TAG 	= MyConstants.LOG_TAG + " - " + MyEpisodeListAdapter.class.getName();

	public MyEpisodeListAdapter(Activity context, int resource,
			int textViewResourceId, ArrayList<Episode> objects) {
		super(context, resource, textViewResourceId, objects);
		
		ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(context)
			.imageDecoder(new MyImageDecoder())
			.build();
		
		this.loader = ImageLoader.getInstance();
		if(this.loader.isInited()){
			this.loader.destroy();
		}
		this.loader.init(configuration);
		
		this.animateFirstListener = new AnimateFirstDisplayListener();
				
		options = new DisplayImageOptions.Builder()
			.cacheInMemory(true)
			.resetViewBeforeLoading(true)
			.displayer(new RoundedBitmapDisplayer(5))
			.showImageOnLoading(R.drawable.no_banner_found)
			.showImageForEmptyUri(R.drawable.no_banner_found)
			.showImageOnFail(R.drawable.no_banner_found)
			.cacheOnDisc(true)
			.build();
		
		forteTypeface = Typeface.createFromAsset(context.getAssets(),
				"fonts/forte.ttf");

	}

	@Override
	public View getView(final int position, View convertView,
			ViewGroup parent) {

		final ViewHolder viewHolder;

		if (convertView == null) {

			viewHolder = new ViewHolder();

			convertView = View.inflate(getContext(), R.layout.episode_list_row, null);

			viewHolder.episodeName = (TextView) convertView.findViewById(R.id.episode_name);
			viewHolder.episodeNumber = (TextView) convertView.findViewById(R.id.episode_number);
			viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image_view);
			viewHolder.imageDownload = (ImageView) convertView.findViewById(R.id.image_download); 
			viewHolder.imageSub = (ImageView) convertView.findViewById(R.id.image_sub);
			viewHolder.poster =  (ImageView) convertView.findViewById(R.id.poster);
			viewHolder.light = (ImageView) convertView.findViewById(R.id.light);
			convertView.setTag(viewHolder);
		} else{
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.episodeName.setText(getItem(position).getEpisodeName());

		if(getItem(position).getEpisodeImageUrl()!=null)
			loader.displayImage(getItem(position).getEpisodeImageUrl(), viewHolder.poster, options, animateFirstListener);
		
		
		if(!getItem(position).isView()){
			viewHolder.imageView.setImageResource(R.drawable.ic_view_not);
		}else{
			viewHolder.light.setImageResource(R.drawable.blue_light);
		}
		
		if(!getItem(position).isDownload()){
			viewHolder.imageDownload.setImageResource(R.drawable.ic_download_not);
		}else{
			viewHolder.light.setImageResource(R.drawable.red_light);
		}
		
		if(!getItem(position).isSub()){
			viewHolder.imageSub.setImageResource(R.drawable.ic_sub_not);
		}else{
			
		}
		
		return convertView;
	}

	private static class ViewHolder {
		TextView episodeName; 
		TextView episodeNumber;
		ImageView imageView;
		ImageView imageDownload; 
		ImageView imageSub;
		ImageView poster;
		ImageView light;
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
}