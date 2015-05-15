package com.sayler.bonjourmadame.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

import java.io.IOException;

/**
 * Created by lchromy on 12.05.15.
 */
public class WallpaperManager {
  private Activity activity;
  private Bitmap previousWallpaper;

  public WallpaperManager(Activity activity) {
    this.activity = activity;
  }

  public void setBitmapAsWallpaperAndSavePreviousWallpaper(Bitmap bitmap) {
    savePreviousWallpaper();
    setBitmapAsWallpaper(bitmap);
  }

  public void setBitmapAsWallpaper(Bitmap bitmap) {
    DisplayMetrics metrics = new DisplayMetrics();
    activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
    // get the height and width of screen
    int height = metrics.heightPixels;
    int width = metrics.widthPixels;

    android.app.WallpaperManager wallpaperManager = android.app.WallpaperManager.getInstance(activity);
    try {
      wallpaperManager.setBitmap(bitmap);
      wallpaperManager.suggestDesiredDimensions(width, height);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void setPreviousWallpaper() {
    if (previousWallpaper != null) {
      setBitmapAsWallpaper(previousWallpaper);
    }
  }

  private void savePreviousWallpaper() {
    final android.app.WallpaperManager wallpaperManager = android.app.WallpaperManager.getInstance(activity);
    previousWallpaper = drawableToBitmap(wallpaperManager.getDrawable());
  }

  public static Bitmap drawableToBitmap(Drawable drawable) {
    if (drawable instanceof BitmapDrawable) {
      return ((BitmapDrawable) drawable).getBitmap();
    }

    int width = drawable.getIntrinsicWidth();
    width = width > 0 ? width : 1;
    int height = drawable.getIntrinsicHeight();
    height = height > 0 ? height : 1;

    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);
    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
    drawable.draw(canvas);

    return bitmap;
  }
}
