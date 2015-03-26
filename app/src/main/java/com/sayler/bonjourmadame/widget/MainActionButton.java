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
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
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

  private int defaultColor;
  private int loadingColor1 = R.color.mainLight;
  private int loadingColor2 = R.color.mainLight2;
  private ObjectAnimator colorAnimator;
  private Animation fadeOut;
  private Animation fadeIn;

  public MainActionButton(Context context, AttributeSet attrs) {
    super(context, attrs);

    setupAnimation(context);
  }

  /* --------------------------------------------------- ANIMATIONS --------------------------------------------------*/

  private void setupAnimation(Context context) {
    revealAnimation = AnimationUtils.loadAnimation(context, R.anim.reveal);
    zoomInAnimation = AnimationUtils.loadAnimation(context, R.anim.zoom_in);
    zoomOutAnimation = AnimationUtils.loadAnimation(context, R.anim.zoom_out);
    fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in);
    fadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out);

    /**
     * action button loading state color animation
     */
    loadingColorAnimator = ObjectAnimator.ofArgb(getImageButton(), "backgroundColor", getResources().getColor(loadingColor1), getResources().getColor(loadingColor2), getResources().getColor(loadingColor1));
    loadingColorAnimator.setEvaluator(new ArgbEvaluator());
    loadingColorAnimator.setDuration(1500);
    loadingColorAnimator.setRepeatCount(ValueAnimator.INFINITE);
  }

  private void setupColorChangeAnimation() {
    colorAnimator = ObjectAnimator.ofArgb(getImageButton(), "backgroundColor", getResources().getColor(loadingColor1), defaultColor);
    colorAnimator.setEvaluator(new ArgbEvaluator());
    colorAnimator.setDuration(1000);
    colorAnimator.setRepeatCount(0);
  }

  public void loadingStartAnimation() {
    getProgressBarCircle().startAnimation(fadeIn);
    getProgressBarCircle().setVisibility(View.VISIBLE);
    loadingColorAnimator.start();
    this.startAnimation(zoomInAnimation);
    getImageButton().setImageDrawable(getContext().getDrawable(android.R.color.transparent));
    getImageButton().setElevation(getResources().getDimension(R.dimen.elevation_high));
  }

  public void loadingFinishAnimation() {
    getProgressBarCircle().startAnimation(fadeIn);
    getProgressBarCircle().setVisibility(View.GONE);
    revealAnimation.cancel();
    loadingColorAnimator.end();
    if (colorAnimator != null) {
      colorAnimator.start();
    }
    getImageButton().setElevation(getResources().getDimension(R.dimen.elevation_low));
    this.startAnimation(zoomOutAnimation);

    Drawable backgrounds[] = new Drawable[2];
    backgrounds[0] = getImageButton().getDrawable();
    backgrounds[1] = getContext().getDrawable(android.R.drawable.ic_input_add);
    TransitionDrawable transitionDrawable = new TransitionDrawable(backgrounds);

    getImageButton().setImageDrawable(transitionDrawable);
    transitionDrawable.startTransition(1000);
  }

  /*---------------------------------------------- GETTERS AND SETTERS -----------------------------------------------*/

  public void setDefaultColor(int defaultColor) {
    this.defaultColor = defaultColor;
    setupColorChangeAnimation();
  }
}
