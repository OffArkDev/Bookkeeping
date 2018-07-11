package com.example.android.bookkeeping.ui;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.bookkeeping.R;
import com.example.android.bookkeeping.currency.ApiAdapter;
import com.example.android.bookkeeping.currency.CurrencyConverter;
import com.example.android.bookkeeping.currency.DownloadActualCurrency;
import com.example.android.bookkeeping.data.AccountData;
import com.example.android.bookkeeping.data.DBHelper;
import com.example.android.bookkeeping.firebase.FirebaseStartActivity;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class AccountsListActivity extends AppCompatActivity  {
    final String LOG_TAG = "myAccountList";


    DBHelper dbHelper;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts_list);




        final Context context = this;

        final Intent intentTransactions = new Intent(this, TransactionsListActivity.class);


        Button buttonCreateAccount = findViewById(R.id.add_account_button);
        Button buttonDeleteAccount = findViewById(R.id.delete_account_button);
        Button buttonCloud = findViewById(R.id.button_cloud);
        final ListView listView = findViewById(R.id.accounts_list_view);

        // создаем объект для создания и управления версиями БД

        dbHelper = new DBHelper(this);

        if (!DownloadActualCurrency.isDownloaded) {
            ApiAdapter adapter = new ApiAdapter();

            CurrencyConverter currencyConverter = new CurrencyConverter();


            try {
                currencyConverter = new DownloadActualCurrency().execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }

        final ArrayList<AccountData> list = getAccountsListFromDb();

        final AccountsListAdapter accountsListAdapter = new AccountsListAdapter(this, list);

        if (list == null) {
            Log.i(LOG_TAG, "no data from base");
        } else {

            listView.setAdapter(accountsListAdapter);
        }



        buttonCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.add_account_button:
                        createAccount();
                        break;
                }
            }
        });

        buttonDeleteAccount.setOnClickListener(new View.OnClickListener() {
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
                intentTransactions.putExtra("position", String.valueOf(position));
                startActivity(intentTransactions);



            }
        });

        buttonCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FirebaseStartActivity.class);
                startActivity(intent);
            }
        });


    }

    public void deleteAccountFromDB(int position) {
        int pos = position + 1;

        SQLiteDatabase database = dbHelper.getWritableDatabase();
//        int delCount = database.delete(DBHelper.TABLE_ACCOUNTS_DATA, DBHelper.KEY_ID + "=" + position, null);
        database.delete(DBHelper.TABLE_ACCOUNTS_DATA, "ID = ?",new String[] {String.valueOf(pos)});

        Log.d("mLog", "deleted rows count = " + pos);
    }

    public ArrayList<AccountData> getAccountsListFromDb() {


        SQLiteDatabase db = dbHelper.getReadableDatabase();

        boolean applicationIsJustNowCreated = DBHelper.maxAccountsId == -1;

                Log.d(LOG_TAG, "--- Rows in mytable: ---");
                Cursor c = db.query(DBHelper.TABLE_ACCOUNTS_DATA, null, null, null, null, null, null);


        ArrayList<AccountData> accountDataList = new ArrayList<>();
                if (c.moveToFirst()) {

                    int idColIndex = c.getColumnIndex(DBHelper.KEY_ID);
                    int nameColIndex = c.getColumnIndex(DBHelper.KEY_NAME);
                    int valueColIndex = c.getColumnIndex(DBHelper.KEY_VALUE);
                    int currencyColIndex = c.getColumnIndex(DBHelper.KEY_CURRENCY);

                    do {

                        if (applicationIsJustNowCreated) DBHelper.maxAccountsId++;

                        String name = c.getString(nameColIndex);
                        String value = c.getString(valueColIndex);
                        String currency = c.getString(currencyColIndex);
                        Log.d(LOG_TAG,
                                "ID = " + c.getInt(idColIndex) +
                                        ", name = " + name +
                                        ", value = " + value +
                                        ", currency = " + currency);

                        AccountData accountData = new AccountData(name, value, currency);
                        accountData.convertValueRUB();
                        accountDataList.add(accountData);

                    } while (c.moveToNext());
                } else {
                    Log.d(LOG_TAG, "0 rows");
                    return null;
                }
                c.close();

        dbHelper.close();


        return  accountDataList;
        }

        public void createAccount() {
            Intent intent = new Intent(this, CreateAccountActivity.class);
            startActivity(intent);
        }







}

