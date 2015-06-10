package com.sayler.bonjourmadame.widget;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class HackyDrawerLayout extends DrawerLayout {

  public HackyDrawerLayout(Context context) {
    super(context);
  }

  public HackyDrawerLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public HackyDrawerLayout(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  private boolean isDisallowIntercept = false;

  @Override
  public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    // keep the info about if the innerViews do requestDisallowInterceptTouchEvent
    isDisallowIntercept = disallowIntercept;
    super.requestDisallowInterceptTouchEvent(disallowIntercept);
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    // the incorrect array size will only happen in the multi-touch scenario.
    if (ev.getPointerCount() > 1 && isDisallowIntercept) {
      requestDisallowInterceptTouchEvent(false);
      boolean handled = super.dispatchTouchEvent(ev);
      requestDisallowInterceptTouchEvent(true);
      return handled;
    } else {
      return super.dispatchTouchEvent(ev);
    }
  }
}