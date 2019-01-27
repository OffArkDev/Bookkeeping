package com.example.android.bookkeeping.ui.account;

import android.content.Context;

import com.example.android.bookkeeping.model.pojo.CurrenciesRatesData;
import com.example.android.bookkeeping.model.AccountSaver;
import com.example.android.bookkeeping.ui.mvp.MvpView;

import java.util.List;

public interface AccountsMvpView extends MvpView {

    Context getContext();

    void showLoading();

    void hideLoading();

    void updateListView(List<AccountSaver> listAccounts);

    void openCreateAccountActivity(String[] currenciesNames);

    void openTransactionsActivity(int accountId, List<AccountSaver> listAccounts, String[] currenciesNames);

    void changeDeleteButtonState();

    void openCloudActivity();

    void openChartActivity(CurrenciesRatesData currenciesRatesData);
}
