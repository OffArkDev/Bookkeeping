package com.example.android.bookkeeping.ui.chart;

import android.content.Intent;

import com.example.android.bookkeeping.ui.mvp.MvpPresenter;

public interface ChartMvpPresenter <V extends ChartMvpView> extends MvpPresenter<V> {

    void setRatesFromIntent(Intent intent);

    void setChosenCurrency(String chosenCurrency);

    void loadAndShowCurrencies();
}
