package com.sayler.bonjourmadame.util;

import android.graphics.Color;

/**
 * Created by lchromy on 21.04.15.
 */
public class ColorUtils {
  public static int darkenColor(int color) {
    float[] hsv = new float[3];
    Color.colorToHSV(color, hsv);
    hsv[2] *= 0.8f;
    hsv[1] *= 1.4f;
    color = Color.HSVToColor(hsv);
    return color;
  }
}
