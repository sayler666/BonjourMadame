package com.sayler.bonjourmadame.util;

import android.graphics.Color;

/**
 * Created by lchromy on 21.04.15.
 */
public class ColorUtils {
  public static int amendColor(int color, float hsv0Factor, float hsv1Factor, float hsv2Factor) {
    float[] hsv = new float[3];
    Color.colorToHSV(color, hsv);
    hsv[0] *= hsv0Factor;
    hsv[1] *= hsv1Factor;
    hsv[2] *= hsv2Factor;
    color = Color.HSVToColor(hsv);
    return color;
  }

  public static int amendColorAlpha(int color, int alpha) {
    return Color.argb(alpha, (color >> 16) & 0xff, (color >> 8) & 0xff, color & 0xff);
  }
}
