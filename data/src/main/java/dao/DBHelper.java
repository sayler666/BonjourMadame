/**
 * Created by Lukasz Chromy on 10.01.14.
 *
 * Copyright 2014 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import entity.Madame;

import java.sql.SQLException;

/**
 * @author Lukasz Chromy
 */
public class DBHelper extends OrmLiteSqliteOpenHelper {

  private static final String TAG = "DBHelper";
  private static final String DATABASE_NAME = "bonjourmadame.db";
  private static final int DATABASE_VERSION = 5;

  public DBHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
    Log.d(TAG, "Creating database...");
    try {
      dropTables(connectionSource);
      createTables(connectionSource);
    } catch (SQLException e) {
      Log.d(TAG, "Cannot create table", e);
    }
    Log.d(TAG, "Database creation finished.");

  }

  private void createTables(ConnectionSource connectionSource) throws SQLException {
    TableUtils.createTableIfNotExists(connectionSource, Madame.class);
  }

  private void dropTables(ConnectionSource connectionSource) throws SQLException {
    TableUtils.dropTable(connectionSource, Madame.class, true);
  }

  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int currentVersion, int newVersion) {
    Log.d(TAG, "Upgrading database from version: " + currentVersion + "  to version: " + newVersion);
    onCreate(sqLiteDatabase, connectionSource);
    Log.d(TAG, "Upgrade finished.");
  }
}