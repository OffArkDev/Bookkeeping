package com.example.android.bookkeeping.model.pojo;

import java.math.BigDecimal;

public class Pair
{
    private String currency;
    private BigDecimal rate;

    Pair(String currency, BigDecimal rate)
    {
        this.currency = currency;
        this.rate = rate;
    }

    public String getCurrency() {
        return currency;
    }

    BigDecimal getRate() {
        return rate;
    }
}
