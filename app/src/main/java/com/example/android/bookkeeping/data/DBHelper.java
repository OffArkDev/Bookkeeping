package com.example.android.bookkeeping.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private final String LOG_TAG = "myDBHelper";

    public static int maxAccountsId = -1;
    public static int maxTransactionsId = -1;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "accountsList";
    public static final String TABLE_ACCOUNTS_DATA = "accounts";
    public static final String TABLE_TRANSACTIONS_DATA = "transactions";

    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_VALUE = "value";
    public static final String KEY_CURRENCY = "currency";

   public static final String KEY_POSITION = "position";
   public static final String KEY_TYPE = "type";
   public static final String KEY_DATE = "date";
   public static final String KEY_COMMENT = "comment";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("myTag", "--- onCreate database ---");

        db.execSQL("create table " + TABLE_ACCOUNTS_DATA + "("
                + KEY_ID + " integer primary key,"
                + KEY_NAME + " text,"
                + KEY_VALUE + " real,"
                + KEY_CURRENCY + " text"
                + ");");

        db.execSQL("create table " + TABLE_TRANSACTIONS_DATA + "("
                + KEY_ID + " integer primary key,"
                + KEY_POSITION + " integer,"
                + KEY_NAME + " text,"
                + KEY_VALUE + " real,"
                + KEY_CURRENCY + " text,"
                + KEY_TYPE + " text,"
                + KEY_DATE + " text,"
                + KEY_COMMENT + " text"
                + ");");

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " +TABLE_ACCOUNTS_DATA);

        onCreate(db);
    }

    public boolean insertLastAccountsData(String name,String value, String currency) {
        Log.d(LOG_TAG, "-------insert in Account ----");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_ID, maxAccountsId + 1);
        contentValues.put(KEY_NAME, name);
        contentValues.put(KEY_VALUE, value);
        contentValues.put(KEY_CURRENCY, currency);
        long result = db.insert(TABLE_ACCOUNTS_DATA,null ,contentValues);
        if(result == -1) {
            Log.d(LOG_TAG, "result is -1");
            return false; }
        else
            maxAccountsId++;
        Log.d(LOG_TAG, "maxAccountid is " + maxAccountsId);
        return true;
    }

    public boolean insertLastTransactionData (String position, String name, String value, String date, String comment, String currency, String type){
        ContentValues cv = new ContentValues();

        SQLiteDatabase db = this.getWritableDatabase();
        Log.d(LOG_TAG, "--- Insert in transition table: ---");
        // подготовим данные для вставки в виде пар: наименование столбца - значение

        cv.put(DBHelper.KEY_ID, maxTransactionsId + 1);
        cv.put(DBHelper.KEY_NAME, name);
        cv.put(DBHelper.KEY_POSITION, Integer.parseInt(position));

        Log.d(LOG_TAG, "position " + position);


        cv.put(DBHelper.KEY_VALUE, value);
        cv.put(DBHelper.KEY_CURRENCY, currency);
        cv.put(DBHelper.KEY_DATE, date);
        cv.put(DBHelper.KEY_COMMENT, comment);
        cv.put(DBHelper.KEY_TYPE, type);
        Log.d(LOG_TAG, "name " + name + "value " + value + "currency " +currency);

        // вставляем запись и получаем ее ID
        long result = db.insert(DBHelper.TABLE_TRANSACTIONS_DATA, null, cv);
        if(result == -1) {
            Log.d(LOG_TAG, "result is -1");
            return false; }
        else
            maxTransactionsId++;
        Log.d(LOG_TAG, "maxTransid is " + maxTransactionsId);
        return true;
    }


    public Cursor getAccountsALLData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from "+TABLE_ACCOUNTS_DATA,null);

    }

    public AccountData getAccountIdRow(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = "id = ?";
        String[] selectionArgs = new String[] {id};
        Cursor c = db.query(DBHelper.TABLE_ACCOUNTS_DATA, null, selection, selectionArgs, null, null,
                null);

        AccountData accountData;
        if (c.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
            int idColIndex = c.getColumnIndex(DBHelper.KEY_ID);
            int nameColIndex = c.getColumnIndex(DBHelper.KEY_NAME);
            int valueColIndex = c.getColumnIndex(DBHelper.KEY_VALUE);
            int currencyColIndex = c.getColumnIndex(DBHelper.KEY_CURRENCY);

                // получаем значения по номерам столбцов и пишем все в лог
                String name = c.getString(nameColIndex);
                String value = c.getString(valueColIndex);
                String currency = c.getString(currencyColIndex);
                Log.d(LOG_TAG,
                        "ID = " + c.getInt(idColIndex) +
                                ", name = " + name +
                                ", value = " + value +
                                ", currency = " + currency);
                accountData = new AccountData(name, value, currency);

                // переход на следующую строку
                // а если следующей нет (текущая - последняя), то false - выходим из цикла
        } else {
            Log.d(LOG_TAG, "0 rows");
            return null;
        }
        c.close();

        return accountData;
    }

    public Transaction getTransactionIdRow(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = "id = ?";
        String[] selectionArgs = new String[] {id};
        Cursor c = db.query(DBHelper.TABLE_TRANSACTIONS_DATA, null, selection, selectionArgs, null, null,
                null);

        Transaction transaction;
        if (c.moveToFirst()) {

            int idColIndex = c.getColumnIndex((DBHelper.KEY_ID));
            int nameColIndex = c.getColumnIndex(DBHelper.KEY_NAME);
            int valueColIndex = c.getColumnIndex(DBHelper.KEY_VALUE);
            int currencyColIndex = c.getColumnIndex(DBHelper.KEY_CURRENCY);
            int typeColIndex = c.getColumnIndex(DBHelper.KEY_TYPE);
            int dateColIndex = c.getColumnIndex(DBHelper.KEY_DATE);
            int commentColIndex = c.getColumnIndex(DBHelper.KEY_COMMENT);

            String name = c.getString(nameColIndex);
            String value = c.getString(valueColIndex);
            String currency = c.getString(currencyColIndex);
            String type = c.getString(typeColIndex);
            String date = c.getString(dateColIndex);
            String comment = c.getString(commentColIndex);

            Log.d(LOG_TAG,
                    "id = " + c.getInt(idColIndex) +
                            ", name = " + name +
                            ", value = " + value.toString() +
                            ", currency = " + currency);
            transaction = new Transaction(type, name, date, value, currency, comment);

        } else {
            Log.d(LOG_TAG, "0 rows");
            return null;
        }
        c.close();

        return transaction;
    }

    public boolean updateAccountData(int id, String name,String value, String currency) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //deleteAccountData(id)
        contentValues.put(KEY_ID, id);
        contentValues.put(KEY_NAME, name);
        contentValues.put(KEY_VALUE, value);
        contentValues.put(KEY_CURRENCY, currency);
        long result = db.insert(TABLE_ACCOUNTS_DATA,null ,contentValues);
        if(result == -1)
            return false;
        else
            maxAccountsId++;
        return true;
    }

    public void deleteAccountData (String id) {
        Log.d(LOG_TAG, "-----delete---- id " + id);
        SQLiteDatabase db = this.getWritableDatabase();
        List<AccountData> accountDataList = new ArrayList<>();
        int number = Integer.parseInt(id);

        if (number < 0 || number > maxAccountsId) {
            Log.d(LOG_TAG, "wrond id maxid is " + maxAccountsId);
            return;
        }

        deleteTransactionsOfAccount(id);

        db.delete(TABLE_ACCOUNTS_DATA, "ID = ?",new String[] {id});
        maxAccountsId--;
        Log.d(LOG_TAG, "maxid is " + maxAccountsId);
        int max = maxAccountsId + 2;
        for (int i = number + 1; i < max; i++) {
            accountDataList.add(getAccountIdRow(String.valueOf(i)));
            Log.d(LOG_TAG, "delete this id " + String.valueOf(i));

            db.delete(TABLE_ACCOUNTS_DATA, "ID = ?",new String[] {String.valueOf(i)});
            maxAccountsId--;
            Log.d(LOG_TAG, "maxid is " + maxAccountsId);
        }

        for (AccountData ad: accountDataList) {
            insertLastAccountsData(ad.getName(), String.valueOf(ad.getValue()), ad.getCurrency());
        }

    }

    public void deleteTransactionsOfAccount(String position) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = "position = ?";
        String[] selectionArgs = new String[]{position};
        Log.d(LOG_TAG, "-------------deleting transactions of account--------------- " + position);

        Cursor c = db.query(TABLE_TRANSACTIONS_DATA, null, selection, selectionArgs, null, null, null);
        int idColIndex = c.getColumnIndex(DBHelper.KEY_ID);

        if (c.moveToFirst()) {
            do {
                Log.d(LOG_TAG, "found position, transaction id = " + c.getString(idColIndex));

                deleteTransactionData(c.getString(idColIndex));

            } while (c.moveToNext());
        }
    }

    public void deleteTransactionData(String id) {
        Log.d(LOG_TAG, "-----delete transaction---- id " + id);


        SQLiteDatabase db = this.getWritableDatabase();
        List<Transaction> transactions = new ArrayList<>();
        int number = Integer.parseInt(id);

        if (number < 0 || number > maxTransactionsId) {
            Log.d(LOG_TAG, "wrond id maxid is " + maxTransactionsId);
            return;
        }

        Log.d(LOG_TAG, "number " + number);
        ArrayList<String> positions = getPositionsArray(number);

        db.delete(TABLE_TRANSACTIONS_DATA, "ID = ?",new String[] {id});
        maxTransactionsId--;
        Log.d(LOG_TAG, "maxid is " + maxTransactionsId);
        int max = maxTransactionsId + 2;
        for (int i = number + 1; i < max; i++) {
            transactions.add(getTransactionIdRow(String.valueOf(i)));
            Log.d(LOG_TAG, "delete this id " + String.valueOf(i));

            db.delete(TABLE_TRANSACTIONS_DATA, "ID = ?",new String[] {String.valueOf(i)});
            maxTransactionsId--;
            Log.d(LOG_TAG, "maxid is " + maxTransactionsId);
        }

        for (int i = 0; i < transactions.size(); i++) {
            Transaction tr = transactions.get(i);
            Log.d(LOG_TAG, "transactions.get " + i);
            Log.d(LOG_TAG, "transactions.size " + transactions.size());
            Log.d(LOG_TAG, "positions.size " + positions.size());
            insertLastTransactionData(positions.get(i), tr.getName(), String.valueOf(tr.getValue()), tr.getDate(), tr.getComment(),
                    tr.getCurrency(), tr.getType());

        }

    }

    public ArrayList<String> getPositionsArray(int id) {
        Log.d(LOG_TAG, "------------getpositArray-------" + id);
        ArrayList<String> positions = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = "id > ?";
        String[] selectionArgs = new String[] {String.valueOf(id)};
        String[] columns = new String[]{KEY_POSITION};
        Cursor c = db.query(DBHelper.TABLE_TRANSACTIONS_DATA, null, selection, selectionArgs, null, null,
                null);

        Log.d(LOG_TAG, "insert " + maxTransactionsId);
        if (c.moveToFirst()) {
            int posColIndex = c.getColumnIndex((DBHelper.KEY_POSITION));
            Log.d(LOG_TAG, "posColIndex " + posColIndex);
            int idColIndex = c.getColumnIndex((DBHelper.KEY_ID));
       //     Log.d(LOG_TAG, "id!!!!!! " + c.getString(idColIndex));
            String pos = c.getString(posColIndex);
            Log.d(LOG_TAG, "posit = " + pos);
            positions.add(pos);

        } else {
            Log.d(LOG_TAG, "0 rows");
            return null;
        }
        c.close();

        Log.d(LOG_TAG, "return positions ");
        return positions;
    }

    public ArrayList<Transaction> getTransactionsListOfAccount(int position) {
        Cursor cursor;
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        ArrayList<Transaction> transactions = new ArrayList<>();

        Log.d(LOG_TAG, "---INNER JOIN with query trans---");
        Log.d(LOG_TAG, "position " + position);
        int pos = position + 1;
        String sqlQuery = "select  * "
                + "from transactions "
                + "where position = ?";
        cursor = sqLiteDatabase.rawQuery(sqlQuery, new String[] {String.valueOf(pos)});

        int nameColIndex = cursor.getColumnIndex(DBHelper.KEY_NAME);
        int valueColIndex = cursor.getColumnIndex(DBHelper.KEY_VALUE);
        int currencyColIndex = cursor.getColumnIndex(DBHelper.KEY_CURRENCY);
        int typeColIndex = cursor.getColumnIndex(DBHelper.KEY_TYPE);
        int dateColIndex = cursor.getColumnIndex(DBHelper.KEY_DATE);
        int commentColIndex = cursor.getColumnIndex(DBHelper.KEY_COMMENT);


        Log.d(LOG_TAG, "hmmmm");
        if (cursor.moveToFirst()) {
            Log.d(LOG_TAG, "hmmmm1");
            String str;
            do {
                Log.d(LOG_TAG, "hmmmm2");
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
                transactions.add(new Transaction(type, name, date, value, currency, comment));

            } while (cursor.moveToNext());
        }

        cursor.close();
        Log.d(LOG_TAG, "--- ---");

        return transactions;
    }
}
