package com.example.android.bookkeeping.currency;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;


/** Using list of pairs, can convert currency **/

public class CurrencyRatesData  {
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

    public BigDecimal convertCurrency(BigDecimal value, String currentCurrency, String resultCurrency) {
        BigDecimal currentRate = getRate(currentCurrency);
        BigDecimal valueEuro = value.divide(currentRate, RoundingMode.HALF_EVEN);

        if (resultCurrency.equals("EUR")) {
            return valueEuro;
        }
        BigDecimal resultRate = getRate(resultCurrency);

        return valueEuro.multiply(resultRate);
    }

}
