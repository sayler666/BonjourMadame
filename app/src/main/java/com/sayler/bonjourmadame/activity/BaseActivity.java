/**
 * Created by sayler666 on 2015-03-19.
 * <p>
 * Copyright 2015 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.bonjourmadame.activity;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;
import com.sayler.bonjourmadame.inject.ActivityComponent;

/**
 * TODO Add class description...
 *
 * @author sayler666
 */
public class BaseActivity extends Activity {

  private ActivityComponent activityComponent;

  public ActivityComponent getActivityComponent() {
    return activityComponent;
  }

  public void animateStatusBarColor(int color, int duration) {
    Window window = getWindow();
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

    ValueAnimator valueAnimator = ValueAnimator.ofArgb(window.getStatusBarColor(), color);
    valueAnimator.setDuration(duration);
    valueAnimator.addUpdateListener(animation -> window.setStatusBarColor((int) animation.getAnimatedValue()));
    valueAnimator.start();
  }
}
