package com.example.android.bookkeeping.di.components;

import com.example.android.bookkeeping.di.modules.ActivityModule;
import com.example.android.bookkeeping.di.modules.StorageModule;
import com.example.android.bookkeeping.di.modules.UrlParserModule;
import com.example.android.bookkeeping.ui.TransactionsActivity;

import javax.inject.Singleton;

import dagger.Subcomponent;


@Singleton
@Subcomponent(modules = {ActivityModule.class, StorageModule.class})
public interface TransactionComponent {
    void inject(TransactionsActivity transactionsActivity);


}
