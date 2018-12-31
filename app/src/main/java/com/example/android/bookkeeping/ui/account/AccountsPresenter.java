package com.example.android.bookkeeping.ui.account;

import android.util.Log;

import com.example.android.bookkeeping.currency.CurrencyRatesData;
import com.example.android.bookkeeping.currency.UrlParser;
import com.example.android.bookkeeping.data.model.AccountSaver;
import com.example.android.bookkeeping.repository.AccountsRepository;
import com.example.android.bookkeeping.ui.adapters.AccountsListAdapter;
import com.example.android.bookkeeping.ui.mvp.BasePresenter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class AccountsPresenter <V extends AccountsMvpView> extends BasePresenter<V> implements AccountsMvpPresenter<V> {

    private static final String TAG = "BasePresenter";

    private CurrencyRatesData currencyRatesData;

    @Inject
    public UrlParser urlParser;

    @Inject
    public AccountsRepository accountsRepository;

    @Inject
    public CompositeDisposable compositeDisposable;

    private List<AccountSaver> listAccounts = new ArrayList<>();


    @Inject
    public AccountsPresenter() {
    }

    @Override
    public void onAttach(V mvpView) {
        super.onAttach(mvpView);

        getMvpView().showLoading();

        parseUrl();

        getAccountsFromDatabase();
    }


    @Override
    public void onDetach() {
        compositeDisposable.dispose();
        super.onDetach();
    }

    public void parseUrl() {
        Observable.create(urlParser)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CurrencyRatesData>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(CurrencyRatesData data) {
                        currencyRatesData = data;
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        getMvpView().hideLoading();
                    }
                });
    }

    @Override
    public void btnCreateAccountClick() {
        getMvpView().openCreateAccountActivity(currencyRatesData);
    }

    @Override
    public void btnCloudClick() {
        getMvpView().openCloudActivity();
    }


    @Override
    public void btnChartClick() {
        getMvpView().openChartActivity(currencyRatesData);
    }

    @Override
    public void itemAccountsClick(int accountId, boolean isDeleteClicked) {
        if (isDeleteClicked) {
            deleteAccount(accountId);
        } else {
            getMvpView().openTransactionsActivity(accountId, listAccounts, currencyRatesData);
        }
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
                                   getMvpView().updateListView();
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
    public void getAccountsFromDatabase() {
        compositeDisposable.add(accountsRepository.getAll()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<AccountSaver>>() {
                    @Override
                    public void accept(List<AccountSaver> accountSavers) {
                        listAccounts = accountSavers;
                        getMvpView().updateListView();
                    }
                }));
    }

    @Override
    public void createAccount(String name, String value, String currency) {

        String valueRUB = convertValueRub(value, currency);

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
                        getMvpView().updateListView();
                    }
                }));
    }

    public String convertValueRub(String value, String currency) {
        String valueRUB = "";
        if (currency.equals("RUB")) {
            valueRUB = value;
        } else if (!value.equals("") && !currency.equals("")) {
           valueRUB = currencyRatesData.convertCurrency(new BigDecimal(value), currency, "RUB").toString();
        }
        return valueRUB;
    }

    public List<AccountSaver> getListAccounts() {
        return listAccounts;
    }

    @Override
    public AccountsListAdapter initAdapter() {
        return new AccountsListAdapter(getMvpView().getContext(), listAccounts);
    }
}
