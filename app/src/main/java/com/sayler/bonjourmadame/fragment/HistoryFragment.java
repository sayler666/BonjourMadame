package com.sayler.bonjourmadame.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.sayler.bonjourmadame.R;
import com.sayler.bonjourmadame.activity.MainActivity;
import com.sayler.bonjourmadame.adapter.ImageAdapter;
import entity.Madame;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class HistoryFragment extends Fragment {

  private MainActivity mainActivity;
  @InjectView(R.id.history_list_recycler_view)
  public RecyclerView recyclerView;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.f_history, container, false);
    ButterKnife.inject(this, rootView);
    return rootView;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    mainActivity = (MainActivity) getActivity();

    setupViews();
  }

  private void setupViews() {
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    List<Madame> madameList = Collections.emptyList();
    try {
      madameList = mainActivity.getMadameDataProvider().getAll();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    ImageAdapter adapter = new ImageAdapter(madameList, getActivity());
    adapter.setOnItemClickListener((view, position) -> {
      // Set shared and scene transitions
      setSharedElementReturnTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.image_transition));
      setExitTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));

      ImageView imageView = (ImageView) view.findViewById(R.id.image);
      Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
      LoadingFragment loadingFragment = LoadingFragment.newInstanceWithImage(bitmap);

      // Set shared and scene transitions on 2nd fragment
      loadingFragment.setSharedElementEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.image_transition));
      loadingFragment.setEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.no_transition));

      // You need to make sure the transitionName is both unique to each instance of the view you
      // want to animate as well as known to the 2nd fragment.  Since these views are inside
      // a RecyclerView or ListView, they can have many instances.  In your adapter you need to
      // set a transitionName dynamically (I use the position), then pass that unique transitionName
      // to the 2nd fragment before you animate
      loadingFragment.setImageTransitionName(imageView.getTransitionName());
      FragmentTransaction trans = getFragmentManager().beginTransaction();
      trans.replace(R.id.container, loadingFragment);
      trans.addToBackStack(null);

      trans.addSharedElement(imageView, imageView.getTransitionName());
      trans.commit();
    });
    recyclerView.setAdapter(adapter);
  }
}
