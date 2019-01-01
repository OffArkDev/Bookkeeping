package com.example.android.bookkeeping.ui.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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
import com.example.android.bookkeeping.currency.CurrencyRatesData;
import com.example.android.bookkeeping.data.model.AccountSaver;
import com.example.android.bookkeeping.di.components.AccountComponent;
import com.example.android.bookkeeping.di.modules.ActivityModule;
import com.example.android.bookkeeping.di.modules.StorageModule;
import com.example.android.bookkeeping.di.modules.UrlParserModule;
import com.example.android.bookkeeping.ui.chart.ChartActivity;
import com.example.android.bookkeeping.ui.account.create.CreateAccountActivity;
import com.example.android.bookkeeping.ui.adapters.AccountsListAdapter;
import com.example.android.bookkeeping.ui.cloud.auth.FirebaseAuthActivity;
import com.example.android.bookkeeping.ui.mvp.BaseActivity;
import com.example.android.bookkeeping.ui.transaction.TransactionsActivity;

import java.util.List;

import javax.inject.Inject;

public class AccountsActivity extends BaseActivity implements AccountsMvpView {

    final String TAG = "myAccountsList";

    private Button btnCreateAccount;
    private Button btnDeleteAccount;
    private Button btnCloud;
    private ListView listView;
    private View vButtonsLayer;
    private ProgressBar progressBar;
    private ImageView ivChartButton;

    public AccountsListAdapter accountsListAdapter;

    @Inject
    public AccountsMvpPresenter<AccountsMvpView> mPresenter;

    private boolean isDeleteClicked = false;

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, AccountsActivity.class);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts_list);
        getAccountComponent().inject(this);
        findViews();
        setOnClickListeners();
        mPresenter.onAttach(this);
        initAdapter();
    }


    @Override
    protected void onDestroy() {
        mPresenter.onDetach();
        super.onDestroy();
    }

    public AccountComponent getAccountComponent() {
        return ((MyApplication) getApplication())
                .getApplicationComponent()
                .newAccountComponent(new ActivityModule(this), new StorageModule(this), new UrlParserModule(Constants.URL_DAILY));
    }

    @Override
    public void findViews() {
        btnCreateAccount = findViewById(R.id.add_account_button);
        btnDeleteAccount = findViewById(R.id.delete_account_button);
        btnCloud = findViewById(R.id.button_cloud);
        listView = findViewById(R.id.accounts_list_view);
        vButtonsLayer = findViewById(R.id.button_layer);
        progressBar = findViewById(R.id.progress_bar);
        ivChartButton = findViewById(R.id.chart_btn);
    }


    @Override
    public void setOnClickListeners() {
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.btnCreateAccountClick();
            }
        });

        btnCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.btnCloudClick();
            }
        });


        ivChartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.btnChartClick();
            }
        });

        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.btnDeleteAccount();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPresenter.itemAccountsClick((int) id, isDeleteClicked);
            }
        });

    }

    public void initAdapter(){
        accountsListAdapter = mPresenter.initAccountsAdapter();
        listView.setAdapter(accountsListAdapter);
    }


    @Override
    public void updateListView(List<AccountSaver> listAccounts) {
        accountsListAdapter.updateList(listAccounts);
    }


    @Override
    public void showLoading() {
        vButtonsLayer.setVisibility(View.INVISIBLE);
        ivChartButton.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        vButtonsLayer.setVisibility(View.VISIBLE);
        ivChartButton.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
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

    @Override
    public void openCreateAccountActivity(CurrencyRatesData currencyRatesData) {
        Intent intent = CreateAccountActivity.getStartIntent(this, currencyRatesData);
        startActivityForResult(intent, 1);
    }

    @Override
    public void openTransactionsActivity(int accountId, List<AccountSaver> listAccounts, CurrencyRatesData currencyRatesData) {
        Intent intentTransactions = TransactionsActivity.getStartIntent(this, accountId, listAccounts, currencyRatesData);
        startActivity(intentTransactions);
    }



    @Override
    public void changeDeleteButtonState() {
        if (isDeleteClicked) {
            isDeleteClicked = false;
            btnDeleteAccount.setBackground(ContextCompat.getDrawable(this, R.drawable.empty_button));
        } else {
            isDeleteClicked = true;
            Toast.makeText(this, R.string.click_account_to_delete, Toast.LENGTH_LONG).show();
            btnDeleteAccount.setBackground(ContextCompat.getDrawable(this, R.drawable.paint_button));
        }
    }

    @Override
    public void openCloudActivity() {
        Intent intent = FirebaseAuthActivity.getStartIntent(this);
        startActivityForResult(intent, 2);
    }

    @Override
    public void openChartActivity(CurrencyRatesData currencyRatesData) {
        Intent intent = ChartActivity.getStartIntent(this, currencyRatesData);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK && data != null) {
                String name = data.getStringExtra("name");
                String value = data.getStringExtra("value");
                String currency = data.getStringExtra("currency");
                mPresenter.createAccount(name, value, currency);
            }
        } else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                mPresenter.getAccountsFromDatabase();
            }
        }
    }
}


