package com.example.android.bookkeeping.di.components;


import com.example.android.bookkeeping.di.modules.ActivityModule;
import com.example.android.bookkeeping.di.modules.UrlParserModule;
import com.example.android.bookkeeping.di.modules.StorageModule;
import com.example.android.bookkeeping.ui.AccountsActivity;

import dagger.Subcomponent;

@Subcomponent(modules = {ActivityModule.class, StorageModule.class, UrlParserModule.class})
public interface AccountComponent {
    void inject(AccountsActivity accountsActivity);
}
