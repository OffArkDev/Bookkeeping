package com.example.android.bookkeeping.currency;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiAdapter {
    private final static String LOG_TAG = "myApiAdapter";

    private Retrofit retrofit;
    private ApiService apiService;

    private String url;
    public static String key;

        public ApiAdapter() {
        url = "http://apilayer.net/api/";
        key = "dee17357e24501fdc5513f507e1a13eb";

        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(new OkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }
    public ApiService createRequest() {
        return this.apiService;
        }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
