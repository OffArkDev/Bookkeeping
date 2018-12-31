package com.example.android.bookkeeping.ui.account.create;

import com.example.android.bookkeeping.ui.mvp.BasePresenter;

import javax.inject.Inject;

public class CreateAccountPresenter<V extends CreateAccountMvpView> extends BasePresenter<V> implements CreateAccountMvpPresenter<V> {

    private String[] currencies;

    @Inject
    public CreateAccountPresenter() {
    }

    @Override
    public void onAttach(V mvpView) {
        super.onAttach(mvpView);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
