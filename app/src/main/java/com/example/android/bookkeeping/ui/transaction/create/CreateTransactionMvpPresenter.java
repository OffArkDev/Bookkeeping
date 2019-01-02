package com.example.android.bookkeeping.ui.transaction.create;

import android.content.Intent;
import android.widget.ArrayAdapter;

import com.example.android.bookkeeping.currency.CurrenciesRatesData;
import com.example.android.bookkeeping.ui.mvp.MvpPresenter;

public interface CreateTransactionMvpPresenter<V extends CreateTransactionMvpView> extends MvpPresenter<V> {

    void getDataFromIntent(Intent intent);

    void btnCurrencyClick();

    void btnDoneClick();

    String[] getCurrenciesNames();

    ArrayAdapter<String> initAccountsAdapter();

    boolean checkInputDataFormat(String value, String sDate);

    CurrenciesRatesData getCurrenciesRatesData(String date);

}
