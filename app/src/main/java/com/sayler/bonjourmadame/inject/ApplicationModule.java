package com.sayler.bonjourmadame.inject;

import android.content.Context;
import com.sayler.bonjourmadame.BonjourMadameApplication;
import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

  private final BonjourMadameApplication application;

  public ApplicationModule(BonjourMadameApplication application) {
    this.application = application;
  }

  @Provides
  public Context provideContext() {
    return application;
  }

}