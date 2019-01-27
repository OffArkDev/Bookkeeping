package com.example.android.bookkeeping.ui.transaction.create;

import android.content.Intent;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.example.android.bookkeeping.Constants;
import com.example.android.bookkeeping.R;
import com.example.android.bookkeeping.model.pojo.CurrenciesRatesData;
import com.example.android.bookkeeping.model.pojo.UrlParser;
import com.example.android.bookkeeping.ui.mvp.BasePresenter;
import com.example.android.bookkeeping.utils.DateUtil;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class CreateTransactionPresenter<V extends CreateTransactionMvpView> extends BasePresenter<V> implements CreateTransactionMvpPresenter<V> {

    private final static String TAG = "createTrPresenter";

    private String[] currenciesNames;
    private String str1[] = {"in", "out"};

    private ArrayList<CurrenciesRatesData> listHistoryCurrencies = new ArrayList<>();

    private boolean isCurrenciesLoaded = false;
    private boolean isBtnDoneClicked = false;

    @Inject
    public CompositeDisposable compositeDisposable;

    @Inject
    UrlParser urlParser;

    @Inject
    CreateTransactionPresenter() {
    }


    @Override
    public void onAttach(V mvpView) {
        super.onAttach(mvpView);

        loadCurrencies();
    }


    private void loadCurrencies() {
        Flowable.create(urlParser, BackpressureStrategy.DROP)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CurrenciesRatesData>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(CurrenciesRatesData currenciesRatesData) {
                        listHistoryCurrencies.add(currenciesRatesData);

                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.i(TAG, "onError: " + t.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        if (isBtnDoneClicked) {
                            getMvpView().returnActivityResult();
                        }
                        isCurrenciesLoaded = true;
                    }
                });
    }

    @Override
    public void getDataFromIntent(Intent intent) {
        currenciesNames = intent.getStringArrayExtra("currenciesNames");
    }

    @Override
    public void btnCurrencyClick() {
        getMvpView().showDialog();
    }

    @Override
    public String[] getCurrenciesNames() {
        return currenciesNames;
    }

    @Override
    public ArrayAdapter<String> initAccountsAdapter() {
        ArrayAdapter<String> adapterSp = new ArrayAdapter<>(getMvpView().getContext(), android.R.layout.simple_spinner_item, str1);
        adapterSp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapterSp;
    }

    @Override
    public void btnDoneClick() {
        isBtnDoneClicked = true;
        if (!isCurrenciesLoaded) {
            getMvpView().showLoading();
        } else {
            getMvpView().returnActivityResult();
        }
    }

    @Override
    public boolean checkInputDataFormat(String value, String sDate) {
        if (value.equals("")) {
            getMvpView().showMessage(R.string.write_value);
            return false;
        }
        Date dDate = DateUtil.stringToDate(sDate);
        if (dDate == null) {
            getMvpView().showMessage(R.string.wrong_date_format);
            return false;
        } else if (dDate.after(new Date())) {
            getMvpView().showMessage(R.string.later_date_error);
            return false;
        } else if (dDate.before(Constants.FIRST_CURRENCY_DATE)) {
            getMvpView().showMessage(R.string.early_date_error);
            return false;
        }
        return true;
    }

    @Override
    public CurrenciesRatesData getCurrenciesRatesData(String date) {
        String searcher = DateUtil.changeFormatToXml(date, Constants.DATE_MAIN_FORMAT);
        if (searcher == null) {
            getMvpView().showMessage(R.string.date_format_error);
            return null;
        }
        for (CurrenciesRatesData data : listHistoryCurrencies) {
            if (data.getTime().equals(searcher)) {
                return data;
            }
        }
        getMvpView().showMessage(R.string.currencies_rates_search_error);
        return null;
    }
}
