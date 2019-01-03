package com.example.android.bookkeeping.ui.mvp;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.android.bookkeeping.R;

public abstract class BaseActivity extends AppCompatActivity implements MvpView {


    @Override
    public void onError(int resId) {

    }

    @Override
    public void onError(String message) {

    }

    @Override
    public void showMessage(String message) {
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.some_error), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showMessage(@StringRes int resId) {
        showMessage(getString(resId));
    }


    void onFragmentAttached() {

        }


    @Override
    public Context getContext() {
        return this;
    }


}
