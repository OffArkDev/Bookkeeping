package com.example.android.bookkeeping.di.modules;


import android.arch.persistence.room.Room;
import android.content.Context;

import com.example.android.bookkeeping.data.dao.AccountDao;
import com.example.android.bookkeeping.data.dao.TransactionDao;
import com.example.android.bookkeeping.data.datasource.AccountsDataSource;
import com.example.android.bookkeeping.repository.AccountsRepository;
import com.example.android.bookkeeping.data.datasource.BookkeepingDatabase;
import com.example.android.bookkeeping.data.datasource.TransactionsDataSource;
import com.example.android.bookkeeping.repository.TransactionsRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class StorageModule {

    private BookkeepingDatabase bookkeepingDatabase;


    public StorageModule(Context context){
        bookkeepingDatabase = Room.databaseBuilder(context, BookkeepingDatabase.class, "database").build();
    }

    @Singleton
    @Provides
    BookkeepingDatabase providesAccountsDataBase() {
        return bookkeepingDatabase;
    }

    @Provides
    AccountDao providesAccountDao(BookkeepingDatabase bookkeepingDatabase) {
        return bookkeepingDatabase.accountDao();
    }

    @Singleton
    @Provides
    AccountsRepository providesAccountRepository(AccountDao accountDao) {
        return new AccountsDataSource(accountDao);
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
