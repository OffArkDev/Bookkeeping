package com.example.android.bookkeeping.ui.dialogs.date;

import com.example.android.bookkeeping.ui.mvp.MvpPresenter;

public interface DateDialogMvpPresenter<V extends DateDialogMvpView> extends MvpPresenter<V> {

    void setFormattedDate(int day, int month, int year);
}
