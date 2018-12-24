package com.example.android.bookkeeping.data;

import com.example.android.bookkeeping.data.AccountSaver;
import com.example.android.bookkeeping.data.TransactionSaver;

import java.util.List;

public class DataPOJO {
  private  List<AccountSaver> accountsList;
  private  List<TransactionSaver> transactionList;

    public void setAccountsList(List<AccountSaver> accountsList) {
        this.accountsList = accountsList;
    }

    public void setTransactionList(List<TransactionSaver> transactionList) {
        this.transactionList = transactionList;
    }

    public List<AccountSaver> getAccountsList() {
        return accountsList;
    }

    public List<TransactionSaver> getTransactionList() {
        return transactionList;
    }
}
