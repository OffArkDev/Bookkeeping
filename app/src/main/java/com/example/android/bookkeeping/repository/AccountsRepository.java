package com.example.android.bookkeeping.repository;

import com.example.android.bookkeeping.data.AccountSaver;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public interface AccountsRepository {

    Flowable<List<AccountSaver>> getAll();

    AccountSaver getById(long id);

    Completable insert(AccountSaver accountSaver);

    Completable update(AccountSaver accountSaver);

    Completable delete(AccountSaver accountSaver);
}
