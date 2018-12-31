package com.example.android.bookkeeping.ui.account;

import com.example.android.bookkeeping.currency.CurrencyRatesData;
import com.example.android.bookkeeping.data.model.AccountSaver;
import com.example.android.bookkeeping.ui.mvp.MvpView;

import java.util.List;

public interface AccountsMvpView extends MvpView {

    void showLoading();

    void hideLoading();

    void updateListView();

    void openCreateAccountActivity(CurrencyRatesData currencyRatesData);

     void openTransactionsActivity(int accountId, List<AccountSaver> listAccounts, CurrencyRatesData currencyRatesData);

    void changeDeleteButtonSate();

    void openCloudActivity();

    void openChartActivity(CurrencyRatesData currencyRatesData);
}
