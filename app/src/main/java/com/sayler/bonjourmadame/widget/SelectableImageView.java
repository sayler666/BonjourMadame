package com.sayler.bonjourmadame.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.sayler.bonjourmadame.util.Constants;

/**
 * Created by lchromy on 22.07.15.
 */
public class SelectableImageView extends ImageView {
  public SelectableImageView(Context context) {
    super(context);
  }

  public SelectableImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public SelectableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public SelectableImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  @Override
  public void setActivated(boolean activated) {
    super.setActivated(activated);
    if (activated) {
      ObjectAnimator.ofFloat(this, "scaleX", 1f, 0.9f).setDuration(Constants.DURATION_SHORT).start();
      ObjectAnimator.ofFloat(this, "scaleY", 1f, 0.9f).setDuration(Constants.DURATION_SHORT).start();
    } else {
      ObjectAnimator.ofFloat(this, "scaleX", 0.9f, 1f).setDuration(Constants.DURATION_SHORT).start();
      ObjectAnimator.ofFloat(this, "scaleY", 0.9f, 1f).setDuration(Constants.DURATION_SHORT).start();
      this.setColorFilter(null);
    }
  }
}
