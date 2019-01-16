package com.example.android.bookkeeping.ui.account;

import android.util.Log;

import com.example.android.bookkeeping.Constants;
import com.example.android.bookkeeping.currency.CurrenciesRatesData;
import com.example.android.bookkeeping.currency.UrlParser;
import com.example.android.bookkeeping.data.model.AccountSaver;
import com.example.android.bookkeeping.repository.AccountsRepository;
import com.example.android.bookkeeping.ui.adapters.AccountsListAdapter;
import com.example.android.bookkeeping.ui.mvp.BasePresenter;

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
import io.reactivex.functions.Function3;
import io.reactivex.schedulers.Schedulers;

public class AccountsPresenter <V extends AccountsMvpView> extends BasePresenter<V> implements AccountsMvpPresenter<V> {

    private static final String TAG = "BasePresenter";

    private CurrenciesRatesData currenciesRatesData;

    @Inject
    public UrlParser urlParser;

    @Inject
    public AccountsRepository accountsRepository;

    @Inject
    public CompositeDisposable compositeDisposable;

    @Inject
    public List<AccountSaver> listAccounts;

    @Inject
    public AccountsPresenter() {
    }

    @Override
    public void onAttach(V mvpView) {
        super.onAttach(mvpView);

        getMvpView().showLoading();

        zipFlows();
    }

    public void zipFlows() {
        getMvpView().showLoading();
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

    @Override
    public void getAccountsFromDatabase() {
        compositeDisposable.add(accountsRepository.getAll()
                .subscribeOn(Schedulers.io())
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
                        getMvpView().hideLoading();
                        getMvpView().setOnClickListeners();
                        getMvpView().updateListView(listAccounts);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        Log.e(TAG, "delete account fail " + throwable.getMessage());
                    }
                }));
    }


    @Override
    public void btnCreateAccountClick() {
        getMvpView().openCreateAccountActivity(currenciesRatesData.getCurrenciesList());
    }

    @Override
    public void btnCloudClick() {
        getMvpView().openCloudActivity();
    }


    @Override
    public void btnChartClick() {
        getMvpView().openChartActivity(currenciesRatesData);
    }

    @Override
    public void itemAccountsClick(int accountId, boolean isDeleteClicked) {
        if (isDeleteClicked) {
            deleteAccount(accountId);
        } else {
            getMvpView().openTransactionsActivity(accountId, listAccounts, currenciesRatesData.getCurrenciesList());
        }
    }

    @Override
    public void btnDeleteAccount() {
        getMvpView().changeDeleteButtonState();
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
                                   getMvpView().updateListView(listAccounts);
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) {
                                   Log.e(TAG, "delete account fail " + throwable.getMessage());
                               }
                           }
                ));

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
                        getMvpView().updateListView(listAccounts);
                    }
                }));
    }

    public String convertValueRub(String value, String currency) {
        String valueRUB = "";
        if (currency.equals(Constants.NAME_CURRENCY_RUB)) {
            valueRUB = value;
        } else if (!value.equals("") && !currency.equals("")) {
            valueRUB = currenciesRatesData.convertCurrency(value, currency, Constants.NAME_CURRENCY_RUB);
        }
        return valueRUB;
    }

    public List<AccountSaver> getListAccounts() {
        return listAccounts;
    }

}
