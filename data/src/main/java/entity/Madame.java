package entity;

/**
 * Created by lchromy on 26.05.15.
 */

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "madame")
public class Madame extends BaseEntity {
  public static final String TYPE_COL = "TYPE_COL";
  @DatabaseField(columnName = TYPE_COL)
  private String type;
  public static final String NAME_COL = "NAME_COL";
  @DatabaseField(columnName = NAME_COL)
  private String name;
  @DatabaseField(unique = true)
  private String url;
  public static final String FAVOURITE_COL = "FAVOURITE_COL";
  @DatabaseField(columnName = FAVOURITE_COL)
  private boolean isFavourite;

  public Madame() {
  }

  public Madame(String type, String name, String url) {
    this.type = type;
    this.name = name;
    this.url = url;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public boolean isFavourite() {
    return isFavourite;
  }

  public void setIsFavourite(boolean isFavourite) {
    this.isFavourite = isFavourite;
  }
}
