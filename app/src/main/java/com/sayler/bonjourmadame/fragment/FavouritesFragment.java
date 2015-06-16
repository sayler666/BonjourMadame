package com.sayler.bonjourmadame.fragment;

import android.support.annotation.NonNull;
import entity.Madame;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * Created by lchromy on 16.06.15.
 */
public class FavouritesFragment extends HistoryFragment {

  @Override
  @NonNull
  protected List<Madame> loadMadameList() {
    List<Madame> madameList = Collections.emptyList();
    try {
      madameList = mainActivity.getMadameDataProvider().getDao().queryForEq(Madame.FAVOURITE_COL, true);
      Collections.reverse(madameList);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return madameList;
  }
}
