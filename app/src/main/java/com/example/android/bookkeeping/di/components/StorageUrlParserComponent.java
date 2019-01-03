package com.example.android.bookkeeping.di.components;


import com.example.android.bookkeeping.di.modules.ActivityModule;
import com.example.android.bookkeeping.di.modules.UrlParserModule;
import com.example.android.bookkeeping.di.modules.StorageModule;
import com.example.android.bookkeeping.ui.account.AccountsActivity;

import javax.inject.Singleton;

import dagger.Subcomponent;

@Singleton
@Subcomponent(modules = {ActivityModule.class, StorageModule.class, UrlParserModule.class})
public interface StorageUrlParserComponent {
    void inject(AccountsActivity accountsActivity);
}
