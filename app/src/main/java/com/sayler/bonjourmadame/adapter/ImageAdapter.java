package com.sayler.bonjourmadame.adapter;

import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sayler.bonjourmadame.R;
import entity.Madame;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
  public interface ItemClickListener {
    void onItemClick(View view, int position);
  }

  private ItemClickListener itemClickListener;
  private List<Madame> madameList = new ArrayList<>();
  private RecyclerView recyclerView;

  public void setOnItemClickListener(ItemClickListener listener) {
    this.itemClickListener = listener;
  }

  public ImageAdapter(List<Madame> madameList, RecyclerView recyclerView) {
    this.madameList = madameList;
    this.recyclerView = recyclerView;
  }

  @Override
  public ImageAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
    View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.i_history_row, null);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder viewHolder, final int i) {
    viewHolder.container.setOnClickListener(view -> {
      if (itemClickListener != null) {
        itemClickListener.onItemClick(view, i);
      }
    });
    viewHolder.image.setTransitionName("image" + i);
    ImageLoader.getInstance().displayImage(madameList.get(i).getUrl(), viewHolder.image);
    // Picasso.with(context).load(madameList.get(i).getUrl()).into(viewHolder.image);
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
    recyclerView.removeViews(0, count);
    System.gc();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    public ImageView image;
    public View container;

    public ViewHolder(View itemView) {
      super(itemView);
      image = (ImageView) itemView.findViewById(R.id.image);
      container = itemView.findViewById(R.id.container);
    }
  }
}