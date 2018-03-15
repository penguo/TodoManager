package com.afordev.todomanagermini.Manager;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.afordev.todomanagermini.MainActivity;
import com.afordev.todomanagermini.R;
import com.afordev.todomanagermini.SubItem.DataTodo;
import com.afordev.todomanagermini.SubItem.DateForm;

import java.util.Calendar;

/**
 * Created by penguo on 2018-03-07.
 */

public class CustomTimePicker {

    public static void show(final Context mContext, final DataTodo data, final RecyclerView.Adapter<RecyclerView.ViewHolder> rcvAdapter, final int position) {
        LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) li.inflate(R.layout.dialog_timepicker, null);
        final TimePicker mTimePicker = layout.findViewById(R.id.dialog_ti_timepicker);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final AlertDialog dialog;

        int hour, minute;
        if (data.getIsTimeActivated() == 1) {
            hour = data.getDate().getHour();
            minute = data.getDate().getMinute();
        } else {
            Calendar cal = Calendar.getInstance();
            DateForm date = new DateForm(cal);
            date.setHour(cal.get(Calendar.HOUR));
            date.setMinute(cal.get(Calendar.MINUTE));
            hour = date.getHour();
            minute = date.getMinute();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mTimePicker.setHour(hour);
            mTimePicker.setMinute(minute);
        } else {
            mTimePicker.setCurrentHour(hour);
            mTimePicker.setCurrentMinute(minute);
        }
        builder.setView(layout);

        builder.setPositiveButton("저장", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int hour, minute;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    hour = mTimePicker.getHour();
                    minute = mTimePicker.getMinute();
                } else {
                    hour = mTimePicker.getCurrentHour();
                    minute = mTimePicker.getCurrentMinute();
                }
                data.getDate().setMinute(minute);
                data.getDate().setHour(hour);
                data.setIsTimeActivated(1);
                rcvAdapter.notifyItemChanged(position);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNeutralButton("사용하지 않음", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                data.setIsTimeActivated(0);
                rcvAdapter.notifyItemChanged(position);
                dialog.dismiss();
            }
        });

        dialog = builder.create(); //builder.show()를 create하여 dialog에 저장하는 방식.
        dialog.show();
    }
}
