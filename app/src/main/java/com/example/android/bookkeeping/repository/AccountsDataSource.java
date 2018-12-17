package com.example.android.bookkeeping.repository;

import com.example.android.bookkeeping.data.AccountDao;
import com.example.android.bookkeeping.data.AccountSaver;

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

    public Single<Long> insert(final AccountSaver accountSaver) {

        return Single.fromCallable(new Callable<Long>() {
            @Override
            public Long call () throws Exception {
                return accountDao.insert(accountSaver);
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

}
