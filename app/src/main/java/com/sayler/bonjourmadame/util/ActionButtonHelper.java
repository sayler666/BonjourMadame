/**
 * Created by sayler666 on 2015-03-22.
 * <p>
 * Copyright 2015 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.bonjourmadame.util;

import android.view.View;
import android.widget.RelativeLayout;

public class ActionButtonHelper {
  /**
   * set action button position in parent container
   *
   * @param buttonContainer action button
   * @param location        location to be set
   */
  public static void setActionButtonPosition(View buttonContainer, ActionButtonLocationEmum location) {
    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) buttonContainer.getLayoutParams();

    /**
     * add rules
     */
    for (int rule : location.getRulesToAdd()) {
      layoutParams.addRule(rule);
    }

    /**
     * remove rules
     */
    for (int rule : location.getRulesToRemove()) {
      layoutParams.removeRule(rule);
    }

    /**
     * set layout params
     */
    buttonContainer.setLayoutParams(layoutParams);
  }
}
