package com.example.android.bookkeeping.ui.dialogs.currencies;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.example.android.bookkeeping.ui.mvp.MvpPresenter;

public interface CurrenciesDialogMvpPresenter<V extends CurrenciesDialogMvpView> extends MvpPresenter<V> {

    ArrayAdapter<String> initAdapter();

    void setArguments(Bundle args);

    String getCurrency(int id);

    void btnCancelClick();

    void itemGridViewClick(int id);

}
