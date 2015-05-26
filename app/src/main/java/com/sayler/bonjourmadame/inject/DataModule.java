package com.sayler.bonjourmadame.inject;

import android.app.Activity;
import dagger.Module;
import dagger.Provides;
import mapper.MadamEntityDataMapper;

@Module
public class DataModule {

  @Provides
  public MadamEntityDataMapper provideDataMapper() {
    return new MadamEntityDataMapper();
  }
}