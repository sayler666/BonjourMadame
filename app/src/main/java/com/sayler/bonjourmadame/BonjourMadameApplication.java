package com.sayler.bonjourmadame;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import com.sayler.bonjourmadame.inject.ApplicationComponent;
import com.sayler.bonjourmadame.inject.ApplicationModule;
import com.sayler.bonjourmadame.inject.DaggerApplicationComponent;
import com.sayler.bonjourmadame.network.DaggerNetworkDataComponent;
import com.sayler.bonjourmadame.network.NetworkDataComponent;

public class BonjourMadameApplication extends Application {

  private ApplicationComponent applicationComponent;
  private NetworkDataComponent networkComponent;

  @Override
  public void onCreate() {
    super.onCreate();

    applicationComponent = DaggerApplicationComponent.builder()
        .applicationModule(new ApplicationModule(this))
        .build();

    networkComponent = DaggerNetworkDataComponent.builder()
        .build();

  }

  public ApplicationComponent getApplicationComponent() {
    return applicationComponent;
  }

  public NetworkDataComponent getNetworkComponent() {
    return networkComponent;
  }

  public static BonjourMadameApplication get(@NonNull Context context) {
    return (BonjourMadameApplication) context.getApplicationContext();
  }
}