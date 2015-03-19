/**
 * Created by sayler666 on 2015-03-19.
 * <p>
 * Copyright 2015 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.bonjourmadame.network;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dagger.Module;
import dagger.Provides;
import retrofit.converter.Converter;
import retrofit.converter.GsonConverter;

/**
 *
 * @author sayler666
 */

@Module
public class GsonModule {

  @Provides
  public Gson provideObjectMapper() {

    return new GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
        .create();
  }

  @Provides
  public Converter provideConverter(Gson objectMapper) {
    return new GsonConverter(objectMapper);
  }
}
