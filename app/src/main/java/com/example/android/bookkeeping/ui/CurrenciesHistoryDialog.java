package com.example.android.bookkeeping.ui;

import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;

import com.example.android.bookkeeping.R;

import java.util.ArrayList;
import java.util.List;

public class CurrenciesHistoryDialog extends DialogFragment {

    private View rootView;
    private GridView gridView;
    private ArrayAdapter<String> adapter;
    private Button btnCancel;
    private Button btnDone;

    private String[] currencies;
    private List<String> chosenCurrencies;

    private DialogCommunicator dialogCommunicator;

    private final int CHOICE_TAG = 0;

    @Override
    public void setArguments(Bundle args) {
        currencies = args.getStringArray("currencies");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_history_currencies, container, false);
        gridView = rootView.findViewById(R.id.grid_layout);
        btnCancel = rootView.findViewById(R.id.btn_cancel);
        btnDone = rootView.findViewById(R.id.btn_done);
        chosenCurrencies = new ArrayList<>();
        adapter = new ArrayAdapter<>(getActivity(), R.layout.dialog_item, R.id.txt_currency, currencies);
        gridView.setAdapter(adapter);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCommunicator.sendRequest(2, chosenCurrencies.toArray(new String[0]));
                getDialog().dismiss();
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                boolean chosen = false;
                if (view.getTag() != null) {
                    chosen = (boolean) view.getTag();
                }
                if (chosen) {
                    String currency = currencies[(int) id];
                    chosenCurrencies.remove(currency);
                    view.setTag(false);
                    view.setBackgroundColor(Color.TRANSPARENT);
                } else {
                    String currency = currencies[(int) id];
                    chosenCurrencies.add(currency);
                    view.setTag(true);
                    view.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.paint_button));
                }
            }
        });
        return rootView;
    }

    public void setDialogCommunicator(DialogCommunicator dialogCommunicator) {
        this.dialogCommunicator = dialogCommunicator;
    }
}
