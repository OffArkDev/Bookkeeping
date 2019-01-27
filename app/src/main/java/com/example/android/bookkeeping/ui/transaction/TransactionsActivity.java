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

import com.example.android.bookkeeping.Constants;
import com.example.android.bookkeeping.MyApplication;
import com.example.android.bookkeeping.R;
import com.example.android.bookkeeping.pojo.CurrenciesRatesData;
import com.example.android.bookkeeping.data.entities.TransactionSaver;
import com.example.android.bookkeeping.di.components.StorageComponent;
import com.example.android.bookkeeping.di.modules.ActivityModule;
import com.example.android.bookkeeping.di.modules.StorageModule;
import com.example.android.bookkeeping.data.repository.TransactionsRepository;
import com.example.android.bookkeeping.ui.adapters.TransactionsListAdapter;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class TransactionsActivity extends AppCompatActivity {

    private final static String TAG = "myTransactionsList";

    private Button btnCreateTransaction;
    private ListView listView;
    private Button btnDeleteTransaction;

    private long accountId;

    private CurrenciesRatesData currenciesRatesData;

    boolean isDeleteClicked = false;

    @Inject
    public Context context;

    @Inject
    public CompositeDisposable compositeDisposable;

    @Inject
    public TransactionsRepository transactionsRepository;

    @Inject
    public List<TransactionSaver> listTransactions;
    @Inject
    public TransactionsListAdapter transactionsListAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions_list);
        getTransactionComponent().inject(this);
        findViews();
        getRatesFromIntent();
        compositeDisposable.add(getTransactionsFromDatabase());
        initAdapter();
        setOnClickListeners();
    }

    public StorageComponent getTransactionComponent() {
        return ((MyApplication) getApplication())
                .getApplicationComponent()
                .newStorageComponent(new ActivityModule(this), new StorageModule(this));
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
                createTransaction();
            }
        });

        btnDeleteTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isDeleteClicked) {
                    isDeleteClicked = false;
                    btnDeleteTransaction.setBackground(ContextCompat.getDrawable(context, R.drawable.empty_button));
                    listView.setOnItemClickListener(null);
                } else {
                    isDeleteClicked = true;
                    Toast.makeText(context, R.string.click_on_transaction_to_delete, Toast.LENGTH_LONG).show();
                    btnDeleteTransaction.setBackground(ContextCompat.getDrawable(context, R.drawable.paint_button));
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            deleteTransaction((int) id);
                        }
                    });
                }
            }
        });
    }

    public void initAdapter() {
        listView.setAdapter(transactionsListAdapter);
    }

    public void updateListView(List<TransactionSaver> listTransactions) {
        transactionsListAdapter.updateList(listTransactions);
    }


    public void getRatesFromIntent() {
        Intent intent = getIntent();
        accountId = intent.getLongExtra("accountId", 0L);
        Gson gson = new Gson();
        currenciesRatesData = gson.fromJson(intent.getStringExtra("currencyRates"), CurrenciesRatesData.class);
        Log.i(TAG, "getRatesFromIntent: " + currenciesRatesData.getTime());

    }

    public Disposable getTransactionsFromDatabase() {
        return transactionsRepository.getTransactionsOfAccount(accountId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<TransactionSaver>>() {
                    @Override
                    public void accept(List<TransactionSaver> transactionSavers) {
                        listTransactions = transactionSavers;
                        updateListView(listTransactions);
                    }
                });
    }

    public void createTransaction() {
        Intent intent = new Intent(this, CreateTransactionActivity.class);
        intent.putExtra("ratesNames", currenciesRatesData.getCurrenciesList());
        startActivityForResult(intent, 1);
    }

    public void deleteTransaction(final int id) {
        compositeDisposable.add(transactionsRepository.delete(listTransactions.get(id))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() {
                        Log.i(TAG, "delete transaction complete");
                        listTransactions.remove(id);
                        updateListView(listTransactions);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        Log.i(TAG, "delete transaction fail " + throwable.getMessage());
                    }
                })
        );
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }

    public String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat(Constants.DATE_MAIN_FORMAT, Locale.ENGLISH);
        return mdformat.format(calendar.getTime());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            if (requestCode == 1) {
                if (resultCode == RESULT_OK) {
                    String type = data.getStringExtra("type");
                    String name = data.getStringExtra("name");
                    String date = getCurrentDate();
                    String value = data.getStringExtra("value");
                    String currency = data.getStringExtra("currency");
                    String comment = data.getStringExtra("comment");
                    String valueRUB = "";
                    if (currency.equals(Constants.NAME_CURRENCY_RUB)) {
                        valueRUB = value;
                    }
                    if (!value.equals("") && !currency.equals("")) {
                        valueRUB = currenciesRatesData.convertCurrency(value, currency, Constants.NAME_CURRENCY_RUB);
                    }
                    final TransactionSaver newTransactionSaver = new TransactionSaver(accountId, type, name, date, value, valueRUB, currency, comment);
                    compositeDisposable.add(transactionsRepository.insert(newTransactionSaver)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<Long>() {
                                @Override
                                public void accept(Long aLong) {
                                    Log.i(TAG, "insert success");
                                    newTransactionSaver.setId(aLong);
                                    listTransactions.add(newTransactionSaver);
                                    updateListView(listTransactions);
                                }
                            }));
                }
            }
        }
    }
}
