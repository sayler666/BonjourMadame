/**
 * Created by sayler666 on 2015-05-11.
 * <p>
 * Copyright 2015 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.bonjourmadame.model;

import android.graphics.drawable.Drawable;

/**
 * TODO Add class description...
 *
 * @author sayler666
 */
public class NavigationItem {
  private String text;
  private NavigationListener navigationListener;
  private Drawable icon;

  public NavigationItem(Drawable icon, String text, NavigationListener navigationListener) {
    this.icon = icon;
    this.text = text;
    this.navigationListener = navigationListener;
  }

  public String getText() {
    return text;
  }

  public Drawable getIcon() {
    return icon;
  }

  public void navigationClick(){
    navigationListener.onNavigationClick();
  }

  public interface NavigationListener {
    void onNavigationClick();
  }

}
