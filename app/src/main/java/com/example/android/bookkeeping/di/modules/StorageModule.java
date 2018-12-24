package com.example.android.bookkeeping.di.modules;


import android.arch.persistence.room.Room;
import android.content.Context;

import com.example.android.bookkeeping.data.AccountDao;
import com.example.android.bookkeeping.data.TransactionDao;
import com.example.android.bookkeeping.repository.AccountsDataSource;
import com.example.android.bookkeeping.repository.AccountsRepository;
import com.example.android.bookkeeping.repository.BookkeepingDatabase;
import com.example.android.bookkeeping.repository.TransactionsDataSource;
import com.example.android.bookkeeping.repository.TransactionsRepository;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

@Module
public class StorageModule {

    private BookkeepingDatabase bookkeepingDatabase;


    public StorageModule(Context context){
        bookkeepingDatabase = Room.databaseBuilder(context, BookkeepingDatabase.class, "database").build();
    }

    @Provides
    BookkeepingDatabase providesAccountsDataBase() {
        return bookkeepingDatabase;
    }

    @Provides
    AccountDao providesAccountDao(BookkeepingDatabase bookkeepingDatabase) {
        return bookkeepingDatabase.accountDao();
    }

    @Provides
    AccountsRepository providesAccountRepository(AccountDao accountDao) {
        return new AccountsDataSource(accountDao);
    }


    @Provides
    TransactionDao providesTransactionDao(BookkeepingDatabase bookkeepingDatabase) {
        return bookkeepingDatabase.transactionDao();
    }

    @Provides
    TransactionsRepository providesTransactionRepository(TransactionDao transactionDao) {
        return new TransactionsDataSource(transactionDao);
    }


}
