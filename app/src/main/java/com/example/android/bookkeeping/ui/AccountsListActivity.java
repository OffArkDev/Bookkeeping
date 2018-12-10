package com.example.android.bookkeeping.ui;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.android.bookkeeping.R;
import com.example.android.bookkeeping.currency.CurrencyRatesData;
import com.example.android.bookkeeping.currency.RatesListener;
import com.example.android.bookkeeping.currency.UrlParser;
import com.example.android.bookkeeping.data.AccountSaver;
import com.example.android.bookkeeping.data.DBHelper;
import com.example.android.bookkeeping.di.AppModule;
import com.example.android.bookkeeping.di.DaggerAppComponent;
import com.example.android.bookkeeping.di.StorageModule;
import com.example.android.bookkeeping.di.UrlParserModule;
import com.example.android.bookkeeping.firebase.FirebaseStartActivity;
import com.example.android.bookkeeping.repository.AccountsRepository;
import com.example.android.bookkeeping.ui.adapters.AccountsListAdapter;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

public class AccountsListActivity extends AppCompatActivity implements RatesListener {

    final String TAG = "myAccountList";

    DBHelper dbHelper;

    private Button btnCreateAccount;
    private Button btnDeleteAccount;
    private Button btnCloud;
    private ListView listView;

    private Context context = this;

    private AccountsListAdapter accountsListAdapter;
    private List<AccountSaver> list;
    private CurrencyRatesData currencyRatesData;

    private final String url = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";


    @Inject
    public AccountsRepository accountsRepository;

    @Inject
    public UrlParser urlParser;

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

        initViews();


        accountsRepository.getAll()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<AccountSaver>>() {
                    @Override
                    public void accept(List<AccountSaver> accountSavers) {
                        list = accountSavers;
                        accountsListAdapter = new AccountsListAdapter(context, list);
                        listView.setAdapter(accountsListAdapter);
                    }
                });

        urlParser.execute();



        accountsListAdapter = new AccountsListAdapter(this, list);
        listView.setAdapter(accountsListAdapter);

        setOnclickListeners();
    }

    public void initViews () {
        btnCreateAccount = findViewById(R.id.add_account_button);
        btnDeleteAccount = findViewById(R.id.delete_account_button);
        btnCloud = findViewById(R.id.button_cloud);
        listView = findViewById(R.id.accounts_list_view);
    }

    public void setOnclickListeners() {

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.add_account_button:
                        createAccount();
                        break;
                }
            }
        });

        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.delete(DBHelper.TABLE_ACCOUNTS_DATA, null, null);
                db.delete(DBHelper.TABLE_TRANSACTIONS_DATA, null, null);
                accountsListAdapter.notifyDataSetChanged();
                recreate();
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intentTransactions = new Intent(context, TransactionsListActivity.class);
                intentTransactions.putExtra("position", String.valueOf(position));
                startActivity(intentTransactions);

            }
        });

        btnCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FirebaseStartActivity.class);
                startActivity(intent);
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
                    //save result to list and database
                    AccountSaver newAccountSaver = new AccountSaver(name, value, valueRUB, currency);
                    list.add(newAccountSaver);

                    accountsRepository.insert(newAccountSaver);
                }
            }
        }
    }
}

