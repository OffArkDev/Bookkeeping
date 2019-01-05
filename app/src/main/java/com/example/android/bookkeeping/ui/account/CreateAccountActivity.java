package com.example.android.bookkeeping.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.bookkeeping.R;
import com.example.android.bookkeeping.ui.dialogs.CurrenciesDialog;
import com.example.android.bookkeeping.ui.dialogs.DialogCommunicator;

public class CreateAccountActivity extends AppCompatActivity implements DialogCommunicator {

    private EditText nameAccount;
    private EditText valueAccount;
    private Button btnCurrency;
    private Button btnDone;

    private String[] ratesNames;

    private CurrenciesDialog currenciesDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        findViews();
        initDialog();
        setRatesFromIntent();
        setOnClickListeners();
    }

    public void findViews() {
        btnCurrency = findViewById(R.id.account_currency_btn);
        btnDone = findViewById(R.id.button_done_create_account);
        nameAccount = findViewById(R.id.name_create_account);
        valueAccount = findViewById(R.id.value_create_account);
    }

    public void setRatesFromIntent() {
        Intent intent = getIntent();
        ratesNames = intent.getStringArrayExtra("rates");
    }

    public void initDialog() {
        currenciesDialog = new CurrenciesDialog();
        currenciesDialog.setDialogCommunicator(this);
    }

    public void setOnClickListeners() {
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.button_done_create_account:
                        String name = nameAccount.getText().toString();
                        String value = valueAccount.getText().toString();
                        String currency = btnCurrency.getText().toString();
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

        btnCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putStringArray("currencies", ratesNames);
                currenciesDialog.setArguments(args);
                currenciesDialog.show(getFragmentManager(), "currency");
            }
        });
    }

    @Override
    public void sendRequest(int code, String result) {
        if (code == 1) {
            btnCurrency.setText(result);
        }
    }
}
