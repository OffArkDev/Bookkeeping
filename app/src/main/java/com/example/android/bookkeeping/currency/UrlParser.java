package com.example.android.bookkeeping.currency;

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

import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class UrlParser implements FlowableOnSubscribe<CurrenciesRatesData> {

    private CurrenciesRatesData currenciesRatesData;
    private String parsedUrl;

    public UrlParser(String parsedUrl) {
        this.parsedUrl = parsedUrl;
    }

    @Override
    public void subscribe(FlowableEmitter<CurrenciesRatesData> emitter){
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
            boolean rateStart = false;
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equalsIgnoreCase("cube")) {
                        if (xpp.getAttributeCount() > 0) {
                            String attribute = xpp.getAttributeName(0);
                            if (attribute.equals("time")) {
                                rateStart = true;
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
                }  else if (eventType == XmlPullParser.END_TAG) {
                    String name = xpp.getName();
                    int atCount = xpp.getAttributeCount();
                    String atName = "";
                    if (atCount > 0) {
                        atName = xpp.getAttributeName(0);
                    }
                    if (xpp.getName().equalsIgnoreCase("cube") && rateStart && xpp.getAttributeCount() == -1) {
                        currenciesRatesData = new CurrenciesRatesData(params, time);
                        emitter.onNext(currenciesRatesData);
                        params = new ArrayList<>();
                        rateStart = false;
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
        emitter.onComplete();

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
