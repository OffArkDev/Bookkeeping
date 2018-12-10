package com.example.android.bookkeeping.di;


import android.app.Application;
import android.arch.persistence.room.Room;

import com.example.android.bookkeeping.data.AccountDao;
import com.example.android.bookkeeping.repository.AccountsDataSource;
import com.example.android.bookkeeping.repository.AccountsDatabase;
import com.example.android.bookkeeping.repository.AccountsRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class StorageModule {

    private AccountsDatabase accountsDatabase;



    public StorageModule(Application application){

        accountsDatabase = Room.databaseBuilder(application, AccountsDatabase.class, "database").build();
    }


    @Singleton
    @Provides
    AccountsDatabase providesAccountsDataBase() {
        return accountsDatabase;
    }

    @Singleton
    @Provides
    AccountDao providesAccountDao(AccountsDatabase accountsDatabase) {
        return accountsDatabase.accountDao();
    }

    @Singleton
    @Provides
    AccountsRepository providesAccountRepository(AccountDao accountDao) {
        return new AccountsDataSource(accountDao);
    }


}
