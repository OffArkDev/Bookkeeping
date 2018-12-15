package com.example.android.bookkeeping.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface AccountDao {

    @Query("SELECT * FROM AccountSaver")
    Flowable<List<AccountSaver>> getAll();

    @Query("SELECT * FROM AccountSaver WHERE id = :id")
    AccountSaver getById(long id);

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    long insert(AccountSaver accountSaver);

    @Update
    void update(AccountSaver accountSaver);

    @Delete
    void delete(AccountSaver accountSaver);



}
