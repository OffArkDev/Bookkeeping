package com.example.android.bookkeeping.ui.account.create;

import android.content.Intent;

import com.example.android.bookkeeping.Constants;
import com.example.android.bookkeeping.R;
import com.example.android.bookkeeping.ui.mvp.BasePresenter;
import com.example.android.bookkeeping.utils.DateUtil;

import java.util.Date;

import javax.inject.Inject;

public class CreateAccountPresenter<V extends CreateAccountMvpView> extends BasePresenter<V> implements CreateAccountMvpPresenter<V> {

    private String[] ratesNames;


    @Override
    public void getRatesFromIntent(Intent intent) {
        ratesNames = intent.getStringArrayExtra("rates");
    }

    @Inject
    public CreateAccountPresenter() {
    }

    @Override
    public String[] getRatesNames() {
        return ratesNames;
    }

    @Override
    public void btnCurrencyClick() {
        getMvpView().showCurrenciesDialog();
    }

    @Override
    public void btnDoneClick() {
        getMvpView().returnActivityResult();
    }

    @Override
    public boolean checkInputDataFormat(String name) {
        if (name.equals("")) {
            getMvpView().showMessage(R.string.write_name);
            return false;
        }
        return true;
    }
}
