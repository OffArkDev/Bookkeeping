package com.example.android.bookkeeping.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.android.bookkeeping.R;
import com.example.android.bookkeeping.currency.CurrencyRatesData;
import com.example.android.bookkeeping.currency.RatesListener;
import com.example.android.bookkeeping.currency.UrlParser;
import com.example.android.bookkeeping.di.AppModule;
import com.example.android.bookkeeping.di.DaggerAppComponent;
import com.example.android.bookkeeping.di.StorageModule;
import com.example.android.bookkeeping.di.UrlParserModule;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ChartActivity extends AppCompatActivity implements RatesListener {

    private LineChart lineChart;

    private final String url = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";
    private CurrencyRatesData currencyRatesData;


    @Inject
    public Context context;

    @Inject
    public UrlParser urlParser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        findViews();
        DaggerAppComponent.builder()
                .appModule(new AppModule(getApplication()))
                .storageModule(new StorageModule(getApplication()))
                .urlParserModule(new UrlParserModule(url, this))
                .build()
                .injectChartActivity(this);

        urlParser.execute();

    }

    public void findViews() {
        lineChart = findViewById(R.id.chart);
    }


    @Override
    public void loadingComplete(CurrencyRatesData currencyRatesData) {
        this.currencyRatesData = currencyRatesData;
        List<Entry> entries = currencyRatesData.getChartData();
        LineDataSet dataSet = new LineDataSet(entries, "Label");
        dataSet.setColor(R.color.violet);
        dataSet.setValueTextColor(R.color.black);
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.invalidate(); // refresh

    }
}
