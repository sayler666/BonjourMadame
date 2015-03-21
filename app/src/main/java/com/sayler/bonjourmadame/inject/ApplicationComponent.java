package com.sayler.bonjourmadame.inject;

import android.content.Context;
import com.sayler.bonjourmadame.activity.BaseActivity;
import dagger.Component;

@Component(
    modules = {
        ApplicationModule.class
    }
)
public interface ApplicationComponent {

  Context context();

  // Injections

  void inject(BaseActivity baseActivity);
}