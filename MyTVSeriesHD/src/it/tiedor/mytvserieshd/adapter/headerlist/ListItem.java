package it.tiedor.mytvserieshd.adapter.headerlist;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.decode.MyImageDecoder;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapBackgroundDisplayer;

import it.tiedor.mydbhelper.persistence.Episode;
import it.tiedor.mytvserieshd.MyConstants;
import it.tiedor.mytvserieshd.R;
import it.tiedor.mytvserieshd.adapter.MyEpisodeListAdapter;
import it.tiedor.mytvserieshd.adapter.MyFilteredEpisodeListAdapter.RowType;
import it.tiedor.mytvserieshd.listener.EpisodeClickListener;
import it.tiedor.mytvserieshd.utils.DateUtil;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ListItem implements Item{

	private final Episode 	episode;
	private final Context 	context;

	private 				ImageLoader 			loader;
	private 				DisplayImageOptions 	options;
	private 				ImageLoadingListener 	animateFirstListener;
    private					Typeface				forteTypeface;
	private final static 	String 					LOG_TAG 	= MyConstants.LOG_TAG + " - " + MyEpisodeListAdapter.class.getName();
	
	public ListItem(Context context, Episode episode) {
		this.episode = episode;
		this.context = context;
		
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
		.resetViewBeforeLoading(true)
		.cacheInMemory(false)
		.cacheOnDisc(true)
		.displayer(new SimpleBitmapBackgroundDisplayer())
		.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
		.build();

		forteTypeface = Typeface.createFromAsset(context.getAssets(),
				"fonts/forte.ttf");
	}

	@Override
	public int getViewType() {
		return RowType.LIST_ITEM.ordinal();
	}

	@Override
    public View getView(int bucketPosition, LayoutInflater inflater, View convertView, Activity activity, int count){
		return null;
	}
	
	public View getBucketElement(int position, LayoutInflater inflater, View convertView, final Activity activity) {
		final ViewHolder viewHolder;

		if(convertView == null){
			convertView = inflater.inflate(R.layout.episode_poster, null);

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

		viewHolder.text.setText(episode.getEpisodeName());
		
		String date;

		if(episode.getAirTime() == null)
			date = "Non pianificato";
		else if(DateUtil.checkToday(episode.getAirTime()))
			date = "Oggi";
		else if(DateUtil.checkYesterday(episode.getAirTime()))
			date = "Ieri";
		else if(DateUtil.checkTomorrow(episode.getAirTime()))
			date = "Domani";
		else
			date = new SimpleDateFormat("dd/MM/yy").format(episode.getAirTime());
		
		viewHolder.text2.setText(date+" ["+episode.getSeason().getSeason()+"X"+episode.getEpisode()+"]");

		if(episode.getEpisodeImageUrl()!=null)
			loader.displayImage(episode.getEpisodeImageUrl(), viewHolder.poster, options, animateFirstListener);		
		else{
			loader.displayImage("drawable://"+R.drawable.no_episode_found, viewHolder.poster, options, animateFirstListener);
		}
				
		if(episode.getAirTime() != null && episode.getAirTime().before(Calendar.getInstance().getTime())){
			viewHolder.image_icon.setVisibility(View.VISIBLE);
			if(!episode.isDownloading()){
				viewHolder.image_icon.setImageResource(R.drawable.download);
				viewHolder.image_icon.setOnClickListener(new EpisodeClickListener(activity, 1, episode, position));
			}else if(!episode.isDownload()){
				viewHolder.image_icon.setImageResource(R.drawable.search_episode);
				viewHolder.image_icon.setOnClickListener(new EpisodeClickListener(activity, 2, episode, position));
			}else if(!episode.isSub() && episode.getItasaSubUrl() != null && !episode.getItasaSubUrl().equals("")){
				viewHolder.image_icon.setImageResource(R.drawable.subba);
				viewHolder.image_icon.setOnClickListener(new EpisodeClickListener(activity, 3, episode, position));
			}else if(!episode.isView() && episode.isSub() && episode.isDownload()){
				viewHolder.image_icon.setImageResource(R.drawable.play);
				viewHolder.image_icon.setOnClickListener(new EpisodeClickListener(activity, 4, episode, position));
			}else
				viewHolder.image_icon.setVisibility(View.GONE);
		}else
			viewHolder.image_icon.setVisibility(View.GONE);
		
		final View view = convertView;
		final int finalPosition = position;
		
		convertView.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				
				PreferenceManager.getDefaultSharedPreferences(activity).edit()
					.putLong(MyConstants.EPISODE_ID_PROCESSING, episode.getEpisodeId())
					.putInt(MyConstants.EPISODE_POSITION_PROCESSING, finalPosition)
					.commit();
				
				activity.openContextMenu(view);
				return true;
			}
		});		
		
		return convertView;
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
