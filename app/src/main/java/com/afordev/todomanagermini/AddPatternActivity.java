package com.afordev.todomanagermini;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afordev.todomanagermini.Dialog.DialogExpandMenu;
import com.afordev.todomanagermini.Manager.DBManager;
import com.afordev.todomanagermini.Manager.Manager;
import com.afordev.todomanagermini.SubItem.DataPattern;
import com.afordev.todomanagermini.SubItem.DataTodo;
import com.afordev.todomanagermini.SubItem.DateForm;

import java.util.ArrayList;
import java.util.Calendar;

public class AddPatternActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private Button btnDOW0, btnDOW1, btnDOW2, btnDOW3, btnDOW4, btnDOW5, btnDOW6, btnDateStart, btnDateEnd;
    private TextView tvDOW, tvRecently;
    private DBManager dbManager = DBManager.getInstance(this);
    private DataPattern dataPattern;
    private DataTodo dataTodo;
    private EditText etMemo;
    private ArrayList<String> dowNum = new ArrayList<>();

    private EditText etTodoTitle;
    private TextView tvTags;
    private ImageView ivEditLeft, ivEditExpandMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pattern);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        btnDOW0 = findViewById(R.id.add_pattern_btn_dow0);
        btnDOW1 = findViewById(R.id.add_pattern_btn_dow1);
        btnDOW2 = findViewById(R.id.add_pattern_btn_dow2);
        btnDOW3 = findViewById(R.id.add_pattern_btn_dow3);
        btnDOW4 = findViewById(R.id.add_pattern_btn_dow4);
        btnDOW5 = findViewById(R.id.add_pattern_btn_dow5);
        btnDOW6 = findViewById(R.id.add_pattern_btn_dow6);
        tvDOW = findViewById(R.id.add_pattern_tv_dow);
        tvRecently = findViewById(R.id.add_pattern_tv_recently);
        btnDateStart = findViewById(R.id.add_pattern_btn_date_start);
        btnDateEnd = findViewById(R.id.add_pattern_btn_date_end);
        etMemo = findViewById(R.id.add_pattern_et_title);

        etTodoTitle = findViewById(R.id.item_todo_et_edit_title);
        tvTags = findViewById(R.id.item_todo_tv_edit_tag);
        ivEditLeft = findViewById(R.id.item_todo_iv_edit_left);
        ivEditExpandMenu = findViewById(R.id.item_todo_iv_edit_expandmenu);

        btnDOW0.setOnClickListener(this);
        btnDOW1.setOnClickListener(this);
        btnDOW2.setOnClickListener(this);
        btnDOW3.setOnClickListener(this);
        btnDOW4.setOnClickListener(this);
        btnDOW5.setOnClickListener(this);
        btnDOW6.setOnClickListener(this);
        btnDateStart.setOnClickListener(this);
        btnDateEnd.setOnClickListener(this);
        ivEditLeft.setOnClickListener(this);
        ivEditExpandMenu.setOnClickListener(this);

        initSet();
    }

    private void initSet() {
        int id = getIntent().getIntExtra("pattern_id", -1);
        if (id == -1) {
            dataPattern = new DataPattern();
            mToolbar.setTitle("새로운 퍼즐");
        } else {
            dataPattern = dbManager.getPattern(id);
            mToolbar.setTitle("퍼즐 수정");
        }
        dataTodo = dataPattern.getPatternTodo();
        String[] sts = dataPattern.getDayOfWeek().split(",");
        for (int i = 0; i < sts.length; i++) {
            switch (sts[i]) {
                case ("0"):
                    btnDOW0.setSelected(true);
                    break;
                case ("1"):
                    btnDOW1.setSelected(true);
                    break;
                case ("2"):
                    btnDOW2.setSelected(true);
                    break;
                case ("3"):
                    btnDOW3.setSelected(true);
                    break;
                case ("4"):
                    btnDOW4.setSelected(true);
                    break;
                case ("5"):
                    btnDOW5.setSelected(true);
                    break;
                case ("6"):
                    btnDOW6.setSelected(true);
                    break;
            }
        }
        setData();
    }

    public void setData() {
        ArrayList<String> dowList = new ArrayList<>();
        dowNum.clear();
        etMemo.setText(dataPattern.getMemo());
        btnDateStart.setText(Manager.getDateForm(this, dataPattern.getTimeStart()));
        btnDateEnd.setText(Manager.getDateForm(this, dataPattern.getTimeEnd()));
        etTodoTitle.setText(dataTodo.getTitle());
        if (dataTodo.getTurn() > 0) {
            tvRecently.setVisibility(View.VISIBLE);
            tvRecently.setText("최근 추가 날짜: " + Manager.getDateForm(this, dataPattern.getTimeRecently()) + ", " + dataTodo.getTurn() + "회차");
        } else {
            tvRecently.setVisibility(View.GONE);
        }

        StringBuilder sb = new StringBuilder();
        if (dataTodo.getTimeDead().compareTo(new DateForm(Calendar.getInstance())) == 0) {
            sb.append(Manager.getTimeForm(dataTodo.getTimeDead()));
        }
        if (!dataTodo.getTags().equals("")) {
            if (!sb.toString().equals("")) {
                sb.append(", ");
            }
            ArrayList<String> st = dataTodo.getTagList();
            for (int i = 0; i < st.size(); i++) {
                sb.append("#" + st.get(i) + " ");
            }
        }
        tvTags.setText(sb.toString());

        String[] dow = getResources().getStringArray(R.array.dayofweek);
        if (btnDOW0.isSelected()) {
            dowList.add(dow[0]);
            dowNum.add("0");
        }
        if (btnDOW1.isSelected()) {
            dowList.add(dow[1]);
            dowNum.add("1");
        }
        if (btnDOW2.isSelected()) {
            dowList.add(dow[2]);
            dowNum.add("2");
        }
        if (btnDOW3.isSelected()) {
            dowList.add(dow[3]);
            dowNum.add("3");
        }
        if (btnDOW4.isSelected()) {
            dowList.add(dow[4]);
            dowNum.add("4");
        }
        if (btnDOW5.isSelected()) {
            dowList.add(dow[5]);
            dowNum.add("5");
        }
        if (btnDOW6.isSelected()) {
            dowList.add(dow[6]);
            dowNum.add("6");
        }
        if (dowList.size() == 0) {
            tvDOW.setText("요일을 선택해주세요.");
        } else if (dowList.size() == 7) {
            tvDOW.setText("매일");
        } else {
            sb = new StringBuilder();
            sb.append("매 주 ");
            for (int i = 0; i < dowList.size(); i++) {
                sb.append(dowList.get(i));
                if (i < dowList.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append("요일");
            tvDOW.setText(sb.toString());
        }
        switch (dataTodo.getImportance()) {
            case (1):
                ivEditLeft.setImageResource(R.drawable.ic_star_half);
                break;
            case (2):
                ivEditLeft.setImageResource(R.drawable.ic_star_true);
                break;
            case (0):
            default:
                ivEditLeft.setImageResource(R.drawable.ic_star_false);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menuxml_addpat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case (R.id.menu_save):
                dataPattern.setMemo(etMemo.getText().toString());
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < dowNum.size(); i++) {
                    sb.append(dowNum.get(i));
                    if (i < dowNum.size() - 1) {
                        sb.append(",");
                    }
                }
                dataPattern.setDayOfWeek(sb.toString());
                dataTodo.setTitle(etTodoTitle.getText().toString());
                dataPattern.setPatternTodo(dataTodo);

                if (dataPattern.getId() == -1) {
                    dbManager.insertPattern(dataPattern);
                } else {
                    dbManager.updatePattern(dataPattern);
                }
                setResult(RESULT_OK);
                finish();
                return true;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        dataTodo.setTitle(etTodoTitle.getText().toString());
        dataPattern.setMemo(etMemo.getText().toString());
        switch (view.getId()) {
            case (R.id.add_pattern_btn_dow0):
            case (R.id.add_pattern_btn_dow1):
            case (R.id.add_pattern_btn_dow2):
            case (R.id.add_pattern_btn_dow3):
            case (R.id.add_pattern_btn_dow4):
            case (R.id.add_pattern_btn_dow5):
            case (R.id.add_pattern_btn_dow6):
                Button btn = (Button) view;
                if (btn.isSelected()) {
                    btn.setSelected(false);
                } else {
                    btn.setSelected(true);
                }
                setData();
                break;
            case (R.id.add_pattern_btn_date_start):
                datePicker(true);
                break;
            case (R.id.add_pattern_btn_date_end):
                datePicker(false);
                break;

            case (R.id.item_todo_iv_edit_left):
                switch (dataTodo.getImportance()) {
                    case (0):
                        dataTodo.setImportance(1);
                        break;
                    case (1):
                        dataTodo.setImportance(2);
                        break;
                    case (2):
                        dataTodo.setImportance(0);
                        break;
                }
                setData();
                break;

            case (R.id.item_todo_iv_edit_expandmenu):
                DialogExpandMenu dialogExpandMenu = new DialogExpandMenu(AddPatternActivity.this, dataTodo, null, -1);
                dialogExpandMenu.show();
                break;
        }
    }

    public void datePicker(final boolean isStart) {
        final DateForm date;
        if (isStart) {
            date = dataPattern.getTimeStart();
        } else {
            date = dataPattern.getTimeEnd();
        }
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.setYear(year);
                date.setMonth(monthOfYear);
                date.setDay(dayOfMonth);
                date.setHour(0);
                date.setMinute(0);
                if (dataPattern.getTimeStart().getTime() > dataPattern.getTimeEnd().getTime()) {
                    dataPattern.setTimeEnd(dataPattern.getTimeStart());
                }
                setData();
            }
        };
        DatePickerDialog dpDialog = new DatePickerDialog(this, listener,
                date.getYear(),
                date.getMonth(),
                date.getDay());
        if (isStart) {
            dpDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
        } else {
            dpDialog.getDatePicker().setMinDate(dataPattern.getTimeStart().getTime() * 60000);
        }
        dpDialog.show();
    }
}


