package it.tiedor.mytvserieshd.adapter.headerlist;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import it.tiedor.mytvserieshd.R;
import it.tiedor.mytvserieshd.adapter.MyFilteredEpisodeListAdapter.RowType;

public class Header implements Item{
	private 		Typeface 	bodoni_hand;
	private final 	String 		name;

	public Header(Context context, String name) {
		this.name = name;
		bodoni_hand = Typeface.createFromAsset(context.getAssets(),
				"fonts/bodoni_hand.otf");
	}

	@Override
	public int getViewType() {
		return RowType.HEADER_ITEM.ordinal();
	}

	@Override
	public View getView(int bucketPosition, LayoutInflater inflater, View convertView, Activity ctx, int count) {
		View view;
		if (convertView == null) {
			view = (View) inflater.inflate(R.layout.filtered_episode_header, null);
		} else {
			view = convertView;
		}

		((TextView) view.findViewById(R.id.season_name)).setTypeface(bodoni_hand);
		TextView text = (TextView) view.findViewById(R.id.season_name);
		text.setText(name);

		return view;
	}
}
