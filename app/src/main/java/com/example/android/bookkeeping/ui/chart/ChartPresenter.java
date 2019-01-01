package com.example.android.bookkeeping.ui.chart;

import android.content.Intent;
import android.util.Log;

import com.example.android.bookkeeping.R;
import com.example.android.bookkeeping.currency.CurrencyRatesData;
import com.example.android.bookkeeping.currency.UrlParser;
import com.example.android.bookkeeping.ui.mvp.BasePresenter;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
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

public class ChartPresenter<V extends ChartMvpView> extends BasePresenter<V> implements ChartMvpPresenter<V> {

    private final static String TAG = "ChartPresenter";

    private ArrayList<CurrencyRatesData> listHistoryCurrencies = new ArrayList<>();
    private String[] ratesNames;
    private String chosenCurrency;
    private ArrayList<String> timesList = new ArrayList<>();


    @Inject
    public CompositeDisposable compositeDisposable;

    @Inject
    public UrlParser urlParser;

    @Inject
    public ChartPresenter() {
    }


    @Override
    public void onAttach(V mvpView) {
        super.onAttach(mvpView);
        getMvpView().showDialog(ratesNames);
    }

    @Override
    public void onDetach() {
        compositeDisposable.dispose();
        super.onDetach();
    }

    @Override
    public void setRatesFromIntent(Intent intent) {
        ratesNames = intent.getStringArrayExtra("rates");
    }

    @Override
    public void loadAndShowCurrencies() {
        getMvpView().showLoading();
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
                        getMvpView().setLineChartData(lineData, timesList);
                        getMvpView().hideLoading();

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

    @Override
    public void setChosenCurrency(String chosenCurrency) {
        this.chosenCurrency = chosenCurrency;
    }
}
