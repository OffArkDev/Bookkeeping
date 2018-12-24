package com.example.android.bookkeeping.di.modules;


import com.example.android.bookkeeping.currency.UrlParser;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class UrlParserModule {

    private UrlParser urlParser;

    public UrlParserModule(String parseUrl) {
        urlParser = new UrlParser(parseUrl);
    }


    @Provides
    UrlParser providesRxUrlParser() {
        return urlParser;
    }
}
