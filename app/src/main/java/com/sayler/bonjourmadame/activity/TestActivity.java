package com.sayler.bonjourmadame.activity;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.sayler.bonjourmadame.BonjourMadameApplication;
import com.sayler.bonjourmadame.R;
import com.squareup.picasso.Picasso;
import dao.MadameDataProvider;
import entity.Madame;
import mapper.MadamEntityDataMapper;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestActivity extends BaseActivity {
  @Inject
  MadamEntityDataMapper madamEntityDataMapper;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    BonjourMadameApplication bonjourMadameApplication = BonjourMadameApplication.get(this);
    bonjourMadameApplication.getApplicationComponent().inject(this);
    bonjourMadameApplication.getNetworkComponent().inject(this);

    if (madamEntityDataMapper != null) {
      Log.d("TEST", madamEntityDataMapper.toString());
    }

    setContentView(R.layout.activity_my);
    if (savedInstanceState == null) {
      getFragmentManager().beginTransaction()
          .add(R.id.main_container, new PlaceholderFragment())
          .commit();
    }
  }

  public static class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
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
      View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_my, null);
      ViewHolder viewHolder = new ViewHolder(view);
      return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
      viewHolder.container.setOnClickListener(view -> {
        if (mItemClickListener != null) {
          mItemClickListener.onItemClick(view, i);
        }
      });
      viewHolder.blue.setTransitionName("testBlue" + i);
      viewHolder.orange.setTransitionName("testOrange" + i);
      viewHolder.image.setTransitionName("testImage" + i);

      Picasso.with(context).load(items.get(i).getUrl()).into(viewHolder.image);
    }

    @Override
    public int getItemCount() {
      return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
      public View orange;
      public View blue;
      public ImageView image;
      public View container;

      public ViewHolder(View itemView) {
        super(itemView);
        blue = itemView.findViewById(R.id.blue_bar);
        orange = itemView.findViewById(R.id.orange_bar);
        image = (ImageView) itemView.findViewById(R.id.image);
        container = itemView.findViewById(R.id.container);
      }
    }
  }

  /**
   * A placeholder fragment containing a simple view.
   */
  @SuppressLint("ValidFragment")
  public static class PlaceholderFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      View rootView = inflater.inflate(R.layout.fragment_my, container, false);

      RecyclerView list = (RecyclerView) rootView.findViewById(R.id.streams_list);
      list.setLayoutManager(new LinearLayoutManager(getActivity()));

      MadameDataProvider madameDataProvider = new MadameDataProvider(getActivity());
      List<Madame> madames = Collections.emptyList();
      try {
        madames = madameDataProvider.getAll();
      } catch (SQLException e) {
        e.printStackTrace();
      }
      ImageAdapter adapter = new ImageAdapter(madames, getActivity());
      adapter.setOnItemClickListener((view, position) -> {
        // Set shared and scene transitions
        setSharedElementReturnTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.trans_my));
        setExitTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));

        View orange = view.findViewById(R.id.orange_bar);
        View blue = view.findViewById(R.id.blue_bar);
        ImageView imageView = (ImageView) view.findViewById(R.id.image);

        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        SecondFragment secondFragment = SecondFragment.newInstance(bitmap);
        // Set shared and scene transitions on 2nd fragment
        secondFragment.setSharedElementEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.trans_my));
        secondFragment.setEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));

        // You need to make sure the transitionName is both unique to each instance of the view you
        // want to animate as well as known to the 2nd fragment.  Since these views are inside
        // a RecyclerView or ListView, they can have many instances.  In your adapter you need to
        // set a transitionName dynamically (I use the position), then pass that unique transitionName
        // to the 2nd fragment before you animate
        secondFragment.setBlueId(blue.getTransitionName());
        secondFragment.setOrangeId(orange.getTransitionName());
        secondFragment.setImageId(imageView.getTransitionName());
        FragmentTransaction trans = getFragmentManager().beginTransaction();
        trans.replace(R.id.main_container, secondFragment);
        trans.addToBackStack(null);
        trans.addSharedElement(blue, blue.getTransitionName());
        trans.addSharedElement(orange, orange.getTransitionName());
        trans.addSharedElement(imageView, imageView.getTransitionName());
        trans.commit();
      });
      list.setAdapter(adapter);

      return rootView;
    }
  }

  public static class SecondFragment extends Fragment {
    private String mOrangeId;
    private String mBlueId;
    private String imageId;
    private Drawable drawable;

    public static SecondFragment newInstance(Bitmap bitmap) {
      SecondFragment fragment = new SecondFragment();
      Bundle bundle = new Bundle();
      bundle.putParcelable("bitmap", bitmap);
      fragment.setArguments(bundle);

      return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View rootView = inflater.inflate(R.layout.second_fragment, container, false);
      rootView.findViewById(R.id.blue_bar).setTransitionName(mBlueId);
      rootView.findViewById(R.id.orange_bar).setTransitionName(mOrangeId);
      rootView.findViewById(R.id.image).setTransitionName(imageId);

      if (getArguments() != null) {
        ((ImageView) rootView.findViewById(R.id.image)).setImageBitmap(getArguments().getParcelable("bitmap"));
      }

      return rootView;
    }

    public void setOrangeId(String id) {
      mOrangeId = id;
    }

    public void setBlueId(String id) {
      mBlueId = id;
    }

    public void setImageId(String imageId) {
      this.imageId = imageId;
    }

  }
}