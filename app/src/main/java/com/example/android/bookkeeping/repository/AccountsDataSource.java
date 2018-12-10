package com.example.android.bookkeeping.repository;

import android.util.Log;

import com.example.android.bookkeeping.data.AccountDao;
import com.example.android.bookkeeping.data.AccountSaver;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

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
    public void insert(AccountSaver accountSaver) {

        Observable.fromCallable(new CallableInsert(accountSaver))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(final Integer integer) throws Exception {
                        Log.i("tag", "success " + integer);
                    }
                });

      //  accountDao.insert(accountSaver);
    }

    @Override
    public void update(AccountSaver accountSaver) {
        accountDao.update(accountSaver);
    }

    @Override
    public void delete(AccountSaver accountSaver) {
        accountDao.delete(accountSaver);
    }

    class CallableInsert implements Callable <Integer>{
        private AccountSaver accountSaver;

        public CallableInsert(AccountSaver accountSaver) {
            this.accountSaver = accountSaver;
        }

        @Override
        public Integer call() throws Exception {
            accountDao.insert(accountSaver);
            return 0;
        }
    }
}
