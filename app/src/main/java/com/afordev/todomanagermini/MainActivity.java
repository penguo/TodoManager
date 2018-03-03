package com.afordev.todomanagermini;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.afordev.todomanagermini.Manager.DBManager;
import com.afordev.todomanagermini.Manager.Manager;
import com.afordev.todomanagermini.SubItem.DateForm;
import com.afordev.todomanagermini.Manager.TodoRcvAdapter;
import com.afordev.todomanagermini.SubItem.ItemNotice;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private Toolbar mToolbar;
    private RecyclerView rcvTodo;
    public TodoRcvAdapter todoRcvAdapter;
    private SwipeRefreshLayout mSwipe;
    private DateForm date;
    private DBManager dbManager = DBManager.getInstance(this);
    private boolean isToday;
    private ItemNotice notice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initSet();
        Manager.checkService(this);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        rcvTodo = findViewById(R.id.today_rcv);
        mSwipe = findViewById(R.id.today_swipe);
        notice = new ItemNotice(this, findViewById(R.id.today_notice));

        LinearLayoutManager llm = new LinearLayoutManager(this);
        DividerItemDecoration did = new DividerItemDecoration(this, llm.getOrientation());
        rcvTodo.addItemDecoration(did);
        rcvTodo.setLayoutManager(llm);
        mSwipe.setOnRefreshListener(this);
        setData();
    }

    public void initSet() {
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(
                    this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Manager.VERSIONCODE = pInfo.versionCode;
        Manager.VERSIONNAME = pInfo.versionName;
    }

    public void setData() {
        try {
            date = new DateForm(getIntent().getStringExtra("date"));
            isToday = false;
            mToolbar.setTitle(date.toString());
        } catch (Exception e) {
            date = new DateForm(Calendar.getInstance());
            isToday = true;
            mToolbar.setTitle("오늘의 할 일");
        }
        todoRcvAdapter = new TodoRcvAdapter(this, dbManager, date);
        rcvTodo.setAdapter(todoRcvAdapter);
    }

    public void setTodayInfo() {
        notice.setTitle("개의 중요 일정이 완료되지 않았습니다.");
        notice.setLeftImage(R.drawable.ic_error);
        notice.btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        notice.btnIgnore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_today, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case (R.id.menu_cal):
                dateSelectOption();
                return true;
            case (R.id.menu_today):
                if (!isToday) {
                    finish();
                }
                return true;
            case (R.id.menu_setting):
                intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(intent, Manager.RC_MAIN_TO_SETTING);
                return true;
            case (R.id.menu_search):
                intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivityForResult(intent, Manager.RC_MAIN_TO_SEARCH);
                return true;
        }
        return false;
    }

    @Override
    public void onRefresh() {
        Manager.checkService(this);
        todoRcvAdapter.onRefresh();
        mSwipe.setRefreshing(false);
    }

    @Override
    public void onBackPressed() {
        if (todoRcvAdapter.editModePosition == -1) {
            super.onBackPressed();
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("작업 중인 정보가 사라집니다.");
            dialog.setCancelable(true);
            dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int i = todoRcvAdapter.editModePosition;
                    todoRcvAdapter.editModePosition = -1;
                    todoRcvAdapter.notifyItemChanged(i);
                    dialog.dismiss();
                }
            });
            dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    public void dateSelectOption() {
        DatePickerDialog dpDialog;
        dpDialog = new DatePickerDialog(this, listener, date.getYear(), date.getMonth() - 1, date.getDay());
        dpDialog.show();
    }

    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            DateForm temp = new DateForm(year, monthOfYear + 1, dayOfMonth);
            DateForm today = new DateForm(Calendar.getInstance());
            if (temp.getYear() == today.getYear() &&
                    temp.getMonth() == today.getMonth() &&
                    temp.getDay() == today.getDay()) {
                if (!isToday) {
                    finish();
                }
            } else {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.putExtra("date", temp.toDBString());
                if (!isToday) {
                    finish();
                }
                startActivity(intent);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Manager.RC_MAIN_TO_SETTING) {
            if (resultCode == RESULT_OK) {
                onRefresh();
            }
        }
        if (requestCode == Manager.RC_MAIN_TO_SEARCH) {
            if (resultCode == RESULT_OK) {
                onRefresh();
            }
        }
    }
}
