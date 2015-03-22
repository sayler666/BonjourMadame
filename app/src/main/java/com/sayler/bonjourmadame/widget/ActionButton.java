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
import android.content.res.TypedArray;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.sayler.bonjourmadame.R;

/**
 * TODO Add class description...
 *
 * @author sayler666
 */
public class ActionButton extends RelativeLayout {

  private final Context context;
  @InjectView(R.id.action_button) ImageButton imageButton;
  @InjectView(R.id.progress_bar_circle) ProgressBar progressBarCircle;
  private View container;
  private Drawable actionIcon;
  private Drawable actionBackground;
  private Animation actionButtonRevealAnimation;
  private Animation actionButtonZoomInAnimation;
  private Animation actionButtonZoomOutAnimation;
  private ObjectAnimator actionButtonLoadingColorAnimator;

  public ActionButton(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.context = context;

    inflateView(context);
    parseAttributes(context.obtainStyledAttributes(attrs, R.styleable.ActionButton));
    setupViews();
    setupAnimation(context);
  }

  private void inflateView(Context context) {
    container = LayoutInflater.from(context).inflate(R.layout.i_action_button, this, true);

  }

  private void parseAttributes(TypedArray a) {
    actionIcon = a.getDrawable(R.styleable.ActionButton_actionIcon);
    actionBackground = a.getDrawable(R.styleable.ActionButton_actionBackground);

    a.recycle();
  }

  private void setupViews() {
    ButterKnife.inject(this, container);
    setupOvalOutline(imageButton, getResources().getDimensionPixelSize(R.dimen.diameter));
    updateViewState();
  }

  private void updateViewState() {
    imageButton.setImageDrawable(actionIcon);
    imageButton.setBackground(actionBackground);
  }

  private void setupOvalOutline(ImageButton actionButton, final int size) {
    final ViewOutlineProvider actionButtonViewOutlineProvider = new ViewOutlineProvider() {
      @Override
      public void getOutline(View view, Outline outline) {
        outline.setOval(0, 0, size, size);
        view.setClipToOutline(true);
      }
    };
    actionButton.setOutlineProvider(actionButtonViewOutlineProvider);
  }


  /* --------------------------------------------------- ANIMATIONS --------------------------------------------------*/

  private void setupAnimation(Context context) {
    actionButtonRevealAnimation = AnimationUtils.loadAnimation(context, R.anim.reveal);
    actionButtonZoomInAnimation = AnimationUtils.loadAnimation(context, R.anim.zoom_in);
    actionButtonZoomOutAnimation = AnimationUtils.loadAnimation(context, R.anim.zoom_out);

    /**
     * action button loading state color animation
     */
    actionButtonLoadingColorAnimator = ObjectAnimator.ofArgb(imageButton, "backgroundColor", getResources().getColor(R.color.mainLight), getResources().getColor(R.color.mainLight2), getResources().getColor(R.color.mainLight));
    actionButtonLoadingColorAnimator.setEvaluator(new ArgbEvaluator());
    actionButtonLoadingColorAnimator.setDuration(1500);
    actionButtonLoadingColorAnimator.setRepeatCount(ValueAnimator.INFINITE);
  }

  public void loadingStartAnimation() {
    progressBarCircle.setVisibility(View.VISIBLE);
    actionButtonLoadingColorAnimator.start();
    container.startAnimation(actionButtonZoomInAnimation);
    imageButton.setImageDrawable(context.getDrawable(android.R.color.transparent));
    imageButton.setElevation(getResources().getDimension(R.dimen.elevation_high));
  }

  public void loadingFinishAnimation() {
    progressBarCircle.setVisibility(View.GONE);
    actionButtonRevealAnimation.cancel();
    actionButtonLoadingColorAnimator.end();
    imageButton.setBackground(getResources().getDrawable(R.drawable.oval, context.getTheme()));
    imageButton.setElevation(getResources().getDimension(R.dimen.elevation_low));
    container.startAnimation(actionButtonZoomOutAnimation);
    imageButton.setImageDrawable(context.getDrawable(android.R.drawable.ic_input_add));
  }
}
