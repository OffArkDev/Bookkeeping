package com.example.android.bookkeeping.ui.dialogs.history;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import com.example.android.bookkeeping.R;
import com.example.android.bookkeeping.ui.mvp.BasePresenter;

import javax.inject.Inject;

public class CurrenciesHistoryPresenter<V extends CurrenciesHistoryMvpView> extends BasePresenter<V> implements CurrenciesHistoryMvpPresenter<V> {

    private String[] currencies;

    private String chosenCurrency;

    @Inject
    public CurrenciesHistoryPresenter() {
    }

    @Override
    public ArrayAdapter<String> initAdapter(Context context) {
        return new ArrayAdapter<>(context, R.layout.dialog_item, R.id.txt_currency, currencies);
    }

    @Override
    public void setArguments(Bundle args) {
        currencies = args.getStringArray("currencies");
    }

    @Override
    public void btnCancelClick() {
        getMvpView().dismissDialog();
    }

    @Override
    public void btnDoneClick() {
        getMvpView().returnResult(chosenCurrency);
    }

    @Override
    public void itemGridViewClick(int id, View view) {
        chosenCurrency = currencies[id];
        getMvpView().changeCurrencyViewColor(view);
    }
}
