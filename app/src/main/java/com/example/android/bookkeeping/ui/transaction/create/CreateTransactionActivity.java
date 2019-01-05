package com.example.android.bookkeeping.ui.transaction.create;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.example.android.bookkeeping.Constants;
import com.example.android.bookkeeping.MyApplication;
import com.example.android.bookkeeping.R;
import com.example.android.bookkeeping.currency.CurrenciesRatesData;
import com.example.android.bookkeeping.di.components.UrlParserComponent;
import com.example.android.bookkeeping.di.modules.ActivityModule;
import com.example.android.bookkeeping.di.modules.UrlParserModule;
import com.example.android.bookkeeping.ui.dialogs.currencies.CurrenciesDialog;
import com.example.android.bookkeeping.ui.dialogs.DialogCommunicator;
import com.example.android.bookkeeping.ui.dialogs.date.DateDialog;
import com.example.android.bookkeeping.ui.mvp.BaseActivity;
import com.google.gson.Gson;

import javax.inject.Inject;

public class CreateTransactionActivity extends BaseActivity implements DialogCommunicator, CreateTransactionMvpView {

    private EditText etName;
    private EditText etValue;
    private EditText etComment;
    private EditText etDate;
    private Button btnCurrency;
    private Spinner spinnerType;
    private Button btnDone;
    private ProgressBar pbCurrenciesLoading;

    private DateDialog dateDialog;
    private CurrenciesDialog currenciesDialog;

    @Inject
    CreateTransactionMvpPresenter<CreateTransactionMvpView> presenter;

    public static Intent getStartIntent(Context context, String[] currenciesNames) {
        Intent intent = new Intent(context, CreateTransactionActivity.class);
        intent.putExtra("currenciesNames", currenciesNames);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_transaction);
        getUrlParserComponent().inject(this);
        findViews();
        setRatesFromIntent();
        initDialogs();
        setOnClickListeners();

        presenter.onAttach(this);
        initAdapter();
    }

    public UrlParserComponent getUrlParserComponent() {
        return ((MyApplication) getApplication())
                .getApplicationComponent()
                .newUrlParserComponent(new ActivityModule(this), new UrlParserModule(Constants.URL_HISTORY));
    }

    public void findViews() {
        etName = findViewById(R.id.edit_name_transaction);
        etValue = findViewById(R.id.edit_value_transaction);
        etComment = findViewById(R.id.edit_transaction_comment);
        btnCurrency = findViewById(R.id.transaction_currency_btn);
        spinnerType = findViewById(R.id.transaction_type_spinner);
        btnDone = findViewById(R.id.button_done_create_transaction);
        etDate = findViewById(R.id.edit_transaction_date);
        pbCurrenciesLoading = findViewById(R.id.progress_bar);
    }

    private void setRatesFromIntent() {
        Intent intent = getIntent();
        presenter.getDataFromIntent(intent);
    }

    public void initAdapter() {
        ArrayAdapter<String> adapterSp = presenter.initAccountsAdapter();
        spinnerType.setAdapter(adapterSp);
        spinnerType.setSelection(0);
    }

    public void initDialogs() {
        currenciesDialog = CurrenciesDialog.newInstance();
        currenciesDialog.setDialogCommunicator(this);
        dateDialog = DateDialog.newInstance();
        dateDialog.setDialogCommunicator(this);
    }

    public void setOnClickListeners() {
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.btnDoneClick();
                }
            });

        btnCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.btnCurrencyClick();
            }
        });

        etDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View view, boolean hasfocus) {
                if (hasfocus) {
                    dateDialog.show(getSupportFragmentManager(), "date dateDialog");
                }
            }

        });
    }

    @Override
    public void showDialog() {
        Bundle args = new Bundle();
        args.putStringArray("currencies", presenter.getCurrenciesNames());
        currenciesDialog.setArguments(args);
        currenciesDialog.show(getSupportFragmentManager(), "currency");
    }

    @Override
    public void returnActivityResult() {
        String name = etName.getText().toString();
        String value = etValue.getText().toString();
        String comment = etComment.getText().toString();
        String date  = etDate.getText().toString();
        String currency = btnCurrency.getText().toString();
        String type = spinnerType.getSelectedItem().toString();
        if (presenter.checkInputDataFormat(value, date)) {
            CurrenciesRatesData ratesData = presenter.getCurrenciesRatesData(date);
            if (ratesData != null) {
                Intent resultIntent = new Intent();
                Gson gson = new Gson();
                String json = gson.toJson(ratesData);
                resultIntent.putExtra("currencyRates", json);
                resultIntent.putExtra("name", name);
                resultIntent.putExtra("value", value);
                resultIntent.putExtra("date", date);
                resultIntent.putExtra("comment", comment);
                resultIntent.putExtra("currency", currency);
                resultIntent.putExtra("type", type);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        }
    }

    @Override
    public void showLoading() {
        pbCurrenciesLoading.setVisibility(View.VISIBLE);
        btnDone.setVisibility(View.GONE);
    }

    @Override
    public void hideLoading() {
        pbCurrenciesLoading.setVisibility(View.GONE);
        btnDone.setVisibility(View.VISIBLE);
    }

    @Override
    public void sendRequest(int code, String result) {
        if (code == Constants.CURRENCIES_DIALOG_CODE) {
            btnCurrency.setText(result);
        }
        if (code == Constants.DATE_DIALOG_CODE) {
            etDate.setText(result);
        }
    }

    @Override
    protected void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }
}
