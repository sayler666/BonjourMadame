package com.sayler.bonjourmadame;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import com.sayler.bonjourmadame.inject.ApplicationComponent;
import com.sayler.bonjourmadame.inject.ApplicationModule;
import com.sayler.bonjourmadame.inject.Dagger_ApplicationComponent;
import com.sayler.bonjourmadame.network.Dagger_NetworkComponent;
import com.sayler.bonjourmadame.network.NetworkComponent;

public class BonjourMadameApplication extends Application {

  private ApplicationComponent applicationComponent;
  private NetworkComponent networkComponent;

  @Override
  public void onCreate() {
    super.onCreate();

    applicationComponent = Dagger_ApplicationComponent.builder()
        .applicationModule(new ApplicationModule(this))
        .build();

    networkComponent = Dagger_NetworkComponent.builder()
        .applicationComponent(applicationComponent)
        .build();

  }

  public ApplicationComponent getApplicationComponent() {
    return applicationComponent;
  }

  public NetworkComponent getNetworkComponent() {
    return networkComponent;
  }

  public static BonjourMadameApplication get(@NonNull Context context) {
    return (BonjourMadameApplication) context.getApplicationContext();
  }
}