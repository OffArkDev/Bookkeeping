package com.example.android.bookkeeping.data;

import java.util.ArrayList;

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
    }


    public void setToTransactions (Transaction transaction) {
        transactions.add(transaction);
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
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

    public void setValueRUB(String valueRUB) {
        this.valueRUB = valueRUB;
    }
}
