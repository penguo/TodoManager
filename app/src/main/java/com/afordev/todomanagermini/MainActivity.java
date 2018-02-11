package com.afordev.todomanagermini;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.afordev.todomanagermini.Manager.DBManager;
import com.afordev.todomanagermini.Manager.DateForm;
import com.afordev.todomanagermini.Manager.TodoRcvAdapter;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private View ui;
    private Toolbar mToolbar;
    private RecyclerView rcvTodo;
    private TodoRcvAdapter todoRcvAdapter;
    private SwipeRefreshLayout mSwipe;
    private DBManager dbManager = new DBManager(this, "todo.db", null, 1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ui = findViewById(R.id.main_ui);
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

    public void setDate(DateForm date){
        mToolbar.setTitle(date.toString());
        todoRcvAdapter = new TodoRcvAdapter(this, dbManager, date);
        rcvTodo.setAdapter(todoRcvAdapter);
    }

    @Override
    public void onRefresh() {
        todoRcvAdapter.onRefresh();
        mSwipe.setRefreshing(false);
    }
}
