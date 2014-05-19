package it.tiedor.mytvserieshd.fragment;

import it.tiedor.mytvserieshd.MyConstants;
import it.tiedor.mytvserieshd.R;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

public class MyFragmentManager {

	public static void setFragment(int position, Bundle bundle, Activity fragmentActivity, String tag){
		Fragment fragment = null;
		FragmentTransaction fragmentManager = fragmentActivity.getFragmentManager().beginTransaction();

		switch (position) {
		case 1: //Da scaricare
			fragment = new FilteredEpisodeListFragment();
			bundle = new Bundle();
			bundle.putInt(MyConstants.EPISODE_FILTER, position);
			fragment.setArguments(bundle);

			break;
		case 2: //In download
			fragment = new FilteredEpisodeListFragment();
			bundle = new Bundle();
			bundle.putInt(MyConstants.EPISODE_FILTER, position);
			fragment.setArguments(bundle);

			break;
		case 3: //Da Subbare
			fragment = new FilteredEpisodeListFragment();
			bundle = new Bundle();
			bundle.putInt(MyConstants.EPISODE_FILTER, position);
			fragment.setArguments(bundle);

			break;
		case 4: //Da Vedere	
			fragment = new FilteredEpisodeListFragment();
			bundle = new Bundle();
			bundle.putInt(MyConstants.EPISODE_FILTER, position);
			fragment.setArguments(bundle);

			break;
		case 6: //Da Vedere	
			fragment = new HelpFragment();
			break;
		case 101: //Da scaricare
			fragment = new SearchNewSeriesListFragment();
			if(bundle!=null)
				fragment.setArguments(bundle);

			break;
		case 102: //In download
			fragment = new SeasonListFragment();
			if(bundle!=null)
				fragment.setArguments(bundle);

			break;
		case 103: //Da Subbare
			fragment = new EpisodeListFragment();
			if(bundle!=null)
				fragment.setArguments(bundle);

			break;
		default:
			fragment = new MyOwnSeriesListFragment();
			if(bundle!=null)
				fragment.setArguments(bundle);
			break;
		}

		int resId;

		if(!MyConstants.isTwoPane || tag==null || tag.equals(MyConstants.TAG_MAIN))
			resId = R.id.content_frame;
		else
			resId = R.id.serie_detail_container;


		fragmentManager        	
		.replace(resId, fragment)
		.commit();
	}

	public static void reloadFragment(int position, Bundle bundle, Activity fragmentActivity, String tag){
		Fragment fragment = null;
		FragmentTransaction fragmentManager = fragmentActivity.getFragmentManager().beginTransaction();

		switch (position) {
		case 1:
			fragment = new SearchNewSeriesListFragment();
			if(bundle!=null)
				fragment.setArguments(bundle);
			break;
		case 2:
			fragment = new SeasonListFragment();
			if(bundle!=null)
				fragment.setArguments(bundle);
			break;
		case 3:
			fragment = new EpisodeListFragment();
			if(bundle!=null)
				fragment.setArguments(bundle);
			break;
		default:
			fragment = new MyOwnSeriesListFragment();
			if(bundle!=null)
				fragment.setArguments(bundle);
			break;
		}

		int resId;

		if(fragmentActivity.findViewById(R.id.serie_detail_container)==null || tag.equals(MyConstants.TAG_MAIN)){
			resId = R.id.content_frame;
			/*if(fragmentActivity.findViewById(R.id.serie_detail_container)!=null)
				removeFragment(fragmentActivity, R.id.serie_detail_container);*/
		}else
			resId = R.id.serie_detail_container;
			

		fragmentManager        	
		.replace(resId, fragment)
		.commit();
	}
	
	public static void removeFragment(Activity fragmentActivity, String tag){
		FragmentManager fragmentManager = fragmentActivity.getFragmentManager();
		Fragment fragment = fragmentManager.findFragmentByTag(tag);
		if(fragment != null)
			fragmentManager.beginTransaction().remove(fragment).commit();
	}
	
	public static void removeFragment(Activity fragmentActivity, int id){
		FragmentManager fragmentManager = fragmentActivity.getFragmentManager();
		Fragment fragment = fragmentManager.findFragmentById(id);
		if(fragment != null)
			fragmentManager.beginTransaction().remove(fragment).commit();
	}
}
