package com.sayler.bonjourmadame.adapter;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.SwappingHolder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sayler.bonjourmadame.R;
import entity.Madame;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

  private static final String TAG = "ImageAdapter";
  private final int normalRowsHeight;
  private int topRowsHeight;

  public interface ItemClickListener {
    void onItemClick(View view, int position);
  }

  private ItemClickListener itemClickListener;
  private List<Madame> madameList = new ArrayList<>();
  private RecyclerView recyclerView;
  private final Bitmap chosenBitmap;
  private final int chosenPosition;
  private MultiSelector multiSelector;
  private int spanCount;
  private MultiSelectorListener multiSelectorListener;

  public void setOnItemClickListener(ItemClickListener listener) {
    this.itemClickListener = listener;
  }

  public ImageAdapter(List<Madame> madameList, RecyclerView recyclerView, Bitmap chosenBitmap, int chosenPosition, MultiSelector multiSelector, int spanCount, MultiSelectorListener multiSelectorListener) {
    this.madameList = madameList;
    this.recyclerView = recyclerView;
    this.chosenBitmap = chosenBitmap;
    this.chosenPosition = chosenPosition;
    this.multiSelector = multiSelector;
    this.spanCount = spanCount;
    this.multiSelectorListener = multiSelectorListener;
    topRowsHeight = (int) recyclerView.getContext().getResources().getDimension(R.dimen.abc_action_bar_default_height_material);
    normalRowsHeight = (int) recyclerView.getContext().getResources().getDimension(R.dimen.h_history_row);
  }

  @Override
  public ImageAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
    View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.i_history_row, null);
    return new ViewHolder(view, multiSelector, multiSelectorListener);
  }

  @Override
  public void onBindViewHolder(ViewHolder viewHolder, final int i) {

    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewHolder.image.getLayoutParams();
    if (i < spanCount) {
      viewHolder.image.setImageBitmap(null);
      params.height = topRowsHeight;
      viewHolder.image.setLayoutParams(params);
    } else {
      params.height = normalRowsHeight;
      viewHolder.image.setLayoutParams(params);

      viewHolder.container.setOnClickListener(view -> {
        if (itemClickListener != null) {
          itemClickListener.onItemClick(view, i);
        }
      });
      viewHolder.image.setTransitionName("image" + i);

      if (i == chosenPosition) {
        viewHolder.image.setImageBitmap(chosenBitmap);
      } else {
        ImageLoader.getInstance().displayImage(madameList.get(i).getUrl(), viewHolder.image);
      }

      if (multiSelector.isSelected(i, i)) {
        Log.d(TAG, "selected id:" + i);
      }
    }
  }

  @Override
  public int getItemCount() {
    return madameList.size();
  }

  public void destroy() {
    final int count = recyclerView.getChildCount();
    for (int i = 0; i < count; i++) {
      final View view = recyclerView.getChildAt(i);
      ImageView imageView = (ImageView) view.findViewById(R.id.image);
      final BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
      if (drawable != null && drawable.getBitmap() != null) {
        drawable.getBitmap().recycle();
      }
    }
    if (chosenBitmap != null) {
      chosenBitmap.recycle();
    }
    recyclerView.removeViews(0, count);
  }

  public static class ViewHolder extends SwappingHolder {
    private final MultiSelector multiSelector;
    private MultiSelectorListener multiSelectorListener;
    public ImageView image;
    public View container;

    public ViewHolder(View itemView, MultiSelector multiSelector, MultiSelectorListener multiSelectorListener) {
      super(itemView, multiSelector);
      this.multiSelector = multiSelector;
      this.multiSelectorListener = multiSelectorListener;
      image = (ImageView) itemView.findViewById(R.id.image);
      container = itemView.findViewById(R.id.container);
      itemView.setOnLongClickListener(v -> {
            multiSelector.setSelectable(true);
            multiSelectorListener.onSelectableStart();
            return true;
          }
      );
    }


  }

  public interface MultiSelectorListener {
    void onSelectableStart();

    void onSelectableFinish();
  }

}