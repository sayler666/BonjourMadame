/**
 * Created by Lukasz Chromy on 13.01.14.
 *
 * Copyright 2014 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.bonjourmadame;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;

public class CircularReveal extends View implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {

  public static final float STROKE_WIDTH_FACTOR = 1.3f;
  private Mode MODE = Mode.HIDE;

  private int durationReveal = 2200;
  private int durationHide = 2500;
  private float progress = 100f;
  //Sizes (with defaults)
  private int layoutHeight = 0;

  private int layoutWidth = 0;
  private int longerSide;
  //Colors (with defaults)
  private int barColor = 0xAA000000;

  //Paints
  private Paint barPaint = new Paint();
  private Paint circlePaint = new Paint();

  private Paint fillPaint;
  private Paint clearPaint;
  private Paint defaultPaint;
  //rect
  private RectF fullCircle = new RectF();

  private Handler spinHandler = new Handler() {

    @Override
    public void handleMessage(Message msg) {
      invalidate();
    }
  };
  private long toClear = -1;

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

  //----------------------------------

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
    barPaint.setColor(barColor);
    barPaint.setAntiAlias(true);
    barPaint.setStyle(Style.FILL);

    circlePaint.setColor(barColor);
    circlePaint.setAntiAlias(true);
    circlePaint.setStrokeWidth(longerSide * STROKE_WIDTH_FACTOR);
    circlePaint.setStyle(Style.STROKE);

    clearPaint = new Paint();
    clearPaint.setAntiAlias(false);
    clearPaint.setColor(0xFFFFFFFF);
    clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

    fillPaint = new Paint();
    fillPaint.setAntiAlias(true);
    fillPaint.setColor(barPaint.getColor());

    defaultPaint = new Paint();
    defaultPaint.setAntiAlias(true);

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

    barColor = a.getColor(R.styleable.CircularReveal_barColor, barColor);

    a.recycle();
  }

  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    fullCircle.left = layoutWidth / 2 - longerSide * (getProgress() / 100f);
    fullCircle.top = layoutHeight / 2 - longerSide * (getProgress() / 100f);
    fullCircle.right = layoutWidth / 2 + longerSide * (getProgress() / 100f);
    fullCircle.bottom = layoutHeight / 2 + longerSide * (getProgress() / 100f);
    switch (MODE) {
      case REVEAL:

        // fix double buffer
        if (toClear > 0) {
          canvas.drawColor(barColor);
          toClear--;
        } else if (toClear == 0) {
          circlePaint.setStrokeWidth(circlePaint.getStrokeWidth() - getProgress());
          canvas.drawArc(fullCircle, 0, 360, false, circlePaint);
        } else {
          canvas.drawColor(android.R.color.transparent);
        }
        break;
      case HIDE:
        // fix double buffer
        if (toClear > 0) {
          canvas.drawColor(android.R.color.transparent);
          toClear--;
        } else if (toClear == 0) {
          canvas.drawOval(fullCircle, barPaint);
        } else {
          canvas.drawColor(barColor);
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
    MODE = Mode.HIDE;
    startAnimation(withAnimation);
  }

  public void reveal(boolean withAnimation) {
    MODE = Mode.REVEAL;
    startAnimation(withAnimation);
  }

  public void toggle(boolean withAnimation) {
    if (MODE == Mode.HIDE) {
      MODE = Mode.REVEAL;
    } else {
      MODE = Mode.HIDE;
    }

    startAnimation(withAnimation);
  }

  private void startAnimation(boolean withAnimation) {
    if (withAnimation) {
      ValueAnimator animation = ValueAnimator.ofFloat(0f, 100f);

      switch (MODE) {
        case REVEAL:
          animation.setDuration(durationReveal);
          toClear = 2;
          circlePaint.setStrokeWidth(longerSide * STROKE_WIDTH_FACTOR);
          break;
        case HIDE:
          animation.setDuration(durationHide);
          toClear = 2;
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