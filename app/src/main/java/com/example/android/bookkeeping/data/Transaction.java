package com.example.android.bookkeeping.data;

import android.util.Log;

import com.example.android.bookkeeping.currency.ApiAdapter;
import com.example.android.bookkeeping.currency.CurrencyConverter;
import com.example.android.bookkeeping.currency.DownloadActualCurrency;

import java.util.concurrent.ExecutionException;

public class Transaction {
    private final static String LOG_TAG = "myTransaction";

    private String type;
    private String name;
    private String date;
    private String value;
    private String valueRUB;
    private String currency;
    private String comment;


    public Transaction(String type, String name, String date, String value, String currency, String comment) {
        this.type = type;
        this.name = name;
        this.date = date;
        this.value = value;
        this.currency = currency;
        this.comment = comment;
    }

    public void convertValueRUB() {
        if (currency.equals("RUB")) {
            valueRUB = value;
        } else {

            ApiAdapter adapter = new ApiAdapter();
            CurrencyConverter currencyConverter = new CurrencyConverter();

            try {
                currencyConverter =  new DownloadActualCurrency().execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();            }

            Log.i(LOG_TAG, "RUB " + currencyConverter.getCurrencyArray().getUSDRUB() + " EUR " + currencyConverter.getCurrencyArray().getUSDEUR());
            Log.i(LOG_TAG, "currency " + currency);
            if (currency.equals("USD")) {
                valueRUB = currencyConverter.getCurrencyArray().getRUBFromUSD(value);
                Log.i(LOG_TAG, "valueRUB " + valueRUB);
            } else if (currency.equals("EUR")) {
                valueRUB = currencyConverter.getCurrencyArray().getRUBFromEUR(value);
                Log.i(LOG_TAG, "valueRUB " + valueRUB);
            }
        }
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getValue() {
        return value;
    }

    public String getValueRUB() {
        return valueRUB;
    }

    public String getCurrency() {
        return currency;
    }

    public String getComment() {
        return comment;
    }

    public String getDataString() {

        return String.format("Date %s, value %s, currency %s, comment %s", date, value, currency, comment);
    }

}
