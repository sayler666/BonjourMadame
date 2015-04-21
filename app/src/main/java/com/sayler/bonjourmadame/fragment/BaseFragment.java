/**
 * Created by sayler666 on 2015-03-21.
 * <p>
 * Copyright 2015 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.bonjourmadame.fragment;

import android.app.Fragment;
import com.sayler.bonjourmadame.activity.BaseActivity;

/**
 * Base fragment class
 *
 * @author sayler666
 */
public class BaseFragment extends Fragment {

  public BaseActivity getBaseActivity() {
    return (BaseActivity) getActivity();
  }

}
