package com.sayler.bonjourmadame.network;

import android.content.Context;
import dagger.Module;
import dagger.Provides;
import dao.MadameDataProvider;
import mapper.MadamEntityDataMapper;

@Module
public class DataModule {

  @Provides
  public MadamEntityDataMapper provideDataMapper() {
    return new MadamEntityDataMapper();
  }

  @Provides
  public MadameDataProvider provideMadameDataProvider(Context context) {
    return new MadameDataProvider(context);
  }

}