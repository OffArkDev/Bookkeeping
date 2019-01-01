package com.example.android.bookkeeping.ui.cloud.auth;

import com.example.android.bookkeeping.ui.mvp.MvpView;

public interface FirebaseAuthMvpView extends MvpView {

    void showLoading();

    void hideLoading();

    void signIn(String email, String password);

    void registration(String email, String password);

    void openFirebaseStorageActivity(String email);
}
