package com.example.android.bookkeeping.ui.dialogs.currencies;

import com.example.android.bookkeeping.ui.mvp.BasePresenter;

import javax.inject.Inject;

public class CurrenciesDialogPresenter<V extends CurrenciesDialogMvpView> extends BasePresenter<V> implements CurrenciesDialogMvpPresenter<V> {


    @Inject
    public CurrenciesDialogPresenter() {
    }
}
