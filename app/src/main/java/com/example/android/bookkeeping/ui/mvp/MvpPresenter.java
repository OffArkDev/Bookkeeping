package com.example.android.bookkeeping.ui.mvp;

public interface MvpPresenter<V extends MvpView> {

    void onAttach(V mvpView);

    void onDetach();

}

