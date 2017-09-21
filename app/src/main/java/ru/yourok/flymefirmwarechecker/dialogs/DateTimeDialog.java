package ru.yourok.flymefirmwarechecker.dialogs;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import ru.yourok.flymefirmwarechecker.R;

/**
 * Created by yourok on 21.09.17.
 */

public class DateTimeDialog {

    View dialogView;
    AlertDialog alertDialog;
    long timestamp;
    Runnable runnable;

    public DateTimeDialog(Context context, long timestamp) {
        dialogView = View.inflate(context, R.layout.datetime_picker, null);
        if (timestamp != -1) {
            Date date = new Date(timestamp * 1000);
            ((TimePicker) dialogView.findViewById(R.id.time_picker)).setIs24HourView(true);
            ((TimePicker) dialogView.findViewById(R.id.time_picker)).setHour(date.getHours());
            ((TimePicker) dialogView.findViewById(R.id.time_picker)).setMinute(date.getMinutes());
            ((DatePicker) dialogView.findViewById(R.id.date_picker)).updateDate(date.getYear()+1900, date.getMonth(), date.getDate());
        }
        alertDialog = new AlertDialog.Builder(context).create();
    }

    public DateTimeDialog show() {
        dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
                TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);
                Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                        datePicker.getMonth(),
                        datePicker.getDayOfMonth(),
                        timePicker.getCurrentHour(),
                        timePicker.getCurrentMinute());
                timestamp = calendar.getTimeInMillis()/1000;
                alertDialog.dismiss();
                runnable.run();
            }
        });
        dialogView.findViewById(R.id.date_time_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setView(dialogView);
        alertDialog.show();
        return this;
    }

    public void onConfirm(Runnable runnable) {
        this.runnable = runnable;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
