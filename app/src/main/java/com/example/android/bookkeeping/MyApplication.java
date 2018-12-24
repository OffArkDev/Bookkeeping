package com.example.android.bookkeeping;

import android.app.Application;

import com.example.android.bookkeeping.di.components.AppComponent;
import com.example.android.bookkeeping.di.components.DaggerAppComponent;
import com.example.android.bookkeeping.di.modules.AppModule;

public class MyApplication extends Application {

    private AppComponent mApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
    }


    public AppComponent getApplicationComponent() {
        if (mApplicationComponent == null) {
            mApplicationComponent = DaggerAppComponent.builder()
                    .appModule(new AppModule(this))
                    .build();
        }
        return mApplicationComponent;
    }
}
