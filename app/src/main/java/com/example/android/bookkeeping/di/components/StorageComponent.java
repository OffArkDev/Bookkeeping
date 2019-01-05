package com.example.android.bookkeeping.di.components;

import com.example.android.bookkeeping.di.modules.ActivityModule;
import com.example.android.bookkeeping.di.modules.StorageModule;
import com.example.android.bookkeeping.ui.cloud.FirebaseStorageActivity;
import com.example.android.bookkeeping.ui.transaction.TransactionsActivity;

import javax.inject.Singleton;

import dagger.Subcomponent;


@Singleton
@Subcomponent(modules = {ActivityModule.class, StorageModule.class})
public interface StorageComponent {
    void inject(TransactionsActivity transactionsActivity);

    void inject(FirebaseStorageActivity firebaseStorageActivity);

}
