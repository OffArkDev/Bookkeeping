package com.example.android.bookkeeping.ui.dialogs.date;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.example.android.bookkeeping.MyApplication;
import com.example.android.bookkeeping.di.components.FragmentComponent;
import com.example.android.bookkeeping.di.modules.ActivityModule;
import com.example.android.bookkeeping.ui.dialogs.DialogCommunicator;
import com.example.android.bookkeeping.ui.mvp.BaseDialog;

import java.util.Calendar;

import javax.inject.Inject;

public class DateDialog extends BaseDialog implements DatePickerDialog.OnDateSetListener, DateDialogMvpView {

    private DialogCommunicator dialogCommunicator;

    @Inject
    DateDialogMvpPresenter<DateDialogMvpView> presenter;

    public static DateDialog newInstance() {
        DateDialog fragment = new DateDialog();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentComponent().inject(this);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        presenter.onAttach(this);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public FragmentComponent getFragmentComponent() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            return ((MyApplication) getActivity().getApplication())
                    .getApplicationComponent()
                    .newFragmentComponent(new ActivityModule(activity));
        } else return null;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        presenter.setFormattedDate(day, month, year);
    }

    @Override
    public void returnResult(String result) {
        dialogCommunicator.sendRequest(3, result);

    }

    public void setDialogCommunicator(DialogCommunicator dialogCommunicator) {
        this.dialogCommunicator = dialogCommunicator;
    }

    @Override
    public void dismissDialog() {
        getDialog().dismiss();
    }

    @Override
    public void findViews() {

    }

    @Override
    public void setOnClickListeners() {

    }
}
