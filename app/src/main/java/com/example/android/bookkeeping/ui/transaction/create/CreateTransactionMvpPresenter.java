package com.example.android.bookkeeping.ui.transaction.create;

import android.content.Intent;
import android.widget.ArrayAdapter;

import com.example.android.bookkeeping.ui.mvp.MvpPresenter;

public interface CreateTransactionMvpPresenter<V extends CreateTransactionMvpView> extends MvpPresenter<V> {

    void getRatesFromIntent(Intent intent);

    void btnCurrencyClick();

    void btnDoneClick();

    String[] getRatesNames();

    ArrayAdapter<String> initAccountsAdapter();

    boolean checkInputDataFormat(String value, String sDate);


}
