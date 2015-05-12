package com.sayler.bonjourmadame.util;

import android.app.Activity;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;

import java.io.IOException;

/**
 * Created by lchromy on 12.05.15.
 */
public class WallpaperHelper {
  public static void setBitmapAsWallpaper(Bitmap bitmap, Activity activity) {
    DisplayMetrics metrics = new DisplayMetrics();
    activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
    // get the height and width of screen
    int height = metrics.heightPixels;
    int width = metrics.widthPixels;

    WallpaperManager wallpaperManager = WallpaperManager.getInstance(activity);
    try {
      wallpaperManager.setBitmap(bitmap);
      wallpaperManager.suggestDesiredDimensions(width, height);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
