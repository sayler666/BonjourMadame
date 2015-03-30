package com.sayler.bonjourmadame.fragment;

import android.animation.LayoutTransition;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import com.sayler.bonjourmadame.widget.CircularReveal;
import com.sayler.bonjourmadame.widget.MainActionButton;
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
  @InjectView(R.id.actionButton) MainActionButton mainActionButton;
  @InjectView(R.id.circural_reveal) CircularReveal circularReveal;
  @InjectView(R.id.mainContainer) RelativeLayout mainContainer;
  @InjectView(R.id.loadedMadameImageView) ImageView loadedMadameImageView;
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
    circularReveal.reveal(true);
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
    int darkenColor = darkenColor(colorArt.getBackgroundColor());

    mainActivity.getToolbar().setBackgroundColor(colorArt.getBackgroundColor());
    mainActivity.getToolbar().setTitleTextColor(colorArt.getDetailColor());
    mainActionButton.setDefaultColor(darkenColor);
    mainActionButton.setTint(colorArt.getDetailColor());

    getBaseActivity().animateStatusBarColor(darkenColor, 1000);
    getBaseActivity().animateNavigationBarColor(darkenColor, 1000);
  }

  private int darkenColor(int color) {
    float[] hsv = new float[3];
    Color.colorToHSV(color, hsv);
    hsv[2] *= 0.8f;
    hsv[1] *= 1.4f;
    color = Color.HSVToColor(hsv);
    return color;
  }

  private void setupLayoutTransition(RelativeLayout mainContainer) {
    LayoutTransition layoutTransition = mainContainer.getLayoutTransition();
    layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
    layoutTransition.setDuration(1000);
    layoutTransition.setInterpolator(LayoutTransition.CHANGING, new OvershootInterpolator());
  }

  private void loadingStartAnimations() {
    circularReveal.hide(true);
    ActionButtonHelper.setActionButtonPosition(mainActionButton, ActionButtonHelper.ActionButtonLocationEnum.CENTER);
    mainActionButton.loadingStartAnimation();
    mainActivity.hideToolbar();
  }

  private void loadingFinishAnimations() {
    circularReveal.reveal(true);
    ActionButtonHelper.setActionButtonPosition(mainActionButton, ActionButtonHelper.ActionButtonLocationEnum.BOTTOM_RIGHT);
    mainActionButton.loadingFinishAnimation();
    mainActivity.showToolbar();
  }
}
