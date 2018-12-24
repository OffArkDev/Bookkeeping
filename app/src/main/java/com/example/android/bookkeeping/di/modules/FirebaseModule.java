package com.example.android.bookkeeping.di.modules;

import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class FirebaseModule {

    private FirebaseAuth firebaseAuth;

    public FirebaseModule() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Singleton
    @Provides
    FirebaseAuth provideFirebaseAuth() {
        return firebaseAuth;
    }

}
