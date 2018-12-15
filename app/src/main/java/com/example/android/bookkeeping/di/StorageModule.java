package com.example.android.bookkeeping.di;


import android.app.Application;
import android.arch.persistence.room.Room;

import com.example.android.bookkeeping.data.AccountDao;
import com.example.android.bookkeeping.data.TransactionDao;
import com.example.android.bookkeeping.repository.AccountsDataSource;
import com.example.android.bookkeeping.repository.AccountsDatabase;
import com.example.android.bookkeeping.repository.TransactionsDataSource;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

@Module
public class StorageModule {

    private AccountsDatabase accountsDatabase;

    public StorageModule(Application application){

        accountsDatabase = Room.databaseBuilder(application, AccountsDatabase.class, "database").build();
    }


    @Provides
    @Singleton
    AccountsDatabase providesAccountsDataBase() {
        return accountsDatabase;
    }


    @Provides
    @Singleton
    AccountDao providesAccountDao(AccountsDatabase accountsDatabase) {
        return accountsDatabase.accountDao();
    }

    @Provides
    @Singleton
    AccountsDataSource providesAccountRepository(AccountDao accountDao) {
        return new AccountsDataSource(accountDao);
    }

    @Provides
    @Singleton
    CompositeDisposable providesCompositeDisposable() {
        return new CompositeDisposable();
    }


    @Singleton
    @Provides
    TransactionDao providesTransactionDao(AccountsDatabase accountsDatabase) {
        return accountsDatabase.transactionDao();
    }

    @Singleton
    @Provides
    TransactionsDataSource providesTransactionDataSource(TransactionDao transactionDao) {
        return new TransactionsDataSource(transactionDao);
    }


}
