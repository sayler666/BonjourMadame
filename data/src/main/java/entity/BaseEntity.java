/**
 * Created by Lukasz Chromy on 10.01.14.
 *
 * Copyright 2014 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package entity;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

/**
 * @author Lukasz Chromy
 */
public abstract class BaseEntity implements Serializable {
  @DatabaseField(generatedId = true)
  protected Long id;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }
}