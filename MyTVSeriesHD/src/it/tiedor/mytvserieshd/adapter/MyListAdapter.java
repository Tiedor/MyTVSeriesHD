package it.tiedor.mytvserieshd.adapter;

import it.tiedor.mydbhelper.DataBaseHelperFactory;
import it.tiedor.mydbhelper.DatabaseHelper;
import it.tiedor.mydbhelper.persistence.Episode;
import it.tiedor.mydbhelper.persistence.Serie;
import it.tiedor.mytvserieshd.MyConstants;
import it.tiedor.mytvserieshd.R;
import it.tiedor.mytvserieshd.fragment.MyFragmentManager;
import it.tiedor.mytvserieshd.utils.DateUtil;

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
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.decode.MyImageDecoder;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapBackgroundDisplayer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.SyncStateContract.Helpers;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

public class MyListAdapter extends ArrayAdapter<Serie> {

	private 				ImageLoader 			loader;
	private 				DisplayImageOptions 	options;
	private 				ImageLoadingListener 	animateFirstListener;
    private					Typeface				forteTypeface;
	private final static 	String 					LOG_TAG 	= MyConstants.LOG_TAG + " - " + MyListAdapter.class.getSimpleName();

	public MyListAdapter(final Activity context, int resource,
			int textViewResourceId, ArrayList<Serie> objects) {
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
			.displayer(new BitmapDisplayer() {
				@Override
				public Bitmap display(Bitmap bitmap, ImageView imageView, LoadedFrom loadedFrom) {
					imageView.setImageResource(R.drawable.gradient_shape);
					/*imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, bitmap.getHeight()));
					((RelativeLayout)imageView.getParent()).setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, bitmap.getHeight()));*/
					BitmapDrawable drawable = new BitmapDrawable(bitmap);
					imageView.setBackgroundDrawable(drawable);
					return bitmap;
				}
			})
			.imageScaleType(ImageScaleType.EXACTLY)
			.cacheOnDisc(true)
			.build();
		
		forteTypeface = Typeface.createFromAsset(context.getAssets(),
				"fonts/forte.ttf");

	}
	
	
	
	public void removeItem(int position){
		super.remove(getItem(position));
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		final ViewHolder viewHolder;
		final Serie serie = getItem(position);
		if (convertView == null) {

			viewHolder = new ViewHolder();

			convertView = View.inflate(getContext(), R.layout.series_list_row, null);

			viewHolder.relative_layout_1 = (RelativeLayout) convertView.findViewById(R.id.relative_layout_1);
			viewHolder.seriesName = (TextView) convertView.findViewById(R.id.series_name);
			viewHolder.nextEp = (TextView) convertView.findViewById(R.id.series_next_episode);
			viewHolder.nextEp_date = (TextView) convertView.findViewById(R.id.series_next_episode_date);
			viewHolder.banner = (ImageView) convertView.findViewById(R.id.image_banner);
			viewHolder.expand = (RelativeLayout) convertView.findViewById(R.id.ic_menu_expand_layout);

			convertView.setTag(viewHolder);
		} else{
			viewHolder = (ViewHolder) convertView.getTag();
		}

		final String seriesName = getItem(position).getShowName();
		final long seriesId = getItem(position).getShowID();

		viewHolder.seriesName.setText(seriesName);
		viewHolder.seriesName.setTypeface(forteTypeface);
		viewHolder.seriesName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);

		viewHolder.nextEp.setTypeface(forteTypeface);
		viewHolder.nextEp_date.setTypeface(forteTypeface);

		try {
			Episode nextEpisode = getItem(position).getNextAirEpisode();
			viewHolder.nextEp.setText(nextEpisode.getEpisodeName());
			
			String date;
			if(DateUtil.checkToday(nextEpisode.getAirTime()))
				date = "Oggi";
			else if(DateUtil.checkYesterday(nextEpisode.getAirTime()))
				date = "Ieri";
			else if(DateUtil.checkTomorrow(nextEpisode.getAirTime()))
				date = "Domani";
			else
				date = new SimpleDateFormat("dd/MM/yy").format(nextEpisode.getAirTime());
			
			viewHolder.nextEp_date.setText(date);
		} catch (Exception e) {
			viewHolder.nextEp.setText(getItem(position).getStatus());
			viewHolder.nextEp_date.setText("");
		}

		//Log.d(LOG_TAG, "Sto per caricare l'immagine all'URL: "+getItem(position).getBannerURI());
		
		if(getItem(position).getBannerURI()!=null)
			loader.displayImage(getItem(position).getBannerURI(), viewHolder.banner, options, animateFirstListener);

		viewHolder.relative_layout_1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try{
					Bundle bundle = new Bundle();
					bundle.putLong(MyConstants.SERIES_ID_KEY, seriesId);
					bundle.putInt(MyConstants.SERIES_POSITION, position);
					
					MyFragmentManager.setFragment(102, bundle, ((Activity)getContext()), MyConstants.TAG_SLAVE);
				}catch(Exception e){
					Log.e(LOG_TAG, "Errore nel recuperare gli episodi", e);
				}
			}
		});

		viewHolder.relative_layout_1.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				PreferenceManager.getDefaultSharedPreferences(getContext()).edit()
					.putLong(MyConstants.SERIE_ID_PROCESSING, serie.getShowID())
					.commit();
				((Activity)getContext()).openContextMenu(v);
				return true;
			}
		});
		
		return convertView;
	}

	private static class ViewHolder {
		RelativeLayout relative_layout_1;
		TextView seriesName;
		TextView nextEp;
		TextView nextEp_date;
		ImageView banner;
		RelativeLayout expand;
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
					//Log.d(LOG_TAG, "Grandezza della ImageView: "+imageView.getMeasuredHeight()+", "+imageView.getMeasuredWidth());
				}
			}
		}
	}
}