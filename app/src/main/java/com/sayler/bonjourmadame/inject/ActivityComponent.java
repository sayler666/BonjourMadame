package com.sayler.bonjourmadame.inject;

import android.app.Activity;
import dagger.Component;

@Component(
    modules = {
        ActivityModule.class
    },
    dependencies = {
        ApplicationComponent.class
    }
)
public interface ActivityComponent {

  // Provide
  Activity activity();
}