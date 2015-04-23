/**
 * Created by sayler666 on 2015-03-22.
 * <p>
 * Copyright 2015 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.bonjourmadame.widget;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
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
public class RefreshActionButton extends ActionButton {

  private Animation revealAnimation;
  private Animation zoomInAnimation;
  private Animation zoomOutAnimation;
  private ObjectAnimator loadingColorAnimator;

  private int backgroundColor;
  private int loadingColor1 = R.color.mainLight;
  private int loadingColor2 = R.color.mainLight2;
  private ObjectAnimator backgroundColorAnimator;
  private Animation fadeOut;
  private Animation fadeIn;
  private Integer strokeColor;
  private GradientDrawable strokeGradient;
  private RippleDrawable rippleDrawable;

  public RefreshActionButton(Context context, AttributeSet attrs) {
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

  private void setupBackgroundColorAnimation() {
    backgroundColorAnimator = ObjectAnimator.ofArgb(getImageButton(), "backgroundColor", getResources().getColor(loadingColor1), backgroundColor);
    backgroundColorAnimator.setEvaluator(new ArgbEvaluator());
    backgroundColorAnimator.setDuration(1000);
    backgroundColorAnimator.setRepeatCount(0);
  }

  public void loadingStartAnimation() {
    getProgressBarCircle().startAnimation(fadeIn);
    getProgressBarCircle().setVisibility(View.VISIBLE);
    loadingColorAnimator.start();
    startAnimation(zoomInAnimation);
    getImageButton().setImageDrawable(getContext().getDrawable(android.R.color.transparent));
    getImageButton().setElevation(getResources().getDimension(R.dimen.elevation_high));
    getImageButtonStroke().setVisibility(GONE);
  }

  public void loadingFinishAnimation() {
    getProgressBarCircle().startAnimation(fadeOut);
    getProgressBarCircle().setVisibility(View.GONE);
    revealAnimation.cancel();
    loadingColorAnimator.end();
    if (backgroundColorAnimator != null) {
      backgroundColorAnimator.start();
      backgroundColorAnimator.addListener(new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
          //not used
        }

        @Override
        public void onAnimationEnd(Animator animation) {
          setUpColors();
          getImageButtonStroke().setVisibility(VISIBLE);
        }

        @Override
        public void onAnimationCancel(Animator animation) {
          //not used
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
          //not used
        }
      });
    } else {
      setUpColors();
      getImageButtonStroke().setVisibility(VISIBLE);
    }

    getImageButton().setElevation(getResources().getDimension(R.dimen.elevation_low));
    startAnimation(zoomOutAnimation);

    Drawable backgrounds[] = new Drawable[2];
    backgrounds[0] = getImageButton().getDrawable();
    backgrounds[1] = getContext().getDrawable(android.R.drawable.ic_popup_sync);
    TransitionDrawable transitionDrawable = new TransitionDrawable(backgrounds);
    getImageButton().setImageDrawable(transitionDrawable);
    transitionDrawable.startTransition(1000);
  }

  private void setUpColors() {
    if (strokeColor != null) {
      setStrokeColor(strokeColor);
    }
    if (strokeGradient != null) {
      setStrokeGradient(strokeGradient);
    }
    if (rippleDrawable != null) {
      setActionBackground(rippleDrawable);
    }
  }

  /*---------------------------------------------- GETTERS AND SETTERS -----------------------------------------------*/

  public void setBackgroundColorAfterFinishLoading(int color) {
    backgroundColor = color;
    setupBackgroundColorAnimation();
  }

  @Override
  public void setTint(int tintColor) {
    ColorStateList tintColorStateList = new ColorStateList(new int[][]{EMPTY_STATE_SET}, new int[]{tintColor});
    getImageButton().setImageTintList(tintColorStateList);
  }

  @Override
  public void setStrokeColor(int strokeColor) {
    getImageButtonStroke().setBackgroundColor(strokeColor);
  }

  @Override
  public void setStrokeGradient(GradientDrawable strokeGradient) {
    getImageButtonStroke().setBackground(strokeGradient);
  }

  @Override
  public void setActionBackground(Drawable actionBackground) {
    getImageButton().setBackground(actionBackground);
  }

  public void setStrokeGradientAfterFinishLoading(int topColor, int bottomColor) {
    strokeGradient = prepareStrokeGradient(topColor, bottomColor);
  }

  public void setStrokeColorAfterFinishLoading(int color) {
    strokeColor = color;
  }

  public void setRippleDrawableAfterFinishLoading(int normalColor, int pressedColor) {
    rippleDrawable = prepareRippleDrawable(normalColor, pressedColor);
  }

}
