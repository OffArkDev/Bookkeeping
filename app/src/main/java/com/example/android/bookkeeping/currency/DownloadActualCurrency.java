package com.example.android.bookkeeping.currency;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

public class DownloadActualCurrency extends AsyncTask <Void, Void, CurrencyConverter> {
    public static final String LOG_TAG = "myDownloadCurr";

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.i("uRi", "start of downloading");
    }

    @Override
    protected CurrencyConverter doInBackground(Void... voids) {

        Log.i(LOG_TAG, "-----------downloading currency-----------");
        ApiAdapter adapter = new ApiAdapter();
        CurrencyConverter currencyConverter = new CurrencyConverter();
        try {
           currencyConverter = adapter.createRequest().getUSD(ApiAdapter.key).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return currencyConverter;
    }

    @Override
    protected void onPostExecute(CurrencyConverter currencyConverter) {
        super.onPostExecute(currencyConverter);
        Log.i("uRi", "end of downloading");

    }
}
