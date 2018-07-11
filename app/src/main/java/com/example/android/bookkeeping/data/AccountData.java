package com.example.android.bookkeeping.data;

import android.util.Log;

import com.example.android.bookkeeping.currency.ApiAdapter;
import com.example.android.bookkeeping.currency.CurrencyConverter;
import com.example.android.bookkeeping.currency.DownloadActualCurrency;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class AccountData {
    private final static String LOG_TAG = "myAccount";

   private String name;
   private String value;
    private String valueRUB;

    private String currency;
   private List<Transaction> transactions;

    public AccountData(String name, String value, String currency) {
        this.name = name;
        this.value = value;
        this.currency = currency;
        convertValueRUB();
    }




    public String getValue() {
        return value;
    }

    public String getValueRUB() {
        return valueRUB;
    }

    public String getName() {
        return name;
    }

    public String getCurrency() {
        return currency;
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

    public String getLastTransaction() {
        if (transactions == null) {
            return "no last transaction";
        }
        String lastTransaction = transactions.get(transactions.size() - 1).getDataString();
        if (lastTransaction == null || lastTransaction.equals(""))
            return "no last transactions";
        else return lastTransaction;
    }
}
