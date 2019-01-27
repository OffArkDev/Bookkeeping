package com.example.android.bookkeeping.ui.transaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.bookkeeping.MyApplication;
import com.example.android.bookkeeping.R;
import com.example.android.bookkeeping.model.pojo.CurrenciesRatesData;
import com.example.android.bookkeeping.model.AccountSaver;
import com.example.android.bookkeeping.model.TransactionSaver;
import com.example.android.bookkeeping.di.components.StorageComponent;
import com.example.android.bookkeeping.di.modules.ActivityModule;
import com.example.android.bookkeeping.di.modules.StorageModule;
import com.example.android.bookkeeping.ui.adapters.TransactionsListAdapter;
import com.example.android.bookkeeping.ui.mvp.BaseActivity;
import com.example.android.bookkeeping.ui.transaction.create.CreateTransactionActivity;
import com.google.gson.Gson;

import java.util.List;

import javax.inject.Inject;

public class TransactionsActivity extends BaseActivity implements TransactionMvpView {

    private final static String TAG = "myTransactionsList";

    private Button btnCreateTransaction;
    private ListView listView;
    private Button btnDeleteTransaction;



    boolean isDeleteClicked = false;

    @Inject
    public TransactionMvpPresenter<TransactionMvpView> presenter;

    @Inject
    public TransactionsListAdapter transactionsListAdapter;



    public static Intent getStartIntent(Context context, int accountId, List<AccountSaver> listAccounts, String[] currenciesNames) {
        Intent intentTransactions = new Intent(context, TransactionsActivity.class);
        intentTransactions.putExtra("accountId", listAccounts.get(accountId).getId());
        intentTransactions.putExtra("currenciesNames", currenciesNames);
        return intentTransactions;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions_list);
        getTransactionComponent().inject(this);
        findViews();
        getDataFromIntent();
        setOnClickListeners();
        presenter.onAttach(this);
        initAdapter();
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
                presenter.btnCreateTransactionClick();
            }
        });

        btnDeleteTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.btnDeleteClick();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                presenter.itemTransactionsClick((int) id, isDeleteClicked);
            }
        });
    }

    public void initAdapter() {
        listView.setAdapter(transactionsListAdapter);
    }

    public void getDataFromIntent() {
        Intent intent = getIntent();
        presenter.getDataFromIntent(intent);

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

    public void openCreateTransactionActivity(String[] ratesNames) {
        Intent intent = CreateTransactionActivity.getStartIntent(this, ratesNames);
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
                    CurrenciesRatesData currenciesRatesData = new Gson().fromJson(data.getStringExtra("currencyRates"), CurrenciesRatesData.class);
                    String currency = data.getStringExtra("currency");
                    String comment = data.getStringExtra("comment");
                    presenter.createTransaction(name, value, currency, date, type, comment, currenciesRatesData);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }
}
