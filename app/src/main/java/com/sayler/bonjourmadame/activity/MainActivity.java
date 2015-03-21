package com.sayler.bonjourmadame.activity;

import android.os.Bundle;
import com.sayler.bonjourmadame.BonjourMadameApplication;
import com.sayler.bonjourmadame.R;
import com.sayler.bonjourmadame.fragment.LoadingFragment;
import com.sayler.bonjourmadame.network.BonjourMadameAPI;

import javax.inject.Inject;

public class MainActivity extends BaseActivity {
  @Inject
  BonjourMadameAPI bonjourMadameAPI;

  public BonjourMadameAPI getBonjourMadameAPI() {
    return bonjourMadameAPI;
  }

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

}
