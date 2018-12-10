package com.example.android.bookkeeping.repository;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.example.android.bookkeeping.data.AccountDao;
import com.example.android.bookkeeping.data.AccountSaver;


@Database(entities = {AccountSaver.class}, version = 1)
public abstract class AccountsDatabase extends RoomDatabase {
    public abstract AccountDao accountDao();
}
