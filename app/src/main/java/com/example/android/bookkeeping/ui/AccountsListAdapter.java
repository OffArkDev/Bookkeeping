package com.example.android.bookkeeping.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.android.bookkeeping.data.AccountData;
import com.example.android.bookkeeping.R;

import java.util.ArrayList;


public class AccountsListAdapter extends BaseAdapter {


    private LayoutInflater LInflater;
    private ArrayList<AccountData> list;


    public AccountsListAdapter(Context context, ArrayList<AccountData> data){

        list = data;
        LInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }



    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public AccountData getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        View v = convertView;

        if ( v == null){
            holder = new ViewHolder();
            v = LInflater.inflate(R.layout.account_list_item, parent, false);
            holder.name =  v.findViewById(R.id.account_name);
            holder.value = v.findViewById(R.id.account_value);
            holder.valueRUB = v.findViewById(R.id.account_value_rub);
            holder.currency = v.findViewById(R.id.account_currency);
            holder.lastTransactions =  v.findViewById(R.id.account_transactions);
            v.setTag(holder);
        }

        holder = (ViewHolder) v.getTag();
        AccountData accountData = getData(position);

        holder.name.setText(String.format("name: %s", accountData.getName()));
        holder.value.setText(String.format("value: %s", accountData.getValue()));
        holder.valueRUB.setText(String.format("value in RUB: %s",accountData.getValueRUB()));
        holder.currency.setText(String.format("currency: %s", accountData.getCurrency()));
        holder.lastTransactions.setText(String.format("last transactions: %s", accountData.getLastTransaction()));

        return v;
    }

   private AccountData getData(int position){
        return (getItem(position));
    }


    private static class ViewHolder {
        private TextView name;
        private TextView value;
        private TextView valueRUB;
        private TextView lastTransactions;
        private TextView currency;

    }
}