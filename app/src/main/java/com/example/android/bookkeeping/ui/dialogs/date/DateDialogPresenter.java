package com.example.android.bookkeeping.ui.dialogs.date;

import com.example.android.bookkeeping.ui.mvp.BasePresenter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

public class DateDialogPresenter<V extends DateDialogMvpView> extends BasePresenter<V> implements DateDialogMvpPresenter<V> {

    private String formattedDate;

    @Inject
    public DateDialogPresenter() {
    }

    @Override
    public void setFormattedDate(int day, int month, int year) {
        String str = day+"."+(month+1)+"."+year;
        SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        Date date = null;
        try {
            date = f.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        formattedDate = f.format(date);
        getMvpView().returnResult(formattedDate);
    }
}
