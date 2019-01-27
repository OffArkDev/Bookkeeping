package com.example.android.bookkeeping.ui.transaction;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.bookkeeping.Constants;
import com.example.android.bookkeeping.MyApplication;
import com.example.android.bookkeeping.R;
import com.example.android.bookkeeping.pojo.CurrenciesRatesData;
import com.example.android.bookkeeping.pojo.UrlParser;
import com.example.android.bookkeeping.di.components.UrlParserComponent;
import com.example.android.bookkeeping.di.modules.ActivityModule;
import com.example.android.bookkeeping.di.modules.UrlParserModule;
import com.example.android.bookkeeping.ui.dialogs.CurrenciesDialog;
import com.example.android.bookkeeping.ui.dialogs.DateDialog;
import com.example.android.bookkeeping.ui.dialogs.DialogCommunicator;
import com.example.android.bookkeeping.utils.DateUtil;
import com.google.gson.Gson;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class CreateTransactionActivity extends AppCompatActivity implements DialogCommunicator {

    private final static String TAG = "createTrActivity";


    private EditText etName;
    private EditText etValue;
    private EditText etDate;
    private EditText etComment;
    private Button btnCurrency;
    private Spinner spinnerType;
    private Button btnDone;
    private ProgressBar pbCurrenciesLoading;
    private CurrenciesDialog currenciesDialog;

    private DateDialog dateDialog;


    private ArrayAdapter<String> adapterSp;

    private String[] ratesNames;

    private ArrayList<CurrenciesRatesData> listHistoryCurrencies = new ArrayList<>();

    private boolean isCurrenciesLoaded = false;
    private boolean isBtnDoneClicked = false;

    @Inject
    public CompositeDisposable compositeDisposable;

    @Inject
    public UrlParser urlParser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_transaction);
        getUrlParserComponent().inject(this);
        findViews();
        setRatesFromIntent();
        initDialog();
        initAdapter();
        setOnClickListeners();
        loadCurrencies();
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

    public void setRatesFromIntent() {
        Intent intent = getIntent();
        ratesNames = intent.getStringArrayExtra("ratesNames");
    }

    public void initAdapter() {
        String str1[] = {"in", "out"};
        adapterSp = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, str1);
        adapterSp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapterSp);
        spinnerType.setSelection(0);
    }

    public void initDialog() {
        currenciesDialog = new CurrenciesDialog();
        currenciesDialog.setDialogCommunicator(this);
        dateDialog = DateDialog.newInstance();
        dateDialog.setDialogCommunicator(this);
    }
    public void setOnClickListeners() {
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isBtnDoneClicked = true;
                if (!isCurrenciesLoaded) {
                    showLoading();
                } else {
                    returnActivityResult();
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

        etDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View view, boolean hasfocus) {
                if (hasfocus) {
                    dateDialog.show(getFragmentManager(), "date dateDialog");
                }
            }

        });

    }

    private void loadCurrencies() {
        Flowable.create(urlParser, BackpressureStrategy.DROP)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CurrenciesRatesData>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(CurrenciesRatesData currenciesRatesData) {
                        listHistoryCurrencies.add(currenciesRatesData);

                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.i(TAG, "onError: " + t.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        if (isBtnDoneClicked) {
                            returnActivityResult();
                        }
                        isCurrenciesLoaded = true;
                    }
                });
    }



    public void returnActivityResult() {
        String name = etName.getText().toString();
        String value = etValue.getText().toString();
        String comment = etComment.getText().toString();
        String date  = etDate.getText().toString();
        String currency = btnCurrency.getText().toString();
        String type = spinnerType.getSelectedItem().toString();
        if (checkInputDataFormat(value, date)) {
            CurrenciesRatesData ratesData = getCurrenciesRatesData(date);
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

    public boolean checkInputDataFormat(String value, String sDate) {
        if (value.equals("")) {
            showMessage(R.string.write_value);
            return false;
        }
        Date dDate = DateUtil.stringToDate(sDate);
        if (dDate == null) {
            showMessage(R.string.wrong_date_format);
            return false;
        } else if (dDate.after(new Date())) {
            showMessage(R.string.later_date_error);
            return false;
        } else if (dDate.before(Constants.FIRST_CURRENCY_DATE)) {
            showMessage(R.string.early_date_error);
            return false;
        }
        return true;
    }

    public CurrenciesRatesData getCurrenciesRatesData(String date) {
        String searcher = DateUtil.changeFormatToXml(date, Constants.DATE_MAIN_FORMAT);
        if (searcher == null) {
            showMessage(R.string.date_format_error);
            return null;
        }
        for (CurrenciesRatesData data : listHistoryCurrencies) {
            if (data.getTime().equals(searcher)) {
                return data;
            }
        }
        showMessage(R.string.currencies_rates_search_error);
        return null;
    }

    public void showMessage(String message) {
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.some_error), Toast.LENGTH_SHORT).show();
        }
    }

    public void showLoading() {
        pbCurrenciesLoading.setVisibility(View.VISIBLE);
        btnDone.setVisibility(View.GONE);
    }

    public void showMessage(@StringRes int resId) {
        showMessage(getString(resId));
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
}
