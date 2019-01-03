package com.example.android.bookkeeping.repository;

import com.example.android.bookkeeping.data.model.AccountSaver;

import java.util.List;
import java.util.Observable;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public interface AccountsRepository {


    Flowable<List<AccountSaver>> getAll();

    AccountSaver getById(long id);

    Completable updateValueRub(long id, String valueRUB);

    Single<Long> insert(final AccountSaver accountSaver);

    Single<long[]> insertList(List<AccountSaver> list);

    Completable update(final AccountSaver accountSaver);

    Completable delete(final AccountSaver accountSaver);

    Completable deleteAll();
}


