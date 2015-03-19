/**
 * Created by sayler666 on 2015-03-19.
 * <p>
 * Copyright 2015 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.bonjourmadame.network;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.converter.Converter;

/**
 * TODO Add class description...
 *
 * @author sayler666
 */
@Module
public class NetworkModule {

  private static final String ENDPOINT = "https://api.parse.com/";

  @Provides
  public BonjourMadameAPI bonjourMadameApi(Converter converter) {

    String baseUrl = ENDPOINT;

    RestAdapter.Builder restAdapterBuilder = new RestAdapter.Builder()
        .setEndpoint(baseUrl)
        .setConverter(converter);

    return restAdapterBuilder.build().create(BonjourMadameAPI.class);
  }
}

