package com.example.android.bookkeeping.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.bookkeeping.R;
import com.example.android.bookkeeping.currency.CurrencyRatesData;
import com.example.android.bookkeeping.currency.UrlParser;
import com.example.android.bookkeeping.data.AccountSaver;
import com.example.android.bookkeeping.di.AppModule;
import com.example.android.bookkeeping.di.DaggerAppComponent;
import com.example.android.bookkeeping.di.StorageModule;
import com.example.android.bookkeeping.di.UrlParserModule;
import com.example.android.bookkeeping.firebase.FirebaseStartActivity;
import com.example.android.bookkeeping.repository.AccountsRepository;
import com.example.android.bookkeeping.ui.adapters.AccountsListAdapter;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class AccountsActivity extends AppCompatActivity{

    final String TAG = "myAccountsList";

    private Button btnCreateAccount;
    private Button btnDeleteAccount;
    private Button btnCloud;
    private ListView listView;
    private View vButtonsLayer;
    private ProgressBar progressBar;
    private ImageView ivChartButton;

    private AccountsListAdapter accountsListAdapter;
    private List<AccountSaver> listAccounts = new ArrayList<>();

    private CurrencyRatesData currencyRatesData;

    private final String url = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";

    private boolean isDeleteClicked = false;

    @Inject
    public Context context;

    @Inject
    public AccountsRepository accountsRepository;

    @Inject
    public UrlParser urlParser;

    @Inject
    public CompositeDisposable compositeDisposable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts_list);
        DaggerAppComponent.builder()
                .appModule(new AppModule(getApplication()))
                .storageModule(new StorageModule(getApplication()))
                .urlParserModule(new UrlParserModule(url))
                .build()
                .injectAccountsActivity(this);

        findViews();
        parseUrl();
        getAccountsFromDatabase();
        setAdapter();
        setOnClickListeners();
    }

    public void findViews() {
        btnCreateAccount = findViewById(R.id.add_account_button);
        btnDeleteAccount = findViewById(R.id.delete_account_button);
        btnCloud = findViewById(R.id.button_cloud);
        listView = findViewById(R.id.accounts_list_view);
        vButtonsLayer = findViewById(R.id.button_layer);
        progressBar = findViewById(R.id.progress_bar);
        ivChartButton = findViewById(R.id.chart_btn);
    }

    public void setAdapter() {
        accountsListAdapter = new AccountsListAdapter(this, listAccounts);
        listView.setAdapter(accountsListAdapter);
    }



    public void setOnClickListeners() {
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });


        final AdapterView.OnItemClickListener accountTransactionsClick = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intentTransactions = new Intent(context, TransactionsActivity.class);
                intentTransactions.putExtra("accountId", listAccounts.get((int)id).getId());
                Gson gson = new Gson();
                String json = gson.toJson(currencyRatesData);
                intentTransactions.putExtra("currencyRates", json);
                startActivity(intentTransactions);
            }
        };

        final AdapterView.OnItemClickListener accountDeleteClick = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //There is no chance that the number of accounts will be greater than max int. So we can use this unsafely typecasting.
                deleteAccount((int)id);
            }
        };

        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isDeleteClicked) {
                    isDeleteClicked = false;
                    btnDeleteAccount.setBackground(ContextCompat.getDrawable(context, R.drawable.empty_button));
                    listView.setOnItemClickListener(accountTransactionsClick);
                } else {
                    isDeleteClicked = true;
                    Toast.makeText(context, "click on account to delete", Toast.LENGTH_LONG).show();
                    btnDeleteAccount.setBackground(ContextCompat.getDrawable(context, R.drawable.paint_button));
                    listView.setOnItemClickListener(accountDeleteClick);
                }
            }
        });

        listView.setOnItemClickListener(accountTransactionsClick);

        btnCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FirebaseStartActivity.class);
                startActivity(intent);
            }
        });



        ivChartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChartActivity.class);
                intent.putExtra("rates", currencyRatesData.getCurrenciesList());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }


    public void getAccountsFromDatabase() {
        compositeDisposable.add(accountsRepository.getAll()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<AccountSaver>>() {
                    @Override
                    public void accept(List<AccountSaver> transactionSavers) {
                        listAccounts = transactionSavers;
                        setAdapter();
                    }
                }));
    }

    public void parseUrl() {
        showOrHideProgressBar(true);
        Observable.create(urlParser)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CurrencyRatesData>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(CurrencyRatesData data) {
                        currencyRatesData = data;
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        showOrHideProgressBar(false);
                    }
                });
    }


    public void showOrHideProgressBar(boolean show) {
        if (show) {
            vButtonsLayer.setVisibility(View.INVISIBLE);
            ivChartButton.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            vButtonsLayer.setVisibility(View.VISIBLE);
            ivChartButton.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void createAccount() {
        Intent intent = new Intent(this, CreateAccountActivity.class);
        intent.putExtra("rates", currencyRatesData.getCurrenciesList());
        startActivityForResult(intent, 1);
    }

    public void deleteAccount (final int id) {
        compositeDisposable.add(accountsRepository.delete(listAccounts.get(id))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                               @Override
                               public void run() {
                                   Log.i(TAG, "delete account complete");
                                   listAccounts.remove(id);
                                   setAdapter();
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable)  {
                                   Log.e(TAG, "delete account fail " + throwable.getMessage());
                               }
                           }
                ));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

    @Override
    protected void onStop() {
        super.onStop();
        compositeDisposable.clear();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            if (requestCode == 1) {
                if (resultCode == RESULT_OK) {
                    //getting data of new account from intent
                    String name = data.getStringExtra("name");
                    String value = data.getStringExtra("value");
                    String currency = data.getStringExtra("currency");
                    //convert value to value in rubles
                    String valueRUB = "";
                    if (currency.equals("RUB")) {
                        valueRUB = value;
                    }
                    if (!value.equals("") && !currency.equals("")) {
                        valueRUB = currencyRatesData.convertCurrency(new BigDecimal(value), currency, "RUB").toString();
                    }
                    //save result to listAccounts and database
                    final AccountSaver newAccount = new AccountSaver(name, value, valueRUB, currency);
                    compositeDisposable.add(accountsRepository.insert(newAccount)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<Long>() {
                                @Override
                                public void accept(Long aLong) {
                                    Log.i(TAG, "insert success");
                                    newAccount.setId(aLong);
                                    listAccounts.add(newAccount);
                                    setAdapter();
                                }
                            }));

                }

            }
        }
    }
}

