package com.example.android.bookkeeping.ui.dialogs.currencies;

import com.example.android.bookkeeping.ui.mvp.DialogMvpView;

public interface CurrenciesDialogMvpView extends DialogMvpView {

    void returnResult(String chosenCurrency);

    void dismissDialog();

}
