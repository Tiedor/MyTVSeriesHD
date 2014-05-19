package it.tiedor.mytvserieshd.loaders.core;

import android.widget.ImageView;

public class FavouriteDisplayer {

	public FavouriteDisplayer() {}
	
	public void displayStarOn(ImageView imageView){
		imageView.setImageResource(android.R.drawable.btn_star_big_on);
	}
	
	public void displayStarOff(ImageView imageView){
		imageView.setImageResource(android.R.drawable.btn_star_big_off);
	}
}
