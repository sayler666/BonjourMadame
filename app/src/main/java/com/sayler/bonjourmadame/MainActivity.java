package com.sayler.bonjourmadame;

import android.app.Activity;
import android.os.Bundle;
import com.sayler.bonjourmadame.fragment.LoadingFragment;

public class MainActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
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
