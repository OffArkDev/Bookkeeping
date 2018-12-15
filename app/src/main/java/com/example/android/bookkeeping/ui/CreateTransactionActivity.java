package com.example.android.bookkeeping.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.bookkeeping.R;

public class CreateTransactionActivity extends AppCompatActivity {

    private final static String LOG_TAG = "myCreateTransaction";

    private EditText nameTransaction;
    private EditText valueTransaction;
    private EditText commentTransaction;
    private Spinner spinnerCurrency;
    private Spinner spinnerType;
    private Button buttonCreateTransaction;


    private String[] ratesNames;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_transaction);
        findViews();
        setRatesFromIntent();
        setAdapters();
        setOnClickListeners();
    }

    public void findViews() {
        nameTransaction = findViewById(R.id.edit_name_transaction);
        valueTransaction = findViewById(R.id.edit_value_transaction);
        commentTransaction = findViewById(R.id.edit_transaction_comment);
        spinnerCurrency = findViewById(R.id.transaction_currency_spinner);
        spinnerType = findViewById(R.id.transaction_type_spinner);
        buttonCreateTransaction = findViewById(R.id.button_done_create_transaction);
    }

    public void setRatesFromIntent() {
        Intent intent = getIntent();
        ratesNames = intent.getStringArrayExtra("ratesNames");
    }

    public void setAdapters() {
        ArrayAdapter<String> adapterSp1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ratesNames);
        adapterSp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCurrency.setAdapter(adapterSp1);
        spinnerCurrency.setSelection(0);

        String str1[] = {"in", "out"};
        ArrayAdapter<String> adapterSp2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, str1);
        adapterSp2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapterSp2);
        spinnerType.setSelection(0);
    }

    public void setOnClickListeners() {
        buttonCreateTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.button_done_create_transaction:
                        String name = nameTransaction.getText().toString();
                        String value = valueTransaction.getText().toString();
                        String comment = commentTransaction.getText().toString();
                        String currency = spinnerCurrency.getSelectedItem().toString();
                        String type = spinnerType.getSelectedItem().toString();

                        if (value.equals("")) {
                            Toast.makeText(CreateTransactionActivity.this, getString(R.string.write_value), Toast.LENGTH_LONG).show();
                        } else {
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("name", name);
                            resultIntent.putExtra("value", value);
                            resultIntent.putExtra("comment", comment);
                            resultIntent.putExtra("currency", currency);
                            resultIntent.putExtra("type", type);

                            setResult(RESULT_OK, resultIntent);
                            finish();
                        }
                }
            }
        });


    }

}
