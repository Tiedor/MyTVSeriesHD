package it.tiedor.mytvserieshd.listener;

import it.tiedor.mytvserieshd.MyConstants;
import it.tiedor.mytvserieshd.R;
import it.tiedor.mytvserieshd.fragment.MyFragmentManager;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SearchSeriesOnReturnListener implements OnKeyListener{

	private Activity activity;
    private DrawerLayout myDrawerLayout;
    private LinearLayout myLinearDrawer;
	
	public SearchSeriesOnReturnListener(Activity activity, DrawerLayout myDrawerLayout, LinearLayout myLinearDrawer) {
		this.activity = activity;
		this.myDrawerLayout = myDrawerLayout;
		this.myLinearDrawer = myLinearDrawer;
	}
	
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if(keyCode == 66 && event.getAction() == 0 && ((EditText)v).getEditableText() != null && !((EditText)v).getEditableText().toString().equals("")){
			
			ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE); 
			if (cm.getActiveNetworkInfo() == null) {
				Toast.makeText(activity, "Nessun collegamento ad Internet", Toast.LENGTH_LONG).show();
				return false; 
			}
			
			myDrawerLayout.closeDrawer(myLinearDrawer);
			InputMethodManager imm = (InputMethodManager)this.activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			
			Bundle bundle = new Bundle();
			bundle.putString(MyConstants.SEARCH_SERIES_KEY, ((EditText)v).getEditableText().toString());
			
			MyFragmentManager.setFragment(101, bundle, this.activity, MyConstants.TAG_SLAVE);
	        
	        ((EditText)v).setText("");
		}
		
		return true;
	}

}
