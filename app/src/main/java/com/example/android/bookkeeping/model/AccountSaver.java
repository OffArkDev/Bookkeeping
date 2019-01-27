package com.example.android.bookkeeping.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class AccountSaver {

    @PrimaryKey(autoGenerate = true)
    public long id;

    private String name;
    private String value;
    private String valueRUB;
    private String currency;

    public AccountSaver(String name, String value, String valueRUB, String currency) {
        this.name = name;
        this.value = value;
        this.valueRUB = valueRUB;
        this.currency = currency;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setValueRUB(String valueRUB) {
        this.valueRUB = valueRUB;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getName() {
        return name;
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
