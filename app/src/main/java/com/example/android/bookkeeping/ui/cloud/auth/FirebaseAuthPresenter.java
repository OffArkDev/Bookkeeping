package com.example.android.bookkeeping.ui.cloud.auth;

import com.example.android.bookkeeping.ui.mvp.BasePresenter;

import javax.inject.Inject;

public class FirebaseAuthPresenter <V extends FirebaseAuthMvpView> extends BasePresenter<V> implements FirebaseAuthMvpPresenter<V> {

    private final static String TAG = "authPresenter";



    @Inject
    public FirebaseAuthPresenter() {
    }

    @Override
    public void btnRegistrationClick(String email, String password) {
        getMvpView().registration(email, password);
    }

    @Override
    public void btnAuthorizationClick(String email , String password) {
        getMvpView().signIn(email, password);
    }
}
