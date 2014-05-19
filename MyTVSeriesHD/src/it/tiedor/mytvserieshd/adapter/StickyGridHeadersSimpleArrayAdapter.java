package it.tiedor.mytvserieshd.adapter;

import it.tiedor.mydbhelper.persistence.Episode;
import it.tiedor.mydbhelper.persistence.Season;
import it.tiedor.mytvserieshd.MyConstants;
import it.tiedor.mytvserieshd.R;
import it.tiedor.mytvserieshd.listener.EpisodeClickListener;
import it.tiedor.mytvserieshd.listener.MultiChoiceModeListener;
import it.tiedor.mytvserieshd.utils.DateUtil;
import it.tiedor.mytvserieshd.view.CheckableLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.internal.he;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapBackgroundDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersBaseAdapter;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;

import android.app.Activity;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class StickyGridHeadersSimpleArrayAdapter extends BaseAdapter implements StickyGridHeadersBaseAdapter {

	private HashMap<Season, ArrayList<Episode>> data;
	private ArrayList<Season>					mHeaders;
	private ArrayList<Episode> 					mEpisodes;
	//private ActionMode 							mActionMode;

	private LayoutInflater 						mInflater;
	private StickyGridHeadersGridView			gridView;
	private	MultiChoiceModeListener				modeListener;
	
	private ImageLoader 						loader;
	private DisplayImageOptions 				options;
	private ImageLoadingListener 				animateFirstListener;
	private	Typeface							forteTypeface;
	private Typeface 							bodoni_hand;
	private Activity							activity;
	
	private int									filter;
	
	private final String 						LOG_TAG 	= MyConstants.LOG_TAG + " - " + getClass().getSimpleName();

	public StickyGridHeadersSimpleArrayAdapter(Activity activity, LinkedHashMap<Season, ArrayList<Episode>> items, int filter) {
		
		mInflater = LayoutInflater.from(activity);
		
		this.activity = activity;
		
		this.filter = filter;
		
		this.mHeaders = new ArrayList<Season>();
		this.mEpisodes = new ArrayList<Episode>();
		this.data = items;
		
		for(Season season : items.keySet()){
			mHeaders.add(season);
			mEpisodes.addAll(items.get(season));
		}
		
		this.loader = ImageLoader.getInstance();

		this.animateFirstListener = new AnimateFirstDisplayListener();

		options = new DisplayImageOptions.Builder()
			.resetViewBeforeLoading(true)
			.cacheInMemory(false)
			.cacheOnDisc(true)
			.displayer(new SimpleBitmapDisplayer())
			.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
			.build();

		forteTypeface = Typeface.createFromAsset(activity.getAssets(),
				"fonts/forte.ttf");
		
		bodoni_hand = Typeface.createFromAsset(activity.getAssets(),
				"fonts/bodoni_hand.otf");
	}

	@Override
	public int getCount() {
		return mEpisodes.size();
	}

	@Override
	public Episode getItem(int position) {
		return mEpisodes.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mEpisodes.get(position).getEpisodeId();
	}

	@Override
	public int getCountForHeader(int arg0) {
		Season season = mHeaders.get(arg0);
		int count = data.get(season).size();
		
		return count;
	}

	@Override
	public int getNumHeaders() {
		return mHeaders.size();
	}

	public void setModeListener(MultiChoiceModeListener modeListener) {
		this.modeListener = modeListener;
	}
	
	public void setGridView(StickyGridHeadersGridView gridView) {
		this.gridView = gridView;
	}
	
	@Override
	public View getHeaderView(int position, View convertView, ViewGroup parent) {

		final HeaderViewHolder viewHolder;
		
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.filtered_episode_header, parent, false);

			viewHolder = new HeaderViewHolder();
			viewHolder.season_name = (TextView) convertView.findViewById(R.id.season_name);

			viewHolder.season_name.setTypeface(this.bodoni_hand);
			
			convertView.setTag(viewHolder);
		}else
			viewHolder = (HeaderViewHolder) convertView.getTag();
		
		viewHolder.season_name.setText(mHeaders.get(position).getSerie().getShowName() + " - Stagione "+mHeaders.get(position).getSeason());
		
		return convertView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.episode_poster, parent, false);

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

		viewHolder.text.setText(getItem(position).getEpisodeName());
		
		String date;
		if(getItem(position).getAirTime() == null)
			date = "Non pianificato";
		else if(DateUtil.checkToday(getItem(position).getAirTime()))
			date = "Oggi";
		else if(DateUtil.checkYesterday(getItem(position).getAirTime()))
			date = "Ieri";
		else if(DateUtil.checkTomorrow(getItem(position).getAirTime()))
			date = "Domani";
		else
			date = new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(getItem(position).getAirTime());
		
		viewHolder.text2.setText(date+" ["+getItem(position).getSeason().getSeason()+"X"+getItem(position).getEpisode()+"]");
		
		if(getItem(position).getEpisodeImageUrl()!=null)
			loader.displayImage(getItem(position).getEpisodeImageUrl(), viewHolder.poster, options, animateFirstListener);		
		else{
			loader.displayImage("drawable://"+R.drawable.no_episode_found, viewHolder.poster, options, animateFirstListener);
		}
				
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);
		
		if(getItem(position).getAirTime() != null && today.getTime().after(getItem(position).getAirTime())){
			viewHolder.image_icon.setVisibility(View.VISIBLE);
			if(this.filter == 3 && !getItem(position).isSub() && getItem(position).getItasaSubUrl() != null && !getItem(position).getItasaSubUrl().equals("")){
				viewHolder.image_icon.setImageResource(R.drawable.subba);
				viewHolder.image_icon.setOnClickListener(new EpisodeClickListener(activity, 3, getItem(position), position));
			}else if(this.filter == 1 && !getItem(position).isDownloading()){
				viewHolder.image_icon.setImageResource(R.drawable.download);
				viewHolder.image_icon.setOnClickListener(new EpisodeClickListener(activity, 1, getItem(position), position));
			}else if(this.filter == 2 && !getItem(position).isDownload()){
				viewHolder.image_icon.setImageResource(R.drawable.search_episode);
				viewHolder.image_icon.setOnClickListener(new EpisodeClickListener(activity, 2, getItem(position), position));
			}else if(this.filter == 4 && !getItem(position).isView() && getItem(position).isSub() && getItem(position).isDownload()){
				viewHolder.image_icon.setImageResource(R.drawable.play);
				viewHolder.image_icon.setOnClickListener(new EpisodeClickListener(activity, 4, getItem(position), position));
			}else
				viewHolder.image_icon.setVisibility(View.GONE);
		}else
			viewHolder.image_icon.setVisibility(View.GONE);
		
		final View view = convertView;
		final Episode episode = getItem(position);
		final int finalPosition = position;
		
		/*convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CheckableLayout checkableLayout = (CheckableLayout) v;
				checkableLayout.setGridView(gridView);
				checkableLayout.setPosition(finalPosition);
				checkableLayout.toggle();
				if(mActionMode == null)
					mActionMode = activity.startActionMode(modeListener);
			}
		});*/
		
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

	private class HeaderViewHolder{
		TextView season_name;
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

	public void removeItem(int position){		
		Season header = getItem(position).getSeason();
		
		if(data.get(header).size()==1){			
			mHeaders.remove(header);
			data.remove(header);
		}else
			data.get(header).remove(getItem(position));
				
		mEpisodes.remove(position);
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
}
