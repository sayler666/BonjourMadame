package com.sayler.bonjourmadame.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sayler.bonjourmadame.R;
import com.squareup.picasso.Picasso;
import entity.Madame;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
  public interface OnItemClickListener {
    void onItemClick(View view, int position);
  }

  private OnItemClickListener mItemClickListener;

  List<Madame> items = new ArrayList<>();
  private Context context;

  public void setOnItemClickListener(OnItemClickListener listener) {
    this.mItemClickListener = listener;
  }

  public ImageAdapter(List<Madame> madames, Context context) {
    items = madames;
    this.context = context;
  }

  @Override
  public ImageAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
    View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.i_history_row, null);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder viewHolder, final int i) {
    viewHolder.container.setOnClickListener(view -> {
      if (mItemClickListener != null) {
        mItemClickListener.onItemClick(view, i);
      }
    });
    viewHolder.image.setTransitionName("image" + i);
    ImageLoader.getInstance().displayImage(items.get(i).getUrl(), viewHolder.image);
   // Picasso.with(context).load(items.get(i).getUrl()).into(viewHolder.image);
  }

  @Override
  public int getItemCount() {
    return items.size();
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