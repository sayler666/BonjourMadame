/**
 * Created by Lukasz Chromy on 13.01.14.
 * <p>
 * Copyright 2014 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.bonjourmadame.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;
import com.sayler.bonjourmadame.R;

public class CircularReveal extends View implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {

  public static final float STROKE_WIDTH_FACTOR = 1.3f;
  //Durations
  private int durationReveal = 2200;
  private int durationHide = 2500;
  //Sizes
  private int layoutHeight = 0;
  private int layoutWidth = 0;
  private int longerSide;

  //Colors
  private int fillColor = 0xAA000000;

  //Paints
  private Paint fullCirclePaint = new Paint();
  private Paint strokeCirclePaint = new Paint();
  //Rectangle
  private RectF fullCircle = new RectF();
  //Others
  private Mode animationMode = Mode.HIDE;
  private long timesToClear = -1;
  private float progress = 100f;
  //Handlers
  private Handler spinHandler = new Handler(new IncomingHandlerCallback());

  private class IncomingHandlerCallback implements Handler.Callback {

    @Override
    public boolean handleMessage(Message message) {
      invalidate();
      return true;
    }
  }

  /**
   * The constructor for the ProgressWheel
   *
   * @param context
   * @param attrs
   */
  public CircularReveal(Context context, AttributeSet attrs) {
    super(context, attrs);

    parseAttributes(context.obtainStyledAttributes(attrs,
        R.styleable.CircularReveal));
  }

  public int getFillColor() {
    return fillColor;
  }

  public void setFillColor(int fillColor) {
    this.fillColor = fillColor;
    setupPaints();
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);

    // Share the dimensions
    layoutWidth = w;
    layoutHeight = h;
    longerSide = layoutWidth;
    if (layoutHeight > layoutWidth) {
      longerSide = layoutHeight;
    }

    setupBounds();
    setupPaints();
    invalidate();
  }

  private void setupPaints() {
    fullCirclePaint.setColor(fillColor);
    fullCirclePaint.setAntiAlias(true);
    fullCirclePaint.setStyle(Style.FILL);

    strokeCirclePaint.setColor(fillColor);
    strokeCirclePaint.setAntiAlias(true);
    strokeCirclePaint.setStrokeWidth(longerSide * STROKE_WIDTH_FACTOR);
    strokeCirclePaint.setStyle(Style.STROKE);
  }

  /**
   * Set the bounds of the component
   */
  private void setupBounds() {
    fullCircle = new RectF(layoutWidth / 2 - longerSide, layoutHeight / 2 - longerSide, layoutWidth / 2 + longerSide, layoutHeight / 2 + longerSide);
  }

  /**
   * Parse the attributes passed to the view from the XML
   *
   * @param a the attributes to parse
   */
  private void parseAttributes(TypedArray a) {
    fillColor = a.getColor(R.styleable.CircularReveal_barColor, fillColor);
    a.recycle();
  }

  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    fullCircle.left = layoutWidth / 2 - longerSide * (getProgress() / 100f);
    fullCircle.top = layoutHeight / 2 - longerSide * (getProgress() / 100f);
    fullCircle.right = layoutWidth / 2 + longerSide * (getProgress() / 100f);
    fullCircle.bottom = layoutHeight / 2 + longerSide * (getProgress() / 100f);
    switch (animationMode) {
      case REVEAL:
        // fix double buffer
        if (timesToClear > 0) {
          canvas.drawColor(fillColor);
          timesToClear--;
        } else if (timesToClear == 0) {
          strokeCirclePaint.setStrokeWidth(strokeCirclePaint.getStrokeWidth() - getProgress());
          canvas.drawArc(fullCircle, 0, 360, false, strokeCirclePaint);
        } else {
          canvas.drawColor(android.R.color.transparent);
        }
        break;
      case HIDE:
        // fix double buffer
        if (timesToClear > 0) {
          canvas.drawColor(android.R.color.transparent);
          timesToClear--;
        } else if (timesToClear == 0) {
          canvas.drawOval(fullCircle, fullCirclePaint);
        } else {
          canvas.drawColor(fillColor);
        }
        break;
    }

  }

  public void setProgress(float i) {
    //value animator
    progress = i;
    postInvalidateOnAnimation();
  }

  public void hide(boolean withAnimation) {
    animationMode = Mode.HIDE;
    startAnimation(withAnimation);
  }

  public void reveal(boolean withAnimation) {
    animationMode = Mode.REVEAL;
    startAnimation(withAnimation);
  }

  public void toggle(boolean withAnimation) {
    if (animationMode == Mode.HIDE) {
      animationMode = Mode.REVEAL;
    } else {
      animationMode = Mode.HIDE;
    }

    startAnimation(withAnimation);
  }

  private void startAnimation(boolean withAnimation) {
    if (withAnimation) {
      ValueAnimator animation = ValueAnimator.ofFloat(0f, 100f);

      timesToClear = 5;
      switch (animationMode) {
        case REVEAL:
          animation.setDuration(durationReveal);
          strokeCirclePaint.setStrokeWidth(longerSide * STROKE_WIDTH_FACTOR);
          break;
        case HIDE:
          animation.setDuration(durationHide);
          break;
      }

      animation.start();
      animation.setInterpolator(new OvershootInterpolator());
      animation.addUpdateListener(this);
    } else {
      setProgress(100);
    }
  }

  @Override
  public void onAnimationUpdate(ValueAnimator valueAnimator) {
    setProgress((Float) valueAnimator.getAnimatedValue());
    spinHandler.sendEmptyMessage(0);
  }

  @Override
  public void onAnimationStart(Animator animation) {

  }

  @Override
  public void onAnimationEnd(Animator animation) {

  }

  @Override
  public void onAnimationCancel(Animator animation) {

  }

  @Override
  public void onAnimationRepeat(Animator animation) {

  }

  public float getProgress() {
    return progress;
  }

  public int getDurationReveal() {
    return durationReveal;
  }

  public void setDurationReveal(int durationReveal) {
    this.durationReveal = durationReveal;
  }

  public int getDurationHide() {
    return durationHide;
  }

  public void setDurationHide(int durationHide) {
    this.durationHide = durationHide;
  }

  public enum Mode {
    REVEAL, HIDE;
  }
}