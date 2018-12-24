package com.example.android.bookkeeping.ui.dialogs;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;

import com.example.android.bookkeeping.R;

public class CurrenciesDialog extends DialogFragment {

   private View rootView;
   private GridView gridView;
   private ArrayAdapter<String> adapter;
   private Button btnCancel;

   private String[] currencies;

   private DialogCommunicator dialogCommunicator;

    @Override
    public void setArguments(Bundle args) {
        currencies = args.getStringArray("currencies");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_currencies, container, false);
        gridView = rootView.findViewById(R.id.grid_layout);
        btnCancel = rootView.findViewById(R.id.btn_cancel);
        adapter = new ArrayAdapter<>(getActivity(), R.layout.dialog_item, R.id.txt_currency, currencies);
        gridView.setAdapter(adapter);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String currency = currencies[(int) id];
                dialogCommunicator.sendRequest(1, currency);
                getDialog().dismiss();
            }
        });

        return rootView;
    }

    public void setDialogCommunicator(DialogCommunicator dialogCommunicator) {
        this.dialogCommunicator = dialogCommunicator;
    }
}
