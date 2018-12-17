package com.example.android.bookkeeping.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.android.bookkeeping.R;
import com.example.android.bookkeeping.currency.CurrencyRatesData;
import com.example.android.bookkeeping.currency.RatesListener;
import com.example.android.bookkeeping.currency.RxUrlParser;
import com.example.android.bookkeeping.currency.UrlParser;
import com.example.android.bookkeeping.di.AppModule;
import com.example.android.bookkeeping.di.DaggerAppComponent;
import com.example.android.bookkeeping.di.StorageModule;
import com.example.android.bookkeeping.di.UrlParserModule;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ChartActivity extends AppCompatActivity implements DialogCommunicator, RatesListener {

    private final static String TAG = "chartActivity";

    private LineChart barChart;

    private final String url = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist.xml";
    private ArrayList<CurrencyRatesData> listHistoryCurrencies = new ArrayList<>();
    private CurrenciesHistoryDialog currenciesDialog;

    private String[] ratesNames;
    private String[] chosenNames;



    @Inject
    public Context context;

    @Inject
    public UrlParser urlParser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        DaggerAppComponent.builder()
                .appModule(new AppModule(getApplication()))
                .storageModule(new StorageModule(getApplication()))
                .urlParserModule(new UrlParserModule(url, this))
                .build()
                .injectChartActivity(this);
        findViews();
        setDialog();
        setRatesFromIntent();
        showDialog();



    }

    public void findViews() {
        barChart = findViewById(R.id.bar_chart);
    }


    public void setDialog() {
        currenciesDialog = new CurrenciesHistoryDialog();
        currenciesDialog.setDialogCommunicator(this);
    }

    public void setRatesFromIntent() {
        Intent intent = getIntent();
        ratesNames = intent.getStringArrayExtra("rates");
    }

    public void showDialog() {
        Bundle args = new Bundle();
        args.putStringArray("currencies", ratesNames);
        currenciesDialog.setArguments(args);
        currenciesDialog.show(getFragmentManager(), "currency");
    }

    @Override
    public void sendRequest(int code, String[] result) {
        if (code == 2) {
            chosenNames = result;
            loadAndShowCurrencies();
        }
    }

    public void loadAndShowCurrencies() {
        RxUrlParser rxUrlParser = new RxUrlParser(url);
        Observable.create(rxUrlParser)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CurrencyRatesData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CurrencyRatesData data) {
                        listHistoryCurrencies.add(data);
                        addDataToChart();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });
    }

    public void addDataToChart() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
    @Override
    public void loadingComplete(CurrencyRatesData currencyRatesData) {
  //      this.listHistoryCurrencies = currencyRatesData;
        ArrayList<Entry> entries = new ArrayList<>();
                //listHistoryCurrencies.getChartData();
        entries.add(new Entry(1985, 3f));
        entries.add(new Entry(1986, 8f));
        entries.add(new Entry(1987, 6f));
        entries.add(new Entry(1988, 11f));
        entries.add(new Entry(1989, 5f));
        entries.add(new Entry(1990, 14f));
        entries.add(new Entry(1991, 14f));
        entries.add(new Entry(1992, 14f));
        entries.add(new Entry(1993, 14f));
        entries.add(new Entry(1994, 14f));
        entries.add(new Entry(1995, 14f));
        entries.add(new Entry(1996, 14f));
        entries.add(new Entry(1997, 14f));
        entries.add(new Entry(1998, 14f));
        entries.add(new Entry(1999, 14f));
        entries.add(new Entry(2000, 14f));
        entries.add(new Entry(2001, 14f));
        entries.add(new Entry(2002, 14f));
        entries.add(new Entry(2003, 14f));
        entries.add(new Entry(2004, 14f));
        entries.add(new Entry(2005, 14f));
        entries.add(new Entry(2006, 14f));
        entries.add(new Entry(2007, 14f));



        LineDataSet dataSet = new LineDataSet(entries,"Horizontal Bar");

        LineData data = new LineData(dataSet);
        barChart.setData(data);
        barChart.invalidate();

//        final ArrayList<String> xLabels = new ArrayList<>();
//               //new ArrayList<>(Arrays.asList(listHistoryCurrencies.getCurrenciesList()));
//        xLabels.add("January");
//        xLabels.add("February");
//        xLabels.add("March");
//        xLabels.add("April");
//        xLabels.add("May");
//        xLabels.add("June");
//        xLabels.add("o");
//        xLabels.add("a");
//        xLabels.add("b");
//        xLabels.add("c");
//        xLabels.add("d");
//        xLabels.add("e");
//        xLabels.add("f");
//        xLabels.add("g");
//        xLabels.add("h");
//        xLabels.add("[");
//        xLabels.add("p");
//        xLabels.add("q");
//        xLabels.add("r");
//        xLabels.add("s");
//        xLabels.add("t");
//        xLabels.add("k");



//        XAxis xAxis = barChart.getXAxis();
//        xAxis.setValueFormatter(new IAxisValueFormatter() {
//            @Override
//            public String getFormattedValue(float value, AxisBase axis) {
//                return xLabels.get((int) value);
//            }
//
//        });

    }
}
