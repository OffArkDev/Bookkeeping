package com.example.android.bookkeeping.ui.transaction;

import android.content.Intent;

import com.example.android.bookkeeping.model.pojo.CurrenciesRatesData;
import com.example.android.bookkeeping.ui.mvp.MvpPresenter;

public interface TransactionMvpPresenter <V extends TransactionMvpView> extends MvpPresenter<V> {

    void btnDeleteClick();

    void btnCreateTransactionClick();

    void itemTransactionsClick(int accountId, boolean isDeleteClicked);

    void getDataFromIntent(Intent intent);

    void createTransaction(String name, String value, String currency, String date, String type, String comment, CurrenciesRatesData currenciesRatesData);
}
