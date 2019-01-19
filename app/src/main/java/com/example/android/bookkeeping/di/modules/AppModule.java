package com.example.android.bookkeeping.di.modules;

import android.app.Application;

import com.example.android.bookkeeping.di.scopes.AppScope;

import dagger.Module;
import dagger.Provides;


@Module
public class AppModule {

   private final Application mApplication;

    public AppModule(Application application) {
        this.mApplication = application;
    }

    @AppScope
    @Provides
    Application providesApplication() {
        return mApplication;
    }

}
