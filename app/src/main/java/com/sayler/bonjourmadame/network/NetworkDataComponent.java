package com.sayler.bonjourmadame.network;

import com.sayler.bonjourmadame.activity.MainActivity;
import com.sayler.bonjourmadame.inject.ApplicationComponent;
import dagger.Component;

@Component(
    modules = {
        GsonModule.class,
        NetworkModule.class,
        DataModule.class
    },
    dependencies = {
        ApplicationComponent.class
    }
)
public interface NetworkDataComponent {

  void inject(MainActivity mainActivity);
}