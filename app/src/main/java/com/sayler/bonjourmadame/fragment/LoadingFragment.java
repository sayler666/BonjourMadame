package com.sayler.bonjourmadame.fragment;

import android.animation.*;
import android.app.Fragment;
import android.graphics.Outline;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toolbar;
import com.sayler.bonjourmadame.widget.CircularReveal;
import com.sayler.bonjourmadame.R;


public class LoadingFragment extends Fragment {

  private boolean isLoading = true;
  private View rootView;
  private Toolbar toolbar;
  private RelativeLayout buttonContainer;
  private ImageButton actionButton;
  private CircularReveal circularReveal;
  private ProgressBar progressBarCircle;
  private Animation actionButtonZoomInAnimation;
  private Animation actionButtonZoomOutAnimation;
  private Animation toolbarDropOutAnimation;
  private Animation toolbarDropInAnimation;
  private Animation actionButtonRevealAnimation;
  private ObjectAnimator actionButtonLoadingColorAnimator;
  private Handler finishLoadingHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
      onLoadingFinish();
    }
  };

  public LoadingFragment() {
  }

  @Override
  public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                           Bundle savedInstanceState) {
    rootView = inflater.inflate(R.layout.f_loading, container, false);

    setupToolbar();
    setupViews();

    return rootView;
  }

  private void setupViews() {
    buttonContainer = (RelativeLayout) rootView.findViewById(R.id.button_container);
    actionButton = (ImageButton) rootView.findViewById(R.id.action_button);
    circularReveal = (CircularReveal) rootView.findViewById(R.id.circural_reveal);
    progressBarCircle = (ProgressBar) rootView.findViewById(R.id.progress_bar_circle);
    RelativeLayout mainContainer = (RelativeLayout) rootView.findViewById(R.id.mainContainer);

    /**
     * layout transition for action button animation
     */
    setupLayoutTransition(mainContainer);

    /**
     * action button outline
     */
    setupOvalOutline(actionButton, getResources().getDimensionPixelSize(R.dimen.diameter));

    /**
     * animations
     */
    setupAnimation();

    /**
     * on action button click listener
     */
    actionButton.setOnClickListener(actionButtonClickListener);

    /**
     * start loading on enter app
     */
    startLoading();

    //TODO delete it (mock loading time)
    finishLoadingHandler.sendEmptyMessageDelayed(1, 3000);
  }

  private void startLoading() {
    /**
     * hide main content
     */
    circularReveal.hide(true);
    loadingStartAnimations();

    isLoading = true;
  }

  private void onLoadingFinish() {

    /**
     * reveal main content
     */
    circularReveal.reveal(true);
    loadingFinishAnimations();

    isLoading = false;
  }

  private void setupAnimation() {
    actionButtonRevealAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.reveal);
    actionButtonZoomInAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.zoom_in);
    actionButtonZoomOutAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.zoom_out);
    toolbarDropOutAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.drop_out);
    toolbarDropInAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.drop_in);

    /**
     * action button loading state color animation
     */
    actionButtonLoadingColorAnimator = ObjectAnimator.ofArgb(actionButton, "backgroundColor", getResources().getColor(R.color.amberLight), getResources().getColor(R.color.amberLight2), getResources().getColor(R.color.amberLight));
    actionButtonLoadingColorAnimator.setEvaluator(new ArgbEvaluator());
    actionButtonLoadingColorAnimator.setDuration(1500);
    actionButtonLoadingColorAnimator.setRepeatCount(ValueAnimator.INFINITE);
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

  private void setupLayoutTransition(RelativeLayout mainContainer) {
    LayoutTransition layoutTransition = mainContainer.getLayoutTransition();
    layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
    layoutTransition.setDuration(1000);
    layoutTransition.setInterpolator(LayoutTransition.CHANGING, new OvershootInterpolator());
  }

  private void setupToolbar() {
    toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
    toolbar.setTitle(R.string.app_name);
    getActivity().setActionBar(toolbar);
  }

  private View.OnClickListener actionButtonClickListener = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      if (isLoading) {
        loadingFinishAnimations();
      } else {
        loadingStartAnimations();
      }
      isLoading = !isLoading;
    }
  };

  private void loadingStartAnimations() {
    setActionButtonPosition(buttonContainer, ActionButtonLocation.CENTER);
    circularReveal.hide(true);

    progressBarCircle.setVisibility(View.VISIBLE);

    actionButtonLoadingColorAnimator.start();
    buttonContainer.startAnimation(actionButtonZoomInAnimation);
    actionButton.setImageDrawable(getActivity().getDrawable(android.R.color.transparent));
    actionButton.setElevation(getResources().getDimension(R.dimen.elevation_high));
    toolbar.startAnimation(toolbarDropOutAnimation);
  }

  private void loadingFinishAnimations() {
    setActionButtonPosition(buttonContainer, ActionButtonLocation.BOTTOM_RIGHT);
    circularReveal.reveal(true);

    progressBarCircle.setVisibility(View.GONE);
    actionButtonRevealAnimation.cancel();

    actionButtonLoadingColorAnimator.end();
    actionButton.setBackground(getResources().getDrawable(R.drawable.oval, getActivity().getTheme()));
    actionButton.setElevation(getResources().getDimension(R.dimen.elevation_low));
    buttonContainer.startAnimation(actionButtonZoomOutAnimation);
    actionButton.setImageDrawable(getActivity().getDrawable(android.R.drawable.ic_input_add));
    toolbar.startAnimation(toolbarDropInAnimation);
  }

  /**
   * set action button position in parent container
   *
   * @param buttonContainer action button
   * @param location        location to be set
   */
  private void setActionButtonPosition(View buttonContainer, ActionButtonLocation location) {
    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) buttonContainer.getLayoutParams();

    /**
     * add rules
     */
    for (int rule : location.getRulesToAdd()) {
      layoutParams.addRule(rule);
    }

    /**
     * remove rules
     */
    for (int rule : location.getRulesToRemove()) {
      layoutParams.removeRule(rule);
    }

    /**
     * set layout params
     */
    buttonContainer.setLayoutParams(layoutParams);
  }

  private enum ActionButtonLocation {
    BOTTOM_RIGHT(new int[]{RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.ALIGN_PARENT_RIGHT}, new int[]{RelativeLayout.CENTER_IN_PARENT}),
    CENTER(new int[]{RelativeLayout.CENTER_IN_PARENT}, new int[]{RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.ALIGN_PARENT_RIGHT});

    final private int[] rulesToAdd;
    final private int[] rulesToRemove;

    public int[] getRulesToRemove() {
      return rulesToRemove.clone();
    }

    public int[] getRulesToAdd() {
      return rulesToAdd.clone();
    }

    ActionButtonLocation(int[] rulesToAdd, int[] rulesToRemove) {
      this.rulesToAdd = rulesToAdd;
      this.rulesToRemove = rulesToRemove;
    }
  }
}
