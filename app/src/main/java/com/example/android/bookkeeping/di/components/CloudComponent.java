package com.example.android.bookkeeping.di.components;


import com.example.android.bookkeeping.di.modules.ActivityModule;
import com.example.android.bookkeeping.di.modules.StorageModule;
import com.example.android.bookkeeping.ui.cloud.FirebaseStorageActivity;

import dagger.Subcomponent;

@Subcomponent(modules = {ActivityModule.class, StorageModule.class})
public interface CloudComponent {
    void inject(FirebaseStorageActivity firebaseStorageActivity);
}
