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
import com.example.android.bookkeeping.currency.CurrencyRatesData;
import com.example.android.bookkeeping.currency.UrlParser;

import com.example.android.bookkeeping.di.components.ChartComponent;
import com.example.android.bookkeeping.di.modules.ActivityModule;
import com.example.android.bookkeeping.di.modules.UrlParserModule;
import com.example.android.bookkeeping.ui.dialogs.history.CurrenciesHistoryDialog;
import com.example.android.bookkeeping.ui.dialogs.DialogCommunicator;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ChartActivity extends AppCompatActivity implements DialogCommunicator {

    private final static String TAG = "chartActivity";

    private LineChart lineChart;
    private ProgressBar progressBar;

    private ArrayList<CurrencyRatesData> listHistoryCurrencies = new ArrayList<>();
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

    public static Intent getStartIntent(Context context, CurrencyRatesData currencyRatesData) {
        Intent intent = new Intent(context, ChartActivity.class);
        intent.putExtra("rates", currencyRatesData.getCurrenciesList());
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        getChartComponent().inject(this);
        findViews();
        setDialog();
        setRatesFromIntent();
        showDialog();

    }

    public ChartComponent getChartComponent() {
        return ((MyApplication) getApplication())
                .getApplicationComponent()
                .newChartComponent(new ActivityModule(this), new UrlParserModule(Constants.URL_HISTORY));
    }

    public void findViews() {
        lineChart = findViewById(R.id.bar_chart);
        progressBar = findViewById(R.id.progress_bar);
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
    public void sendRequest(int code, String result) {
        if (code == 2) {
            chosenCurrency = result;
            loadAndShowCurrencies();
        }
    }

    public void loadAndShowCurrencies() {
        showOrHideProgress(true);
        Observable.create(urlParser)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CurrencyRatesData>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(CurrencyRatesData data) {
                        listHistoryCurrencies.add(data);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: " + e.getMessage());
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
                        showOrHideProgress(false);
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
                CurrencyRatesData data = listHistoryCurrencies.get(j);
                Entry entry = new Entry(j, data.getRate(chosenCurrency).floatValue());
                dataSet.add(entry);
                lDataSet1 = new LineDataSet(dataSet, chosenCurrency);
                lDataSet1.setCircleColor(R.color.green);
            }
            lines.add(lDataSet1);
        for (CurrencyRatesData data: listHistoryCurrencies) {
            timesList.add(data.getTime());
        }

        return new LineData(lines);
    }

    public void showOrHideProgress(Boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            lineChart.setVisibility(View.INVISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            lineChart.setVisibility(View.VISIBLE);
        }
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
