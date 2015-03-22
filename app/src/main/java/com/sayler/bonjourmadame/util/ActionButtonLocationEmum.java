package com.sayler.bonjourmadame.util;

import android.widget.RelativeLayout;

public enum ActionButtonLocationEmum {
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

  ActionButtonLocationEmum(int[] rulesToAdd, int[] rulesToRemove) {
    this.rulesToAdd = rulesToAdd;
    this.rulesToRemove = rulesToRemove;
  }
}