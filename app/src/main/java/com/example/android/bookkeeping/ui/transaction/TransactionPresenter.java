package com.example.android.bookkeeping.ui.transaction;

import android.content.Intent;
import android.util.Log;

import com.example.android.bookkeeping.currency.CurrenciesRatesData;
import com.example.android.bookkeeping.data.model.TransactionSaver;
import com.example.android.bookkeeping.repository.TransactionsRepository;
import com.example.android.bookkeeping.ui.adapters.TransactionsListAdapter;
import com.example.android.bookkeeping.ui.mvp.BasePresenter;

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

public class TransactionPresenter<V extends TransactionMvpView> extends BasePresenter<V> implements TransactionMvpPresenter<V> {

    private static final String TAG = "TransactionPresenter";

    private List<TransactionSaver> listTransactions = new ArrayList<>();
    private long accountId;
    private String[] currenciesNames;

    @Inject
    public CompositeDisposable compositeDisposable;

    @Inject
    public TransactionsRepository transactionsRepository;

    @Inject
    public TransactionPresenter() {
    }

    @Override
    public void onAttach(V mvpView) {
        super.onAttach(mvpView);

        getTransactionsFromDatabase();

    }

    @Override
    public void onDetach() {
        compositeDisposable.dispose();
        super.onDetach();
    }

    @Override
    public void btnDeleteClick() {
        getMvpView().changeDeleteButtonState();
    }


    @Override
    public void btnCreateTransactionClick() {
        getMvpView().openCreateTransactionActivity(currenciesNames);
    }

    @Override
    public void itemTransactionsClick(int accountId, boolean isDeleteClicked) {
        if (isDeleteClicked) {
            deleteTransaction(accountId);
        }
    }

    public Disposable getTransactionsFromDatabase() {
        return transactionsRepository.getTransactionsOfAccount(accountId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<TransactionSaver>>() {
                    @Override
                    public void accept(List<TransactionSaver> transactionSavers) {
                        listTransactions = transactionSavers;
                        getMvpView().updateListView(listTransactions);
                    }
                });
    }

    @Override
    public TransactionsListAdapter initTransactionsAdapter() {
        return new TransactionsListAdapter(getMvpView().getContext(), listTransactions);
    }


    @Override
    public void getDataFromIntent(Intent intent) {
        accountId = intent.getLongExtra("accountId", 0L);
        currenciesNames = intent.getStringArrayExtra("currenciesNames");
    }

    public void deleteTransaction(final int id) {
        compositeDisposable.add(transactionsRepository.delete(listTransactions.get(id))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() {
                        Log.i(TAG, "delete transaction complete");
                        listTransactions.remove(id);
                        getMvpView().updateListView(listTransactions);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        Log.i(TAG, "delete transaction fail " + throwable.getMessage());
                    }
                })
        );
    }

    @Override
    public void createTransaction(String name, String value, String currency, String date, String type, String comment, CurrenciesRatesData currenciesRatesData) {
        String valueRUB = convertValueRub(currency, value, currenciesRatesData);

        final TransactionSaver newTransactionSaver = new TransactionSaver(accountId, type, name, date, value, valueRUB, currency, comment);
        compositeDisposable.add(transactionsRepository.insert(newTransactionSaver)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) {
                        Log.i(TAG, "insert success");
                        newTransactionSaver.setId(aLong);
                        listTransactions.add(newTransactionSaver);
                        getMvpView().updateListView(listTransactions);
                    }
                }));
    }

    private String convertValueRub(String currency, String value, CurrenciesRatesData currenciesRatesData) {
        String valueRUB = "";
        if (currency.equals("RUB")) {
            valueRUB = value;
        }
        if (!value.equals("") && !currency.equals("")) {
            valueRUB = currenciesRatesData.convertCurrency(new BigDecimal(value), currency, "RUB").toString();
        }
        return valueRUB;
    }



}
