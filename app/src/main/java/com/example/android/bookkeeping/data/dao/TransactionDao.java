package com.example.android.bookkeeping.data.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.android.bookkeeping.model.TransactionSaver;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface TransactionDao {

    @Query("SELECT * FROM TransactionSaver")
    Flowable<List<TransactionSaver>> getAll();

    @Query("SELECT * FROM TransactionSaver WHERE id = :id")
    TransactionSaver getById(long id);

    @Query("SELECT * FROM TransactionSaver WHERE account_id = :account_id")
    Flowable<List<TransactionSaver>> getAllTransactions(long account_id);

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    long insert(TransactionSaver transactionSaver);

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    long[] insertList(List<TransactionSaver> list);

    @Update
    void update(TransactionSaver transactionSaver);

    @Delete
    void delete(TransactionSaver transactionSaver);

    @Query("DELETE FROM AccountSaver")
    void deleteAll();
}
