package com.example.android.bookkeeping.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.android.bookkeeping.R;
import com.github.mikephil.charting.charts.LineChart;

public class ChartActivity extends AppCompatActivity {

    private LineChart lineChart;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        findViews();

    }

    public void findViews() {
        lineChart = findViewById(R.id.chart);
    }

}
