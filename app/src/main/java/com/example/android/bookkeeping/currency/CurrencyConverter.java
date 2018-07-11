package com.example.android.bookkeeping.currency;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CurrencyConverter {

    @SerializedName("quotes")
    @Expose
    CurrencyArray currencyArray;

    public CurrencyArray getCurrencyArray() {
        return currencyArray;
    }
}
