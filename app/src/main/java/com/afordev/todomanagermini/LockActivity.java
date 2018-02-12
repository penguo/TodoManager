package com.afordev.todomanagermini;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.afordev.todomanagermini.Manager.DBManager;
import com.afordev.todomanagermini.Manager.DateForm;
import com.afordev.todomanagermini.Manager.TodoRcvAdapter;

import java.util.Calendar;

public class LockActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private Toolbar mToolbar;
    private RecyclerView rcvTodo;
    private TodoRcvAdapter todoRcvAdapter;
    private SwipeRefreshLayout mSwipe;
    private DBManager dbManager = new DBManager(this, "todo.db", null, 1);
    private LinearLayout layoutBtnEnter, layoutBtnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        mToolbar = findViewById(R.id.today_toolbar);
        setSupportActionBar(mToolbar);
        rcvTodo = findViewById(R.id.today_rcv);
        mSwipe = findViewById(R.id.today_swipe);
        layoutBtnEnter = findViewById(R.id.lock_layout_btn_enter);
        layoutBtnBack = findViewById(R.id.lock_layout_btn_back);

        layoutBtnEnter.setOnClickListener(this);
        layoutBtnBack.setOnClickListener(this);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        DividerItemDecoration did = new DividerItemDecoration(this, llm.getOrientation());
        rcvTodo.addItemDecoration(did);
        rcvTodo.setLayoutManager(llm);
        mSwipe.setOnRefreshListener(this);
        setDate(new DateForm(Calendar.getInstance()));
    }

    public void setDate(DateForm date) {
        mToolbar.setTitle(date.toString());
        todoRcvAdapter = new TodoRcvAdapter(this, dbManager, date, true);
        rcvTodo.setAdapter(todoRcvAdapter);
    }

    @Override
    public void onRefresh() {
        todoRcvAdapter.onRefresh();
        mSwipe.setRefreshing(false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.lock_layout_btn_enter):
                Intent intent = new Intent(LockActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case (R.id.lock_layout_btn_back):
                finish();
                break;
        }
    }
}
