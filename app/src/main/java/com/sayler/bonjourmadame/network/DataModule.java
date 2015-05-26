package com.sayler.bonjourmadame.network;

import dagger.Module;
import dagger.Provides;
import mapper.MadamEntityDataMapper;

import javax.inject.Singleton;

@Module
public class DataModule {

  @Provides
  public MadamEntityDataMapper provideDataMapper() {
    return new MadamEntityDataMapper();
  }
}