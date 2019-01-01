package com.example.android.bookkeeping.ui.transaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.bookkeeping.MyApplication;
import com.example.android.bookkeeping.R;
import com.example.android.bookkeeping.currency.CurrencyRatesData;
import com.example.android.bookkeeping.data.model.AccountSaver;
import com.example.android.bookkeeping.data.model.TransactionSaver;
import com.example.android.bookkeeping.di.components.TransactionComponent;
import com.example.android.bookkeeping.di.modules.ActivityModule;
import com.example.android.bookkeeping.di.modules.StorageModule;
import com.example.android.bookkeeping.ui.adapters.TransactionsListAdapter;
import com.example.android.bookkeeping.ui.transaction.create.CreateTransactionActivity;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class TransactionsActivity extends AppCompatActivity implements TransactionMvpView {

    private final static String TAG = "myTransactionsList";

    private Button btnCreateTransaction;
    private ListView listView;
    private Button btnDeleteTransaction;


    private TransactionsListAdapter transactionsListAdapter;


    boolean isDeleteClicked = false;

    @Inject
    TransactionMvpPresenter<TransactionMvpView> presenter;



    public static Intent getStartIntent(Context context, int accountId, List<AccountSaver> listAccounts, CurrencyRatesData currencyRatesData) {
        Intent intentTransactions = new Intent(context, TransactionsActivity.class);
        intentTransactions.putExtra("accountId", listAccounts.get(accountId).getId());
        Gson gson = new Gson();
        String json = gson.toJson(currencyRatesData);
        intentTransactions.putExtra("currencyRates", json);
        return intentTransactions;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions_list);
        getTransactionComponent().inject(this);
        findViews();
        getRatesFromIntent();
        setOnClickListeners();
        presenter.onAttach(this);
        initAdapter();
    }

    public TransactionComponent getTransactionComponent() {
        return ((MyApplication) getApplication())
                .getApplicationComponent()
                .newTransactionComponent(new ActivityModule(this), new StorageModule(this));
    }

    public void findViews() {
        btnCreateTransaction = findViewById(R.id.add_transaction_button);
        listView = findViewById(R.id.transactions_list_view);
        btnDeleteTransaction = findViewById(R.id.delete_transaction_button);
    }

    public void setOnClickListeners() {
        btnCreateTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.btnCreateTransactionClick();
            }
        });

        btnDeleteTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.btnDeleteClick();
            }
        });
    }

    public void initAdapter() {
        transactionsListAdapter = presenter.initTransactionsAdapter();
        listView.setAdapter(transactionsListAdapter);
    }

    public void getRatesFromIntent() {
        Intent intent = getIntent();
        presenter.getRatesFromIntent(intent);

    }

    @Override
    public void updateListView(List<TransactionSaver> listTransactions) {
        transactionsListAdapter.updateList(listTransactions);
    }

    @Override
    public void changeDeleteButtonState() {
        if (isDeleteClicked) {
            isDeleteClicked = false;
            btnDeleteTransaction.setBackground(ContextCompat.getDrawable(this, R.drawable.empty_button));
        } else {
            isDeleteClicked = true;
            Toast.makeText(this, R.string.click_on_transaction_to_delete, Toast.LENGTH_LONG).show();
            btnDeleteTransaction.setBackground(ContextCompat.getDrawable(this, R.drawable.paint_button));
        }
    }

    public void openCreateTransactionActivity(CurrencyRatesData currencyRatesData) {
        Intent intent = CreateTransactionActivity.getStartIntent(this, currencyRatesData);
        startActivityForResult(intent, 1);
    }

    @Override
    public Context getContext() {
        return this;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            if (requestCode == 1) {
                if (resultCode == RESULT_OK) {
                    String type = data.getStringExtra("type");
                    String name = data.getStringExtra("name");
                    String value = data.getStringExtra("value");
                    String currency = data.getStringExtra("currency");
                    String comment = data.getStringExtra("comment");
                    presenter.createTransaction(type, name, value, currency, comment);
                }
            }
        }
    }



    @Override
    public void onError(int resId) {

    }

    @Override
    public void onError(String message) {

    }

    @Override
    public void showMessage(String message) {

    }

    @Override
    public void showMessage(int resId) {

    }





}
