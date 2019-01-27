package com.example.android.bookkeeping.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.android.bookkeeping.R;
import com.example.android.bookkeeping.data.entities.AccountSaver;

import java.util.List;


public class AccountsListAdapter extends BaseAdapter {


    private LayoutInflater LInflater;
    private List<AccountSaver> list;


    public AccountsListAdapter(Context context, List<AccountSaver> data){

        list = data;
        LInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        if (list == null) {
            return 0;
        } else return list.size();
    }

    @Override
    public AccountSaver getItem(int position) {
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
            v.setTag(holder);
        }

        holder = (ViewHolder) v.getTag();
        AccountSaver accountData = getData(position);

        holder.name.setText(String.format("%s", accountData.getName()));
        holder.value.setText(String.format("%s", accountData.getValue()));
        holder.valueRUB.setText(String.format("%s",accountData.getValueRUB()));
        holder.currency.setText(String.format("%s", accountData.getCurrency()));

        return v;
    }

    public void updateList(List<AccountSaver> newList) {
        list.clear();
        list.addAll(newList);
        this.notifyDataSetChanged();
    }

    private AccountSaver getData(int position){
        return (getItem(position));
    }


    private static class ViewHolder {
        private TextView name;
        private TextView value;
        private TextView valueRUB;
        private TextView currency;

    }
}
