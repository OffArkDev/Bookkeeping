package com.example.android.bookkeeping.ui.mvp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity implements MvpView {



    void onFragmentAttached() {

        }

        void onFragmentDetached(String tag) {

        }

    @Override
    public Context getContext() {
        return this;
    }
}
