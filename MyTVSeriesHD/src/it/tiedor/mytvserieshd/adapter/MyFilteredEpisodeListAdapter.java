package it.tiedor.mytvserieshd.adapter;

import it.tiedor.mytvserieshd.MyConstants;
import it.tiedor.mytvserieshd.adapter.headerlist.Item;
import it.tiedor.mytvserieshd.adapter.headerlist.ListItem;

import java.util.ArrayList;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class MyFilteredEpisodeListAdapter extends ArrayAdapter<Item> {

	private 				LayoutInflater 			mInflater;
	private					int						totalHeader		= 0;
	private final static 	String 					LOG_TAG 		= MyConstants.LOG_TAG + " - " + MyFilteredEpisodeListAdapter.class.getSimpleName();

	public MyFilteredEpisodeListAdapter(Activity context, int resource,
			int textViewResourceId, ArrayList<Item> objects) {
		super(context, resource, textViewResourceId, objects);

		mInflater = LayoutInflater.from(context);
	}

	public enum RowType {
		LIST_ITEM, 
		HEADER_ITEM
	}

	@Override
	public int getViewTypeCount() {
		return RowType.values().length;
	}

	@Override
	public int getItemViewType(int position) {
		return getItem(position).getViewType();
	}

	@Override
	public View getView(final int position, View convertView,
			ViewGroup parent) {
		
		int mypos = (position+totalHeader)/2;
		
		Log.d(LOG_TAG, "Position: "+position+", mypos: "+mypos+", parent child: "+parent.getChildCount());
		
		if(getItemViewType(position) == RowType.LIST_ITEM.ordinal()){
			
			if(parent.getChildCount() > mypos){
				convertView = parent.getChildAt(mypos);
				((AdapterView)parent).removeView(parent.getChildAt(position));
			}
			
			return getBucketView(position, mInflater, convertView, ((Activity)getContext()), super.getCount());
		}else{
			totalHeader++;
			return getItem(position).getView(position, mInflater, convertView, ((Activity)getContext()), super.getCount());
		}
	}

	public View getBucketView(int bucketPosition, LayoutInflater inflater, View convertView, Activity activity, int count){
		LinearLayout bucket;
		FrameLayout bucketElementFrame;
		
		if (convertView != null) {
			bucket = (LinearLayout)convertView;

			Log.i(LOG_TAG, "Reusing bucket view");
		} else {
			bucket = new LinearLayout(activity);
			bucket.setLayoutParams(new AbsListView.LayoutParams(
					AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));
			bucket.setOrientation(LinearLayout.HORIZONTAL);

			Log.i(LOG_TAG, "Instantiating new bucket view");
		}

		bucketElementFrame = new FrameLayout(activity);
		bucketElementFrame.setLayoutParams(new LinearLayout.LayoutParams(0,
				LinearLayout.LayoutParams.MATCH_PARENT, 1));

		View current = ((ListItem)getItem(bucketPosition)).getBucketElement(bucketPosition, inflater, null, activity);
		bucketElementFrame.addView(current);

		bucket.addView(bucketElementFrame);
		
		return bucket;
	}
}