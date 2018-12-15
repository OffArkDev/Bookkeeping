package com.example.android.bookkeeping.di;

import com.example.android.bookkeeping.ui.AccountsActivity;
import com.example.android.bookkeeping.ui.ChartActivity;
import com.example.android.bookkeeping.ui.TransactionsActivity;

import javax.inject.Singleton;

import dagger.Component;



@Singleton
@Component(modules = {AppModule.class, StorageModule.class, UrlParserModule.class})
public interface AppComponent {
    void injectAccountsActivity(AccountsActivity accountsActivity);
    void injectTransactionsActivity(TransactionsActivity transactionsActivity);
    void injectChartActivity(ChartActivity chartActivity);

}
