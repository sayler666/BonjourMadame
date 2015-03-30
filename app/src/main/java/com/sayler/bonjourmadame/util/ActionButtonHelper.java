/**
 * Created by sayler666 on 2015-03-22.
 * <p>
 * Copyright 2015 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.bonjourmadame.util;

import android.view.View;
import android.widget.RelativeLayout;

import java.util.Map;

public class ActionButtonHelper {
  /**
   * set action button position in parent container
   *
   * @param buttonContainer action button
   * @param location        location to be set
   */
  public static void setActionButtonPosition(View buttonContainer, ActionButtonLocationEnum location) {
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

  public static void setActionButtonPosition(View buttonContainer, com.sayler.bonjourmadame.util.ActionButtonLocation location) {
    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) buttonContainer.getLayoutParams();

    /**
     * add rules
     */
    for (Map.Entry<Integer, Integer> rule : location.getRulesToAdd().entrySet()) {
      layoutParams.addRule(rule.getKey(), rule.getValue());
    }
    /**
     * remove rules
     */
    for (Map.Entry<Integer, Integer> rule : location.getRulesToRemove().entrySet()) {
      layoutParams.removeRule(rule.getKey());
    }
    /**
     * set layout params
     */
    buttonContainer.setLayoutParams(layoutParams);
  }

  public enum ActionButtonLocationEnum {
    BOTTOM_RIGHT(new int[]{RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.ALIGN_PARENT_RIGHT}, new int[]{RelativeLayout.CENTER_IN_PARENT}),
    CENTER(new int[]{RelativeLayout.CENTER_IN_PARENT}, new int[]{RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.ALIGN_PARENT_RIGHT});

    final private int[] rulesToAdd;
    final private int[] rulesToRemove;

    public int[] getRulesToRemove() {
      return rulesToRemove.clone();
    }

    public int[] getRulesToAdd() {
      return rulesToAdd.clone();
    }

    ActionButtonLocationEnum(int[] rulesToAdd, int[] rulesToRemove) {
      this.rulesToAdd = rulesToAdd;
      this.rulesToRemove = rulesToRemove;
    }
  }
}
