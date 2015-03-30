/**
 * Created by sayler666 on 2015-03-22.
 * <p>
 * Copyright 2015 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.bonjourmadame.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.sayler.bonjourmadame.R;

/**
 * Floating action button with progress circle function
 *
 * @author sayler666
 */
public class ActionButton extends RelativeLayout {

  private ImageButton imageButton;
  private ProgressBar progressBarCircle;
  private Drawable actionBackground;
  private Drawable actionIcon;
  private int diameter;
  private int tint;
  private Integer actionBackgroundColor;

  public ActionButton(Context context, AttributeSet attrs) {
    super(context, attrs);
    inflateView(context);
    parseAttributes(context.obtainStyledAttributes(attrs, R.styleable.ActionButton));
    setupViews();
  }

  protected void inflateView(Context context) {
    LayoutInflater.from(context).inflate(R.layout.i_action_button, this, true);
  }

  protected void parseAttributes(TypedArray a) {
    actionIcon = a.getDrawable(R.styleable.ActionButton_actionIcon);
    actionBackground = a.getDrawable(R.styleable.ActionButton_actionBackground);
    diameter = (int) a.getDimension(R.styleable.ActionButton_diameter, 56);
    tint = a.getColor(R.styleable.ActionButton_tint, 0xffffffff);
    a.recycle();
  }

  protected void setupViews() {
    imageButton = (ImageButton) this.findViewById(R.id.action_button);
    progressBarCircle = (ProgressBar) this.findViewById(R.id.progress_bar_circle);
    setupOvalOutline(imageButton, diameter);
    initSize();
    updateViewState();
  }

  private void initSize() {
    LayoutParams imageButtonLayoutParams = (LayoutParams) imageButton.getLayoutParams();
    imageButtonLayoutParams.height = imageButtonLayoutParams.width = diameter;
    imageButton.setLayoutParams(imageButtonLayoutParams);

    LayoutParams progressLayoutParams = (LayoutParams) imageButton.getLayoutParams();
    progressLayoutParams.height = progressLayoutParams.width = diameter;
    imageButton.setLayoutParams(progressLayoutParams);
  }

  protected void updateViewState() {
    imageButton.setImageDrawable(actionIcon);
    imageButton.setBackground(actionBackground);
    ColorStateList tintColorStateList = new ColorStateList(new int[][]{EMPTY_STATE_SET}, new int[]{tint});
    imageButton.setImageTintList(tintColorStateList);
    if (actionBackgroundColor != null) {
      imageButton.setBackgroundColor(actionBackgroundColor);
    }
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

  /* ------------------------------------------- GETTERS SETTERS -----------------------------------------------------*/

  public void setActionIcon(Drawable actionIcon) {
    this.actionIcon = actionIcon;
    updateViewState();
  }

  public void setActionBackground(Drawable actionBackground) {
    this.actionBackground = actionBackground;
    updateViewState();
  }

  public void setActionBackgroundColor(int color) {
    actionBackgroundColor = color;
    updateViewState();
  }

  public void setTint(int tint) {
    this.tint = tint;
    updateViewState();
  }

  public ProgressBar getProgressBarCircle() {
    return progressBarCircle;
  }

  public ImageButton getImageButton() {
    return imageButton;
  }

}
