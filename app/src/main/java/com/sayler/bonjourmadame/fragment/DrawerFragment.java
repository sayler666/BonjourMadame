/**
 * Created by sayler666 on 2015-05-11.
 * <p>
 * Copyright 2015 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.bonjourmadame.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.WallpaperManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.sayler.bonjourmadame.R;
import com.sayler.bonjourmadame.adapter.NavigationAdapter;
import com.sayler.bonjourmadame.event.ForceCloseDrawerEvent;
import com.sayler.bonjourmadame.event.DrawerClosedEvent;
import com.sayler.bonjourmadame.event.InflateDrawerFragmentEvent;
import com.sayler.bonjourmadame.event.RefreshDrawerTopImage;
import com.sayler.bonjourmadame.model.NavigationItem;
import de.greenrobot.event.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Main navigation drawer fragment
 *
 * @author sayler666
 */
public class DrawerFragment extends BaseFragment {

  @InjectView(R.id.stubImport)
  ViewStub viewStub;
  private DynamicViewStub dynamicViewStub;
  private Fragment fragmentToChange;

  public class DynamicViewStub {
    @InjectView(R.id.navigationListView)
    ListView navigationListView;
    @InjectView(R.id.topImage)
    ImageView topImage;

    public DynamicViewStub(View view) {
      ButterKnife.inject(this, view);
    }
  }

  /* ---------------------------------------------- LIFECYCLE METHODS ------------------------------------------------*/

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
    return rootView;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
  }

  @Override
  public void onPause() {
    super.onPause();
    EventBus.getDefault().unregister(this);
  }

  @Override
  public void onResume() {
    super.onResume();
    EventBus.getDefault().register(this);
  }

  /* ---------------------------------------------- EVENTS -----------------------------------------------------------*/

  public void onEvent(InflateDrawerFragmentEvent event) {
    lazyInflateDrawer();
  }

  public void onEvent(RefreshDrawerTopImage event) {
    setupTopImage();
  }

  public void onEvent(DrawerClosedEvent event) {
    changeFragment();
  }

  /* ---------------------------------------------- PRIVATE METHODS --------------------------------------------------*/

  private void lazyInflateDrawer() {
    if (dynamicViewStub == null) {
      View viewFromViewStub = viewStub.inflate();
      dynamicViewStub = new DynamicViewStub(viewFromViewStub);
      setupTopImage();
      setupNavigation();
    }
  }

  private void setupTopImage() {
    final WallpaperManager wallpaperManager = WallpaperManager.getInstance(getBaseActivity());
    final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
    dynamicViewStub.topImage.setImageDrawable(wallpaperDrawable);
  }

  private void setupNavigation() {
    List<NavigationItem> navigationItems = createNavigationList();
    NavigationAdapter navigationAdapter = new NavigationAdapter(getBaseActivity(), navigationItems);
    dynamicViewStub.navigationListView.setAdapter(navigationAdapter);
    dynamicViewStub.navigationListView.setOnItemClickListener((parent, view, position, id) -> ((NavigationItem) navigationAdapter.getItem(position)).navigationClick());
  }

  private List<NavigationItem> createNavigationList() {
    List<NavigationItem> navigationItems = new ArrayList<>();
    navigationItems.add(new NavigationItem(getResources().getDrawable(R.drawable.ic_refresh_image), "Start", () -> lateChangeFragment(LoadingFragment.newInstanceRandomLoad())));
    navigationItems.add(new NavigationItem(getResources().getDrawable(R.drawable.ic_favourite_image), "Favourites", () -> Log.d("DrawerFragment", "Favourites")));
    navigationItems.add(new NavigationItem(getResources().getDrawable(R.drawable.ic_history), "History", () -> lateChangeFragment(new HistoryFragment())));
    return navigationItems;
  }

  private void lateChangeFragment(Fragment newFragment) {
    fragmentToChange = newFragment;
    EventBus.getDefault().post(new ForceCloseDrawerEvent());
  }

  private void changeFragment() {
    if (fragmentToChange != null) {
      fragmentToChange.setAllowEnterTransitionOverlap(true);

      getFragmentManager().beginTransaction()
          .setCustomAnimations(R.animator.slide_in, R.animator.slide_out)
          .replace(R.id.container, fragmentToChange, fragmentToChange.getClass().getSimpleName())
          .commit();

      fragmentToChange = null;
    }
  }

}
