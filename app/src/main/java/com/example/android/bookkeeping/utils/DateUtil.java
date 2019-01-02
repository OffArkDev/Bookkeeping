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
}
