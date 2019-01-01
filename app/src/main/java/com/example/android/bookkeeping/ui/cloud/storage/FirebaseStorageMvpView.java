package com.example.android.bookkeeping.ui.cloud.storage;

import com.example.android.bookkeeping.ui.mvp.MvpView;

public interface FirebaseStorageMvpView extends MvpView {

    void showLoading();

    void hideLoading();

    void returnResult();
}
