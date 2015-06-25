package com.sayler.bonjourmadame.activity;

import android.animation.ValueAnimator;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.sayler.bonjourmadame.BonjourMadameApplication;
import com.sayler.bonjourmadame.R;
import com.sayler.bonjourmadame.event.ForceCloseDrawerEvent;
import com.sayler.bonjourmadame.event.DrawerClosedEvent;
import com.sayler.bonjourmadame.event.InflateDrawerFragmentEvent;
import com.sayler.bonjourmadame.fragment.DrawerFragment;
import com.sayler.bonjourmadame.fragment.LoadingFragment;
import com.sayler.bonjourmadame.network.BonjourMadameAPI;
import com.sayler.bonjourmadame.util.ToolbarColorizeHelper;
import dao.MadameDataProvider;
import de.greenrobot.event.EventBus;
import mapper.MadamEntityDataMapper;

import javax.inject.Inject;

public class MainActivity extends BaseActivity {
  @Inject
  BonjourMadameAPI bonjourMadameAPI;
  @Inject
  MadamEntityDataMapper madamEntityDataMapper;
  @Inject
  MadameDataProvider madameDataProvider;
  @InjectView(R.id.toolbar)
  Toolbar toolbar;
  @InjectView(R.id.DrawerLayout)
  DrawerLayout drawer;
  ActionBarDrawerToggle drawerToggle;
  private Animation toolbarDropOutAnimation;
  private Animation toolbarDropInAnimation;
  private int currentToolbarColor;
  /* ---------------------------------------------- LIFECYCLE METHODS ------------------------------------------------*/

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    BonjourMadameApplication bonjourMadameApplication = BonjourMadameApplication.get(this);
    bonjourMadameApplication.getApplicationComponent().inject(this);
    bonjourMadameApplication.getNetworkDataComponent().inject(this);

    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    setContentView(R.layout.a_main);
    ButterKnife.inject(this);

    if (madamEntityDataMapper != null) {
      Log.d("TEST", madamEntityDataMapper.toString());
    }

    setupDrawer();
    setupToolbar();
    setupAnimation();
    /**
     * restore state
     */
    if (savedInstanceState == null) {
      String imgUrl = getUrlFromIntent();
      LoadingFragment loadingFragment;

      if (imgUrl != null) {
        loadingFragment = LoadingFragment.newInstanceWithUrl(imgUrl);
      } else {
        loadingFragment = LoadingFragment.newInstanceRandomLoad();
      }

      getFragmentManager().beginTransaction()
          .add(R.id.container, loadingFragment)
          .commit();
      getFragmentManager().beginTransaction()
          .add(R.id.drawerLayout, new DrawerFragment(), DrawerFragment.class.getSimpleName())
          .commit();
    }

  }

  private String getUrlFromIntent() {
    String combine = null;
    if (getIntent().getData() != null) {
      Uri data = getIntent().getData();
      String scheme = data.getScheme();
      String fullPath = data.getEncodedSchemeSpecificPart();
      combine = scheme + ":" + fullPath;
    }
    return combine;
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

  @Override
  public void finish() {
    super.finish();
    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        getFragmentManager().popBackStack();
        return true;
    }
    return false;
  }

  /* ---------------------------------------------- GETTERS & SETTERS ------------------------------------------------*/

  public Toolbar getToolbar() {
    return toolbar;
  }

  public BonjourMadameAPI getBonjourMadameAPI() {
    return bonjourMadameAPI;
  }

  public MadamEntityDataMapper getMadamEntityDataMapper() {
    return madamEntityDataMapper;
  }

  public MadameDataProvider getMadameDataProvider() {
    return madameDataProvider;
  }

  /* ---------------------------------------------- PUBLIC METHODS ---------------------------------------------------*/

  public void hideToolbar() {
    toolbar.startAnimation(toolbarDropOutAnimation);
  }

  public void showToolbar() {
    toolbar.startAnimation(toolbarDropInAnimation);
  }

  public void colorizeToolbarIcons(int color) {
    ToolbarColorizeHelper.colorizeToolbar(toolbar, color, MainActivity.this);
  }

  public void colorizeToolbar(int color, int duration) {
    ValueAnimator valueAnimator = ValueAnimator.ofArgb(currentToolbarColor, color);
    valueAnimator.setDuration(duration);
    valueAnimator.addUpdateListener(animation -> toolbar.setBackgroundColor((int) animation.getAnimatedValue()));
    valueAnimator.start();
    currentToolbarColor = color;
  }

  /* ---------------------------------------------- EVENTS -----------------------------------------------------------*/

  public void onEvent(ForceCloseDrawerEvent event) {
    closeDrawer();
  }

  /* ---------------------------------------------- PRIVATE METHODS --------------------------------------------------*/

  private void setupDrawer() {
    drawerToggle = new MyActionBarDrawerToggle();
    drawer.setDrawerListener(drawerToggle);
    drawerToggle.syncState();
  }

  private void setupToolbar() {
    toolbar.setTitle(R.string.app_name);

    toolbar.setNavigationOnClickListener(v -> {
      if (getFragmentManager().getBackStackEntryCount() > 0) {
        getFragmentManager().popBackStack();
      } else {
        drawer.openDrawer(Gravity.START);
      }
    });

    getFragmentManager().addOnBackStackChangedListener(() -> {
      if (getFragmentManager().getBackStackEntryCount() > 0) {
        toggleActionBarIcon(ActionDrawableState.BURGER, drawerToggle, true);
      } else {
        toggleActionBarIcon(ActionDrawableState.ARROW, drawerToggle, true);
      }
    });
  }

  private void setupAnimation() {
    toolbarDropOutAnimation = AnimationUtils.loadAnimation(this, R.anim.drop_out);
    toolbarDropInAnimation = AnimationUtils.loadAnimation(this, R.anim.drop_in);
  }

  private void closeDrawer() {
    drawer.closeDrawer(Gravity.START);
  }

  private class MyActionBarDrawerToggle extends ActionBarDrawerToggle {

    public MyActionBarDrawerToggle() {
      super(MainActivity.this, MainActivity.this.drawer, MainActivity.this.toolbar, R.string.abc_toolbar_collapse_description, R.string.abc_toolbar_collapse_description);
    }

    @Override
    public void onDrawerOpened(View drawerView) {
      super.onDrawerOpened(drawerView);
      EventBus.getDefault().post(new InflateDrawerFragmentEvent());
    }

    @Override
    public void onDrawerClosed(View drawerView) {
      super.onDrawerClosed(drawerView);
      EventBus.getDefault().post(new DrawerClosedEvent());
    }
  }

  private enum ActionDrawableState {
    BURGER, ARROW
  }

  private static void toggleActionBarIcon(ActionDrawableState state, final ActionBarDrawerToggle toggle, boolean animate) {
    if (animate) {
      float start = state == ActionDrawableState.BURGER ? 0f : 1.0f;
      float end = Math.abs(start - 1);
      ValueAnimator offsetAnimator = ValueAnimator.ofFloat(start, end);
      offsetAnimator.setDuration(300);
      offsetAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
      offsetAnimator.addUpdateListener(animation -> {
        float offset = (Float) animation.getAnimatedValue();
        toggle.onDrawerSlide(null, offset);
      });
      offsetAnimator.start();

    } else {
      if (state == ActionDrawableState.BURGER) {
        toggle.onDrawerClosed(null);
      } else {
        toggle.onDrawerOpened(null);
      }
    }
  }

}
