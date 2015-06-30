/**
 * Created by sayler666 on 2015-06-19.
 * <p>
 * Copyright 2015 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.bonjourmadame.util;

import android.animation.ValueAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

/**
 * Helper class for hiding toolbar on list/recycler scroll
 *
 * @author sayler666
 */
public class ToolbarHiderHelper {
  private static final int SCROLLING_DOWN = 1;
  private static final int SCROLLING_UP = -1;
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
    toolbarHeight = toolbar.getHeight();
  }

  public void startHidingToolbarOnScroll() {
    this.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

      @Override
      public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        switch (newState) {
          case RecyclerView.SCROLL_STATE_IDLE:
            Log.d(TAG, "stop scrolling: posY" + toolbar.getTranslationY() + " sign " + scrollSignum);

            ValueAnimator va = null;
            if (scrollSignum == SCROLLING_DOWN) {
              if (scrollAbsolutePosition > toolbarHeight) {
                va = ValueAnimator.ofInt(((int) toolbar.getTranslationY()), -toolbarHeight);
              }
            } else {
              va = ValueAnimator.ofInt(((int) toolbar.getTranslationY()), 0);
            }
            if (va != null) {
              va.addUpdateListener(animation -> {
                toolbar.setTranslationY((int) animation.getAnimatedValue());
                if (scrollSignum == SCROLLING_DOWN) {
                  tempHidingY = -toolbarHeight - (int) animation.getAnimatedValue();
                }
                if (scrollSignum == SCROLLING_UP) {
                  tempShowingY = -toolbarHeight - (int) animation.getAnimatedValue();
                }
              });
              va.setDuration(500);
              va.start();
            }
            break;
          default:
        }

      }

      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        //ignore initial scroll
        if (dy == 0 && scrollSignum == 0) {
          tempHidingY = -toolbarHeight;
          return;
        }

        if (scrollSignum != (int) Math.signum(dy)) {
          if (scrollSignum == SCROLLING_UP) {
            tempHidingY = tempShowingY;
          }
          if (scrollSignum == SCROLLING_DOWN) {
            tempShowingY = tempHidingY;
          }
        }
        scrollSignum = (int) Math.signum(dy);
        scrollAbsolutePosition += dy;

        //hiding scroll
        if (scrollSignum == SCROLLING_DOWN) {
          if (Math.abs(tempHidingY) <= toolbarHeight) {
            if (Math.abs(tempHidingY + dy) <= toolbarHeight) {
              tempHidingY += dy;
              toolbar.setTranslationY(-toolbarHeight - tempHidingY);
            } else {
              toolbar.setTranslationY(-toolbarHeight);
            }
          }
        }
        //showing scroll
        else {
          if (Math.abs(tempShowingY) <= toolbarHeight) {
            if (Math.abs(tempShowingY + dy) <= toolbarHeight) {
              tempShowingY += dy;
              toolbar.setTranslationY(-toolbarHeight - tempShowingY);
            } else {
              toolbar.setTranslationY(0);
            }
          }
        }

        super.onScrolled(recyclerView, dx, dy);
      }
    });
  }

  public void showToolbar() {
    ValueAnimator va;
    va = ValueAnimator.ofInt(((int) toolbar.getTranslationY()), 0);
    va.addUpdateListener(animation -> {
      toolbar.setTranslationY((int) animation.getAnimatedValue());
      tempShowingY = -toolbarHeight - (int) animation.getAnimatedValue();
    });
    va.setDuration(500);
    va.start();
  }
}
