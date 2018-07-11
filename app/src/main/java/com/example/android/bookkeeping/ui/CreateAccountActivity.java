package com.example.android.bookkeeping.ui;

import android.content.Context;
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

public class CreateAccountActivity extends AppCompatActivity {
    static String LOG_TAG = "myCreateAccount";

    EditText nameAccount;
    EditText valueAccount;
    Spinner spinner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        Button  buttonDone;
        final DBHelper dbHelper = new DBHelper(this);

        spinner = findViewById(R.id.account_currency_spinner);
        buttonDone = findViewById(R.id.button_done_create_account);
        nameAccount = findViewById(R.id.name_create_account);
        valueAccount = findViewById(R.id.value_create_account);

        String str[] = {"RUB","USD", "EUR"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, str);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);

        final Intent intent = new Intent(this, AccountsListActivity.class);

        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.button_done_create_account:
                        String name = nameAccount.getText().toString();
                        String value = valueAccount.getText().toString();
                        String currency = spinner.getSelectedItem().toString();

                        if (name.equals("") || value.equals("") || currency.equals("")) {
                            Toast.makeText(CreateAccountActivity.this, "Please fill all fields", Toast.LENGTH_LONG).show();
                        } else {

                            dbHelper.insertLastAccountsData(name, value, currency);
                            startActivity(intent);
                        }

                        break;
                }
            }
        });
    }




}
