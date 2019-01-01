package com.example.android.bookkeeping.ui.transaction.create;

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
import com.example.android.bookkeeping.currency.CurrencyRatesData;
import com.example.android.bookkeeping.ui.dialogs.currencies.CurrenciesDialog;
import com.example.android.bookkeeping.ui.dialogs.DialogCommunicator;

public class CreateTransactionActivity extends AppCompatActivity implements DialogCommunicator {

    private EditText etName;
    private EditText etValue;
    private EditText etComment;
    private Button btnCurrency;
    private Spinner spinnerType;
    private Button btnCreate;
    private CurrenciesDialog currenciesDialog;


    private String[] ratesNames;


    public static Intent getStartIntent(Context context, CurrencyRatesData currencyRatesData) {
        Intent intent = new Intent(context, CreateTransactionActivity.class);
        intent.putExtra("ratesNames", currencyRatesData.getCurrenciesList());
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_transaction);
        findViews();
        setRatesFromIntent();
        setDialog();
        setAdapter();
        setOnClickListeners();
    }

    public void findViews() {
        etName = findViewById(R.id.edit_name_transaction);
        etValue = findViewById(R.id.edit_value_transaction);
        etComment = findViewById(R.id.edit_transaction_comment);
        btnCurrency = findViewById(R.id.transaction_currency_btn);
        spinnerType = findViewById(R.id.transaction_type_spinner);
        btnCreate = findViewById(R.id.button_done_create_transaction);
    }

    public void setRatesFromIntent() {
        Intent intent = getIntent();
        ratesNames = intent.getStringArrayExtra("ratesNames");
    }

    public void setAdapter() {
        String str1[] = {"in", "out"};
        ArrayAdapter<String> adapterSp = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, str1);
        adapterSp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapterSp);
        spinnerType.setSelection(0);
    }

    public void setDialog() {
        currenciesDialog = new CurrenciesDialog();
        currenciesDialog.setDialogCommunicator(this);
    }
    public void setOnClickListeners() {
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        String name = etName.getText().toString();
                        String value = etValue.getText().toString();
                        String comment = etComment.getText().toString();
                        String currency = btnCurrency.getText().toString();
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
            });

        btnCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putStringArray("currencies", ratesNames);
                currenciesDialog.setArguments(args);
                currenciesDialog.show(getSupportFragmentManager(), "currency");
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
