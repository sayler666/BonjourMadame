/**
 * Created by sayler666 on 2015-03-19.
 * <p>
 * Copyright 2015 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.bonjourmadame.network.model;

import com.google.gson.annotations.SerializedName;

/**
 * Base parse response
 *
 * @author sayler666
 */
public class BaseParseResponse<T extends Object> {

  @SerializedName("result")
  private T result;

  public T getResult() {
    return result;
  }

}
