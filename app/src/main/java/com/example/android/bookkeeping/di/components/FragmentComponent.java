package com.example.android.bookkeeping.di.components;

import com.example.android.bookkeeping.di.modules.ActivityModule;
import com.example.android.bookkeeping.ui.account.CreateAccountActivity;

import javax.inject.Singleton;

import dagger.Subcomponent;

@Singleton
@Subcomponent(modules = {ActivityModule.class})

public interface FragmentComponent {

    void inject(CreateAccountActivity createAccountActivity);
}
