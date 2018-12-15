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
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.bookkeeping.R;
import com.example.android.bookkeeping.currency.CurrencyRatesData;
import com.example.android.bookkeeping.currency.RatesListener;
import com.example.android.bookkeeping.currency.UrlParser;
import com.example.android.bookkeeping.data.AccountSaver;
import com.example.android.bookkeeping.di.AppModule;
import com.example.android.bookkeeping.di.DaggerAppComponent;
import com.example.android.bookkeeping.di.StorageModule;
import com.example.android.bookkeeping.di.UrlParserModule;
import com.example.android.bookkeeping.firebase.FirebaseStartActivity;
import com.example.android.bookkeeping.repository.AccountsDataSource;
import com.example.android.bookkeeping.ui.adapters.AccountsListAdapter;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class AccountsActivity extends AppCompatActivity implements RatesListener {

    final String TAG = "myAccountsList";

    private Button btnCreateAccount;
    private Button btnDeleteAccount;
    private Button btnCloud;
    private ListView listView;


    private AccountsListAdapter accountsListAdapter;
    private List<AccountSaver> listAccounts = new ArrayList<>();

    private CurrencyRatesData currencyRatesData;

    private final String url = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";

    private boolean btnDeleteClicked = false;

    @Inject
    public Context context;

    @Inject
    public AccountsDataSource accountsDataSource;

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
                .urlParserModule(new UrlParserModule(url, this))
                .build()
                .injectAccountsListActivity(this);

        findViews();
        compositeDisposable.add(getAccountsFromDatabase());
        urlParser.execute();
        setAdapter();
        setOnClickListeners();
    }

    public void findViews() {
        btnCreateAccount = findViewById(R.id.add_account_button);
        btnDeleteAccount = findViewById(R.id.delete_account_button);
        btnCloud = findViewById(R.id.button_cloud);
        listView = findViewById(R.id.accounts_list_view);
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
                intentTransactions.putExtra("account_id", listAccounts.get(position).getId());
                EventBus.getDefault().postSticky(currencyRatesData);
                startActivity(intentTransactions);
            }
        };

        final AdapterView.OnItemClickListener accountDeleteClick = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                deleteAccount((int) id);
            }
        };

        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnDeleteClicked) {
                    btnDeleteClicked = false;
                    btnDeleteAccount.setBackgroundResource(android.R.drawable.btn_default);
                    listView.setOnItemClickListener(accountTransactionsClick);
                } else {
                    btnDeleteClicked = true;
                    Toast.makeText(context, "click on account to delete", Toast.LENGTH_LONG).show();
                    btnDeleteAccount.setBackgroundColor(ContextCompat.getColor(context, R.color.blue));
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
    }


    public Disposable getAccountsFromDatabase() {
        return accountsDataSource.getAll()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<AccountSaver>>() {
                    @Override
                    public void accept(List<AccountSaver> transactionSavers) {
                        listAccounts = transactionSavers;
                        setAdapter();
                    }
                });
    }

    @Override
    public void loadingComplete(CurrencyRatesData currencyRatesData) {
        this.currencyRatesData = currencyRatesData;
    }

    public void createAccount() {
        Intent intent = new Intent(this, CreateAccountActivity.class);
        intent.putExtra("rates", currencyRatesData.getCurrenciesList());
        startActivityForResult(intent, 1);
    }

    public void deleteAccount (final int id) {
        compositeDisposable.add(accountsDataSource.delete(listAccounts.get(id))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                               @Override
                               public void run() {
                                   Log.i(TAG, "delete complete");
                                   listAccounts.remove(id);
                                   setAdapter();
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable)  {
                                   Log.e(TAG, "delete fail " + throwable.getMessage());
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
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

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

                    compositeDisposable.add(accountsDataSource.insert(newAccount)
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

