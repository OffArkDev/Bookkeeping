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
    AccountComponent newAccountComponent(ActivityModule activityModule, StorageModule storageModule, UrlParserModule urlParserModule);
    TransactionComponent newTransactionComponent(ActivityModule activityModule, StorageModule storageModule);
    ChartComponent newChartComponent(ActivityModule activityModule, UrlParserModule urlParserModule);
    CloudStorageComponent newCloudStorageComponent(ActivityModule activityModule, StorageModule storageModule);
    CloudAuthComponent newCloudAuthComponent(ActivityModule activityModule, FirebaseModule firebaseModule);
    FragmentComponent newFragmentComponent(ActivityModule activityModule);


}
