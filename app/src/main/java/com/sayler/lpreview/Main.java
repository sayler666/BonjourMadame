package com.sayler.lpreview;

import android.animation.*;
import android.app.Activity;
import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.Outline;
import android.os.Bundle;
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
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
      final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

      final Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
      getActivity().setActionBar(toolbar);
      toolbar.setTitle(R.string.app_name);

      final RelativeLayout buttonContainer = (RelativeLayout) rootView.findViewById(R.id.button_container);
      final ImageButton addButton = (ImageButton) rootView.findViewById(R.id.add_button);
      final CircularReveal progress_bar = (CircularReveal) rootView.findViewById(R.id.progress_bar);
      final ProgressBar progressBarCircle = (ProgressBar) rootView.findViewById(R.id.progress_bar_circle);

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

      LayoutTransition layoutTransition = mainContainer.getLayoutTransition();
      layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
      layoutTransition.setDuration(1000);
      layoutTransition.setInterpolator(LayoutTransition.CHANGING, new OvershootInterpolator());

      final Animation actionButtonAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.reveal);
      buttonContainer.startAnimation(actionButtonAnimation);

      final Animation zoomButtonAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.zoom_in);
      final Animation zoomOutButtonAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.zoom_out);

      final Animation toolbarDropOutAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.drop_out);
      final Animation toolbarDropInAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.drop_in);

      final ObjectAnimator colorAnimator = ObjectAnimator.ofArgb(addButton, "backgroundColor", getResources().getColor(R.color.amberLight), getResources().getColor(R.color.amberLight2), getResources().getColor(R.color.amberLight));
      colorAnimator.setEvaluator(new ArgbEvaluator());
      colorAnimator.setDuration(1500);
      colorAnimator.setRepeatCount(ValueAnimator.INFINITE);

      addButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

          RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) buttonContainer.getLayoutParams();

          if (center) {
            center = false;
            actionButtonAnimation.cancel();
            progressBarCircle.setVisibility(View.GONE);
            layoutParams.removeRule(RelativeLayout.CENTER_IN_PARENT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            rootView.setBackground(getDrawable(android.R.color.transparent));
            progress_bar.reveal(true);
            colorAnimator.end();
            addButton.setBackground(getResources().getDrawable(R.drawable.oval, getTheme()));
            addButton.setElevation(getResources().getDimension(R.dimen.elevation_low));
            buttonContainer.startAnimation(zoomOutButtonAnimation);
            addButton.setImageDrawable(getDrawable(android.R.drawable.ic_input_add));

            toolbar.startAnimation(toolbarDropInAnimation);
          } else {
            center = true;
            progressBarCircle.setVisibility(View.VISIBLE);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            progress_bar.hide(true);
            colorAnimator.start();
            buttonContainer.startAnimation(zoomButtonAnimation);
            addButton.setImageDrawable(getDrawable(android.R.color.transparent));
            addButton.setElevation(getResources().getDimension(R.dimen.elevation_high));
            toolbar.startAnimation(toolbarDropOutAnimation);
          }
          buttonContainer.setLayoutParams(layoutParams);

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
