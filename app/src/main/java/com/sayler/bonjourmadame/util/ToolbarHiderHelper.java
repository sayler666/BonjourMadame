/**
 * Created by sayler666 on 2015-06-19.
 * <p>
 * Copyright 2015 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.bonjourmadame.util;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import com.sayler.bonjourmadame.R;

/**
 * Helper class for hiding toolbar on list/recycler scroll
 *
 * @author sayler666
 */
public class ToolbarHiderHelper {
  private final String TAG = getClass().getSimpleName();
  private final Toolbar toolbar;
  private final RecyclerView recyclerView;
  private int scrollAbsolutePosition = 0;
  private int scrollSignum = 0;
  private final int toolbarHeight;
  private int tempHidingY;
  private int tempShowingY;

  public ToolbarHiderHelper(Toolbar toolbar, RecyclerView recyclerView) {

    this.toolbar = toolbar;
    this.recyclerView = recyclerView;
    toolbarHeight = (int) toolbar.getContext().getResources().getDimension(R.dimen.abc_action_bar_default_height_material);
  }

  public void startHidingToolbarOnScroll() {
    this.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (scrollSignum != (int) Math.signum(dy)) {
          if (scrollSignum == -1) {
            tempHidingY = tempShowingY;
          }
          if (scrollSignum == 1) {
            tempShowingY = tempHidingY;
          }
        }
        scrollSignum = (int) Math.signum(dy);
        scrollAbsolutePosition += dy;

        //hiding scroll
        if (scrollSignum == 1) {
          if (Math.abs(scrollAbsolutePosition) <= toolbarHeight && tempHidingY == 0) {
            toolbar.setTranslationY(-scrollAbsolutePosition);
          } else if (Math.abs(tempHidingY) <= toolbarHeight) {
            if (Math.abs(tempHidingY + dy) <= toolbarHeight) {
              tempHidingY += dy;
              toolbar.setTranslationY(-toolbarHeight - tempHidingY);
            } else {
              toolbar.setTranslationY(-toolbarHeight);
            }
          } else {
            toolbar.setTranslationY(-toolbarHeight);
          }
          tempShowingY = 0;
        }
        //showing scroll
        else {
          if (scrollAbsolutePosition <= toolbarHeight && tempShowingY == 0) {
            toolbar.setTranslationY(-scrollAbsolutePosition);
          } else if (Math.abs(tempShowingY) <= toolbarHeight) {
            if (Math.abs(tempShowingY + dy) <= toolbarHeight) {
              tempShowingY += dy;
              toolbar.setTranslationY(-toolbarHeight - tempShowingY);
            } else {
              toolbar.setTranslationY(0);
            }
          }
          tempHidingY = 0;
        }

        super.onScrolled(recyclerView, dx, dy);
      }
    });
  }
}
