package com.example.android.bookkeeping.currency;

import java.math.BigDecimal;

public class Pair
{
    private String currency;
    private BigDecimal rate;

    public Pair(String currency, BigDecimal rate)
    {
        this.currency = currency;
        this.rate = rate;
    }

    public String getCurrency() {
        return currency;
    }

    public BigDecimal getRate() {
        return rate;
    }
}
