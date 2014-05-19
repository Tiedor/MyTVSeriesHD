package it.tiedor.mytvserieshd.loaders.core;

import java.sql.SQLException;

import it.tiedor.mydbhelper.DatabaseHelper;
import it.tiedor.mydbhelper.persistence.Serie;
import it.tiedor.mytvserieshd.MyConstants;
import android.util.Log;
import android.widget.ImageView;

public class MyFavouriteTask implements Runnable{

	private final String LOG_TAG = MyConstants.LOG_TAG + " - " + this.getClass().getName();
	private DatabaseHelper databaseHelper;
	private Long id;
	private ImageView imageView;
	private FavouriteDisplayer displayer;
	public MyFavouriteTask(DatabaseHelper databaseHelper, Long id, ImageView imageView, FavouriteDisplayer displayer) {
		this.databaseHelper = databaseHelper;
		this.id = id;
		this.imageView = imageView;
		this.displayer = displayer;
	}
	
	@Override
	public void run() {
		try {
			Log.d(LOG_TAG, "Starting MyFavouriteTask - Cerco la serie con id = "+this.id);
			
			Serie serie = this.databaseHelper.getSerieDao().queryForId(this.id);
			if(serie == null)
				throw new SQLException();
			else{
				displayer.displayStarOn(imageView);
				imageView.setOnClickListener(null);
			}
		} catch (SQLException e) {
			displayer.displayStarOff(imageView);
		}
	}

}
