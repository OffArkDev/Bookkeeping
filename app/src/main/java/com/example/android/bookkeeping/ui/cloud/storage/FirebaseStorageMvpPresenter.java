package com.example.android.bookkeeping.ui.cloud.storage;

import android.content.Intent;

import com.example.android.bookkeeping.ui.mvp.MvpPresenter;

public interface FirebaseStorageMvpPresenter <V extends FirebaseStorageMvpView> extends MvpPresenter<V> {

    void getEmailFromIntent(Intent intent);

    void btnSaveClick();

    void btnLoadClick();
}
