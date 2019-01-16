package com.example.android.bookkeeping.di.modules;

import android.content.Context;

import com.example.android.bookkeeping.data.AccountSaver;
import com.example.android.bookkeeping.data.TransactionSaver;
import com.example.android.bookkeeping.ui.adapters.AccountsListAdapter;
import com.example.android.bookkeeping.ui.adapters.TransactionsListAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

@Module
public class ActivityModule {

    private final Context mActivity;

    public ActivityModule(Context activity) {
        mActivity = activity;
    }

    @Singleton
    @Provides
    Context provideContext() {
        return mActivity;
    }

    @Singleton
    @Provides
    CompositeDisposable providesCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Singleton
    @Provides
    List<AccountSaver> provideListAccountsSavers() {
        return new ArrayList<>();
    }


    @Singleton
    @Provides
    AccountsListAdapter provideAccountsListAdapter (List<AccountSaver> accountSavers) {
        return new AccountsListAdapter(mActivity, accountSavers);
    }

    @Singleton
    @Provides
    List<TransactionSaver> provideTransactionListTransactionsSavers() {
        return new ArrayList<>();
    }

    @Singleton
    @Provides
    TransactionsListAdapter provideTransactionsListAdapter(List<TransactionSaver> transactionSavers) {
        return new TransactionsListAdapter(mActivity, transactionSavers);
    }

}
