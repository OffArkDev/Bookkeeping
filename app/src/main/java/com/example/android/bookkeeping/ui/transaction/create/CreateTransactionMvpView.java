package com.example.android.bookkeeping.ui.transaction.create;

import com.example.android.bookkeeping.ui.mvp.MvpView;

public interface CreateTransactionMvpView extends MvpView {

    void showDialog();

    void returnActivityResult();

    void showLoading();

    void hideLoading();
}
