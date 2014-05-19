package it.tiedor.mytvserieshd.listener;

import java.util.ArrayList;
import java.util.List;

import it.tiedor.mytvserieshd.MyConstants;
import it.tiedor.mytvserieshd.R;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.GridView;

public class MultiChoiceModeListener implements GridView.MultiChoiceModeListener {
	
	private 		GridView 	mGrid;
	private final 	String 		LOG_TAG = MyConstants.LOG_TAG + " - " + getClass().getSimpleName();
	private 		int 		selectCount = 0;
	private			List<Integer>	selected = new ArrayList<Integer>();
	
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		mode.setTitle("Select Items");
		mode.setSubtitle("One item selected");
		
		// Inflate the menu for the CAB
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.filterd_episode, menu);
        return true;
		
	}
	
	public void setGrid(GridView mGrid) {
		this.mGrid = mGrid;
	}

	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		return true;
	}

	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		
		Log.d(LOG_TAG, "Action Item clicked");
		
		for(int id : selected)
				Log.d(LOG_TAG, "L'elemento con id "+id+" è selezionato");

		return true;
	}

	public void onDestroyActionMode(ActionMode mode) {
	}

	public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
		
		if(checked)
			selected.add(position);
		else
			selected.remove(position);
		
		selectCount = selected.size();
		
		Log.d(LOG_TAG, "" + selectCount + " items selected");
		
		switch (selectCount) {
		case 1:
			mode.setSubtitle("One item selected");
			break;
		default:
			mode.setSubtitle("" + selectCount + " items selected");
			break;
		}
	}
}
