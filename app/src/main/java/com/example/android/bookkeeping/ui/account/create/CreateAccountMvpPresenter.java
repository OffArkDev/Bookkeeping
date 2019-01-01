package com.example.android.bookkeeping.ui.account.create;

import android.content.Intent;

import com.example.android.bookkeeping.ui.mvp.MvpPresenter;

public interface CreateAccountMvpPresenter<V extends CreateAccountMvpView> extends MvpPresenter<V> {

    void getRatesFromIntent(Intent intent);

    String[] getRatesNames();

    void btnCurrencyClick();

    void btnDoneClick();

}
