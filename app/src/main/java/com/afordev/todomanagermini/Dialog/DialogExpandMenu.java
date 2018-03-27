package com.afordev.todomanagermini.Dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.afordev.todomanagermini.AddPatternActivity;
import com.afordev.todomanagermini.MainActivity;
import com.afordev.todomanagermini.Manager.DBManager;
import com.afordev.todomanagermini.Manager.Manager;
import com.afordev.todomanagermini.R;
import com.afordev.todomanagermini.SubItem.DataTodo;
import com.afordev.todomanagermini.SubItem.DateForm;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by penguo on 2018-03-27.
 */

public class DialogExpandMenu implements View.OnClickListener {

    private Context mContext;
    private DataTodo temp;
    private int position;
    private RecyclerView.Adapter<RecyclerView.ViewHolder> rcvAdapter;

    private AlertDialog dialogExpandMenu;
    private LinearLayout layoutTime, layoutTag, layoutAutoDelay, layoutImportance, layoutPattern, layoutDeadline;
    private TextView tvTime, tvTag, tvAutoDelay, tvImportance, tvPattern, tvDeadline;
    private Switch switchAutoDelay;
    private ImageView ivImportance;
    private DBManager dbManager;
    private CustomDatePicker mDatePicker;
    private CustomTimePicker mTimePicker;

    public DialogExpandMenu(Context mContext, DataTodo temp, RecyclerView.Adapter<RecyclerView.ViewHolder> rcvAdapter, int position) {
        this.mContext = mContext;
        this.temp = temp;
        this.rcvAdapter = rcvAdapter;
        this.position = position;
        dbManager = DBManager.getInstance(mContext);
    }

    public void refreshParentForFinish() {
        if (rcvAdapter != null) {
            rcvAdapter.notifyItemChanged(position);
        } else { // Today의 BottomSheet
            switch (((Activity) mContext).getLocalClassName()) {
                case ("MainActivity"):
                    ((MainActivity) mContext).onRefreshBottom();
                    break;
                case ("AddPatternActivity"):
                    ((AddPatternActivity) mContext).setData();
                    break;
            }
        }
    }

    public void show() {
        LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) li.inflate(R.layout.dialog_expandmenu, null);
        layoutTime = layout.findViewById(R.id.dialog_em_layout_time);
        layoutTag = layout.findViewById(R.id.dialog_em_layout_tag);
        layoutAutoDelay = layout.findViewById(R.id.dialog_em_layout_autodelay);
        layoutImportance = layout.findViewById(R.id.dialog_em_layout_importance);
        layoutPattern = layout.findViewById(R.id.dialog_em_layout_pattern);
        tvTime = layout.findViewById(R.id.dialog_em_tv_time);
        tvTag = layout.findViewById(R.id.dialog_em_tv_tag);
        tvAutoDelay = layout.findViewById(R.id.dialog_em_tv_autodelay);
        tvImportance = layout.findViewById(R.id.dialog_em_tv_importance);
        tvPattern = layout.findViewById(R.id.dialog_em_tv_pattern);
        switchAutoDelay = layout.findViewById(R.id.dialog_em_switch_autodelay);
        ivImportance = layout.findViewById(R.id.dialog_em_iv_importance);
        layoutDeadline = layout.findViewById(R.id.dialog_em_layout_deadline);
        tvDeadline = layout.findViewById(R.id.dialog_em_tv_deadline);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        setDataExpandMenu();

        layoutTime.setOnClickListener(this);
        layoutTag.setOnClickListener(this);
        layoutAutoDelay.setOnClickListener(this);
        layoutImportance.setOnClickListener(this);
        layoutPattern.setOnClickListener(this);
        layoutDeadline.setOnClickListener(this);

        builder.setView(layout);
        builder.setPositiveButton("완료", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialogExpandMenu = builder.create(); //builder.show()를 create하여 dialog에 저장하는 방식.
        dialogExpandMenu.show();
    }

