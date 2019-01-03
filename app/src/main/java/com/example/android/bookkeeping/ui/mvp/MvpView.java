package com.example.android.bookkeeping.ui.mvp;

import android.content.Context;
import android.support.annotation.StringRes;

public interface MvpView {

    void findViews();

    void setOnClickListeners();

    Context getContext();

    void onError(@StringRes int resId);

    void onError(String message);

    void showMessage(String message);

    void showMessage(@StringRes int resId);

}
