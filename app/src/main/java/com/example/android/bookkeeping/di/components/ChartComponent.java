package com.example.android.bookkeeping.di.components;


import com.example.android.bookkeeping.di.modules.ActivityModule;
import com.example.android.bookkeeping.di.modules.UrlParserModule;
import com.example.android.bookkeeping.ui.ChartActivity;

import javax.inject.Singleton;

import dagger.Subcomponent;

@Singleton
@Subcomponent (modules = {ActivityModule.class, UrlParserModule.class})
public interface ChartComponent {
    void inject(ChartActivity chartActivity);
}
