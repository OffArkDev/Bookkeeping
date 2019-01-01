package com.example.android.bookkeeping.ui.chart;

import com.example.android.bookkeeping.ui.mvp.MvpView;
import com.github.mikephil.charting.data.LineData;

import java.util.ArrayList;

public interface ChartMvpView extends MvpView {

    void showDialog(String[] ratesNames);

    void showLoading();

    void hideLoading();

    void setLineChartData(LineData lineData, ArrayList<String> xAxisList);
}
