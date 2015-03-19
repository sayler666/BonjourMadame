/**
 * Created by sayler666 on 2015-03-19.
 * <p>
 * Copyright 2015 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.bonjourmadame.activity;

import android.app.Activity;
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

}
