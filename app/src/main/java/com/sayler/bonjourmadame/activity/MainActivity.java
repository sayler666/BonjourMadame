package com.sayler.bonjourmadame.activity;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.sayler.bonjourmadame.BonjourMadameApplication;
import com.sayler.bonjourmadame.R;
import com.sayler.bonjourmadame.fragment.DrawerFragment;
import com.sayler.bonjourmadame.fragment.LoadingFragment;
import com.sayler.bonjourmadame.network.BonjourMadameAPI;
import com.sayler.bonjourmadame.util.ToolbarColorizeHelper;

import javax.inject.Inject;

public class MainActivity extends BaseActivity {
  @Inject
  BonjourMadameAPI bonjourMadameAPI;
  @InjectView(R.id.toolbar)
  Toolbar toolbar;
  @InjectView(R.id.DrawerLayout)
  DrawerLayout drawer;
  ActionBarDrawerToggle drawerToggle;
  private Animation toolbarDropOutAnimation;
  private Animation toolbarDropInAnimation;
  /* ---------------------------------------------- LIFECYCLE METHODS ------------------------------------------------*/

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    BonjourMadameApplication bonjourMadameApplication = BonjourMadameApplication.get(this);
    bonjourMadameApplication.getApplicationComponent().inject(this);
    bonjourMadameApplication.getNetworkComponent().inject(this);

    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    setContentView(R.layout.a_main);
    ButterKnife.inject(this);

    setupDrawer();
    setupToolbar();
    setupAnimation();
    /**
     * restore state
     */
    if (savedInstanceState == null) {
      getFragmentManager().beginTransaction()
          .add(R.id.container, new LoadingFragment())
          .commit();

      getFragmentManager().beginTransaction()
          .add(R.id.drawerLayout, new DrawerFragment())
          .commit();
    }

  }

  @Override
  public void finish() {
    super.finish();
    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
  }

  /* ---------------------------------------------- GETTERS & SETTERS ------------------------------------------------*/

  public Toolbar getToolbar() {
    return toolbar;
  }

  public BonjourMadameAPI getBonjourMadameAPI() {
    return bonjourMadameAPI;
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

   /* ---------------------------------------------- PRIVATE METHODS --------------------------------------------------*/

  private void setupDrawer() {
    drawerToggle = new MyActionBarDrawerToggle();
    drawer.setDrawerListener(drawerToggle);
    drawerToggle.syncState();
  }

  private void setupToolbar() {
    toolbar.setTitle(R.string.app_name);
  }

  private void setupAnimation() {
    toolbarDropOutAnimation = AnimationUtils.loadAnimation(this, R.anim.drop_out);
    toolbarDropInAnimation = AnimationUtils.loadAnimation(this, R.anim.drop_in);
  }

  private class MyActionBarDrawerToggle extends ActionBarDrawerToggle {

    public MyActionBarDrawerToggle() {
      super(MainActivity.this, MainActivity.this.drawer, MainActivity.this.toolbar, R.string.abc_toolbar_collapse_description, R.string.abc_toolbar_collapse_description);
    }

    @Override
    public void onDrawerOpened(View drawerView) {
      super.onDrawerOpened(drawerView);
    }

    @Override
    public void onDrawerClosed(View drawerView) {
      super.onDrawerClosed(drawerView);
    }

  }
}
