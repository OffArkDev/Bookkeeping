package com.example.android.bookkeeping.repository;

import com.example.android.bookkeeping.data.AccountDao;
import com.example.android.bookkeeping.data.AccountSaver;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.functions.Action;

public class AccountsDataSource implements AccountsRepository {
    private AccountDao accountDao;

    public AccountsDataSource(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Override
    public Flowable<List<AccountSaver>> getAll() {
        return accountDao.getAll();
    }

    @Override
    public AccountSaver getById(long id) {
        return accountDao.getById(id);
    }

    @Override
    public Completable insert(final AccountSaver accountSaver) {
      return   Completable.fromAction(new Action() {
            @Override
            public void run() {
                accountDao.insert(accountSaver);
            }
        });
    }

    @Override
    public Completable update(final AccountSaver accountSaver) {
        return   Completable.fromAction(new Action() {
            @Override
            public void run() {
                accountDao.update(accountSaver);
            }
        });
    }

    @Override
    public Completable delete(final AccountSaver accountSaver) {
        return   Completable.fromAction(new Action() {
            @Override
            public void run() {
                accountDao.delete(accountSaver);
            }
        });
    }

}
