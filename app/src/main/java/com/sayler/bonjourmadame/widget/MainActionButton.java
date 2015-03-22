/**
 * Created by sayler666 on 2015-03-22.
 * <p>
 * Copyright 2015 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.bonjourmadame.widget;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.sayler.bonjourmadame.R;

/**
 * Action button with animation control
 *
 * @author sayler666
 */
public class MainActionButton extends ActionButton {

  private Animation revealAnimation;
  private Animation zoomInAnimation;
  private Animation zoomOutAnimation;
  private ObjectAnimator loadingColorAnimator;

  public MainActionButton(Context context, AttributeSet attrs) {
    super(context, attrs);

    setupAnimation(context);
  }

  /* --------------------------------------------------- ANIMATIONS --------------------------------------------------*/

  private void setupAnimation(Context context) {
    revealAnimation = AnimationUtils.loadAnimation(context, R.anim.reveal);
    zoomInAnimation = AnimationUtils.loadAnimation(context, R.anim.zoom_in);
    zoomOutAnimation = AnimationUtils.loadAnimation(context, R.anim.zoom_out);

    /**
     * action button loading state color animation
     */
    loadingColorAnimator = ObjectAnimator.ofArgb(getImageButton(), "backgroundColor", getResources().getColor(R.color.mainLight), getResources().getColor(R.color.mainLight2), getResources().getColor(R.color.mainLight));
    loadingColorAnimator.setEvaluator(new ArgbEvaluator());
    loadingColorAnimator.setDuration(1500);
    loadingColorAnimator.setRepeatCount(ValueAnimator.INFINITE);
  }

  public void loadingStartAnimation() {
    getProgressBarCircle().setVisibility(View.VISIBLE);
    loadingColorAnimator.start();
    this.startAnimation(zoomInAnimation);
    getImageButton().setImageDrawable(getContext().getDrawable(android.R.color.transparent));
    getImageButton().setElevation(getResources().getDimension(R.dimen.elevation_high));
  }

  public void loadingFinishAnimation() {
    getProgressBarCircle().setVisibility(View.GONE);
    revealAnimation.cancel();
    loadingColorAnimator.end();
    getImageButton().setBackground(getContext().getDrawable(R.drawable.oval));
    getImageButton().setElevation(getResources().getDimension(R.dimen.elevation_low));
    this.startAnimation(zoomOutAnimation);
    getImageButton().setImageDrawable(getContext().getDrawable(android.R.drawable.ic_input_add));
  }
}
