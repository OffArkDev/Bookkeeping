package com.example.android.bookkeeping.currency;

import android.os.AsyncTask;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/** Parser of xml document loaded with url connection
 *  ratesListener passing loaded data to AccountsActivity
 * **/

public class UrlParser extends AsyncTask <Void, Void, Void> {

    RatesListener ratesListener;

    CurrencyRatesData currencyRatesData;
    String parsedUrl;

    public UrlParser(String parsedUrl, RatesListener ratesListener) {
        this.parsedUrl = parsedUrl;
        this.ratesListener = ratesListener;
    }

    @Override
    protected Void doInBackground(Void... urlParse) {

        //init variables
        List<Pair> params = new ArrayList<>();
        URL url = createUrl(parsedUrl);
        String time = "";

        try {
            //Create parser
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser xpp = factory.newPullParser();

            //set inputStream to parser
            InputStream inputStream = getInputStream(url);
            xpp.setInput(inputStream, "UTF_8");

            //parse xml
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equalsIgnoreCase("cube")) {
                        if (xpp.getAttributeCount() > 0) {
                            String attribute = xpp.getAttributeName(0);
                            if (attribute.equals("time")) {
                                time = xpp.getAttributeValue(0);
                            }
                        }
                        if (xpp.getAttributeCount() > 1) {
                            String attribute1 = xpp.getAttributeName(0);
                            String attribute2 = xpp.getAttributeName(1);
                            if (attribute1.equals("currency") && attribute2.equals("rate")) {
                                String attributeValue1 = xpp.getAttributeValue(0);
                                String attributeValue2 = xpp.getAttributeValue(1);
                                Pair pair = new Pair(attributeValue1, new BigDecimal(attributeValue2));
                                params.add(pair);
                            }
                        }
                    }
                }

                try {
                    eventType = xpp.next();
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        currencyRatesData = new CurrencyRatesData(params, time);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        ratesListener.loadingComplete(currencyRatesData);
    }

    private URL createUrl(String url) {
        URL result = null;
        try {
            result = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return result;
    }

    private InputStream getInputStream(URL url) {
        try {
            return url.openConnection().getInputStream();
        } catch (IOException e) {
            return null;
        }
    }

}
