package com.example.android.bookkeeping.data;

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
