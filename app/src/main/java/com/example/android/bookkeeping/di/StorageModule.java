package com.example.android.bookkeeping.di;


import android.app.Application;
import android.arch.persistence.room.Room;

import com.example.android.bookkeeping.data.AccountDao;
import com.example.android.bookkeeping.data.TransactionDao;
import com.example.android.bookkeeping.repository.AccountsDataSource;
import com.example.android.bookkeeping.repository.BookkeepingDatabase;
import com.example.android.bookkeeping.repository.AccountsRepository;
import com.example.android.bookkeeping.repository.TransactionsDataSource;
import com.example.android.bookkeeping.repository.TransactionsRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

@Module
public class StorageModule {

    private BookkeepingDatabase bookkeepingDatabase;

    public StorageModule(Application application){

        bookkeepingDatabase = Room.databaseBuilder(application, BookkeepingDatabase.class, "database").build();
    }


    @Provides
    @Singleton
    BookkeepingDatabase providesAccountsDataBase() {
        return bookkeepingDatabase;
    }


    @Provides
    @Singleton
    AccountDao providesAccountDao(BookkeepingDatabase bookkeepingDatabase) {
        return bookkeepingDatabase.accountDao();
    }

    @Provides
    @Singleton
    AccountsRepository providesAccountRepository(AccountDao accountDao) {
        return new AccountsDataSource(accountDao);
    }

    @Provides
    @Singleton
    CompositeDisposable providesCompositeDisposable() {
        return new CompositeDisposable();
    }


    @Singleton
    @Provides
    TransactionDao providesTransactionDao(BookkeepingDatabase bookkeepingDatabase) {
        return bookkeepingDatabase.transactionDao();
    }

    @Singleton
    @Provides
    TransactionsRepository providesTransactionRepository(TransactionDao transactionDao) {
        return new TransactionsDataSource(transactionDao);
    }


}
