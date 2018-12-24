package com.example.android.bookkeeping.di.modules;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

@Module
public class ActivityModule {

    private final Context mActivity;

    public ActivityModule(Context activity) {
        mActivity = activity;
    }

    @Provides
    Context provideContext() {
        return mActivity;
    }

    @Provides
    CompositeDisposable providesCompositeDisposable() {
        return new CompositeDisposable();
    }

}
