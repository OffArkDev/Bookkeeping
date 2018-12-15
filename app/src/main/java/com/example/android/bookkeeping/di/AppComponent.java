package com.example.android.bookkeeping.di;

import com.example.android.bookkeeping.ui.AccountsActivity;
import com.example.android.bookkeeping.ui.TransactionsActivity;

import javax.inject.Singleton;

import dagger.Component;



@Singleton
@Component(modules = {AppModule.class, StorageModule.class, UrlParserModule.class})
public interface AppComponent {
    void injectAccountsListActivity(AccountsActivity accountsActivity);
    void injectTransactionsListActivity(TransactionsActivity transactionsActivity);

}
