package com.example.android.bookkeeping.data;

import android.util.Log;

import com.example.android.bookkeeping.currency.ApiAdapter;
import com.example.android.bookkeeping.currency.CurrencyArray;
import com.example.android.bookkeeping.currency.CurrencyConverter;
import com.example.android.bookkeeping.currency.DownloadActualCurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AccountData {
    private final static String LOG_TAG = "myAccount";

   private String name;
   private String value;
    private String valueRUB;

    private String currency;
    private ArrayList<Transaction> transactions = new ArrayList<>();

    public AccountData(String name, String value, String currency) {
        this.name = name;
        this.value = value;
        this.currency = currency;
        convertValueRUB();
    }


    public void setTransaction(Transaction transaction) {
        transactions.add(transaction);
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
                if (currency.equals("USD")) {
                    valueRUB = DownloadActualCurrency.getRUBFromUSD(value);
                    Log.i(LOG_TAG, "valueRUB " + valueRUB);
                } else if (currency.equals("EUR")) {
                    valueRUB = DownloadActualCurrency.getRUBFromEUR(value);
                    Log.i(LOG_TAG, "valueRUB " + valueRUB);
                }
        }
    }

    public String getLastTransaction() {
        if (transactions == null) {
            return "no last transaction";
        }
        if (!transactions.isEmpty()) {
            String lastTransaction = transactions.get(transactions.size() - 1).getDataString();
            if (lastTransaction == null || lastTransaction.equals(""))
                return "no last transactions";
            else return lastTransaction;
        } else  return "no last transactions";
    }
}
