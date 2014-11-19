package com.sayler.lpreview;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class Main extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    setContentView(R.layout.activity_main);
    if (savedInstanceState == null) {
      getFragmentManager().beginTransaction()
          .add(R.id.container, new PlaceholderFragment())
          .commit();
    }

//    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
  }

  @Override
  public void finish() {
    super.finish();
    overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
  }

  public class PlaceholderFragment extends Fragment {

    private boolean center;
    private AnimatorSet colorChange;

    public PlaceholderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

      final ImageButton addButton = (ImageButton) rootView.findViewById(R.id.add_button);
      final ImageButton addButton1 = (ImageButton) rootView.findViewById(R.id.add_button_1);
      final CircularReveal progress_bar = (CircularReveal) rootView.findViewById(R.id.progress_bar);

      final ViewOutlineProvider viewOutlineProvider = new ViewOutlineProvider() {
        @Override
        public void getOutline(View view, Outline outline) {
          int size = getResources().getDimensionPixelSize(R.dimen.diameter);
          outline.setOval(0, 0, size, size);
          view.setClipToOutline(true);
        }
      };
      addButton.setOutlineProvider(viewOutlineProvider);

      RelativeLayout mainContainer = (RelativeLayout) rootView.findViewById(R.id.mainContainer);
      mainContainer.setPadding(0, 0, 0, getNavigationBarHeigth());
      LayoutTransition layoutTransition = mainContainer.getLayoutTransition();
      layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
      layoutTransition.setDuration(1000);
      layoutTransition.setInterpolator(LayoutTransition.CHANGING, new OvershootInterpolator());

      Animation actionButtonAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.reveal);
      addButton.startAnimation(actionButtonAnimation);

      colorChange = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(), R.animator.color);
      colorChange.setTarget(addButton);

      addButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

          RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) addButton.getLayoutParams();
          RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams) addButton1.getLayoutParams();

          if (center) {
            layoutParams.removeRule(RelativeLayout.CENTER_IN_PARENT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            layoutParams1.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            rootView.setBackground(getDrawable(android.R.color.transparent));
            progress_bar.reveal(true);
            center = false;
            colorChange.removeAllListeners();
            addButton.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate));
            Drawable[] layers = new Drawable[2];
            layers[0] = getDrawable(android.R.color.transparent);
            layers[1] = getDrawable(android.R.drawable.ic_delete);
            TransitionDrawable transition = new TransitionDrawable(layers);
            addButton.setImageDrawable(transition);
            transition.startTransition(500);
          } else {
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            layoutParams1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            progress_bar.hide(true);
            center = true;
            addButton.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.loader));
            colorChange.start();
            colorChange.addListener(new Animator.AnimatorListener() {
              @Override
              public void onAnimationStart(Animator animator) {

              }

              @Override
              public void onAnimationEnd(Animator animator) {
                colorChange.start();
              }

              @Override
              public void onAnimationCancel(Animator animator) {

              }

              @Override
              public void onAnimationRepeat(Animator animator) {

              }

            });
            Drawable[] layers = new Drawable[2];
            layers[0] = getDrawable(android.R.color.transparent);
            layers[1] = getDrawable(android.R.drawable.ic_dialog_info);
            TransitionDrawable transition = new TransitionDrawable(layers);
            addButton.setImageDrawable(transition);
            transition.startTransition(500);
          }
          addButton.setLayoutParams(layoutParams);

        }
      });
      progress_bar.reveal(false);
      return rootView;
    }

    int getNavigationBarHeigth() {
      Resources resources = Main.this.getResources();
      int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
      if (resourceId > 0) {
        return resources.getDimensionPixelSize(resourceId);
      }
      return 0;
    }

  }
}
