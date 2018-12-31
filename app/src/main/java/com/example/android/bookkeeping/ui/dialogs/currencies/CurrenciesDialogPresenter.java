package com.example.android.bookkeeping.ui.dialogs.currencies;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.example.android.bookkeeping.R;
import com.example.android.bookkeeping.ui.mvp.BasePresenter;

import javax.inject.Inject;

public class CurrenciesDialogPresenter<V extends CurrenciesDialogMvpView> extends BasePresenter<V> implements CurrenciesDialogMvpPresenter<V> {

    private String[] currencies;

    @Inject
    public CurrenciesDialogPresenter() {
    }

    @Override
    public ArrayAdapter<String> initAdapter() {
        return new ArrayAdapter<>(getMvpView().getContext(), R.layout.dialog_item, R.id.txt_currency, currencies);
    }


    @Override
    public void setArguments(Bundle args) {
        currencies = args.getStringArray("currencies");
    }

    @Override
    public String getCurrency(int id) {
        return currencies[id];
    }

}
