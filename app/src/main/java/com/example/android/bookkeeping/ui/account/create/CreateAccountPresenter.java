package com.example.android.bookkeeping.ui.account.create;

import android.content.Intent;

import com.example.android.bookkeeping.ui.mvp.BasePresenter;

import javax.inject.Inject;

public class CreateAccountPresenter<V extends CreateAccountMvpView> extends BasePresenter<V> implements CreateAccountMvpPresenter<V> {

    private String[] ratesNames;


    @Override
    public void setRatesFromIntent(Intent intent) {
        ratesNames = intent.getStringArrayExtra("rates");
    }

    @Inject
    public CreateAccountPresenter() {
    }

    @Override
    public String[] getRatesNames() {
        return ratesNames;
    }
}
