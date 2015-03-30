package com.sayler.bonjourmadame.activity;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toolbar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.sayler.bonjourmadame.BonjourMadameApplication;
import com.sayler.bonjourmadame.R;
import com.sayler.bonjourmadame.fragment.LoadingFragment;
import com.sayler.bonjourmadame.network.BonjourMadameAPI;

import javax.inject.Inject;

public class MainActivity extends BaseActivity {
  @Inject
  BonjourMadameAPI bonjourMadameAPI;
  @InjectView(R.id.toolbar) Toolbar toolbar;
  private Animation toolbarDropOutAnimation;
  private Animation toolbarDropInAnimation;

  /* ---------------------------------------------- LIFECYCLE METHODS ------------------------------------------------*/

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    BonjourMadameApplication bonjourMadameApplication = BonjourMadameApplication.get(this);

    bonjourMadameApplication.getApplicationComponent().inject(this);
    bonjourMadameApplication.getNetworkComponent().inject(this);

    /**
     * set activity's in animation
     */
    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    setContentView(R.layout.a_main);
    ButterKnife.inject(this);

    setupToolbar();
    setupAnimation();
    /**
     * restore state
     */
    if (savedInstanceState == null) {
      getFragmentManager().beginTransaction()
          .add(R.id.container, new LoadingFragment())
          .commit();
    }

  }

  @Override
  public void finish() {
    super.finish();
    /**
     * set activity's out animation
     */
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

   /* ---------------------------------------------- PRIVATE METHODS --------------------------------------------------*/

  private void setupToolbar() {
    toolbar.setTitle(R.string.app_name);
    setActionBar(toolbar);
  }

  private void setupAnimation() {
    toolbarDropOutAnimation = AnimationUtils.loadAnimation(this, R.anim.drop_out);
    toolbarDropInAnimation = AnimationUtils.loadAnimation(this, R.anim.drop_in);
  }

}
