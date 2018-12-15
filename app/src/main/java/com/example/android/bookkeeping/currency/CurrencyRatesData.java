package com.example.android.bookkeeping.currency;

import com.google.gson.Gson;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;


/** Using list of pairs, can convert currency **/

public class CurrencyRatesData {
    String time;
    List<Pair> ratesList;

    public CurrencyRatesData(List<Pair> ratesList, String time) {
        this.ratesList = ratesList;
        this.time = time;
    }


    public String getTime() {
        return time;
    }

    public BigDecimal getRate(String currency) {
        for (Pair pair : ratesList) {
            if (pair.getCurrency().equals(currency)) {
                return pair.getRate();
            }
        }
        return null;
    }

    public String[] getCurrenciesList() {
        String[] result = new String[ratesList.size()];
        for (int i = 0; i < ratesList.size(); i++) {
            result[i] = ratesList.get(i).getCurrency();
        }
        return result;
    }

    public BigDecimal[] getRatesList() {
        BigDecimal[] result = new BigDecimal[ratesList.size()];
        for (int i = 0; i < ratesList.size(); i++) {
            result[i] = ratesList.get(i).getRate();
        }
        return result;
    }

    public BigDecimal convertCurrency(BigDecimal currentValue, String currentCurrency, String resultCurrency) {
        BigDecimal currentRate = getRate(currentCurrency);
        BigDecimal resultRate = getRate(resultCurrency);
        BigDecimal someValue = currentValue.multiply(resultRate);
        if (resultCurrency.equals("EUR")) {
            return someValue.divide(currentRate, RoundingMode.HALF_EVEN);
        }
        return someValue.divide(currentRate, RoundingMode.HALF_EVEN);
    }



}
