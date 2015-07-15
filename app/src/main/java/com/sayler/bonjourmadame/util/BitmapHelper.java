package com.sayler.bonjourmadame.util;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by MiQUiDO on 15.07.15.
 * <p>
 * Copyright 2015 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
public class BitmapHelper {
  public static Uri getLocalBitmapUri(ImageView imageView) {
    // Extract Bitmap from ImageView drawable
    Drawable drawable = imageView.getDrawable();
    Bitmap bmp = null;
    if (drawable instanceof BitmapDrawable) {
      bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
    } else {
      return null;
    }
    // Store image to default external storage directory
    Uri bmpUri = null;
    try {
      File file = new File(Environment.getExternalStoragePublicDirectory(
          Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
      file.getParentFile().mkdirs();
      FileOutputStream out = new FileOutputStream(file);
      bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
      out.close();
      bmpUri = Uri.fromFile(file);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return bmpUri;
  }
}
