package com.example.android.bookkeeping.ui.account;

import com.example.android.bookkeeping.ui.adapters.AccountsListAdapter;
import com.example.android.bookkeeping.ui.mvp.MvpPresenter;


public interface AccountsMvpPresenter <V extends AccountsMvpView> extends MvpPresenter<V> {

    void btnCreateAccountClick();

    void btnDeleteAccount();

    void btnCloudClick();

    void btnChartClick();

    void itemAccountsClick(int accountId, boolean isDeleteClicked);

    void createAccount(String name, String value, String currency);

    void getAccountsFromDatabase();


}
