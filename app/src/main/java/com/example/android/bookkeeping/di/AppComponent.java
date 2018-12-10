package com.example.android.bookkeeping.di;

import android.app.Application;

import com.example.android.bookkeeping.currency.UrlParser;
import com.example.android.bookkeeping.data.AccountDao;
import com.example.android.bookkeeping.repository.AccountsDatabase;
import com.example.android.bookkeeping.repository.AccountsRepository;
import com.example.android.bookkeeping.ui.AccountsListActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, StorageModule.class, UrlParserModule.class})
public interface AppComponent {
    void injectAccountsListActivity(AccountsListActivity accountsListActivity);

    UrlParser urlParser();

    AccountDao accountDao();

    AccountsDatabase accountDatabase();

    AccountsRepository accountRepository();

    Application application();
}
