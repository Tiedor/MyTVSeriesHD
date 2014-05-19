package it.tiedor.mytvserieshd.loaders;

import java.util.concurrent.Executor;

import com.nostra13.universalimageloader.core.DefaultConfigurationFactory;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration.Builder;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import it.tiedor.mydbhelper.DataBaseHelperFactory;
import it.tiedor.mydbhelper.DatabaseHelper;
import it.tiedor.mytvserieshd.MyConstants;
import it.tiedor.mytvserieshd.loaders.core.FavouriteDisplayer;
import it.tiedor.mytvserieshd.loaders.core.MyFavouriteTask;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

public class MyFavouriteLoader {

	private final String LOG_TAG = MyConstants.LOG_TAG + " - " + this.getClass().getName();
	
	private volatile static MyFavouriteLoader instance;

	private DatabaseHelper helper;
	private Executor executor;
	private FavouriteDisplayer displayer;
	private Activity ctx;
		
	/** Returns singleton class instance */
	public static MyFavouriteLoader getInstance(Activity ctx, FavouriteDisplayer displayer) {
		if (instance == null) {
			synchronized (MyFavouriteLoader.class) {
				if (instance == null) {
					instance = new MyFavouriteLoader(ctx, displayer);
				}
			}
		}
		return instance;
	}

	protected MyFavouriteLoader(Activity ctx, FavouriteDisplayer displayer) {
		this.helper = DataBaseHelperFactory.getDatabaseHelper(ctx);
		this.executor = DefaultConfigurationFactory.createExecutor(3, 4, QueueProcessingType.FIFO);
		this.displayer = displayer;
		this.ctx = ctx;
	}
	
	public void displayFavourite(Long id, ImageView imageView){
		Log.d(LOG_TAG, "Starting displayFavourite");
		MyFavouriteTask task = new MyFavouriteTask(this.helper, id, imageView, this.displayer);
		this.ctx.runOnUiThread(task);
		//executor.execute(task);
	}
}
