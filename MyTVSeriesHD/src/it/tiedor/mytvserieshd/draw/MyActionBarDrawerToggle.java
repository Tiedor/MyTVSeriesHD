package it.tiedor.mytvserieshd.draw;

import it.tiedor.mytvserieshd.R;
import android.app.Activity;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

public class MyActionBarDrawerToggle extends ActionBarDrawerToggle {

	private Activity activity;
	private String activityTitle;

	public MyActionBarDrawerToggle(Activity activity, DrawerLayout drawerLayout) {
		super(
				activity, 				/* host Activity */
				drawerLayout, 			/* DrawerLayout object */
				R.drawable.ic_drawer,  	/* nav drawer icon to replace 'Up' caret */
				R.string.drawer_open,  	/* "open drawer" description */
				R.string.drawer_close  	/* "close drawer" description */
				);
		
		this.activity = activity;
		this.activityTitle = this.activity.getTitle().toString();
	}

	/** Called when a drawer has settled in a completely closed state. */
	public void onDrawerClosed(View view) {
		this.activity.getActionBar().setTitle(activityTitle);
	}

	/** Called when a drawer has settled in a completely open state. */
	public void onDrawerOpened(View drawerView) {
		this.activity.getActionBar().setTitle("Menù");
	}

}
