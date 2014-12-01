/**
 * Created by lchromy on 01.12.14.
 *
 * Copyright 2014 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.bonjourmadame.provider;

/**
 * Template for providers
 *
 * @author lchromy
 */
public abstract class Provider {

  /**
   * Should return provider url.
   *
   * @return String provider URL
   */
  public abstract String getProviderUrl();

  /**
   * Start downloading process.
   */
  public abstract void downloadImage();

  /**
   * Set current downloaded image as wallpaper.
   */
  public void setImageAsWallpaper() {

  }

}
