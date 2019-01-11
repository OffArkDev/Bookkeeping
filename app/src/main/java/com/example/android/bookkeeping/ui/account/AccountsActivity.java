package com.example.android.bookkeeping.ui.account;

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

import com.example.android.bookkeeping.Constants;
import com.example.android.bookkeeping.MyApplication;
import com.example.android.bookkeeping.R;
import com.example.android.bookkeeping.currency.CurrenciesRatesData;
import com.example.android.bookkeeping.currency.UrlParser;
import com.example.android.bookkeeping.data.AccountSaver;
import com.example.android.bookkeeping.di.components.StorageParserComponent;
import com.example.android.bookkeeping.di.modules.ActivityModule;
import com.example.android.bookkeeping.di.modules.UrlParserModule;
import com.example.android.bookkeeping.di.modules.StorageModule;
import com.example.android.bookkeeping.repository.AccountsRepository;
import com.example.android.bookkeeping.ui.ChartActivity;
import com.example.android.bookkeeping.ui.cloud.FirebaseAuthActivity;
import com.example.android.bookkeeping.ui.transaction.TransactionsActivity;
import com.example.android.bookkeeping.ui.adapters.AccountsListAdapter;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
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

    private CurrenciesRatesData currenciesRatesData;

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
        getAccountComponent().inject(this);
        findViews();
        zipFlows();
        initAdapter();
        setOnClickListeners();
    }

    public StorageParserComponent getAccountComponent() {
        return ((MyApplication) getApplication())
                .getApplicationComponent()
                .newStorageParserComponent(new ActivityModule(this), new StorageModule(this), new UrlParserModule(Constants.URL_DAILY));
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

    public void initAdapter() {
        accountsListAdapter = new AccountsListAdapter(this, listAccounts);
        listView.setAdapter(accountsListAdapter);
    }

    public void updateListView(List<AccountSaver> listAccounts) {
        accountsListAdapter.updateList(listAccounts);
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
                String json = gson.toJson(currenciesRatesData);
                intentTransactions.putExtra("currencyRates", json);
                startActivity(intentTransactions);
            }
        };

        final AdapterView.OnItemClickListener accountDeleteClick = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
                    Toast.makeText(context, R.string.click_account_to_delete, Toast.LENGTH_LONG).show();
                    btnDeleteAccount.setBackground(ContextCompat.getDrawable(context, R.drawable.paint_button));
                    listView.setOnItemClickListener(accountDeleteClick);
                }
            }
        });

        listView.setOnItemClickListener(accountTransactionsClick);

        btnCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FirebaseAuthActivity.class);
                startActivityForResult(intent, 2);
            }
        });



        ivChartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChartActivity.class);
                intent.putExtra("rates", currenciesRatesData.getCurrenciesList());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }


    public void zipFlows() {
        showLoading();
        Flowable<CurrenciesRatesData> parseUrl = Flowable.create(urlParser, BackpressureStrategy.DROP);
        Flowable<List<AccountSaver>> getAccounts = accountsRepository.getAll();

        BiFunction<CurrenciesRatesData, List<AccountSaver>, Boolean> biFunction = new BiFunction<CurrenciesRatesData, List<AccountSaver>, Boolean>() {
            @Override
            public Boolean apply(CurrenciesRatesData data, List<AccountSaver> accountSavers) {
                currenciesRatesData = data;
                listAccounts = accountSavers;
                return true;
            }
        };


        compositeDisposable.add(
                Flowable.zip(parseUrl.subscribeOn(Schedulers.newThread()),
                        getAccounts.subscribeOn(Schedulers.newThread()),
                        biFunction)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean o){
                                updateDailyCurrencies();
                            }
                        }));
    }


    public void getAccountsFromDatabase() {
        compositeDisposable.add(accountsRepository.getAll()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<AccountSaver>>() {
                    @Override
                    public void accept(List<AccountSaver> accountSavers) {
                        listAccounts = accountSavers;
                        updateDailyCurrencies();
                    }
                }));
    }

    private  void updateDailyCurrencies() {
        for (AccountSaver account : listAccounts) {
            String updatedValueRub = currenciesRatesData.convertCurrency(account.getValue(), account.getCurrency(), Constants.NAME_CURRENCY_RUB);
            account.setValueRUB(updatedValueRub);
        }
        updateStorageData();
    }

    private void updateStorageData() {
        List<Integer> listId = new ArrayList<>();
        for (int i = 0; i < listAccounts.size(); i++) {
            listId.add(i);
        }

        compositeDisposable.clear();

        //for every index of listAccounts create completable request to update new value in rubles
        Flowable<Integer> flow = Flowable.fromIterable(listId);
        compositeDisposable.add(flow.flatMapCompletable(new Function<Integer, Completable>() {
            @Override
            public Completable apply(Integer index) {
                return accountsRepository.updateValueRub(listAccounts.get(index).getId(), listAccounts.get(index).getValueRUB());
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run()  {
                        Log.e(TAG, "update success ");
                        hideLoading();
                        setOnClickListeners();
                        updateListView(listAccounts);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        Log.e(TAG, "delete account fail " + throwable.getMessage());
                    }
                }));
    }


    public void showLoading() {
        vButtonsLayer.setVisibility(View.INVISIBLE);
        ivChartButton.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        vButtonsLayer.setVisibility(View.VISIBLE);
        ivChartButton.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    public void createAccount() {
        Intent intent = new Intent(this, CreateAccountActivity.class);
        intent.putExtra("rates", currenciesRatesData.getCurrenciesList());
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
                                   initAdapter();
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
            if (requestCode == 1) {
                if (resultCode == RESULT_OK && data != null) {
                    String name = data.getStringExtra("name");
                    String value = data.getStringExtra("value");
                    String currency = data.getStringExtra("currency");
                    String valueRUB = "";
                    if (currency.equals(Constants.NAME_CURRENCY_RUB)) {
                        valueRUB = value;
                    } else if (!value.equals("") && !currency.equals("")) {
                        valueRUB = currenciesRatesData.convertCurrency(value, currency, Constants.NAME_CURRENCY_RUB);
                    }
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
                                    updateListView(listAccounts);
                                }
                            }));
                }
            } else  if (requestCode == 2) {
                if (resultCode == RESULT_OK) {
                    getAccountsFromDatabase();
                }
            }

    }

}

