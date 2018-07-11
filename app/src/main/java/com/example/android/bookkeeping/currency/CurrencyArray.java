package com.example.android.bookkeeping.currency;

import java.text.DecimalFormat;

public class CurrencyArray {
    private  String USDRUB;
    private  String USDEUR;


    DecimalFormat df = new DecimalFormat("##.##");

    public String getUSDRUB() {
        return USDRUB;
    }

    public String getUSDEUR() {
        return USDEUR;
    }

    public String getRUBFromEUR(String eur) {
        String str = eur.replace(",", ".");
        String usd = getUSDFromEUR(str);
        return getRUBFromUSD(usd);
    }

    public String getUSDFromEUR(String eur) {
        String str = eur.replace(",", ".");
        double d = Double.parseDouble(str);
        double rate = Double.parseDouble(USDEUR);
        double result = d / rate;

        return df.format(result);
    }


    public String getRUBFromUSD(String usd) {

            String str = usd.replace(",", ".");

        double d = Double.parseDouble(str);
        double rate = Double.parseDouble(USDRUB);
        double result = d * rate;

        return df.format(result);
    }

    public String getUSDFromRUB(String rub) {
        String str = rub.replace(",", ".");
        double d = Double.parseDouble(str);
        double rate = Double.parseDouble(USDRUB);
        double result = d * rate;

        return df.format(result);
    }
}
