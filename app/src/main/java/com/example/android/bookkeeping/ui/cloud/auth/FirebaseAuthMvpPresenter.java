package com.example.android.bookkeeping.ui.cloud.auth;

import com.example.android.bookkeeping.ui.mvp.MvpPresenter;

public interface FirebaseAuthMvpPresenter<V extends FirebaseAuthMvpView> extends MvpPresenter<V> {

    void btnRegistrationClick(String email , String password);

    void btnAuthorizationClick(String email , String password);

}
