package com.sayler.bonjourmadame.activity;

import android.os.Bundle;
import android.util.Log;
import com.sayler.bonjourmadame.BonjourMadameApplication;
import com.sayler.bonjourmadame.R;
import com.sayler.bonjourmadame.fragment.HistoryFragment;
import mapper.MadamEntityDataMapper;

import javax.inject.Inject;

public class TestActivity extends BaseActivity {
  @Inject
  MadamEntityDataMapper madamEntityDataMapper;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    BonjourMadameApplication bonjourMadameApplication = BonjourMadameApplication.get(this);
    bonjourMadameApplication.getApplicationComponent().inject(this);
    bonjourMadameApplication.getNetworkComponent().inject(this);

    if (madamEntityDataMapper != null) {
      Log.d("TEST", madamEntityDataMapper.toString());
    }

    setContentView(R.layout.activity_my);
    if (savedInstanceState == null) {
      getFragmentManager().beginTransaction()
          .add(R.id.main_container, new HistoryFragment())
          .commit();
    }
  }

}