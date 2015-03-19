package com.sayler.bonjourmadame.inject;

import android.content.Context;
import com.sayler.bonjourmadame.activity.BaseActivity;
import dagger.Component;

import java.util.Locale;

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