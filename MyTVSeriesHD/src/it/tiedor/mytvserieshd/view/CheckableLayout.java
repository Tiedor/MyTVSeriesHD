package it.tiedor.mytvserieshd.view;

import it.tiedor.mytvserieshd.MyConstants;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.GridView;
import android.widget.RelativeLayout;

public class CheckableLayout extends RelativeLayout implements Checkable {
	private 		boolean mChecked;
	private			GridView gridView;
	private 		int 	position;
	private final 	String 	LOG_TAG	= MyConstants.LOG_TAG + " - " + getClass().getSimpleName();
	
	public CheckableLayout(Context context) {
		super(context);
	}
	
	public CheckableLayout(Context arg0, AttributeSet arg1) {
		super(arg0, arg1);
	}
	
	public void setGridView(GridView gridView) {
		this.gridView = gridView;
	}
	
	public void setPosition(int position) {
		this.position = position;
	}

	public void setChecked(boolean checked) {
		mChecked = checked;
		refreshDrawableState();
		 
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof Checkable) {
            	child.setVisibility(mChecked ? View.VISIBLE : View.GONE);
                ((Checkable) child).setChecked(mChecked);
                gridView.setItemChecked(position, mChecked);
            }
        }
		
	}

	public boolean isChecked() {
		return mChecked;
	}

	public void toggle() {
		setChecked(!mChecked);
	}

}
