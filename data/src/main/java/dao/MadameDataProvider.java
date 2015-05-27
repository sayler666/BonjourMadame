/**
 * Created by sayler666 on 2015-05-27.
 * <p>
 * Copyright 2015 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package dao;

import android.content.Context;
import android.util.Log;
import com.j256.ormlite.dao.Dao;
import entity.Madame;

import java.sql.SQLException;

/**
 * TODO Add class description...
 *
 * @author sayler666
 */
public class MadameDataProvider extends BaseDataProvider<Madame> {

  private static final String TAG = MadameDataProvider.class.getSimpleName();

  public MadameDataProvider(Context context) {
    super(context);
  }

  @Override
    protected Dao<Madame, Long> setupDao() {
      try {
        DaoHelper.setOpenHelper(context,
            DBHelper.class);
        return DaoHelper.getDao(Madame.class);
      } catch (SQLException e) {
        Log.e(TAG, e.getMessage(), e);
      }
      return null;
    }

}
