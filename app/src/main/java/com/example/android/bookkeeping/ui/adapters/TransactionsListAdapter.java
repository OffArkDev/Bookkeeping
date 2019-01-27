package com.example.android.bookkeeping.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.android.bookkeeping.R;
import com.example.android.bookkeeping.model.TransactionSaver;

import java.util.List;

public class TransactionsListAdapter extends BaseAdapter {
    private LayoutInflater LInflater;
    private List<TransactionSaver> list;


    public TransactionsListAdapter(Context context, List<TransactionSaver> data){

        list = data;
        LInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public TransactionSaver getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TransactionsListAdapter.ViewHolder holder;

        View v = convertView;

        if ( v == null){
            holder = new TransactionsListAdapter.ViewHolder();
            v = LInflater.inflate(R.layout.transaction_list_item, parent, false);
            holder.name =  v.findViewById(R.id.transaction_name);
            holder.value = v.findViewById(R.id.transaction_value);
            holder.valueRUB = v.findViewById(R.id.transaction_value_rub);
            holder.currency = v.findViewById(R.id.transaction_currency);
            holder.date =  v.findViewById(R.id.transaction_date);
            holder.type =  v.findViewById(R.id.transaction_type);
            holder.comment =  v.findViewById(R.id.transaction_comment);
            v.setTag(holder);
        }

        holder = (TransactionsListAdapter.ViewHolder) v.getTag();
        TransactionSaver transaction = getData(position);


        holder.name.setText(transaction.getName());
        holder.value.setText(transaction.getValue());
        holder.valueRUB.setText(transaction.getValueRUB());
        holder.currency.setText(transaction.getCurrency());
        holder.date.setText(transaction.getDate());
        holder.type.setText(transaction.getType());
        holder.comment.setText(transaction.getComment());
        return v;
    }

    private TransactionSaver getData(int position){
        return (getItem(position));
    }

    public void updateList(List<TransactionSaver> listTransactions) {
        list.clear();
        list.addAll(listTransactions);
        notifyDataSetChanged();
    }


    private static class ViewHolder {
        private TextView name;
        private TextView value;
        private TextView valueRUB;
        private TextView currency;
        private TextView date;
        private TextView type;
        private TextView comment;

    }
}
