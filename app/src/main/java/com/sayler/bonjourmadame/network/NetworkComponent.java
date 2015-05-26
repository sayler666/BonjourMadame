package com.sayler.bonjourmadame.network;

import com.sayler.bonjourmadame.activity.MainActivity;
import com.sayler.bonjourmadame.inject.DataModule;
import dagger.Component;

@Component(
    modules = {
        GsonModule.class,
        NetworkModule.class,
        DataModule.class
    }
)
public interface NetworkComponent {

  void inject(MainActivity mainActivity);
}