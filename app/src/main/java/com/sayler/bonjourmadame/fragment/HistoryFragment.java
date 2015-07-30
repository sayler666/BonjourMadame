package com.sayler.bonjourmadame.fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionInflater;
import android.view.*;
import android.widget.ImageView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.sayler.bonjourmadame.R;
import com.sayler.bonjourmadame.activity.MainActivity;
import com.sayler.bonjourmadame.adapter.ImageAdapter;
import com.sayler.bonjourmadame.util.Constants;
import com.sayler.bonjourmadame.util.ToolbarHiderHelper;
import entity.Madame;
import rx.Observable;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * History fragment.
 */
public class HistoryFragment extends Fragment implements ImageAdapter.MultiSelectorListener, ActionMode.Callback {
  public static final int SPAN_COUNT = 3;
  @InjectView(R.id.history_list_recycler_view)
  public RecyclerView recyclerView;
  private MultiSelector multiSelector = new MultiSelector();
  protected MainActivity mainActivity;
  private ImageAdapter adapter;
  private Bitmap chosenBitmap;
  private int chosenPosition = -1;
  private ToolbarHiderHelper toolbarHiderHelper;
  private ActionMode actionMode;

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
    adapter = new ImageAdapter(madameList, recyclerView, chosenBitmap, chosenPosition, multiSelector, SPAN_COUNT, this);
    adapter.setOnItemClickListener((view, position) -> {
      if (multiSelector.isSelectable()) {
        boolean newState = !multiSelector.isSelected(position, position);
        multiSelector.setSelected(position, position, newState);
        //work-around issue with multiSelector not setting active state after notifyItemRangeChanged
        view.setActivated(newState);
        setActionModeTitle();
      } else {
        onMadameClick(madameList, view, position);
      }
    });
  }

  private void setActionModeTitle() {
    int size = multiSelector.getSelectedPositions().size();
    actionMode.setTitle(size + " " + mainActivity.getResources().getQuantityString(R.plurals.items_selected, size));
  }

  private void onMadameClick(List<Madame> madameList, View view, int position) {
    AppObservable.bindFragment(this, Observable.just(view))
        .delay(Constants.DURATION_VERY_SHORT, TimeUnit.MILLISECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(v -> openMadame(madameList, v, position));
  }

  private void openMadame(List<Madame> madameList, View view, int position) {
    ImageView imageView = (ImageView) view.findViewById(R.id.image);
    BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
    if (bitmapDrawable != null) {
      toolbarHiderHelper.showToolbar();

      chosenBitmap = bitmapDrawable.getBitmap();
      chosenPosition = position;

      setSharedElementReturnTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.image_transition));
      setExitTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));

      LoadingFragment loadingFragment = LoadingFragment.newInstanceWithBitmap(chosenBitmap, madameList.get(position));
      loadingFragment.setSharedElementEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.image_transition));
      loadingFragment.setEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));
      loadingFragment.setImageTransitionName(imageView.getTransitionName());

      getFragmentManager().beginTransaction()
          .replace(R.id.container, loadingFragment)
          .addToBackStack(null)
          .addSharedElement(imageView, imageView.getTransitionName())
          .commit();
    }
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

  @Override
  public void onSelectableStart() {
    if (actionMode == null) {
      actionMode = mainActivity.getToolbar().startActionMode(this);
      setActionModeTitle();
    }
  }

  @Override
  public void onSelectableFinish() {
    multiSelector.clearSelections();
    multiSelector.setSelectable(false);
  }

  @Override
  public boolean onCreateActionMode(ActionMode mode, Menu menu) {
    getActivity().getMenuInflater().inflate(R.menu.history, menu);
    mainActivity.colorizeToolbarActionModeBackground();
    mainActivity.colorizeToolbarActionModeIcons();
    return true;
  }

  @Override
  public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
    return false;
  }

  @Override
  public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
    ArrayList<Integer> positions = new ArrayList<>(multiSelector.getSelectedPositions());
    Collections.reverse(positions);
    switch (item.getItemId()) {
      case R.id.action_remove:
        for (Integer integer : positions) {
          handleItemRemove(adapter.getMadamAtPosition(integer));
          adapter.removeItemOnPosition(integer);
        }
        mode.finish();
        multiSelector.clearSelections();
        return true;
      default:
        break;
    }
    return false;
  }

  @NonNull
  protected void handleItemRemove(Madame madamOnPosition) {
    mainActivity.getMadameDataProvider().delete(madamOnPosition);
  }

  @Override
  public void onDestroyActionMode(ActionMode mode) {
    multiSelector.clearSelections();
    multiSelector.setSelectable(false);
    actionMode = null;
  }
}
