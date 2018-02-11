package com.afordev.todomanagermini;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.afordev.todomanagermini.Manager.DBManager;
import com.afordev.todomanagermini.Manager.DateForm;
import com.afordev.todomanagermini.Manager.TodoRcvAdapter;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private Toolbar mToolbar;
    private RecyclerView rcvTodo;
    private TodoRcvAdapter todoRcvAdapter;
    private SwipeRefreshLayout mSwipe;
    private DateForm date;
    private DBManager dbManager = new DBManager(this, "todo.db", null, 1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.today_toolbar);
        setSupportActionBar(mToolbar);
        rcvTodo = findViewById(R.id.today_rcv);
        mSwipe = findViewById(R.id.today_swipe);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        DividerItemDecoration did = new DividerItemDecoration(this, llm.getOrientation());
        rcvTodo.addItemDecoration(did);
        rcvTodo.setLayoutManager(llm);
        mSwipe.setOnRefreshListener(this);
        setDate(new DateForm(Calendar.getInstance()));
    }

    public void setDate(DateForm date) {
        this.date = date;
        mToolbar.setTitle(date.toString());
        todoRcvAdapter = new TodoRcvAdapter(this, dbManager, date);
        rcvTodo.setAdapter(todoRcvAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_today, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.menu_cal):
                dateSelectOption();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                Toast.makeText(getApplicationContext(), "나머지 버튼 클릭됨", Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRefresh() {
        todoRcvAdapter.onRefresh();
        mSwipe.setRefreshing(false);
    }

    @Override
    public void onBackPressed() {
        if (todoRcvAdapter.editModePosition == -1) {
            super.onBackPressed();
        }else{
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
            setDate(new DateForm(year, monthOfYear+1, dayOfMonth));
        }
    };
}
