package com.example.android.bookkeeping.currency;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("live")
    Call<CurrencyConverter> getUSD(
            @Query("access_key") String key
    );

}
