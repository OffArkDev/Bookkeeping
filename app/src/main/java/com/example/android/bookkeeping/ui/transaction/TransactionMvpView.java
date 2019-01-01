package com.example.android.bookkeeping.ui.transaction;

import com.example.android.bookkeeping.currency.CurrencyRatesData;
import com.example.android.bookkeeping.data.model.TransactionSaver;
import com.example.android.bookkeeping.ui.mvp.MvpView;

import java.util.List;

public interface TransactionMvpView extends MvpView {

    void changeDeleteButtonState();

    void openCreateTransactionActivity(CurrencyRatesData currencyRatesData);

    void updateListView(List<TransactionSaver> listTransactions);


}
