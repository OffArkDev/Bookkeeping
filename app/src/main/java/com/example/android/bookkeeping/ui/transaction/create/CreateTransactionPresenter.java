package com.example.android.bookkeeping.ui.transaction.create;

import android.content.Intent;
import android.widget.ArrayAdapter;

import com.example.android.bookkeeping.Constants;
import com.example.android.bookkeeping.R;
import com.example.android.bookkeeping.currency.CurrencyRatesData;
import com.example.android.bookkeeping.currency.UrlParser;
import com.example.android.bookkeeping.ui.mvp.BasePresenter;
import com.example.android.bookkeeping.utils.DateUtil;

import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class CreateTransactionPresenter<V extends CreateTransactionMvpView> extends BasePresenter<V> implements CreateTransactionMvpPresenter<V> {

    private String[] ratesNames;
    private String str1[] = {"in", "out"};

    private ArrayList<CurrencyRatesData> listHistoryCurrencies = new ArrayList<>();


    @Inject
    public CompositeDisposable compositeDisposable;

    @Inject
    public UrlParser urlParser;

    @Inject
    public CreateTransactionPresenter() {
    }


    @Override
    public void getRatesFromIntent(Intent intent) {
        ratesNames = intent.getStringArrayExtra("ratesNames");
    }

    @Override
    public void btnCurrencyClick() {
        getMvpView().showDialog();
    }

    @Override
    public String[] getRatesNames() {
        return ratesNames;
    }

    @Override
    public ArrayAdapter<String> initAccountsAdapter() {
        ArrayAdapter<String> adapterSp = new ArrayAdapter<>(getMvpView().getContext(), android.R.layout.simple_spinner_item, str1);
        adapterSp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapterSp;
    }

    @Override
    public void btnDoneClick() {
        getMvpView().returnActivityResult();
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
}
