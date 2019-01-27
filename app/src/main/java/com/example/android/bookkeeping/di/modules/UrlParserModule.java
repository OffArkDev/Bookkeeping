package com.example.android.bookkeeping.di.modules;


import com.example.android.bookkeeping.model.pojo.UrlParser;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class UrlParserModule {

    private UrlParser urlParser;

    public UrlParserModule(String parseUrl) {
        urlParser = new UrlParser(parseUrl);
    }


    @Singleton
    @Provides
    UrlParser providesRxUrlParser() {
        return urlParser;
    }
}
