package com.sayler.bonjourmadame;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.sayler.bonjourmadame.inject.ApplicationComponent;
import com.sayler.bonjourmadame.inject.ApplicationModule;
import com.sayler.bonjourmadame.inject.DaggerApplicationComponent;
import com.sayler.bonjourmadame.network.DaggerNetworkDataComponent;
import com.sayler.bonjourmadame.network.NetworkDataComponent;

public class BonjourMadameApplication extends Application {

  private ApplicationComponent applicationComponent;
  private NetworkDataComponent networkDataComponent;

  @Override
  public void onCreate() {
    super.onCreate();

    applicationComponent = DaggerApplicationComponent.builder()
        .applicationModule(new ApplicationModule(this))
        .build();

    networkDataComponent = DaggerNetworkDataComponent.builder()
        .applicationComponent(applicationComponent)
        .build();

    configureImageLoader();

  }

  public ApplicationComponent getApplicationComponent() {
    return applicationComponent;
  }

  public NetworkDataComponent getNetworkDataComponent() {
    return networkDataComponent;
  }

  public static BonjourMadameApplication get(@NonNull Context context) {
    return (BonjourMadameApplication) context.getApplicationContext();
  }

  private void configureImageLoader() {
    DisplayImageOptions options = new DisplayImageOptions.Builder()
        .resetViewBeforeLoading(true)
        .cacheOnDisk(true)
        .displayer(new FadeInBitmapDisplayer(500))
        .build();

    ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
        .defaultDisplayImageOptions(options)
        .build();
    ImageLoader.getInstance().init(config);
  }
}