/**
 * Created by sayler666 on 2015-05-11.
 * <p>
 * Copyright 2015 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.bonjourmadame.fragment;

import android.app.WallpaperManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.sayler.bonjourmadame.R;
import com.sayler.bonjourmadame.adapter.NavigationAdapter;
import com.sayler.bonjourmadame.model.NavigationItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Main navigation drawer fragment
 *
 * @author sayler666
 */
public class DrawerFragment extends BaseFragment {

  @InjectView(R.id.navigationListView)
  ListView navigationListView;
  @InjectView(R.id.topImage)
  ImageView topImage;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                           Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.f_drawer, container, false);
    ButterKnife.inject(this, rootView);

    setupTopImage();
    setupNavigation();

    return rootView;
  }

  private void setupTopImage() {
    final WallpaperManager wallpaperManager = WallpaperManager.getInstance(getBaseActivity());
    final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
    topImage.setImageDrawable(wallpaperDrawable);
  }

  private void setupNavigation() {

    List<NavigationItem> navigationItems = new ArrayList<>();
    NavigationItem startItem = new NavigationItem(getResources().getDrawable(R.drawable.ic_refresh_image), "Start", () -> Log.d("DrawerFragment", "Start"));
    NavigationItem favouritesItem = new NavigationItem(getResources().getDrawable(R.drawable.ic_favourite_image), "Favourites", () -> Log.d("DrawerFragment", "Favourites"));
    NavigationItem historyItem = new NavigationItem(getResources().getDrawable(R.drawable.ic_history), "History", () -> Log.d("DrawerFragment", "History"));
    navigationItems.add(startItem);
    navigationItems.add(favouritesItem);
    navigationItems.add(historyItem);

    NavigationAdapter navigationAdapter = new NavigationAdapter(getBaseActivity(), navigationItems);
    navigationListView.setAdapter(navigationAdapter);

  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
  }
}
