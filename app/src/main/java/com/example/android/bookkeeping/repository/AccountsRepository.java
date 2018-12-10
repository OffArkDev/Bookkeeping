package com.example.android.bookkeeping.repository;

import com.example.android.bookkeeping.data.AccountSaver;

import java.util.List;

import io.reactivex.Flowable;

public interface AccountsRepository {

    Flowable<List<AccountSaver>> getAll();

    AccountSaver getById(long id);

    void insert(AccountSaver accountSaver);

    void update(AccountSaver accountSaver);

    void delete(AccountSaver accountSaver);
}
