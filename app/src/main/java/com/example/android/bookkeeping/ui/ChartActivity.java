package com.example.android.bookkeeping.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.android.bookkeeping.Constants;
import com.example.android.bookkeeping.MyApplication;
import com.example.android.bookkeeping.R;
import com.example.android.bookkeeping.pojo.CurrenciesRatesData;
import com.example.android.bookkeeping.pojo.UrlParser;

import com.example.android.bookkeeping.di.components.UrlParserComponent;
import com.example.android.bookkeeping.di.modules.ActivityModule;
import com.example.android.bookkeeping.di.modules.UrlParserModule;
import com.example.android.bookkeeping.ui.dialogs.CurrenciesHistoryDialog;
import com.example.android.bookkeeping.ui.dialogs.DialogCommunicator;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ChartActivity extends AppCompatActivity implements DialogCommunicator {

    private final static String TAG = "chartActivity";

    private LineChart lineChart;
    private ProgressBar progressBar;

    private ArrayList<CurrenciesRatesData> listHistoryCurrencies = new ArrayList<>();
    private CurrenciesHistoryDialog currenciesDialog;

    private String[] ratesNames;
    private String chosenCurrency;

    private ArrayList<String> timesList = new ArrayList<>();

    @Inject
    public Context context;

    @Inject
    public CompositeDisposable compositeDisposable;

    @Inject
    public UrlParser urlParser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        getChartComponent().inject(this);
        findViews();
        initDialog();
        setRatesFromIntent();
        showDialog();

    }

    public UrlParserComponent getChartComponent() {
        return ((MyApplication) getApplication())
                .getApplicationComponent()
                .newUrlParserComponent(new ActivityModule(this), new UrlParserModule(Constants.URL_HISTORY));
    }

    public void findViews() {
        lineChart = findViewById(R.id.bar_chart);
        progressBar = findViewById(R.id.progress_bar);
    }


    public void initDialog() {
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
    public void sendRequest(int code, String result) {
        if (code == 2) {
            chosenCurrency = result;
            loadAndShowCurrencies();
        }
    }

    public void loadAndShowCurrencies() {
       showLoading();
        Flowable.create(urlParser, BackpressureStrategy.DROP)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CurrenciesRatesData>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(CurrenciesRatesData currenciesRatesData) {
                        listHistoryCurrencies.add(currenciesRatesData);

                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.i(TAG, "onError: " + t.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        processData();
                    }
                });
    }

    public void processData() {
        compositeDisposable.add(Observable.fromCallable(new Callable<LineData>() {
            @Override
            public LineData call() {
                Collections.reverse(listHistoryCurrencies);
                return addCurrenciesToChart();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<LineData>() {
                    @Override
                    public void accept(LineData lineData){
                        lineChart.setData(lineData);
                        XAxis xAxis = lineChart.getXAxis();
                        xAxis.setValueFormatter(new IndexAxisValueFormatter(timesList));
                        hideLoading();
                        lineChart.notifyDataSetChanged();
                        lineChart.invalidate();
                    }
                }));

    }


    public LineData addCurrenciesToChart(){
        ArrayList<ILineDataSet> lines = new ArrayList<> ();
        LineDataSet lDataSet1 = new LineDataSet(null, "");
            ArrayList<Entry> dataSet = new ArrayList<>();
            for (int j = 0; j < listHistoryCurrencies.size(); j++) {
                CurrenciesRatesData data = listHistoryCurrencies.get(j);
                Entry entry = new Entry(j, data.getRate(chosenCurrency).floatValue());
                dataSet.add(entry);
                lDataSet1 = new LineDataSet(dataSet, chosenCurrency);
                lDataSet1.setCircleColor(R.color.green);
            }
            lines.add(lDataSet1);
        for (CurrenciesRatesData data: listHistoryCurrencies) {
            timesList.add(data.getTime());
        }

        return new LineData(lines);
    }

    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        lineChart.setVisibility(View.INVISIBLE);
    }

    public void hideLoading() {
        progressBar.setVisibility(View.INVISIBLE);
        lineChart.setVisibility(View.VISIBLE);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onStop() {
        super.onStop();
        compositeDisposable.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
