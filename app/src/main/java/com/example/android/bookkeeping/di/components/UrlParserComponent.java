package com.example.android.bookkeeping.di.components;


import com.example.android.bookkeeping.di.modules.ActivityModule;
import com.example.android.bookkeeping.di.modules.UrlParserModule;
import com.example.android.bookkeeping.ui.ChartActivity;
import com.example.android.bookkeeping.ui.transaction.CreateTransactionActivity;

import javax.inject.Singleton;

import dagger.Subcomponent;

@Singleton
@Subcomponent (modules = {ActivityModule.class, UrlParserModule.class})
public interface UrlParserComponent {
    void inject(ChartActivity chartActivity);

    void inject(CreateTransactionActivity createTransactionActivity);
}
