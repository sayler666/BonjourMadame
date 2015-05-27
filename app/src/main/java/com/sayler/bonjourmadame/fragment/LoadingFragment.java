package com.sayler.bonjourmadame.fragment;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.content.Intent;
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
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.OnClickWrapper;
import com.github.johnpersano.supertoasts.util.OnDismissWrapper;
import com.sayler.bonjourmadame.R;
import com.sayler.bonjourmadame.activity.MainActivity;
import com.sayler.bonjourmadame.activity.TestActivity;
import com.sayler.bonjourmadame.event.InflateDrawerFragmentEvent;
import com.sayler.bonjourmadame.event.RefreshDrawerTopImage;
import com.sayler.bonjourmadame.util.ActionButtonHelper;
import com.sayler.bonjourmadame.util.ActionButtonLocation;
import com.sayler.bonjourmadame.util.ColorUtils;
import com.sayler.bonjourmadame.util.WallpaperManager;
import com.sayler.bonjourmadame.widget.ActionButton;
import com.sayler.bonjourmadame.widget.CircularReveal;
import com.sayler.bonjourmadame.widget.RefreshActionButton;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import dao.MadameDataProvider;
import de.greenrobot.event.EventBus;
import entity.Madame;
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
  public static final int DURATION_SHORT = 500;
  public static final int DURATION_MEDIUM = 1000;
  public static final int DURATION_LONG = 1500;
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
  private Bitmap currentBitmap;
  private ColorArt currentColorArt;
  private WallpaperManager wallpaperManager;

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

    wallpaperManager = new WallpaperManager(mainActivity);
    setupViews();
    startLoading();
  }

  private void setupViews() {
    setupActionButtons();
    setupPhotoView();
    setupLayoutTransition(mainContainer);
  }

  private void setupActionButtons() {
    actionButtonList.add(favouriteImageActionButton);
    actionButtonList.add(shareImageActionButton);
    actionButtonList.add(setWallpaperActionButton);
    actionButtonList.add(refreshActionButton);
  }

  private void setWallpaper() {
    if (photoViewAttacher.getVisibleRectangleBitmap() != null) {
      settingWallpaperStartAnimation();

      AppObservable.bindFragment(this, Observable.just(0))
          .observeOn(Schedulers.io())
          .doOnNext(v -> wallpaperManager.setBitmapAsWallpaperAndSavePreviousWallpaper(photoViewAttacher.getVisibleRectangleBitmap()))
          .delay(DURATION_SHORT, TimeUnit.MILLISECONDS)
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(v -> afterSetWallpaper());
    }
  }

  private void afterSetWallpaper() {
    settingWallpaperFinishAnimations();

    showBackToast();

    EventBus.getDefault().post(new RefreshDrawerTopImage());
  }

  private void showBackToast() {
    SuperActivityToast backToast = new SuperActivityToast(getActivity(), SuperToast.Type.BUTTON);
    backToast.setText(mainActivity.getString(R.string.wallpaper_set_toast_message));
    backToast.setButtonText(mainActivity.getString(R.string.undo_toast_text));
    backToast.setButtonIcon(SuperToast.Icon.Dark.UNDO);
    backToast.setDuration(SuperToast.Duration.EXTRA_LONG);
    backToast.setOnClickWrapper(new OnClickWrapper("backToast", (view, parcelable) ->
        AppObservable.bindFragment(this, Observable.just(0))
            .observeOn(Schedulers.io())
            .doOnNext(v -> wallpaperManager.setPreviousWallpaper())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(v -> afterSettingPreviousWallpaper())));
    backToast.setOnDismissWrapper(new OnDismissWrapper("backToast", view -> afterBackToastDismiss()));
    backToast.show();
  }

  private void afterBackToastDismiss() {
    ObjectAnimator.ofFloat(loadedMadameImageView, "alpha", 0, 1).setDuration(DURATION_MEDIUM).start();
    loadedMadameImageView.setVisibility(View.VISIBLE);
    mainActivity.showToolbar();
    showButtons();
  }

  private void afterSettingPreviousWallpaper() {
    EventBus.getDefault().post(new RefreshDrawerTopImage());
    Toast.makeText(mainActivity, mainActivity.getString(R.string.previous_wallpaper_set_toast_message), Toast.LENGTH_SHORT).show();
  }

  private void setupPhotoView() {
    photoViewAttacher = new PhotoViewAttacher(loadedMadameImageView);
    photoViewAttacher.setScaleType(ImageView.ScaleType.CENTER_CROP);
    photoViewAttacher.setAllowParentInterceptOnEdge(false);
    photoViewAttacher.setOnTouchDownListener(this::startMovingPhoto);
    photoViewAttacher.setOnTouchUpListener(this::finishMovingPhoto);
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
        .map(madamDto -> mainActivity.getMadamEntityDataMapper().transform(madamDto.getResult()))
        .map(this::storeInDb)
        .subscribe(this::onImageRequestFinishSuccessful, this::onErrorLoading);
  }

  private Madame storeInDb(Madame madame) {

    MadameDataProvider madameDataProvider = new MadameDataProvider(mainActivity);
    madameDataProvider.save(madame);

    return madame;
  }

  private void startMovingPhoto() {
    mainActivity.hideToolbar();
    hideButtons();
  }

  private void finishMovingPhoto() {
    mainActivity.showToolbar();
    showButtons();
  }

  private void hideButtons() {
    AppObservable.bindFragment(this, Observable.from(actionButtonList))
        .subscribe(ab -> ObjectAnimator.ofFloat(ab, "alpha", 1, 0).setDuration(DURATION_SHORT).start());

//    AppObservable.bindFragment(this, Observable.range(0, actionButtonList.size()))
//        .map(i -> Observable.just(actionButtonList.get(i)).delay(100, TimeUnit.MILLISECONDS))
//        .concatMap(actionButtonObservable -> actionButtonObservable)
//        .observeOn(AndroidSchedulers.mainThread())
//        .subscribe(ab -> ObjectAnimator.ofFloat(ab, "alpha", 1, 0).setDuration(300).start());
  }

  private void showButtons() {
    AppObservable.bindFragment(this, Observable.from(actionButtonList))
        .subscribe(ab -> ObjectAnimator.ofFloat(ab, "alpha", 0, 1).setDuration(DURATION_SHORT).start());
  }

  /* ------------------------------------ ON CLICK CALLBACKS ---------------------------------------------------------*/

  @OnClick(R.id.shareImageActionButton)
  public void onShareActionButtonClick() {
    startActivity(new Intent(getActivity(), TestActivity.class));
  }

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

  private void onImageRequestFinishSuccessful(Madame madame) {
    Log.d(TAG, madame.getUrl());
    Picasso.with(getBaseActivity()).load(madame.getUrl()).into(imageDownloadTarget);
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
        .delay(DURATION_SHORT, TimeUnit.MILLISECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(i -> loadingFinishAnimations());

    afterFinishLoading();
  }

  private void afterFinishLoading() {
    isLoading = false;
    EventBus.getDefault().post(new InflateDrawerFragmentEvent());
  }

  /* ------------------------------------ ANIMATIONS -----------------------------------------------------------------*/

  private void setupLayoutTransition(RelativeLayout mainContainer) {
    LayoutTransition layoutTransition = mainContainer.getLayoutTransition();
    layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
    layoutTransition.setDuration(DURATION_LONG);
    layoutTransition.setInterpolator(LayoutTransition.CHANGING, new OvershootInterpolator());
  }

  private void updateThemeColorsFromBitmap(Bitmap bitmap) {
    currentColorArt = new ColorArt(bitmap);
    int darkenColor = ColorUtils.amendColor(currentColorArt.getBackgroundColor(), 1f, 1.4f, 0.8f);
    getBaseActivity().animateStatusBarColor(darkenColor, DURATION_LONG);
    getBaseActivity().animateNavigationBarColor(darkenColor, DURATION_LONG);

    mainActivity.getToolbar().setBackgroundColor(currentColorArt.getBackgroundColor());
    mainActivity.colorizeToolbarIcons(currentColorArt.getDetailColor());

    refreshActionButton.setTint(currentColorArt.getDetailColor());
    refreshActionButton.setBackgroundColorAfterFinishLoading(darkenColor);
    refreshActionButton.setRippleDrawableAfterFinishLoading(darkenColor, currentColorArt.getDetailColor());
    refreshActionButton.setStrokeGradientAfterFinishLoading(currentColorArt.getDetailColor(), darkenColor);
    refreshActionButton.setLoadingColors(darkenColor, currentColorArt.getBackgroundColor());

    setWallpaperActionButton.setTint(currentColorArt.getDetailColor());
    setWallpaperActionButton.setActionBackground(setWallpaperActionButton.prepareRippleDrawable(darkenColor, currentColorArt.getDetailColor()));
    setWallpaperActionButton.setStrokeGradient(setWallpaperActionButton.prepareStrokeGradient(currentColorArt.getDetailColor(), darkenColor));

    shareImageActionButton.setTint(currentColorArt.getDetailColor());
    shareImageActionButton.setActionBackground(shareImageActionButton.prepareRippleDrawable(darkenColor, currentColorArt.getDetailColor()));
    shareImageActionButton.setStrokeGradient(shareImageActionButton.prepareStrokeGradient(currentColorArt.getDetailColor(), darkenColor));

    favouriteImageActionButton.setTint(currentColorArt.getDetailColor());
    favouriteImageActionButton.setActionBackground(shareImageActionButton.prepareRippleDrawable(darkenColor, currentColorArt.getDetailColor()));
    favouriteImageActionButton.setStrokeGradient(shareImageActionButton.prepareStrokeGradient(currentColorArt.getDetailColor(), darkenColor));

    circularReveal.setFillColorAfterFinishAnimation(currentColorArt.getBackgroundColor());

    loadedMadameImageView.setBackgroundColor(currentColorArt.getDetailColor());
  }

  private void settingWallpaperStartAnimation() {
    refreshActionButton.setLoadingColors(refreshActionButton.getLoadingColor1(), refreshActionButton.getLoadingColor2());
    refreshActionButton.setBackgroundColorAfterFinishLoading(refreshActionButton.getLoadingColor1());

    loadingStartAnimations();
  }

  private void settingWallpaperFinishAnimations() {
    loadedMadameImageView.setVisibility(View.GONE);
    circularReveal.reveal(true, false);
    refreshActionButton.loadingFinishAnimation();

    ObjectAnimator.ofFloat(refreshActionButton, "alpha", 1, 0).setDuration(DURATION_SHORT).start();
    setButtonDefaultPosition();
  }

  private void loadingStartAnimations() {
    circularReveal.hide(true, true);
    refreshActionButton.loadingStartAnimation();
    mainActivity.hideToolbar();

    ObjectAnimator.ofFloat(setWallpaperActionButton, "alpha", 1, 0).setDuration(DURATION_SHORT).start();
    ObjectAnimator.ofFloat(shareImageActionButton, "alpha", 1, 0).setDuration(DURATION_SHORT).start();
    ObjectAnimator.ofFloat(favouriteImageActionButton, "alpha", 1, 0).setDuration(DURATION_SHORT).start();

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

    ObjectAnimator.ofFloat(setWallpaperActionButton, "alpha", 0, 1).setDuration(DURATION_SHORT).start();
    ObjectAnimator.ofFloat(shareImageActionButton, "alpha", 0, 1).setDuration(DURATION_SHORT).start();
    ObjectAnimator.ofFloat(favouriteImageActionButton, "alpha", 0, 1).setDuration(DURATION_SHORT).start();

    setButtonDefaultPosition();
  }

  private void setButtonDefaultPosition() {
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
