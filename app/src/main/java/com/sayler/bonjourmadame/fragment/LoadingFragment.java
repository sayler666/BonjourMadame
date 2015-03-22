package com.sayler.bonjourmadame.fragment;

import android.animation.ArgbEvaluator;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.sayler.bonjourmadame.R;
import com.sayler.bonjourmadame.activity.MainActivity;
import com.sayler.bonjourmadame.network.model.BaseParseResponse;
import com.sayler.bonjourmadame.network.model.MadameDto;
import com.sayler.bonjourmadame.util.ActionButtonHelper;
import com.sayler.bonjourmadame.util.ActionButtonLocationEmum;
import com.sayler.bonjourmadame.widget.CircularReveal;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import org.michaelevans.colorart.library.ColorArt;
import rx.Observable;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.util.concurrent.TimeUnit;

public class LoadingFragment extends BaseFragment {

  private static final String TAG = "LoadingFragment";
  @InjectView(R.id.toolbar) Toolbar toolbar;
  @InjectView(R.id.button_container) RelativeLayout buttonContainer;
  @InjectView(R.id.action_button) ImageButton actionButton;
  @InjectView(R.id.circural_reveal) CircularReveal circularReveal;
  @InjectView(R.id.progress_bar_circle) ProgressBar progressBarCircle;
  @InjectView(R.id.mainContainer) RelativeLayout mainContainer;
  @InjectView(R.id.loadedMadameImageView) ImageView loadedMadameImageView;
  private Animation actionButtonZoomInAnimation;
  private Animation actionButtonZoomOutAnimation;
  private Animation toolbarDropOutAnimation;
  private Animation toolbarDropInAnimation;
  private Animation actionButtonRevealAnimation;
  private ObjectAnimator actionButtonLoadingColorAnimator;
  private boolean isLoading = true;

  public LoadingFragment() {
  }

  @Override
  public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                           Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.f_loading, container, false);

    ButterKnife.inject(this, rootView);

    return rootView;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    setupViews();
  }

  private void setupViews() {

    setupToolbar();

    setupLayoutTransition(mainContainer);

    setupOvalOutline(actionButton, getResources().getDimensionPixelSize(R.dimen.diameter));

    setupAnimation();

    startLoading();

  }

  private void setupToolbar() {
    toolbar.setTitle(R.string.app_name);
    getActivity().setActionBar(toolbar);
  }

  @OnClick(R.id.action_button)
  public void onActionButtonClick() {
    if (!isLoading) {
      startLoading();
    }
    isLoading = !isLoading;
  }

  private void startLoading() {
    /**
     * hide main content
     */
    isLoading = true;
    circularReveal.hide(true);
    loadingStartAnimations();

    AppObservable.bindFragment(this, ((MainActivity) getBaseActivity()).getBonjourMadameAPI().getRandomMadame())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(this::onImageRequestFinishSuccessful, this::onErrorLoading);
  }

  /* ------------------------------------ REQUEST CALLBACKS ----------------------------------------------------------*/

  private void onErrorLoading(Throwable throwable) {
    Log.e(TAG, throwable.toString(), throwable);
    onLoadingFinishFailure();
  }

  private void onImageRequestFinishSuccessful(BaseParseResponse<MadameDto> baseParseResponse) {
    Log.d(TAG, baseParseResponse.getResult().url);
    Picasso.with(getBaseActivity()).load(baseParseResponse.getResult().url).into(imageDownloadTarget);

  }

  private Target imageDownloadTarget = new Target() {
    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

      updateThemeColorsFromBitmap(bitmap);

      Observable<Bitmap> messageObservable = Observable.just(bitmap);
      AppObservable.bindFragment(LoadingFragment.this, messageObservable)
          .delay(800, TimeUnit.MILLISECONDS)
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(LoadingFragment.this::onLoadingFinishSuccessful);
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {
      onLoadingFinishFailure();
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
      //not used
    }
  };

  /* ------------------------------------ LOADING CALLBACKS ----------------------------------------------------------*/

  private void onLoadingFinishFailure() {
    //TODO handle exception
    circularReveal.reveal(true);
    loadingFinishAnimations();
  }

  private void onLoadingFinishSuccessful(Bitmap bitmap) {
    /**
     * set the image abd reveal main content
     */
    isLoading = false;
    loadedMadameImageView.setImageBitmap(bitmap);
    circularReveal.reveal(true);
    loadingFinishAnimations();
  }

  /* ------------------------------------ ANIMATIONS -----------------------------------------------------------------*/

  private void updateThemeColorsFromBitmap(Bitmap bitmap) {
    ColorArt colorArt = new ColorArt(bitmap);
    toolbar.setBackgroundColor(colorArt.getBackgroundColor());
    Window window = getBaseActivity().getWindow();
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    window.setStatusBarColor(colorArt.getBackgroundColor());
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
    actionButtonLoadingColorAnimator = ObjectAnimator.ofArgb(actionButton, "backgroundColor", getResources().getColor(R.color.mainLight), getResources().getColor(R.color.mainLight2), getResources().getColor(R.color.mainLight));
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

  private void loadingStartAnimations() {
    ActionButtonHelper.setActionButtonPosition(buttonContainer, ActionButtonLocationEmum.CENTER);
    circularReveal.hide(true);

    progressBarCircle.setVisibility(View.VISIBLE);

    actionButtonLoadingColorAnimator.start();
    buttonContainer.startAnimation(actionButtonZoomInAnimation);
    actionButton.setImageDrawable(getActivity().getDrawable(android.R.color.transparent));
    actionButton.setElevation(getResources().getDimension(R.dimen.elevation_high));
    toolbar.startAnimation(toolbarDropOutAnimation);
  }

  private void loadingFinishAnimations() {
    ActionButtonHelper.setActionButtonPosition(buttonContainer, ActionButtonLocationEmum.BOTTOM_RIGHT);
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

}
