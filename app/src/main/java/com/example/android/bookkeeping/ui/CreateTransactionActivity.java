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
import com.example.android.bookkeeping.data.DBHelper;

public class CreateTransactionActivity extends AppCompatActivity {

    private final static String LOG_TAG = "myCreateTransaction";

    EditText nameTransaction;
    EditText valueTransaction;
    EditText dateTransaction;
    EditText commentTransaction;
    Spinner spinnerCurrency;
    Spinner spinnerType;
    Button buttonCreateTransaction;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_transaction);

        Intent intentPrev = getIntent();
        final String position = intentPrev.getStringExtra("position");

        nameTransaction = findViewById(R.id.edit_name_transaction);
        valueTransaction = findViewById(R.id.edit_value_transaction);
        dateTransaction = findViewById(R.id.edit_transaction_date);
        commentTransaction = findViewById(R.id.edit_transaction_comment);
        spinnerCurrency = findViewById(R.id.transaction_currency_spinner);
        spinnerType = findViewById(R.id.transaction_type_spinner);
        buttonCreateTransaction = findViewById(R.id.button_done_create_transaction);

        String str[] = {"RUB","USD", "EUR"};
        ArrayAdapter<String> adapterSp1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, str);
        adapterSp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCurrency.setAdapter(adapterSp1);
        spinnerCurrency.setSelection(0);

        String str1[] = {"in", "out"};
        ArrayAdapter<String> adapterSp2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, str1);
        adapterSp2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapterSp2);
        spinnerType.setSelection(0);

        final Intent intent = new Intent(this, TransactionsListActivity.class);
        intent.putExtra("position", position);

        final DBHelper db = new DBHelper(this);


        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.button_done_create_transaction:

                        String name = nameTransaction.getText().toString();
                        String value = valueTransaction.getText().toString();
                        String date = dateTransaction.getText().toString();
                        String comment= commentTransaction.getText().toString();
                        String currency= spinnerCurrency.getSelectedItem().toString();
                        String type= spinnerType.getSelectedItem().toString();

                        if (name.equals("") || value.equals("") || currency.equals("") || date.equals("") || comment.equals("")) {
                            Toast.makeText(CreateTransactionActivity.this, "Please fill all fields", Toast.LENGTH_LONG).show();
                        } else {


                            db.insertLastTransactionData(position, name, value, date, comment, currency, type);
                            startActivity(intent);
                        }
                }
            }
        };

        buttonCreateTransaction.setOnClickListener(onClickListener);

    }



}
