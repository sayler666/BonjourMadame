package com.sayler.bonjourmadame.fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.sayler.bonjourmadame.R;

public class SecondFragment extends Fragment {
  private String imageId;

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
    rootView.findViewById(R.id.image).setTransitionName(imageId);

    if (getArguments() != null) {
      ((ImageView) rootView.findViewById(R.id.image)).setImageBitmap(getArguments().getParcelable("bitmap"));
    }

    return rootView;
  }

  public void setImageId(String imageId) {
    this.imageId = imageId;
  }

}