    public void setDataExpandMenu() {
        switch (temp.getImportance()) {
            case (0):
                tvImportance.setText("보통");
                ivImportance.setImageResource(R.drawable.ic_star_false);
                break;
            case (1):
                tvImportance.setText("중요");
                ivImportance.setImageResource(R.drawable.ic_star_half);
                break;
            case (2):
                tvImportance.setText("매우 중요");
                ivImportance.setImageResource(R.drawable.ic_star_true);
                break;
            default:
                tvImportance.setText("ERROR");
                ivImportance.setImageResource(R.drawable.ic_star_false);
                break;
        }
        if (temp.getIsTimeActivated() == 1) {
            tvTime.setText(Manager.getTimeForm(temp.getDate()));
        } else {
            tvTime.setText("하루 종일");
        }
        StringBuilder sb = new StringBuilder();
        ArrayList<String> st = temp.getTagList();
        for (int i = 0; i < st.size(); i++) {
            sb.append("#" + st.get(i) + " ");
        }
        tvTag.setText(sb.toString());
        if (temp.getType() == 2) {
            layoutPattern.setVisibility(View.GONE);
            layoutDeadline.setVisibility(View.VISIBLE);
            switchAutoDelay.setChecked(true);
            tvAutoDelay.setText(temp.getTypeValue() + "일 연기됨");
            if (temp.getDateDeadline() != null) {
                sb = new StringBuilder();
                sb.append(Manager.getDateForm(mContext, temp.getDateDeadline()));
                sb.append(" ");
                sb.append(Manager.getTimeForm(temp.getDateDeadline()));
                sb.append("까지");
                tvDeadline.setText(sb.toString());
            } else {
                tvDeadline.setText("마감 날짜 없음");
            }
        } else if (temp.getType() == 1) {
            layoutTime.setVisibility(View.GONE);
            layoutTag.setVisibility(View.GONE);
            layoutAutoDelay.setVisibility(View.GONE);
            layoutImportance.setVisibility(View.GONE);
            tvPattern.setText(temp.getTypeValue() + "회차");
        } else {
            layoutPattern.setVisibility(View.GONE);
            layoutDeadline.setVisibility(View.GONE);
            switchAutoDelay.setChecked(false);
            tvAutoDelay.setText("");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.dialog_em_layout_time):
                mTimePicker = new CustomTimePicker(mContext);
                mTimePicker.setTitle("할 일의 시간을 선택해주세요");
                mTimePicker.setPositiveListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int hour, minute;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            hour = mTimePicker.getTimePicker().getHour();
                            minute = mTimePicker.getTimePicker().getMinute();
                        } else {
                            hour = mTimePicker.getTimePicker().getCurrentHour();
                            minute = mTimePicker.getTimePicker().getCurrentMinute();
                        }
                        temp.getDate().setMinute(minute);
                        temp.getDate().setHour(hour);
                        temp.setIsTimeActivated(1);
                        dialogInterface.dismiss();
                        refreshParentForFinish();
                    }
                });
                mTimePicker.setNeutralListener("하루 종일", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        temp.setIsTimeActivated(0);
                        dialogInterface.dismiss();
                        refreshParentForFinish();
                    }
                });
                if (temp.getIsTimeActivated() != 0) {
                    mTimePicker.show(temp.getDate());
                } else {
                    mTimePicker.show(new DateForm(Calendar.getInstance()));
                }
                dialogExpandMenu.dismiss();
                break;

            case (R.id.dialog_em_layout_tag):
                TagPicker tagPicker = new TagPicker(mContext, DialogExpandMenu.this);
                tagPicker.show(temp);
                dialogExpandMenu.dismiss();
                break;

            case (R.id.dialog_em_layout_autodelay):
                temp.setTypeValue(0);
                if (temp.getType() == 2) {
                    temp.setType(0);
                } else {
                    temp.setType(2);
                }
                setDataExpandMenu();
                break;

            case (R.id.dialog_em_layout_importance):
                ImportancePicker importancePicker = new ImportancePicker(mContext, DialogExpandMenu.this);
                importancePicker.show(temp);
                dialogExpandMenu.dismiss();
                break;

            case (R.id.dialog_em_layout_pattern):
                Toast.makeText(mContext, "Next Version", Toast.LENGTH_SHORT).show();
                // TODO: 2018-03-26
                break;

            case (R.id.dialog_em_layout_deadline):
                mDatePicker = new CustomDatePicker(mContext);
                mTimePicker = new CustomTimePicker(mContext);
                mDatePicker.setTitle("마감 날짜를 입력해주세요");
                mDatePicker.setPositiveListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (temp.getDateDeadline() == null) {
                            temp.setDateDeadline(new DateForm(mDatePicker.getDatePicker().getYear(),
                                    mDatePicker.getDatePicker().getMonth(),
                                    mDatePicker.getDatePicker().getDayOfMonth()));
                        } else {
                            temp.getDateDeadline().setYear(mDatePicker.getDatePicker().getYear());
                            temp.getDateDeadline().setMonth(mDatePicker.getDatePicker().getMonth());
                            temp.getDateDeadline().setDay(mDatePicker.getDatePicker().getDayOfMonth());
                        }
                        mTimePicker.show(temp.getDateDeadline());
                        dialogInterface.dismiss();
                    }
                });
                mDatePicker.setNeutralListener("마감 날짜 없음", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        temp.setDateDeadline(null);
                        dialogInterface.dismiss();
                        refreshParentForFinish();
                    }
                });
                mTimePicker.setTitle("마감 시간을 선택해주세요");
                mTimePicker.setPositiveListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int hour, minute;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            hour = mTimePicker.getTimePicker().getHour();
                            minute = mTimePicker.getTimePicker().getMinute();
                        } else {
                            hour = mTimePicker.getTimePicker().getCurrentHour();
                            minute = mTimePicker.getTimePicker().getCurrentMinute();
                        }
                        temp.getDateDeadline().setMinute(minute);
                        temp.getDateDeadline().setHour(hour);
                        dialogInterface.dismiss();
                        refreshParentForFinish();
                        setDataExpandMenu();
                    }
                });
                mTimePicker.setNeutralListener("당일 까지", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        temp.getDateDeadline().setMinute(59);
                        temp.getDateDeadline().setHour(23);
                        dialogInterface.dismiss();
                        setDataExpandMenu();
                        refreshParentForFinish();
                        setDataExpandMenu();
                    }
                });
                if (temp.getDateDeadline() == null) {
                    mDatePicker.show(new DateForm(Calendar.getInstance()));
                } else {
                    mDatePicker.show(temp.getDateDeadline());
                }
                break;
        }
    }
}
