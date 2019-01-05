package com.example.android.bookkeeping.ui.chart;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ProgressBar;

import com.example.android.bookkeeping.Constants;
import com.example.android.bookkeeping.MyApplication;
import com.example.android.bookkeeping.R;
import com.example.android.bookkeeping.currency.CurrenciesRatesData;

import com.example.android.bookkeeping.di.components.UrlParserComponent;
import com.example.android.bookkeeping.di.modules.ActivityModule;
import com.example.android.bookkeeping.di.modules.UrlParserModule;
import com.example.android.bookkeeping.ui.dialogs.history.CurrenciesHistoryDialog;
import com.example.android.bookkeeping.ui.dialogs.DialogCommunicator;
import com.example.android.bookkeeping.ui.mvp.BaseActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;

import javax.inject.Inject;

public class ChartActivity extends BaseActivity implements DialogCommunicator, ChartMvpView {

    private final static String TAG = "chartActivity";

    private LineChart lineChart;
    private ProgressBar progressBar;

    private CurrenciesHistoryDialog currenciesDialog;


    @Inject
    public ChartMvpPresenter<ChartMvpView> presenter;

    public static Intent getStartIntent(Context context, CurrenciesRatesData currenciesRatesData) {
        Intent intent = new Intent(context, ChartActivity.class);
        intent.putExtra("rates", currenciesRatesData.getCurrenciesList());
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        getChartComponent().inject(this);
        findViews();
        initDialog();
        setRatesFromIntent();

        presenter.onAttach(this);
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

    @Override
    public void setOnClickListeners() {

    }

    public void initDialog() {
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
        if (code == Constants.HISTORY_DIALOG_CODE) {
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
