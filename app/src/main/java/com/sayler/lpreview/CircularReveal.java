/**
 * Created by Lukasz Chromy on 13.01.14.
 *
 * Copyright 2014 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.lpreview;

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

  private Mode MODE = Mode.HIDE;

  private int duration = 2000;
  private int progress = 100;

  //Sizes (with defaults)
  private int layoutHeight = 0;
  private int layoutWidth = 0;
  private int longerSide;

  //Colors (with defaults)
  private int barColor = 0xAA000000;

  //Paints
  private Paint barPaint = new Paint();
  private Paint fillPaint;
  private Paint clearPaint;
  private Paint defaultPaint;

  //rect
  private RectF fullCircle = new RectF();

  //bitmaps canvas
  private Bitmap tmpBitmap;
  private Canvas tmpCanvas;

  private Handler spinHandler = new Handler() {

    @Override
    public void handleMessage(Message msg) {
      invalidate();
    }
  };

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
  //Setting up stuff
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

    clearPaint = new Paint();
    clearPaint.setAntiAlias(true);
    clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

    fillPaint = new Paint();
    fillPaint.setAntiAlias(true);
    fillPaint.setColor(barPaint.getColor());

    defaultPaint = new Paint();
    defaultPaint.setAntiAlias(true);

    tmpBitmap = Bitmap.createBitmap(layoutWidth, layoutHeight, Bitmap.Config.ARGB_8888);
    tmpCanvas = new Canvas(tmpBitmap);
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

        tmpCanvas.drawRect(0, 0, tmpCanvas.getWidth(), tmpCanvas.getHeight(), fillPaint);
        tmpCanvas.drawOval(fullCircle, clearPaint);
        canvas.drawBitmap(tmpBitmap, 0, 0, defaultPaint);
        break;
      case HIDE:
        canvas.drawOval(fullCircle, barPaint);
        break;
    }

  }

  public void resetCount() {
    setProgress(0);
    invalidate();
  }

  public void stopSpinning() {
    setProgress(0);
    spinHandler.removeMessages(0);
  }

  /**
   * Puts the view on spin MODE
   */
  public void spin() {
    spinHandler.sendEmptyMessage(0);
  }

  /**
   * Increment the progress by 1 (of 360)
   */
  public void incrementProgress() {
    setProgress(getProgress() + 1);
    if (getProgress() > 360) {
      setProgress(0);
    }
    spinHandler.sendEmptyMessage(0);
  }

  public void setProgress(int i) {
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

  private void startAnimation(boolean withAnimation) {
    if (withAnimation) {
      ValueAnimator animation = ValueAnimator.ofInt(0, 100);
      animation.setDuration(duration);
      animation.start();
      animation.setInterpolator(new OvershootInterpolator());
      animation.addUpdateListener(this);
    } else {
      setProgress(100);
    }
  }

  @Override
  public void onAnimationUpdate(ValueAnimator valueAnimator) {
    setProgress((Integer) valueAnimator.getAnimatedValue());
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

  public int getProgress() {
    return progress;
  }

  public enum Mode {
    REVEAL, HIDE;
  }

}