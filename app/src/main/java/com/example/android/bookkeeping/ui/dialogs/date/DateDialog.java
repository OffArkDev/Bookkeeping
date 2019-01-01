package com.example.android.bookkeeping.ui.dialogs.date;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import com.example.android.bookkeeping.ui.dialogs.DialogCommunicator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    DialogCommunicator dialogCommunicator;

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, year, month, day);

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        String str=day+"."+(month+1)+"."+year;
        SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        Date date = null;
        try {
            date = f.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String result = f.format(date);
        dialogCommunicator.sendRequest(3, result);
    }

    public void setDialogCommunicator(DialogCommunicator dialogCommunicator) {
        this.dialogCommunicator = dialogCommunicator;
    }
}
