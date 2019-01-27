package com.example.android.bookkeeping.ui.transaction;

import com.example.android.bookkeeping.model.TransactionSaver;
import com.example.android.bookkeeping.ui.mvp.MvpView;

import java.util.List;

public interface TransactionMvpView extends MvpView {

    void changeDeleteButtonState();

    void openCreateTransactionActivity(String[] ratesNames);

    void updateListView(List<TransactionSaver> listTransactions);
}
