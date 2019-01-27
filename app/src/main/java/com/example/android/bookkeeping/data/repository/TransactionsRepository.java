package com.example.android.bookkeeping.data.repository;

import com.example.android.bookkeeping.data.entities.TransactionSaver;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public interface TransactionsRepository {

    Flowable<List<TransactionSaver>> getAll();

    TransactionSaver getById(long id);

    Flowable<List<TransactionSaver>> getTransactionsOfAccount(long account_id);

    Single<Long> insert(final TransactionSaver transactionSaver);

    Single<long[]> insertList(List<TransactionSaver> list);


    Completable update(final TransactionSaver transactionSaver);

    Completable delete(final TransactionSaver transactionSaver);

    Completable deleteAll();
}
