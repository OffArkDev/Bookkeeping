package com.example.android.bookkeeping.di.modules;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;

import com.example.android.bookkeeping.R;
import com.example.android.bookkeeping.data.model.AccountSaver;
import com.example.android.bookkeeping.ui.account.AccountsMvpPresenter;
import com.example.android.bookkeeping.ui.account.AccountsMvpView;
import com.example.android.bookkeeping.ui.account.AccountsPresenter;
import com.example.android.bookkeeping.ui.adapters.AccountsListAdapter;
import com.example.android.bookkeeping.ui.dialogs.currencies.CurrenciesDialog;
import com.example.android.bookkeeping.ui.dialogs.currencies.CurrenciesDialogMvpPresenter;
import com.example.android.bookkeeping.ui.dialogs.currencies.CurrenciesDialogMvpView;
import com.example.android.bookkeeping.ui.dialogs.currencies.CurrenciesDialogPresenter;

import java.util.ArrayList;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

@Module
public class ActivityModule {

    private final Context mActivity;

    public ActivityModule(Context activity) {
        mActivity = activity;
    }

    @Singleton
    @Provides
    Context provideContext() {
        return mActivity;
    }

    @Singleton
    @Provides
    CompositeDisposable providesCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Singleton
    @Provides
    AccountsMvpPresenter<AccountsMvpView> provideAccountsMvpPresenter(
            AccountsPresenter<AccountsMvpView> presenter) {
        return presenter;
    }

    @Singleton
    @Provides
    CurrenciesDialogMvpPresenter<CurrenciesDialogMvpView> provideCurrenciesDialogMvpPresenter(CurrenciesDialogPresenter<CurrenciesDialogMvpView> presenter) {
        return presenter;
    }


}
