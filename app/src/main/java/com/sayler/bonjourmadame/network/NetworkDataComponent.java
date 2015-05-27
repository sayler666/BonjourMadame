package com.sayler.bonjourmadame.network;

import com.sayler.bonjourmadame.activity.MainActivity;
import com.sayler.bonjourmadame.activity.TestActivity;
import dagger.Component;

import javax.inject.Singleton;

@Component(
    modules = {
        GsonModule.class,
        NetworkModule.class,
        DataModule.class
    }
)
public interface NetworkDataComponent {

  void inject(MainActivity mainActivity);

  void inject(TestActivity mainActivity);
}