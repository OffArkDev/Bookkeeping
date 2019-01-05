package com.example.android.bookkeeping.repository;

import com.example.android.bookkeeping.data.AccountDao;
import com.example.android.bookkeeping.data.AccountSaver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.functions.Action;

public class AccountsDataSource implements AccountsRepository{
    private AccountDao accountDao;

    public AccountsDataSource(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public Flowable<List<AccountSaver>> getAll() {
        return accountDao.getAll();
    }

    public AccountSaver getById(long id) {
        return accountDao.getById(id);
    }

    @Override
    public Completable updateValueRub(final long id,final String valueRUB) {
        return Completable.fromAction(new Action() {
            @Override
            public void run()  {
                accountDao.updateValueRub(id, valueRUB);
            }
        });
    }

    public Single<Long> insert(final AccountSaver accountSaver) {

        return Single.fromCallable(new Callable<Long>() {
            @Override
            public Long call () throws Exception {
                return accountDao.insert(accountSaver);
            }
        });
    }

    @Override
    public Single<long[]> insertList(final List<AccountSaver> list) {
        return Single.fromCallable(new Callable<long[]>() {
            @Override
            public long[] call() {
                return accountDao.insertList(list);
            }
        });
    }

    public Completable update(final AccountSaver accountSaver) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() {
                accountDao.update(accountSaver);
            }
        });
    }

    public Completable delete(final AccountSaver accountSaver) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() {
                accountDao.delete(accountSaver);
            }
        });
    }

    @Override
    public Completable deleteAll() {
        return Completable.fromAction(new Action() {
            @Override
            public void run() {
                accountDao.deleteAll();
            }
        });
    }
}
