package it.tiedor.mytvserieshd.adapter.headerlist;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;

public interface Item {
    public int getViewType();
    public View getView(int bucketPosition, LayoutInflater inflater, View convertView, Activity ctx, int count);
}
