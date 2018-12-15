package com.example.android.bookkeeping.repository;

import com.example.android.bookkeeping.data.TransactionDao;
import com.example.android.bookkeeping.data.TransactionSaver;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.functions.Action;

public class TransactionsDataSource {
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
            public Long call() throws Exception {
                return transactionDao.insert(transactionSaver);
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
}