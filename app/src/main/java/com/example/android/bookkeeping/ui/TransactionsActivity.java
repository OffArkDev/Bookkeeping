package com.example.android.bookkeeping.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.android.bookkeeping.R;
import com.example.android.bookkeeping.currency.CurrencyRatesData;
import com.example.android.bookkeeping.data.TransactionSaver;
import com.example.android.bookkeeping.di.AppModule;
import com.example.android.bookkeeping.di.DaggerAppComponent;
import com.example.android.bookkeeping.di.StorageModule;
import com.example.android.bookkeeping.di.UrlParserModule;
import com.example.android.bookkeeping.repository.TransactionsDataSource;
import com.example.android.bookkeeping.ui.adapters.TransactionsListAdapter;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class TransactionsActivity extends AppCompatActivity {

    private final static String TAG = "myTransactionsList";


    private final String url = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";

    private Button btnCreateTransaction;
    private ListView listView;

    private List<TransactionSaver> transactionsList = new ArrayList<>();

    private long accountId;

    private CurrencyRatesData currencyRatesData;

    @Inject
    Context context;


    @Inject
    public CompositeDisposable compositeDisposable;

    @Inject
    public TransactionsDataSource transactionsDataSource;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions_list);
        DaggerAppComponent.builder()
                .appModule(new AppModule(getApplication()))
                .storageModule(new StorageModule(getApplication()))
                .urlParserModule(new UrlParserModule(url, null))
                .build()
                .injectTransactionsListActivity(this);

        findViews();
        setRatesFromIntent();
        compositeDisposable.add(getTransactionsFromDatabase());
        setAdapter();
        setOnClickListeners();
    }

    public void findViews() {
        btnCreateTransaction = findViewById(R.id.add_transaction_button);
        listView = findViewById(R.id.transactions_list_view);
    }

    public void setOnClickListeners() {
        btnCreateTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTransaction();
            }
        });
    }

    public void setAdapter() {
        final TransactionsListAdapter transactionsListAdapter = new TransactionsListAdapter(this, transactionsList);
        listView.setAdapter(transactionsListAdapter);
    }

    public void setRatesFromIntent() {
        Intent intent = getIntent();
        accountId = intent.getLongExtra("account_id", 0L);
        currencyRatesData = EventBus.getDefault().removeStickyEvent(CurrencyRatesData.class);
    }

    public Disposable getTransactionsFromDatabase() {
        return transactionsDataSource.getTransactionsOfAccount(accountId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<TransactionSaver>>() {
                    @Override
                    public void accept(List<TransactionSaver> transactionSavers) {
                        transactionsList = transactionSavers;
                        setAdapter();
                    }
                });
    }

    public void createTransaction() {
        Intent intent = new Intent(this, CreateTransactionActivity.class);
        intent.putExtra("ratesNames", currencyRatesData.getCurrenciesList());
        startActivityForResult(intent, 1);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            if (requestCode == 1) {
                if (resultCode == RESULT_OK) {
                   String type = data.getStringExtra("type");
                   String name = data.getStringExtra("name");
                   String date = data.getStringExtra("date");
                   String value = data.getStringExtra("value");
                   String currency = data.getStringExtra("currency");
                   String comment = data.getStringExtra("comment");
                    String valueRUB = "";
                    if (currency.equals("RUB")) {
                        valueRUB = value;
                    }
                    if (!value.equals("") && !currency.equals("")) {
                        valueRUB = currencyRatesData.convertCurrency(new BigDecimal(value), currency, "RUB").toString();
                    }

                    final TransactionSaver newTransactionSaver = new TransactionSaver(accountId, type, name, date, value, valueRUB, currency, comment);

                    compositeDisposable.add(transactionsDataSource.insert(newTransactionSaver)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<Long>() {
                                @Override
                                public void accept(Long aLong) {
                                    Log.i(TAG, "insert success");
                                    newTransactionSaver.setId(aLong);
                                    transactionsList.add(newTransactionSaver);
                                    setAdapter();
                                }
                            }));
                }
            }
        }
    }
}
