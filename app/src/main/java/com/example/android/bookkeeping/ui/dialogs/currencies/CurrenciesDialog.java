package com.example.android.bookkeeping.ui.dialogs.currencies;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
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

public class CurrenciesDialog extends BaseDialog implements  CurrenciesDialogMvpView{

    @Inject
    public CurrenciesDialogPresenter<CurrenciesDialogMvpView> presenter;

    DialogCommunicator dialogCommunicator;

    public ArrayAdapter<String> adapter;


    private View rootView;
    private GridView gridView;
    private Button btnCancel;

    private Bundle args;


    public static CurrenciesDialog newInstance() {
        CurrenciesDialog fragment = new CurrenciesDialog();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentComponent().inject(this);
    }

    @Override
    public void setArguments(Bundle args) {
        this.args = args;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_currencies, container, false);

        findViews();

        setOnClickListeners();

        passArgsToPresenter();

        presenter.onAttach(this);

        initAdapter();


        return rootView;
    }


    public void findViews() {
        gridView = rootView.findViewById(R.id.grid_layout);
        btnCancel = rootView.findViewById(R.id.btn_cancel);
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
    public void setOnClickListeners() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.btnCancelClick();
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                presenter.itemGridViewClick((int) id);
            }
        });
    }

    public void initAdapter() {
        adapter = presenter.initAdapter();
        gridView.setAdapter(adapter);
    }

    public void passArgsToPresenter() {
        presenter.setArguments(args);
        args.clear();
    }

    public void setDialogCommunicator(DialogCommunicator dialogCommunicator) {
        this.dialogCommunicator = dialogCommunicator;
    }

    @Override
    public void returnResult(String chosenCurrency) {
        dialogCommunicator.sendRequest(1, chosenCurrency);
        dismissDialog();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.onDetach();
    }
}
