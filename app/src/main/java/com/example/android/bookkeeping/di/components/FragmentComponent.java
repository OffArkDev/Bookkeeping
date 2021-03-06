package com.example.android.bookkeeping.di.components;

import com.example.android.bookkeeping.di.modules.ActivityModule;
import com.example.android.bookkeeping.ui.account.create.CreateAccountActivity;
import com.example.android.bookkeeping.ui.dialogs.date.DateDialog;
import com.example.android.bookkeeping.ui.dialogs.history.CurrenciesHistoryDialog;
import com.example.android.bookkeeping.ui.dialogs.currencies.CurrenciesDialog;
import com.example.android.bookkeeping.ui.transaction.create.CreateTransactionActivity;

import javax.inject.Singleton;

import dagger.Subcomponent;

@Singleton
@Subcomponent(modules = {ActivityModule.class})

public interface FragmentComponent {

    void inject(CreateAccountActivity createAccountActivity);

    void inject(CurrenciesDialog currenciesDialog);

    void inject(CurrenciesHistoryDialog currenciesHistoryDialog);

    void inject(DateDialog dateDialog);

}
