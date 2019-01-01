package com.example.android.bookkeeping.ui.account.create;

import com.example.android.bookkeeping.ui.mvp.MvpView;

public interface CreateAccountMvpView extends MvpView {

    void showCurrenciesDialog();

    void returnActivityResult();

}
