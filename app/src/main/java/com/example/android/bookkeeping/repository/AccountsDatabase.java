package com.example.android.bookkeeping.repository;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.example.android.bookkeeping.data.AccountDao;
import com.example.android.bookkeeping.data.AccountSaver;
import com.example.android.bookkeeping.data.TransactionDao;
import com.example.android.bookkeeping.data.TransactionSaver;


@Database(entities = {AccountSaver.class, TransactionSaver.class}, version = 1, exportSchema = false)
public abstract class AccountsDatabase extends RoomDatabase {
    public abstract AccountDao accountDao();
    public abstract TransactionDao transactionDao();
}
