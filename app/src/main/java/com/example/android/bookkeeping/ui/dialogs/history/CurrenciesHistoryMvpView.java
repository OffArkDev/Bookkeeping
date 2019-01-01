package com.example.android.bookkeeping.ui.dialogs.history;

import android.view.View;

import com.example.android.bookkeeping.ui.mvp.DialogMvpView;

public interface CurrenciesHistoryMvpView extends DialogMvpView {

    void changeCurrencyViewColor(View view);

    void returnResult(String chosenCurrency);
}
