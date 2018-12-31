package com.example.android.bookkeeping.ui.mvp;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;

import com.example.android.bookkeeping.di.components.FragmentComponent;

public abstract class BaseDialog extends DialogFragment implements DialogMvpView {

    private BaseActivity mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            BaseActivity mActivity = (BaseActivity) context;
            this.mActivity = mActivity;
            mActivity.onFragmentAttached();
        }
    }


    @Override
    public void onError(String message) {
        if (mActivity != null) {
            mActivity.onError(message);
        }
    }

    @Override
    public void onError(@StringRes int resId) {
        if (mActivity != null) {
            mActivity.onError(resId);
        }
    }

    @Override
    public void showMessage(String message) {
        if (mActivity != null) {
            mActivity.showMessage(message);
        }
    }

    @Override
    public void showMessage(@StringRes int resId) {
        if (mActivity != null) {
            mActivity.showMessage(resId);
        }
    }


    @Override
    public void onDetach() {
        mActivity = null;
        super.onDetach();
    }


    public BaseActivity getBaseActivity() {
        return mActivity;
    }


}
