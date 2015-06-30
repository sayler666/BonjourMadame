package com.sayler.bonjourmadame.fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.sayler.bonjourmadame.R;
import com.sayler.bonjourmadame.activity.MainActivity;
import com.sayler.bonjourmadame.adapter.ImageAdapter;
import com.sayler.bonjourmadame.util.ToolbarHiderHelper;
import entity.Madame;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * History fragment.
 */
public class HistoryFragment extends Fragment {
  public static final int SPAN_COUNT = 3;
  @InjectView(R.id.history_list_recycler_view)
  public RecyclerView recyclerView;
  private MultiSelector multiSelector = new MultiSelector();
  protected MainActivity mainActivity;
  private ImageAdapter adapter;
  private Bitmap chosenBitmap;
  private int chosenPosition = -1;
  private ToolbarHiderHelper toolbarHiderHelper;

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
    setupRecyclerView();
  }

  private void setupRecyclerView() {
    toolbarHiderHelper = new ToolbarHiderHelper(mainActivity.getToolbar(), recyclerView);
    toolbarHiderHelper.startHidingToolbarOnScroll();
    recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), SPAN_COUNT));
    List<Madame> madameList = loadMadameList();
    addHeaderViews(madameList);
    Collections.reverse(madameList);
    setupAdapter(madameList);
    recyclerView.setAdapter(adapter);
  }

  private void setupAdapter(List<Madame> madameList) {
    adapter = new ImageAdapter(madameList, recyclerView, chosenBitmap, chosenPosition, multiSelector, SPAN_COUNT);
    adapter.setOnItemClickListener((view, position) -> {
      ImageView imageView = (ImageView) view.findViewById(R.id.image);
      chosenBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
      chosenPosition = position;

      setSharedElementReturnTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.image_transition));
      setExitTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));

      LoadingFragment loadingFragment = LoadingFragment.newInstanceWithBitmap(chosenBitmap, madameList.get(position));
      loadingFragment.setSharedElementEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.image_transition));
      loadingFragment.setEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));
      loadingFragment.setImageTransitionName(imageView.getTransitionName());

      toolbarHiderHelper.showToolbar();

      getFragmentManager().beginTransaction()
          .replace(R.id.container, loadingFragment)
          .addToBackStack(null)
          .addSharedElement(imageView, imageView.getTransitionName())
          .commit();
    });
  }

  @NonNull
  protected List<Madame> loadMadameList() {
    List<Madame> madameList = Collections.emptyList();
    try {
      madameList = mainActivity.getMadameDataProvider().getAll();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return madameList;
  }

  private void addHeaderViews(List<Madame> madameList) {
    for (int i = 0; i < SPAN_COUNT; i++) {
      madameList.add(new Madame());
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    adapter.destroy();
    System.gc();
  }

  public void clearOpenedBitmap() {
    chosenBitmap = null;
    chosenPosition = -1;
  }

}
