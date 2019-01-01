package com.example.android.bookkeeping.ui.transaction.create;

import android.content.Intent;
import android.widget.ArrayAdapter;

import com.example.android.bookkeeping.ui.mvp.BasePresenter;

import javax.inject.Inject;

public class CreateTransactionPresenter<V extends CreateTransactionMvpView> extends BasePresenter<V> implements CreateTransactionMvpPresenter<V> {

    private String[] ratesNames;
    private String str1[] = {"in", "out"};

    @Inject
    public CreateTransactionPresenter() {
    }


    @Override
    public void getRatesFromIntent(Intent intent) {
        ratesNames = intent.getStringArrayExtra("ratesNames");
    }

    @Override
    public void btnCurrencyClick() {
        getMvpView().showDialog();
    }

    @Override
    public String[] getRatesNames() {
        return ratesNames;
    }

    @Override
    public ArrayAdapter<String> initAccountsAdapter() {
        ArrayAdapter<String> adapterSp = new ArrayAdapter<>(getMvpView().getContext(), android.R.layout.simple_spinner_item, str1);
        adapterSp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapterSp;
    }

    @Override
    public void btnDoneClick() {
        getMvpView().returnActivityResult();
    }
}
