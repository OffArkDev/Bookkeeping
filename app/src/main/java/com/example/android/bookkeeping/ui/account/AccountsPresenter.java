package com.example.android.bookkeeping.ui.account;

import android.util.Log;

import com.example.android.bookkeeping.currency.CurrenciesRatesData;
import com.example.android.bookkeeping.currency.UrlParser;
import com.example.android.bookkeeping.data.model.AccountSaver;
import com.example.android.bookkeeping.repository.AccountsRepository;
import com.example.android.bookkeeping.ui.adapters.AccountsListAdapter;
import com.example.android.bookkeeping.ui.mvp.BasePresenter;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
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

    private List<AccountSaver> listAccounts = new ArrayList<>();

    private boolean isFlowLoaded = false;

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
                .subscribe(new Observer<CurrenciesRatesData>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(CurrenciesRatesData data) {
                        currenciesRatesData = data;
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        if (isFlowLoaded) {
                            updateDailyCurrencies();
                        } else {
                            isFlowLoaded = true;
                        }

                    }
                });



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
                        if (isFlowLoaded) {
                            updateDailyCurrencies();
                        } else {
                            isFlowLoaded = true;
                        }

                    }
                }));
    }


    private  void updateDailyCurrencies() {
        for (AccountSaver account : listAccounts) {
            String updatedValueRub = currenciesRatesData.convertCurrency(account.getValue(), account.getCurrency(), "RUB");
            account.setValueRUB(updatedValueRub);
        }
        updateAccountsData();
    }

    private void updateAccountsData() {
//        compositeDisposable.add(accountsRepository.deleteAll()
//                .subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.io())
//                .andThen(accountsRepository.insertList(listAccounts))
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<long[]>() {
//                    @Override
//                    public void accept(long[] aLong) {
//                        getMvpView().hideLoading();
//                        getMvpView().setOnClickListeners();
//                        getMvpView().updateListView(listAccounts);
//                    }
//                }));

        List<Long> listId = new ArrayList<>();
        for (AccountSaver account : listAccounts) {
            listId.add(account.getId());
        }
        compositeDisposable.clear();
        compositeDisposable.add(accountsRepository.updateValueRub(listId.get(0), "10")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() {
                        Log.e(TAG, "update success ");
                        getMvpView().hideLoading();
                        getMvpView().setOnClickListeners();
                        getMvpView().updateListView(listAccounts);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
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
        if (currency.equals("RUB")) {
            valueRUB = value;
        } else if (!value.equals("") && !currency.equals("")) {
            valueRUB = currenciesRatesData.convertCurrency(value, currency, "RUB");
        }
        return valueRUB;
    }

    public List<AccountSaver> getListAccounts() {
        return listAccounts;
    }

    @Override
    public AccountsListAdapter initAccountsAdapter() {
        return new AccountsListAdapter(getMvpView().getContext(), listAccounts);
    }
}
