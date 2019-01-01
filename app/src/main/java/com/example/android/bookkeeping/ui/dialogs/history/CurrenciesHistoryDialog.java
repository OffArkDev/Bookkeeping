package com.example.android.bookkeeping.ui.dialogs.history;

import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;

import com.example.android.bookkeeping.MyApplication;
import com.example.android.bookkeeping.R;
import com.example.android.bookkeeping.di.components.FragmentComponent;
import com.example.android.bookkeeping.di.modules.ActivityModule;
import com.example.android.bookkeeping.ui.dialogs.DialogCommunicator;
import com.example.android.bookkeeping.ui.mvp.BaseDialog;

import javax.inject.Inject;

public class CurrenciesHistoryDialog extends BaseDialog implements CurrenciesHistoryMvpView{

    private View rootView;
    private GridView gridView;
    private ArrayAdapter<String> adapter;
    private Button btnCancel;
    private Button btnDone;


    private DialogCommunicator dialogCommunicator;

    private View prev_view = null;

    private Bundle args;

    @Inject
    CurrenciesHistoryMvpPresenter<CurrenciesHistoryMvpView> presenter;

    @Override
    public void setArguments(Bundle args) {
        this.args = args;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_history_currencies, container, false);

        findViews();

        setOnClickListeners();

        passArgsToPresenter();

        presenter.onAttach(this);

        initAdapter();

        return rootView;
    }


    public FragmentComponent getFragmentComponent() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            return ((MyApplication) getActivity().getApplication())
                    .getApplicationComponent()
                    .newFragmentComponent(new ActivityModule(activity));
        } else return null;
    }

    public void setDialogCommunicator(DialogCommunicator dialogCommunicator) {
        this.dialogCommunicator = dialogCommunicator;
    }

    private void passArgsToPresenter() {
        presenter.setArguments(args);
        args.clear();
    }

    private void initAdapter() {
        adapter = presenter.initAdapter(getActivity());
        gridView.setAdapter(adapter);
    }

    @Override
    public void findViews() {
        gridView = rootView.findViewById(R.id.grid_layout);
        btnCancel = rootView.findViewById(R.id.btn_cancel);
        btnDone = rootView.findViewById(R.id.btn_done);
    }

    @Override
    public void setOnClickListeners() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.btnCancelClick();
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.btnDoneClick();

            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                presenter.itemGridViewClick((int)id, view);
            }
        });
    }

    @Override
    public void changeCurrencyViewColor(View view) {
        if (prev_view != null) {
            prev_view.setBackgroundColor(Color.TRANSPARENT);
        }
        prev_view = view;
        view.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.paint_button));
    }

    @Override
    public void returnResult(String chosenCurrency) {
        dialogCommunicator.sendRequest(2, chosenCurrency);
        getDialog().dismiss();
    }

    @Override
    public void dismissDialog() {
        getDialog().dismiss();
    }

    @Override
    public void onError(int resId) {

    }

    @Override
    public void onError(String message) {

    }

    @Override
    public void showMessage(String message) {

    }

    @Override
    public void showMessage(int resId) {

    }
}
