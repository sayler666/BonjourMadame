package com.sayler.bonjourmadame.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.sayler.bonjourmadame.R;
import com.sayler.bonjourmadame.model.NavigationItem;

import java.util.List;

public class NavigationAdapter
    extends BaseAdapter {

  private final List<NavigationItem> navigationItems;
  private final LayoutInflater inflater;

  public NavigationAdapter(Context context, List<NavigationItem> navigationItems) {
    inflater = LayoutInflater.from(context);
    this.navigationItems = navigationItems;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View view = inflater.inflate(R.layout.i_navigation_row, parent, false);
    ((ImageView) view.findViewById(R.id.icon)).setImageDrawable(navigationItems.get(position).getIcon());
    ((TextView) view.findViewById(R.id.text)).setText(navigationItems.get(position).getText());
    return view;
  }

  @Override
  public int getCount() {
    return navigationItems.size();
  }

  @Override
  public Object getItem(int position) {
    return navigationItems.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }
}