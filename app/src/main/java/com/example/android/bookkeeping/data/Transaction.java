package com.example.android.bookkeeping.data;

import android.util.Log;

import com.example.android.bookkeeping.currency.ApiAdapter;
import com.example.android.bookkeeping.currency.CurrencyArray;
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

            if (DownloadActualCurrency.isDownloaded) {
                CurrencyArray currencyArray = new CurrencyArray();
                if (currency.equals("USD")) {
                    valueRUB = DownloadActualCurrency.getRUBFromUSD(value);
                    Log.i(LOG_TAG, "valueRUB " + valueRUB);
                } else if (currency.equals("EUR")) {
                    valueRUB = DownloadActualCurrency.getRUBFromEUR(value);
                    Log.i(LOG_TAG, "valueRUB " + valueRUB);
                }


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
