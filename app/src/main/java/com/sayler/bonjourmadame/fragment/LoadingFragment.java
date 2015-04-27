package com.sayler.bonjourmadame.fragment;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.sayler.bonjourmadame.R;
import com.sayler.bonjourmadame.activity.MainActivity;
import com.sayler.bonjourmadame.network.model.BaseParseResponse;
import com.sayler.bonjourmadame.network.model.MadameDto;
import com.sayler.bonjourmadame.util.ActionButtonHelper;
import com.sayler.bonjourmadame.util.ActionButtonLocation;
import com.sayler.bonjourmadame.util.ColorUtils;
import com.sayler.bonjourmadame.widget.ActionButton;
import com.sayler.bonjourmadame.widget.CircularReveal;
import com.sayler.bonjourmadame.widget.RefreshActionButton;
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
  @InjectView(R.id.refreshActionButton)
  RefreshActionButton refreshActionButton;
  @InjectView(R.id.setWallpaperActionButton)
  ActionButton setWallpaperActionButton;
  @InjectView(R.id.shareImageActionButton)
  ActionButton shareImageActionButton;
  @InjectView(R.id.circuralReveal)
  CircularReveal circularReveal;
  @InjectView(R.id.mainContainer)
  RelativeLayout mainContainer;
  @InjectView(R.id.loadedMadameImageView)
  ImageView loadedMadameImageView;
  private boolean isLoading = true;
  private MainActivity mainActivity;

  /* ---------------------------------------------- LIFECYCLE METHODS ------------------------------------------------*/

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
    mainActivity = (MainActivity) getActivity();
    setupViews();
    startLoading();
  }

  private void setupViews() {
    setupLayoutTransition(mainContainer);
  }

  private void startLoading() {
    /**
     * hide main content
     */
    isLoading = true;
    loadingStartAnimations();

    AppObservable.bindFragment(this, ((MainActivity) getBaseActivity()).getBonjourMadameAPI().getRandomMadame())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(this::onImageRequestFinishSuccessful, this::onErrorLoading);
  }

  /* ------------------------------------ ON CLICK CALLBACKS ---------------------------------------------------------*/

  @OnClick(R.id.action_button)
  public void onActionButtonClick() {
    if (!isLoading) {
      startLoading();
      isLoading = !isLoading;
    }
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
      onLoadingFinishSuccessful(bitmap);
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
    isLoading = false;
    circularReveal.reveal(true, false);
    loadingFinishAnimations();
  }

  private void onLoadingFinishSuccessful(Bitmap bitmap) {
    /**
     * set image and reveal main content
     */
    isLoading = false;
    loadedMadameImageView.setImageBitmap(bitmap);

    Observable<Integer> messageObservable = Observable.just(1);
    AppObservable.bindFragment(LoadingFragment.this, messageObservable)
        .delay(500, TimeUnit.MILLISECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(i -> loadingFinishAnimations());
  }

  /* ------------------------------------ ANIMATIONS -----------------------------------------------------------------*/

  private void updateThemeColorsFromBitmap(Bitmap bitmap) {
    ColorArt colorArt = new ColorArt(bitmap);
    int darkenColor = ColorUtils.darkenColor(colorArt.getBackgroundColor());

    getBaseActivity().animateStatusBarColor(darkenColor, 1500);
    getBaseActivity().animateNavigationBarColor(darkenColor, 1500);

    mainActivity.getToolbar().setBackgroundColor(colorArt.getBackgroundColor());
    mainActivity.getToolbar().setTitleTextColor(colorArt.getDetailColor());

    refreshActionButton.setTint(colorArt.getDetailColor());
    refreshActionButton.setBackgroundColorAfterFinishLoading(darkenColor);
    refreshActionButton.setRippleDrawableAfterFinishLoading(darkenColor, colorArt.getDetailColor());
    refreshActionButton.setStrokeGradientAfterFinishLoading(colorArt.getDetailColor(), darkenColor);
    refreshActionButton.setLoadingColors(darkenColor,colorArt.getBackgroundColor());

    setWallpaperActionButton.setTint(colorArt.getDetailColor());
    setWallpaperActionButton.setActionBackground(setWallpaperActionButton.prepareRippleDrawable(darkenColor, colorArt.getDetailColor()));
    setWallpaperActionButton.setStrokeGradient(setWallpaperActionButton.prepareStrokeGradient(colorArt.getDetailColor(), darkenColor));

    shareImageActionButton.setTint(colorArt.getDetailColor());
    shareImageActionButton.setActionBackground(shareImageActionButton.prepareRippleDrawable(darkenColor, colorArt.getDetailColor()));
    shareImageActionButton.setStrokeGradient(shareImageActionButton.prepareStrokeGradient(colorArt.getDetailColor(), darkenColor));

    circularReveal.setFillColorAfterFinishAnimation(colorArt.getBackgroundColor());
  }

  private void setupLayoutTransition(RelativeLayout mainContainer) {
    LayoutTransition layoutTransition = mainContainer.getLayoutTransition();
    layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
    layoutTransition.setDuration(1500);
    layoutTransition.setInterpolator(LayoutTransition.CHANGING, new OvershootInterpolator());
  }

  private void loadingStartAnimations() {
    circularReveal.hide(true,true);
    refreshActionButton.loadingStartAnimation();
    mainActivity.hideToolbar();

    ObjectAnimator.ofFloat(setWallpaperActionButton, "alpha", 1, 0).setDuration(500).start();
    ObjectAnimator.ofFloat(shareImageActionButton, "alpha", 1, 0).setDuration(500).start();

    ActionButtonLocation actionButtonLocation = new ActionButtonLocation.ActionButtonLocationBuilder()
        .addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, null)
        .removeRule(RelativeLayout.ABOVE).build();

    ActionButtonHelper.setActionButtonPosition(refreshActionButton, ActionButtonHelper.ActionButtonLocationEnum.CENTER);
    ActionButtonHelper.setActionButtonPosition(setWallpaperActionButton, actionButtonLocation);
    ActionButtonHelper.setActionButtonPosition(shareImageActionButton, actionButtonLocation);
  }

  private void loadingFinishAnimations() {
    circularReveal.reveal(true, false);
    refreshActionButton.loadingFinishAnimation();
    mainActivity.showToolbar();

    ObjectAnimator.ofFloat(setWallpaperActionButton, "alpha", 0, 1).setDuration(500).start();
    ObjectAnimator.ofFloat(shareImageActionButton, "alpha", 0, 1).setDuration(500).start();

    ActionButtonLocation setWallpaperLocation = new ActionButtonLocation.ActionButtonLocationBuilder()
        .addRule(RelativeLayout.ABOVE, refreshActionButton.getId())
        .removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM).build();

    ActionButtonLocation shareImageLocation = new ActionButtonLocation.ActionButtonLocationBuilder()
        .addRule(RelativeLayout.ABOVE, setWallpaperActionButton.getId())
        .removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM).build();

    ActionButtonHelper.setActionButtonPosition(refreshActionButton, ActionButtonHelper.ActionButtonLocationEnum.BOTTOM_RIGHT);
    ActionButtonHelper.setActionButtonPosition(setWallpaperActionButton, setWallpaperLocation);
    ActionButtonHelper.setActionButtonPosition(shareImageActionButton, shareImageLocation);
  }
}
