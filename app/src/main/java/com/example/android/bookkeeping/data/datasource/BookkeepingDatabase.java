package com.example.android.bookkeeping.data.datasource;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.example.android.bookkeeping.data.dao.AccountDao;
import com.example.android.bookkeeping.model.AccountSaver;
import com.example.android.bookkeeping.data.dao.TransactionDao;
import com.example.android.bookkeeping.model.TransactionSaver;


@Database(entities = {AccountSaver.class, TransactionSaver.class}, version = 1, exportSchema = false)
public abstract class BookkeepingDatabase extends RoomDatabase {
    public abstract AccountDao accountDao();
    public abstract TransactionDao transactionDao();
}
