package com.example.android.bookkeeping;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class Constants {
    public final static String URL_DAILY = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";
    public final static String URL_HISTORY = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist.xml";
    public final static String CLOUD_DATA_PATH = "data/";

    public final static int CURRENCIES_DIALOG_CODE = 1;
    public final static int HISTORY_DIALOG_CODE = 2;
    public final static int DATE_DIALOG_CODE = 3;

    public final static String DATE_MAIN_FORMAT = "dd.MM.yyyy";
    public final static String DATE_XML_FORMAT = "yyyy-MM-dd";
    public final static Locale DATE_MAIN_LOCALE = Locale.ENGLISH;

    public final static String NAME_CURRENCY_RUB = "RUB";

    public final static Date FIRST_CURRENCY_DATE = new GregorianCalendar(1999, 1, 4).getTime();

}
