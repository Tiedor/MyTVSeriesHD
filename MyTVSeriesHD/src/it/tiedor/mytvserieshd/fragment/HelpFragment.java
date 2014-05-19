package it.tiedor.mytvserieshd.fragment;

import it.tiedor.mytvserieshd.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HelpFragment extends MyFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.help, container, false);
		TextView help = (TextView) rootView.findViewById(R.id.help_text01);
		help.setText(getResources().getString(R.string.help01));
		
		help = (TextView) rootView.findViewById(R.id.help_text02);
		help.setText(getResources().getString(R.string.help02));
		
		help = (TextView) rootView.findViewById(R.id.help_text03);
		help.setText(getResources().getString(R.string.help03));
		
		help = (TextView) rootView.findViewById(R.id.help_text04);
		help.setText(getResources().getString(R.string.help04));
		
		help = (TextView) rootView.findViewById(R.id.help_text05);
		help.setText(getResources().getString(R.string.help05));
		
		return rootView;
	}
	
	@Override
	public void onBackPressed() {
		MyFragmentManager.setFragment(0, getArguments(), getActivity(), getTag());
	}

}
