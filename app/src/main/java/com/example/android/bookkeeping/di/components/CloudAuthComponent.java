package com.example.android.bookkeeping.di.components;

import com.example.android.bookkeeping.di.modules.ActivityModule;
import com.example.android.bookkeeping.di.modules.FirebaseModule;
import com.example.android.bookkeeping.ui.cloud.auth.FirebaseAuthActivity;

import javax.inject.Singleton;

import dagger.Subcomponent;

@Singleton
@Subcomponent(modules = {ActivityModule.class, FirebaseModule.class} )
public interface CloudAuthComponent {
    void inject(FirebaseAuthActivity firebaseAuthActivity);
}
