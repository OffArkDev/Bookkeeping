package com.example.android.bookkeeping.di.components;


import com.example.android.bookkeeping.di.modules.AppModule;
import com.example.android.bookkeeping.di.modules.FirebaseModule;
import com.example.android.bookkeeping.di.scopes.AppScope;
import com.example.android.bookkeeping.di.modules.ActivityModule;
import com.example.android.bookkeeping.di.modules.UrlParserModule;
import com.example.android.bookkeeping.di.modules.StorageModule;

import dagger.Component;


@AppScope
@Component(
        modules = {
                AppModule.class,
        }
)
public interface AppComponent {
    StorageParserComponent newStorageUrlParserComponent(ActivityModule activityModule, StorageModule storageModule, UrlParserModule urlParserModule);
    StorageComponent newStorageComponent(ActivityModule activityModule, StorageModule storageModule);
    UrlParserComponent newUrlParserComponent(ActivityModule activityModule, UrlParserModule urlParserModule);
    FirebaseComponent newFirebaseComponent(ActivityModule activityModule, FirebaseModule firebaseModule);
    FragmentComponent newFragmentComponent(ActivityModule activityModule);


}
