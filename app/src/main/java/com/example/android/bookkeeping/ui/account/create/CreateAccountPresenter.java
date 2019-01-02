package com.example.android.bookkeeping.ui.account.create;

import android.content.Intent;

import com.example.android.bookkeeping.R;
import com.example.android.bookkeeping.ui.mvp.BasePresenter;

import javax.inject.Inject;

public class CreateAccountPresenter<V extends CreateAccountMvpView> extends BasePresenter<V> implements CreateAccountMvpPresenter<V> {

    private String[] currenciesNames;


    @Override
    public void getRatesFromIntent(Intent intent) {
        currenciesNames = intent.getStringArrayExtra("currenciesNames");
    }

    @Inject
    public CreateAccountPresenter() {
    }

    @Override
    public String[] getCurrenciesNames() {
        return currenciesNames;
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
