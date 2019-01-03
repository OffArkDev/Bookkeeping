package com.example.android.bookkeeping.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.android.bookkeeping.data.model.AccountSaver;

import java.util.List;
import java.util.Observable;

import io.reactivex.Completable;
import io.reactivex.Flowable;

@Dao
public interface AccountDao {

    @Query("SELECT * FROM AccountSaver")
    Flowable<List<AccountSaver>> getAll();

    @Query("SELECT * FROM AccountSaver WHERE id = :id")
    AccountSaver getById(long id);

    @Query("UPDATE AccountSaver SET valueRUB = :valueRUB WHERE id = :id")
    void updateValueRub(long id, String valueRUB);

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    long insert(AccountSaver accountSaver);

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    long[] insertList(List<AccountSaver> list);

    @Update
    void update(AccountSaver accountSaver);


    @Delete
    void delete(AccountSaver accountSaver);

    @Query("DELETE FROM AccountSaver")
    void deleteAll();


}
