package com.example.android.bookkeeping.firebase;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.android.bookkeeping.R;
import com.example.android.bookkeeping.data.AccountData;
import com.example.android.bookkeeping.data.DBHelper;
import com.example.android.bookkeeping.data.Transaction;

import java.util.ArrayList;

public class FirebaseStorageActivity extends AppCompatActivity {

    private final static String LOG_TAG = "mystorage";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_firebase);

        Button buttonSave = findViewById(R.id.button_cloud_save);
        Button buttonLoad = findViewById(R.id.button_cloud_load);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDataFromDB();
            }
        });

    }

    public void getDataFromDB() {

        DBHelper dbHelper = new DBHelper(this);
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


        }
        c.close();

        ArrayList<Transaction> transactions = new ArrayList<>();

        Log.d(LOG_TAG, "---INNER JOIN with query trans---");

        SQLiteDatabase dbt = dbHelper.getWritableDatabase();

        Cursor cursorT = dbt.query(DBHelper.TABLE_TRANSACTIONS_DATA, null, null, null, null, null,
                null);

        int idColIndex = cursorT.getColumnIndex((DBHelper.KEY_ID));
        int positionIndex = cursorT.getColumnIndex(DBHelper.KEY_POSITION);
        int nameColIndex = cursorT.getColumnIndex(DBHelper.KEY_NAME);
        int valueColIndex = cursorT.getColumnIndex(DBHelper.KEY_VALUE);
        int currencyColIndex = cursorT.getColumnIndex(DBHelper.KEY_CURRENCY);
        int typeColIndex = cursorT.getColumnIndex(DBHelper.KEY_TYPE);
        int dateColIndex = cursorT.getColumnIndex(DBHelper.KEY_DATE);
        int commentColIndex = cursorT.getColumnIndex(DBHelper.KEY_COMMENT);



        boolean applicationIsJustNowCreated1 = DBHelper.maxTransactionsId == -1;

        Log.d(LOG_TAG, "tranfrdb" );
        if (cursorT.moveToFirst()) {
            Log.d(LOG_TAG, "transfrdb curs   " + cursorT.getString(idColIndex));
            String str;
            do {

                if (applicationIsJustNowCreated1) DBHelper.maxTransactionsId++;

                Log.d(LOG_TAG, "aplication created not just now");
                str = "";
                for (String cn : cursorT.getColumnNames()) {
                    str = str.concat(cn + " = " + cursorT.getString(cursorT.getColumnIndex(cn)) + "; ");
                }
                Log.d(LOG_TAG, str);

                int position = cursorT.getInt(positionIndex);
                String name = cursorT.getString(nameColIndex);
                String value = cursorT.getString(valueColIndex);
                String currency = cursorT.getString(currencyColIndex);
                String type = cursorT.getString(typeColIndex);
                String date = cursorT.getString(dateColIndex);
                String comment = cursorT.getString(commentColIndex);
                Log.d(LOG_TAG, "name " + name + "value " + value);
                Transaction transaction = new Transaction(type, name, date, value, currency, comment);
                transaction.convertValueRUB();

                transactions.add(transaction);

            } while (cursorT.moveToNext());
        }

        cursorT.close();
        Log.d(LOG_TAG, "--- ---");
        dbHelper.close();

     //   return transactions;


      //  return accountDataList;
    }

}
