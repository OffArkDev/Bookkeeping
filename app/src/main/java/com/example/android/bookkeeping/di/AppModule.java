package com.example.android.bookkeeping.di;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;

import com.example.android.bookkeeping.repository.AccountsDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;


@Module
public class AppModule {

   private final Application mApplication;

    public AppModule(Application application) {
        this.mApplication = application;
    }

    @Singleton
    @Provides
    Context providesContext() {
        return mApplication;
    }


}
