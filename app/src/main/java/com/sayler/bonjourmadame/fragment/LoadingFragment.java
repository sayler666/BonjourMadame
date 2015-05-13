package com.sayler.bonjourmadame.fragment;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.sayler.bonjourmadame.event.InflateDrawerFragmentEvent;
import com.sayler.bonjourmadame.event.RefreshDrawerTopImage;
import com.sayler.bonjourmadame.network.model.BaseParseResponse;
import com.sayler.bonjourmadame.network.model.MadameDto;
import com.sayler.bonjourmadame.util.ActionButtonHelper;
import com.sayler.bonjourmadame.util.ActionButtonLocation;
import com.sayler.bonjourmadame.util.ColorUtils;
import com.sayler.bonjourmadame.util.WallpaperHelper;
import com.sayler.bonjourmadame.widget.ActionButton;
import com.sayler.bonjourmadame.widget.CircularReveal;
import com.sayler.bonjourmadame.widget.RefreshActionButton;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import de.greenrobot.event.EventBus;
import org.michaelevans.colorart.library.ColorArt;
import rx.Observable;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import uk.co.senab.photoview.PhotoViewAttacher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LoadingFragment extends BaseFragment {

  private static final String TAG = "LoadingFragment";
  List<ActionButton> actionButtonList = new ArrayList<>();
  @InjectView(R.id.refreshActionButton)
  RefreshActionButton refreshActionButton;
  @InjectView(R.id.setWallpaperActionButton)
  ActionButton setWallpaperActionButton;
  @InjectView(R.id.shareImageActionButton)
  ActionButton shareImageActionButton;
  @InjectView(R.id.favouriteImageActionButton)
  ActionButton favouriteImageActionButton;
  @InjectView(R.id.circuralReveal)
  CircularReveal circularReveal;
  @InjectView(R.id.mainContainer)
  RelativeLayout mainContainer;
  @InjectView(R.id.loadedMadameImageView)
  ImageView loadedMadameImageView;
  private boolean isLoading = true;
  private MainActivity mainActivity;
  private PhotoViewAttacher photoViewAttacher;

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
    setupActionButtons();
    setupPhotoView();
    setupLayoutTransition(mainContainer);
  }

  private void setupActionButtons() {
    actionButtonList.add(refreshActionButton);
    actionButtonList.add(setWallpaperActionButton);
    actionButtonList.add(favouriteImageActionButton);
    actionButtonList.add(shareImageActionButton);
  }

  private void setWallpaper() {
    if (photoViewAttacher.getVisibleRectangleBitmap() != null) {
      loadingStartAnimations();
      refreshActionButton.setLoadingColors(refreshActionButton.getLoadingColor1(), refreshActionButton.getLoadingColor2());
      refreshActionButton.setBackgroundColorAfterFinishLoading(refreshActionButton.getLoadingColor1());

      AppObservable.bindFragment(this, Observable.just(1))
          .observeOn(Schedulers.io())
          .subscribe(v -> setWallpaperOnSeparateThread());
    }
  }

  private Handler uiHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
      if (getBaseActivity() != null) {
        afterSetWallpaper();
      }
    }
  };

  private void setWallpaperOnSeparateThread() {
    WallpaperHelper.setBitmapAsWallpaper(photoViewAttacher.getVisibleRectangleBitmap(), getBaseActivity());
    uiHandler.postDelayed(() -> uiHandler.sendEmptyMessage(1), 200);
  }

  private void afterSetWallpaper() {
    loadedMadameImageView.setImageBitmap(null);
    loadedMadameImageView.setBackgroundColor(Color.TRANSPARENT);
    loadingFinishAnimations();

    EventBus.getDefault().post(new RefreshDrawerTopImage());
  }

  private void setupPhotoView() {
    photoViewAttacher = new PhotoViewAttacher(loadedMadameImageView);
    photoViewAttacher.setScaleType(ImageView.ScaleType.CENTER_CROP);
    photoViewAttacher.setAllowParentInterceptOnEdge(false);
    photoViewAttacher.setOnTouchDownListener(this::startMovingPhoto);
    photoViewAttacher.setOnTouchUpListener(this::finishMovingPhoto);
  }

  private void startMovingPhoto() {
    ((MainActivity) getActivity()).hideToolbar();
    AppObservable.bindFragment(this, Observable.from(actionButtonList))
        .doOnEach(ab -> ObjectAnimator.ofFloat(ab.getValue(), "alpha", 1, 0).setDuration(500).start());
  }

  private void finishMovingPhoto() {
    ((MainActivity) getActivity()).showToolbar();
    AppObservable.bindFragment(this, Observable.from(actionButtonList))
        .doOnEach(ab -> ObjectAnimator.ofFloat(ab.getValue(), "alpha", 0, 1).setDuration(500).start());
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

  @OnClick(R.id.refreshActionButton)
  public void onRefreshActionButtonClick() {
    if (!isLoading) {
      startLoading();
    }
  }

  @OnClick(R.id.setWallpaperActionButton)
  public void onSetWallpaperActionButtonClick() {
    setWallpaper();
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
    circularReveal.reveal(true, false);
    loadingFinishAnimations();

    afterFinishLoading();
  }

  private void onLoadingFinishSuccessful(Bitmap bitmap) {
    /**
     * set image and reveal main content
     */
    loadedMadameImageView.setImageBitmap(bitmap);
    photoViewAttacher.update();

    Observable<Integer> messageObservable = Observable.just(1);
    AppObservable.bindFragment(LoadingFragment.this, messageObservable)
        .delay(500, TimeUnit.MILLISECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(i -> loadingFinishAnimations());

    afterFinishLoading();
  }

  private void afterFinishLoading() {
    isLoading = false;
    EventBus.getDefault().post(new InflateDrawerFragmentEvent());
  }

  /* ------------------------------------ ANIMATIONS -----------------------------------------------------------------*/

  private void updateThemeColorsFromBitmap(Bitmap bitmap) {
    ColorArt colorArt = new ColorArt(bitmap);
    int darkenColor = ColorUtils.amendColor(colorArt.getBackgroundColor(), 1f, 1.4f, 0.8f);
    getBaseActivity().animateStatusBarColor(darkenColor, 1500);
    getBaseActivity().animateNavigationBarColor(darkenColor, 1500);

    mainActivity.getToolbar().setBackgroundColor(colorArt.getBackgroundColor());
    mainActivity.colorizeToolbarIcons(colorArt.getDetailColor());

    refreshActionButton.setTint(colorArt.getDetailColor());
    refreshActionButton.setBackgroundColorAfterFinishLoading(darkenColor);
    refreshActionButton.setRippleDrawableAfterFinishLoading(darkenColor, colorArt.getDetailColor());
    refreshActionButton.setStrokeGradientAfterFinishLoading(colorArt.getDetailColor(), darkenColor);
    refreshActionButton.setLoadingColors(darkenColor, colorArt.getBackgroundColor());

    setWallpaperActionButton.setTint(colorArt.getDetailColor());
    setWallpaperActionButton.setActionBackground(setWallpaperActionButton.prepareRippleDrawable(darkenColor, colorArt.getDetailColor()));
    setWallpaperActionButton.setStrokeGradient(setWallpaperActionButton.prepareStrokeGradient(colorArt.getDetailColor(), darkenColor));

    shareImageActionButton.setTint(colorArt.getDetailColor());
    shareImageActionButton.setActionBackground(shareImageActionButton.prepareRippleDrawable(darkenColor, colorArt.getDetailColor()));
    shareImageActionButton.setStrokeGradient(shareImageActionButton.prepareStrokeGradient(colorArt.getDetailColor(), darkenColor));

    favouriteImageActionButton.setTint(colorArt.getDetailColor());
    favouriteImageActionButton.setActionBackground(shareImageActionButton.prepareRippleDrawable(darkenColor, colorArt.getDetailColor()));
    favouriteImageActionButton.setStrokeGradient(shareImageActionButton.prepareStrokeGradient(colorArt.getDetailColor(), darkenColor));

    circularReveal.setFillColorAfterFinishAnimation(colorArt.getBackgroundColor());

    loadedMadameImageView.setBackgroundColor(colorArt.getDetailColor());
  }

  private void setupLayoutTransition(RelativeLayout mainContainer) {
    LayoutTransition layoutTransition = mainContainer.getLayoutTransition();
    layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
    layoutTransition.setDuration(1500);
    layoutTransition.setInterpolator(LayoutTransition.CHANGING, new OvershootInterpolator());
  }

  private void loadingStartAnimations() {
    circularReveal.hide(true, true);
    refreshActionButton.loadingStartAnimation();
    mainActivity.hideToolbar();

    ObjectAnimator.ofFloat(setWallpaperActionButton, "alpha", 1, 0).setDuration(500).start();
    ObjectAnimator.ofFloat(shareImageActionButton, "alpha", 1, 0).setDuration(500).start();
    ObjectAnimator.ofFloat(favouriteImageActionButton, "alpha", 1, 0).setDuration(500).start();

    ActionButtonLocation actionButtonLocation = new ActionButtonLocation.ActionButtonLocationBuilder()
        .addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, null)
        .removeRule(RelativeLayout.ABOVE).build();

    ActionButtonHelper.setActionButtonPosition(refreshActionButton, ActionButtonHelper.ActionButtonLocationEnum.CENTER);
    ActionButtonHelper.setActionButtonPosition(setWallpaperActionButton, actionButtonLocation);
    ActionButtonHelper.setActionButtonPosition(shareImageActionButton, actionButtonLocation);
    ActionButtonHelper.setActionButtonPosition(favouriteImageActionButton, actionButtonLocation);
  }

  private void loadingFinishAnimations() {
    circularReveal.reveal(true, false);
    refreshActionButton.loadingFinishAnimation();
    mainActivity.showToolbar();

    ObjectAnimator.ofFloat(setWallpaperActionButton, "alpha", 0, 1).setDuration(500).start();
    ObjectAnimator.ofFloat(shareImageActionButton, "alpha", 0, 1).setDuration(500).start();
    ObjectAnimator.ofFloat(favouriteImageActionButton, "alpha", 0, 1).setDuration(500).start();

    ActionButtonLocation setWallpaperLocation = new ActionButtonLocation.ActionButtonLocationBuilder()
        .addRule(RelativeLayout.ABOVE, refreshActionButton.getId())
        .removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM).build();

    ActionButtonLocation shareImageLocation = new ActionButtonLocation.ActionButtonLocationBuilder()
        .addRule(RelativeLayout.ABOVE, setWallpaperActionButton.getId())
        .removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM).build();

    ActionButtonLocation favouriteImageLocation = new ActionButtonLocation.ActionButtonLocationBuilder()
        .addRule(RelativeLayout.ABOVE, shareImageActionButton.getId())
        .removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM).build();

    ActionButtonHelper.setActionButtonPosition(refreshActionButton, ActionButtonHelper.ActionButtonLocationEnum.BOTTOM_RIGHT);
    ActionButtonHelper.setActionButtonPosition(setWallpaperActionButton, setWallpaperLocation);
    ActionButtonHelper.setActionButtonPosition(shareImageActionButton, shareImageLocation);
    ActionButtonHelper.setActionButtonPosition(favouriteImageActionButton, favouriteImageLocation);
  }
}
