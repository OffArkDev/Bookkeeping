package com.example.android.bookkeeping.currency;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.text.DecimalFormat;

public class DownloadActualCurrency extends AsyncTask <Void, Void, CurrencyConverter> {
    public static final String LOG_TAG = "myDownloadCurr";
    public static boolean isDownloaded = false;
    public static String USDRUB ;
    public static String USDEUR;

    public static DecimalFormat df = new DecimalFormat("##.##");

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
        isDownloaded = true;
        USDEUR = currencyConverter.currencyArray.getUSDEUR();
        USDRUB = currencyConverter.currencyArray.getUSDRUB();
    }

    public String getUSDRUB() {
        return USDRUB;
    }

    public String getUSDEUR() {
        return USDEUR;
    }

    public static String getRUBFromEUR(String eur) {
        String str = eur.replace(",", ".");
        String usd = getUSDFromEUR(str);
        return getRUBFromUSD(usd);
    }

    public static String getUSDFromEUR(String eur) {
        String str = eur.replace(",", ".");
        double d = Double.parseDouble(str);
        double rate = Double.parseDouble(USDEUR);
        double result = d / rate;

        return df.format(result);
    }


    public static String getRUBFromUSD(String usd) {

        String str = usd.replace(",", ".");

        double d = Double.parseDouble(str);
        double rate = Double.parseDouble(USDRUB);
        double result = d * rate;

        return df.format(result);
    }

    public static String getUSDFromRUB(String rub) {
        String str = rub.replace(",", ".");
        double d = Double.parseDouble(str);
        double rate = Double.parseDouble(USDRUB);
        double result = d * rate;

        return df.format(result);
    }



}
