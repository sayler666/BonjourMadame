package entity;

/**
 * Created by lchromy on 26.05.15.
 */

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "madame")
public class Madame extends BaseEntity {
  @DatabaseField()
  private String type;
  @DatabaseField()
  private String name;
  @DatabaseField()
  private String url;

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
}
