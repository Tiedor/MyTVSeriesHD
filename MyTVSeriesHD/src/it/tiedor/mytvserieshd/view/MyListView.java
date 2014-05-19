package it.tiedor.mytvserieshd.view;

import it.tiedor.mydbhelper.persistence.Episode;
import android.content.Context;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;

public class MyListView extends ListView {

	public MyListView(Context context) {
		super(context);
	}
	
	public class MyListContextMenuInfo implements ContextMenuInfo{
		
		public Episode episode;
		public int position;
		
		public MyListContextMenuInfo(int position, Episode episode) {
			this.position = position;
			this.episode = episode;
		}	
	}

}
