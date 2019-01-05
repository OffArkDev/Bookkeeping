package com.example.android.bookkeeping.currency;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;



public class CurrenciesRatesData {
    String time;
    List<Pair> ratesList;

    public CurrenciesRatesData(List<Pair> ratesList, String time) {
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


    public String convertCurrency(String currentValue, String currentCurrency, String resultCurrency) {
        BigDecimal curValue = new BigDecimal(currentValue);
        BigDecimal currentRate = getRate(currentCurrency);
        if (resultCurrency.equals("EUR")) {
            return curValue.divide(currentRate, RoundingMode.HALF_EVEN).toString();
        }
        BigDecimal resultRate = getRate(resultCurrency);
        BigDecimal someValue = curValue.multiply(resultRate);
        return someValue.divide(currentRate, RoundingMode.HALF_EVEN).toString();
    }



}
