package com.example.android.bookkeeping.ui.chart;

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
import com.example.android.bookkeeping.ui.mvp.BaseActivity;
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

public class ChartActivity extends BaseActivity implements DialogCommunicator, ChartMvpView {

    private final static String TAG = "chartActivity";

    private LineChart lineChart;
    private ProgressBar progressBar;

    private CurrenciesHistoryDialog currenciesDialog;


    @Inject
    public ChartMvpPresenter<ChartMvpView> presenter;

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

        presenter.onAttach(this);
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

    @Override
    public void setOnClickListeners() {

    }

    public void setDialog() {
        currenciesDialog = new CurrenciesHistoryDialog();
        currenciesDialog.setDialogCommunicator(this);
    }

    public void setRatesFromIntent() {
        Intent intent = getIntent();
        presenter.setRatesFromIntent(intent);
    }

    @Override
    public void showDialog(String[] ratesNames) {
        Bundle args = new Bundle();
        args.putStringArray("currencies", ratesNames);
        currenciesDialog.setArguments(args);
        currenciesDialog.show(getSupportFragmentManager(), "currency");
    }

    @Override
    public void setLineChartData(LineData lineData, ArrayList<String> xAxisList) {
        lineChart.setData(lineData);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisList));
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }

    @Override
    public void sendRequest(int code, String result) {
        if (code == 2) {
            presenter.setChosenCurrency(result);
            presenter.loadAndShowCurrencies();
        }
    }




    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        lineChart.setVisibility(View.INVISIBLE);
    }

    @Override
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
    protected void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }

}
