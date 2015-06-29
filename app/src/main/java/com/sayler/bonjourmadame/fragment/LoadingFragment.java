package com.sayler.bonjourmadame.fragment;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sayler.bonjourmadame.R;
import com.sayler.bonjourmadame.activity.MainActivity;
import com.sayler.bonjourmadame.event.InflateDrawerFragmentEvent;
import com.sayler.bonjourmadame.event.RefreshDrawerTopImage;
import com.sayler.bonjourmadame.util.ActionButtonHelper;
import com.sayler.bonjourmadame.util.ActionButtonLocation;
import com.sayler.bonjourmadame.util.ColorUtils;
import com.sayler.bonjourmadame.util.WallpaperManager;
import com.sayler.bonjourmadame.widget.ActionButton;
import com.sayler.bonjourmadame.widget.CircularReveal;
import com.sayler.bonjourmadame.widget.RefreshActionButton;
import de.greenrobot.event.EventBus;
import entity.Madame;
import org.michaelevans.colorart.library.ColorArt;
import rx.Observable;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import uk.co.senab.photoview.PhotoViewAttacher;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LoadingFragment extends BaseFragment {

  private static final String TAG = "LoadingFragment";
  public static final int DURATION_SHORT = 500;
  public static final int DURATION_MEDIUM = 1000;
  public static final int DURATION_LONG = 1500;
  private static final String BITMAP_BUNDLE = "BITMAP_BUNDLE";
  private static final String MADAME_BUNDLE = "MADAME_BUNDLE";
  private static final String URL_BUNDLE = "URL_BUNDLE";
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
  private ColorArt currentColorArt;
  private WallpaperManager wallpaperManager;
  private String imageTransitionName;
  private View rootView;
  private Bitmap currentBitmap;
  private Madame currentMadame;

  /* ---------------------------------------------- FACTORY METHODS --------------------------------------------------*/

  public static LoadingFragment newInstanceWithUrl(String url) {
    LoadingFragment loadingFragment = new LoadingFragment();
    Bundle bundle = new Bundle();
    bundle.putSerializable(URL_BUNDLE, url);

    loadingFragment.setArguments(bundle);

    return loadingFragment;
  }

  public static LoadingFragment newInstanceWithBitmap(Bitmap bitmap, Madame madame) {
    LoadingFragment loadingFragment = new LoadingFragment();
    Bundle bundle = new Bundle();
    bundle.putParcelable(BITMAP_BUNDLE, bitmap);
    bundle.putSerializable(MADAME_BUNDLE, madame);

    loadingFragment.setArguments(bundle);

    return loadingFragment;
  }

  public static LoadingFragment newInstanceRandomLoad() {
    return new LoadingFragment();
  }

  /* ---------------------------------------------- LIFECYCLE METHODS ------------------------------------------------*/
  public void setImageTransitionName(String imageTransitionName) {
    this.imageTransitionName = imageTransitionName;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                           Bundle savedInstanceState) {
    rootView = inflater.inflate(R.layout.f_loading, container, false);
    ButterKnife.inject(this, rootView);
    return rootView;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    mainActivity = (MainActivity) getActivity();

    wallpaperManager = new WallpaperManager(mainActivity);
    setupViews();

    if (getArguments() == null) {
      startLoading();
    } else {

      if (getArguments().containsKey(BITMAP_BUNDLE)) {
        showBitmap(getArguments().getParcelable(BITMAP_BUNDLE));
        currentMadame = (Madame) getArguments().getSerializable(MADAME_BUNDLE);
      } else if (getArguments().containsKey(URL_BUNDLE)) {
        isLoading = true;
        loadingStartAnimations();
        Madame madame = new Madame();
        madame.setUrl(getArguments().getString(URL_BUNDLE));
        AppObservable.bindFragment(this, Observable.just(madame))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .delay(DURATION_SHORT, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .map(this::storeInDb)
            .subscribe(this::onImageRequestFinishSuccessful, this::onErrorLoading);
      } else {
        startLoading();
      }
    }
  }

  private void showBitmap(Bitmap bitmap) {

    hideButtons(false);
    AppObservable.bindFragment(LoadingFragment.this, Observable.just(0))
        .delay(DURATION_MEDIUM, TimeUnit.MILLISECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(i -> showButtons(true));

    currentColorArt = new ColorArt(bitmap);

    rootView.setBackgroundColor(Color.WHITE);
    ValueAnimator valueAnimator = ValueAnimator.ofArgb(Color.WHITE, currentColorArt.getDetailColor());
    valueAnimator.setDuration(DURATION_LONG);
    valueAnimator.addUpdateListener(animation -> rootView.setBackgroundColor((int) animation.getAnimatedValue()));
    valueAnimator.start();

    isLoading = false;
    loadedMadameImageView.setTransitionName(imageTransitionName);
    updateThemeColorsFromBitmap(bitmap, true);
    loadedMadameImageView.setImageBitmap(bitmap);
    circularReveal.reveal(false, true);

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

    showWallpaperSetBackToast();

    EventBus.getDefault().post(new RefreshDrawerTopImage());
  }

  private void showWallpaperSetBackToast() {
    SuperActivityToast backToast = new SuperActivityToast(getActivity(), SuperToast.Type.BUTTON);
    backToast.setText(mainActivity.getString(R.string.wallpaper_set_toast_message));
    backToast.setButtonText(mainActivity.getString(R.string.undo_toast_text));
    backToast.setButtonIcon(SuperToast.Icon.Dark.UNDO);
    backToast.setDuration(SuperToast.Duration.EXTRA_LONG);
    backToast.setOnClickWrapper(new OnClickWrapper("wallpaperSetBackToast", (view, parcelable) ->
        AppObservable.bindFragment(this, Observable.just(0))
            .observeOn(Schedulers.io())
            .doOnNext(v -> wallpaperManager.setPreviousWallpaper())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(v -> afterSettingPreviousWallpaper())));
    backToast.setOnDismissWrapper(new OnDismissWrapper("wallpaperSetBackToast", view -> afterSetWallpaperBackToastDismiss()));
    backToast.show();
  }

  private void showAddToFavouritesBackToast() {
    SuperActivityToast backToast = new SuperActivityToast(getActivity(), SuperToast.Type.BUTTON);
    backToast.setText(mainActivity.getString(R.string.wallpaper_added_to_favourites_toast_message));
    backToast.setButtonText(mainActivity.getString(R.string.undo_toast_text));
    backToast.setButtonIcon(SuperToast.Icon.Dark.UNDO);
    backToast.setDuration(SuperToast.Duration.EXTRA_LONG);
    backToast.setOnClickWrapper(new OnClickWrapper("addToFavouritesToast", (view, parcelable) -> {
      currentMadame.setIsFavourite(false);
      mainActivity.getMadameDataProvider().save(currentMadame);
    }));
    backToast.show();
  }

  private void afterSetWallpaperBackToastDismiss() {
    ObjectAnimator.ofFloat(loadedMadameImageView, "alpha", 0, 1).setDuration(DURATION_MEDIUM).start();
    loadedMadameImageView.setVisibility(View.VISIBLE);
    mainActivity.showToolbar();
    showButtons(true);
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
    clearBitmapFromHistoryFragment();
    isLoading = true;
    loadingStartAnimations();
    AppObservable.bindFragment(this, ((MainActivity) getBaseActivity()).getBonjourMadameAPI().getRandomMadame())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .map(madamDto -> mainActivity.getMadamEntityDataMapper().transform(madamDto.getResult()))
        .map(this::storeInDb)
        .subscribe(this::onImageRequestFinishSuccessful, this::onErrorLoading);
  }

  private void clearBitmapFromHistoryFragment() {
    HistoryFragment historyFragment = (HistoryFragment) getFragmentManager().findFragmentByTag(HistoryFragment.class.getSimpleName());
    if (historyFragment != null) {
      historyFragment.clearOpenedBitmap();
    } else {
      historyFragment = (HistoryFragment) getFragmentManager().findFragmentByTag(HistoryFragment.class.getSimpleName());
      if (historyFragment != null) {
        historyFragment.clearOpenedBitmap();
      }
    }
    setSharedElementReturnTransition(null);
  }

  private Madame storeInDb(Madame madame) {
    //TODO what if already stored in DB?
    mainActivity.getMadameDataProvider().save(madame);
    currentMadame = madame;
    return madame;
  }

  private void startMovingPhoto() {
    mainActivity.hideToolbar();
    hideButtons(true);
  }

  private void finishMovingPhoto() {
    mainActivity.showToolbar();
    showButtons(true);
  }

  private void hideButtons(boolean animation) {
    if (animation) {
      for (ActionButton actionButton : actionButtonList) {
        ObjectAnimator.ofFloat(actionButton, "alpha", 1, 0).setDuration(DURATION_SHORT).start();
      }
    } else {
      for (ActionButton actionButton : actionButtonList) {
        actionButton.setAlpha(0f);
      }
    }

//    AppObservable.bindFragment(this, Observable.range(0, actionButtonList.size()))
//        .map(i -> Observable.just(actionButtonList.get(i)).delay(100, TimeUnit.MILLISECONDS))
//        .concatMap(actionButtonObservable -> actionButtonObservable)
//        .observeOn(AndroidSchedulers.mainThread())
//        .subscribe(ab -> ObjectAnimator.ofFloat(ab, "alpha", 1, 0).setDuration(300).start());
  }

  private void showButtons(boolean animation) {
    if (animation) {
      for (ActionButton actionButton : actionButtonList) {
        ObjectAnimator.ofFloat(actionButton, "alpha", 0, 1).setDuration(DURATION_SHORT).start();
      }
    } else {
      for (ActionButton actionButton : actionButtonList) {
        actionButton.setAlpha(1f);
      }
    }
  }

  /* ------------------------------------ ON CLICK CALLBACKS ---------------------------------------------------------*/

  @OnClick(R.id.shareImageActionButton)
  public void onShareActionButtonClick() {
    Intent sendIntent = new Intent();
    sendIntent.setAction(Intent.ACTION_SEND);
    sendIntent.putExtra(Intent.EXTRA_TEXT, currentMadame.getUrl());
    sendIntent.setType("text/plain");
    startActivity(sendIntent);
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

  @OnClick(R.id.favouriteImageActionButton)
  public void onFavouriteImageActionButtonClick() {
    currentMadame.setIsFavourite(true);
    mainActivity.getMadameDataProvider().save(currentMadame);
    showAddToFavouritesBackToast();
  }

  /* ------------------------------------ REQUEST CALLBACKS ----------------------------------------------------------*/

  private void onErrorLoading(Throwable throwable) {
    Log.e(TAG, throwable.toString(), throwable);
    onLoadingFinishFailure();
  }

  private void onImageRequestFinishSuccessful(Madame madame) {
    Log.d(TAG, madame.getUrl());
    loadImageFromUrl(madame.getUrl());
  }

  private void loadImageFromUrl(String url) {
    ImageLoader.getInstance().loadImage(url, imageDownloadTarget);
  }

  private ImageLoadingListener imageDownloadTarget = new ImageLoadingListener() {
    @Override
    public void onLoadingStarted(String imageUri, View view) {
      //not used
    }

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
      //not used
    }

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
      updateThemeColorsFromBitmap(loadedImage, false);
      onLoadingFinishSuccessful(loadedImage);
    }

    @Override
    public void onLoadingCancelled(String imageUri, View view) {
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

    if (currentBitmap != null) {
      loadedMadameImageView.setImageBitmap(null);
      currentBitmap.recycle();
    }

    currentBitmap = bitmap;
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

  private void updateThemeColorsFromBitmap(Bitmap bitmap, boolean animation) {
    currentColorArt = new ColorArt(bitmap);
    //1, 1.4, .8 are hsv factor that will make color slightly darker
    int darkenColor = ColorUtils.amendColor(currentColorArt.getBackgroundColor(), 1f, 1.4f, 0.8f);
    getBaseActivity().animateStatusBarColor(darkenColor, DURATION_LONG);
    getBaseActivity().animateNavigationBarColor(darkenColor, DURATION_LONG);

    mainActivity.colorizeToolbar(currentColorArt.getBackgroundColor(), DURATION_LONG);
    mainActivity.colorizeToolbarIcons(currentColorArt.getDetailColor());

    refreshActionButton.setTint(currentColorArt.getDetailColor());
    refreshActionButton.setBackgroundColorAfterFinishLoading(darkenColor);
    refreshActionButton.setRippleDrawableAfterFinishLoading(darkenColor, currentColorArt.getDetailColor());
    refreshActionButton.setStrokeGradientAfterFinishLoading(currentColorArt.getDetailColor(), darkenColor);
    refreshActionButton.setLoadingColors(darkenColor, currentColorArt.getBackgroundColor());

    if (animation) {
      refreshActionButton.setActionBackground(refreshActionButton.prepareRippleDrawable(darkenColor, currentColorArt.getDetailColor()));
      refreshActionButton.setStrokeGradient(refreshActionButton.prepareStrokeGradient(currentColorArt.getDetailColor(), darkenColor));
    }

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
    currentColorArt = null;
  }

  private void settingWallpaperStartAnimation() {
    refreshActionButton.setLoadingColors(refreshActionButton.getLoadingColor1(), refreshActionButton.getLoadingColor2());
    refreshActionButton.setBackgroundColorAfterFinishLoading(refreshActionButton.getLoadingColor1());

    loadingStartAnimations();
  }

  private void settingWallpaperFinishAnimations() {
    rootView.setBackgroundColor(Color.TRANSPARENT);
    loadedMadameImageView.setVisibility(View.GONE);
    circularReveal.reveal(true, false);
    refreshActionButton.loadingFinishAnimation();

    ObjectAnimator.ofFloat(refreshActionButton, "alpha", 1, 0).setDuration(DURATION_SHORT).start();
    setButtonDefaultPosition();
  }

  private void loadingStartAnimations() {
    circularReveal.hide(true, true);
    circularReveal.setClickable(true);
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
    circularReveal.setClickable(false);
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

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    loadedMadameImageView.setImageBitmap(null);
    if (currentBitmap != null) {
      currentBitmap.recycle();
    }
    System.gc();
  }
}
