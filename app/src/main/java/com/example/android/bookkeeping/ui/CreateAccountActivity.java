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
import com.example.android.bookkeeping.currency.CurrencyRatesData;
import com.example.android.bookkeeping.data.AccountSaver;
import com.example.android.bookkeeping.data.DBHelper;
import com.example.android.bookkeeping.di.AppModule;
import com.example.android.bookkeeping.di.DaggerAppComponent;
import com.example.android.bookkeeping.di.StorageModule;
import com.example.android.bookkeeping.repository.AccountsDataSource;

import javax.inject.Inject;

public class CreateAccountActivity extends AppCompatActivity {

  private EditText nameAccount;
  private EditText valueAccount;
  private Spinner spinner;
  private Button btnDone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        initViews();

        Intent intent = getIntent();

        String ratesNames[] = intent.getStringArrayExtra("rates");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ratesNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.button_done_create_account:
                        String name = nameAccount.getText().toString();
                        String value = valueAccount.getText().toString();
                        String currency = spinner.getSelectedItem().toString();

                        if (name.equals("")) {
                            Toast.makeText(CreateAccountActivity.this, getString(R.string.write_name), Toast.LENGTH_LONG).show();
                        } else {
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("name", name);
                            resultIntent.putExtra("value", value);
                            resultIntent.putExtra("currency", currency);
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        }

                        break;
                }
            }
        });
    }

    public void initViews() {
        spinner = findViewById(R.id.account_currency_spinner);
        btnDone = findViewById(R.id.button_done_create_account);
        nameAccount = findViewById(R.id.name_create_account);
        valueAccount = findViewById(R.id.value_create_account);
    }

}
