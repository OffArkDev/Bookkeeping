package com.example.android.bookkeeping.ui.dialogs.history;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import com.example.android.bookkeeping.ui.mvp.MvpPresenter;

public interface CurrenciesHistoryMvpPresenter<V extends CurrenciesHistoryMvpView> extends MvpPresenter<V> {

    ArrayAdapter<String> initAdapter(Context context);

    void setArguments(Bundle args);

    void btnCancelClick();

    void btnDoneClick();

    void itemGridViewClick(int id, View view);
}
