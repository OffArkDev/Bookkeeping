package com.example.android.bookkeeping.repository;

import com.example.android.bookkeeping.data.TransactionDao;
import com.example.android.bookkeeping.data.model.TransactionSaver;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.functions.Action;

public class TransactionsDataSource implements TransactionsRepository {
    private TransactionDao transactionDao;

    public TransactionsDataSource(TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }

    public Flowable<List<TransactionSaver>> getAll() {
        return transactionDao.getAll();
    }

    public TransactionSaver getById(long id) {
        return transactionDao.getById(id);
    }

    public Flowable<List<TransactionSaver>> getTransactionsOfAccount(long account_id) {
        return transactionDao.getAllTransactions(account_id);
    }

    public Single<Long> insert(final TransactionSaver transactionSaver) {
        return Single.fromCallable(new Callable<Long>() {
            @Override
            public Long call() {
                return transactionDao.insert(transactionSaver);
            }
        });
    }

    @Override
    public Single<long[]> insertList(final List<TransactionSaver> list) {
        return Single.fromCallable(new Callable<long[]>() {
            @Override
            public long[] call() {
                return transactionDao.insertList(list);
            }
        });
    }

    public Completable update(final TransactionSaver transactionSaver) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                transactionDao.update(transactionSaver);
            }
        });
    }

    public Completable delete(final TransactionSaver transactionSaver) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                transactionDao.delete(transactionSaver);
            }
        });
    }

    @Override
    public Completable deleteAll() {
        return Completable.fromAction(new Action() {
            @Override
            public void run() {
                transactionDao.deleteAll();
            }
        });
    }
}
