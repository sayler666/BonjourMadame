/**
 * Created by sayler666 on 2015-03-19.
 * <p>
 * Copyright 2015 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.bonjourmadame.data;

import android.app.Application;
import com.sayler.bonjourmadame.network.BonjourMadameAPI;

/**
 * BonjourMadame
 *
 * @author sayler666
 */
public class BonjourMadame {

  final BonjourMadameAPI bonjourMadameAPI;
  final Application application;

  private BonjourMadame(Builder builder) {

    bonjourMadameAPI = builder.getBonjourMadameAPI();
    application = builder.getApplication();

  }

  public static final class Builder {

    private final BonjourMadameAPI bonjourMadameAPI;
    private final Application application;
    String endpoint = "https://api.parse.com/";

    public Builder(Application application, BonjourMadameAPI bonjourMadameAPI) {
      this.application = application;
      this.bonjourMadameAPI = bonjourMadameAPI;
    }

    public Builder setEndpoint(String endpoint) {
      this.endpoint = endpoint;
      return this;
    }

    public BonjourMadame build() {
      return new BonjourMadame(this);
    }

    public BonjourMadameAPI getBonjourMadameAPI() {
      return bonjourMadameAPI;
    }

    public Application getApplication() {
      return application;
    }
  }
}
