package com.example.android.bookkeeping.data;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity (foreignKeys = @ForeignKey(entity = AccountSaver.class, parentColumns = "id", childColumns = "account_id" , onDelete = CASCADE),
        indices = {@Index("id")})
public class TransactionSaver {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "account_id")
    private long accountId;

    private String type;
    private String name;
    private String date;
    private String value;
    private String valueRUB;
    private String currency;
    private String comment;

    public TransactionSaver(long accountId, String type, String name, String date, String value, String valueRUB, String currency, String comment) {
        this.accountId = accountId;
        this.type = type;
        this.name = name;
        this.date = date;
        this.value = value;
        this.valueRUB = valueRUB;
        this.currency = currency;
        this.comment = comment;
    }

    public long getId() {
        return id;
    }

    public long getAccountId() {
        return accountId;
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

    public void setId(long id) {
        this.id = id;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date) {
        this.date = date;
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

    public void setComment(String comment) {
        this.comment = comment;
    }
}
