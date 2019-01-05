package com.example.android.bookkeeping.utils;

import com.example.android.bookkeeping.Constants;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    public static Date stringToDate(String strDate) {
        DateFormat format = new SimpleDateFormat(Constants.DATE_MAIN_FORMAT, Constants.DATE_MAIN_LOCALE);
        Date date = null;
        try {
            date = format.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String changeFormatToMain(String strDate, String strFormat) {
        DateFormat givenFormat = new SimpleDateFormat(strFormat, Constants.DATE_MAIN_LOCALE);
        DateFormat mainFormat = new SimpleDateFormat(Constants.DATE_MAIN_FORMAT, Constants.DATE_MAIN_LOCALE);
        String result = null;
        try {
            Date date = givenFormat.parse(strDate);
            result = mainFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String changeFormatToXml(String strDate, String strFormat) {
        DateFormat givenFormat = new SimpleDateFormat(strFormat, Constants.DATE_MAIN_LOCALE);
        DateFormat mainFormat = new SimpleDateFormat(Constants.DATE_XML_FORMAT, Constants.DATE_MAIN_LOCALE);
        String result = null;
        try {
            Date date = givenFormat.parse(strDate);
            result = mainFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }
}
