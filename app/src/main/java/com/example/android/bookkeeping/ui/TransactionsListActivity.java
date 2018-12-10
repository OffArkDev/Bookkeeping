package com.example.android.bookkeeping.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.android.bookkeeping.R;
import com.example.android.bookkeeping.data.AccountData;
import com.example.android.bookkeeping.data.DBHelper;
import com.example.android.bookkeeping.data.Transaction;
import com.example.android.bookkeeping.ui.adapters.TransactionsListAdapter;

import java.util.ArrayList;

public class TransactionsListActivity extends AppCompatActivity {

    private final static String LOG_TAG = "mytag";
    DBHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions_list);

        final Context context = this;

        Button buttonCreateTransaction = findViewById(R.id.add_transaction_button);

        final ListView listView = findViewById(R.id.transactions_list_view);

        Intent intentPrev = getIntent();
        final String pos = intentPrev.getStringExtra("position");

        int position = Integer.parseInt(pos);
        Log.d(LOG_TAG, "pos " + position);
            dbHelper = new DBHelper(this);
     //       AccountData accountData = getAccountFromDB(position);
            ArrayList<Transaction> transactions = getTransactionsListFromDB(position);

        final TransactionsListAdapter transactionsListAdapter = new TransactionsListAdapter(this, transactions);
            if (transactions == null) {
                Log.i(LOG_TAG, "no data from base");
            } else {

                listView.setAdapter(transactionsListAdapter);
            }

        buttonCreateTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.add_transaction_button:
                        createTransaction(pos);
                        break;
                }
            }
        });


    }

    public void createTransaction(String pos) {
        Intent intent = new Intent(this, CreateTransactionActivity.class);
        intent.putExtra("position", pos);
        startActivity(intent);
    }

    public ArrayList<Transaction> getTransactionsListFromDB(int position) {

        ArrayList<Transaction> transactions = new ArrayList<>();

        Log.d(LOG_TAG, "---INNER JOIN with query trans---");
        Log.d(LOG_TAG, "position " + position);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = "position = ?";
        String[] selectionArgs = new String[] {String.valueOf(position)};
        Cursor cursor = db.query(DBHelper.TABLE_TRANSACTIONS_DATA, null, selection, selectionArgs, null, null,
                null);

        int idColIndex = cursor.getColumnIndex((DBHelper.KEY_ID));
        int nameColIndex = cursor.getColumnIndex(DBHelper.KEY_NAME);
        int valueColIndex = cursor.getColumnIndex(DBHelper.KEY_VALUE);
        int currencyColIndex = cursor.getColumnIndex(DBHelper.KEY_CURRENCY);
        int typeColIndex = cursor.getColumnIndex(DBHelper.KEY_TYPE);
        int dateColIndex = cursor.getColumnIndex(DBHelper.KEY_DATE);
        int commentColIndex = cursor.getColumnIndex(DBHelper.KEY_COMMENT);



        boolean applicationIsJustNowCreated = DBHelper.maxTransactionsId == -1;

        Log.d(LOG_TAG, "tranfrdb" );
        if (cursor.moveToFirst()) {
            Log.d(LOG_TAG, "transfrdb curs   " + cursor.getString(idColIndex));
            String str;
            do {

                if (applicationIsJustNowCreated) DBHelper.maxTransactionsId++;

                Log.d(LOG_TAG, "aplication created not just now");
                str = "";
                for (String cn : cursor.getColumnNames()) {
                    str = str.concat(cn + " = " + cursor.getString(cursor.getColumnIndex(cn)) + "; ");
                }
                Log.d(LOG_TAG, str);

                String name = cursor.getString(nameColIndex);
                String value = cursor.getString(valueColIndex);
                String currency = cursor.getString(currencyColIndex);
                String type = cursor.getString(typeColIndex);
                String date = cursor.getString(dateColIndex);
                String comment = cursor.getString(commentColIndex);
                Log.d(LOG_TAG, "name " + name + "value " + value);
                Transaction transaction = new Transaction(type, name, date, value, currency, comment);
                transactions.add(transaction);

            } while (cursor.moveToNext());
        }

        cursor.close();
        Log.d(LOG_TAG, "--- ---");
        dbHelper.close();

        return transactions;
    }

    public AccountData getAccountFromDB(int position) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Log.d(LOG_TAG, "--- Rows in mytable get account---");

        String selection = DBHelper.KEY_ID + " = ?";

        Log.d(LOG_TAG, "position" + (position));
        String[] selectionArgs = new String[] {String.valueOf(position)};
        Cursor c = db.query(DBHelper.TABLE_ACCOUNTS_DATA, null, selection, selectionArgs, null, null, null);

        AccountData accountData;
        if (c.moveToFirst()) {


            do {

                int idColIndex = c.getColumnIndex(DBHelper.KEY_ID);
            int nameColIndex = c.getColumnIndex(DBHelper.KEY_NAME);
            int valueColIndex = c.getColumnIndex(DBHelper.KEY_VALUE);
            int currencyColIndex = c.getColumnIndex(DBHelper.KEY_CURRENCY);


                String name = c.getString(nameColIndex);
                String value = c.getString(valueColIndex);
                String currency = c.getString(currencyColIndex);
                Log.d(LOG_TAG,
                        "ID = " + c.getInt(idColIndex) +
                                ", name = " + name +
                                ", value = " + value +
                                ", currency = " + currency);

                accountData = new AccountData(name, value, currency);
        } while (c.moveToNext());
        } else {
            Log.d(LOG_TAG, "0 rows");
            return null;
        }
        c.close();

        dbHelper.close();

        return  accountData;
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, AccountsListActivity.class);
        startActivity(intent);

    }
}
