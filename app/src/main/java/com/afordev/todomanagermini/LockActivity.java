package com.afordev.todomanagermini;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afordev.todomanagermini.Manager.DBManager;
import com.afordev.todomanagermini.Manager.LockRcvAdapter;
import com.afordev.todomanagermini.SubItem.DateForm;
import com.afordev.todomanagermini.Manager.TodoRcvAdapter;

import java.util.Calendar;

public class LockActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private RecyclerView rcvTodo;
    private LockRcvAdapter lockRcvAdapter;
    private SwipeRefreshLayout mSwipe;
    private DBManager dbManager = DBManager.getInstance(this);
    private LinearLayout layoutBtnEnter, layoutBtnBack;
    private TextView tvDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        rcvTodo = findViewById(R.id.today_rcv);
        mSwipe = findViewById(R.id.today_swipe);
        layoutBtnEnter = findViewById(R.id.lock_layout_btn_enter);
        layoutBtnBack = findViewById(R.id.lock_layout_btn_back);
        tvDate = findViewById(R.id.lock_tv_date);

        layoutBtnEnter.setOnClickListener(this);
        layoutBtnBack.setOnClickListener(this);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        DividerItemDecoration did = new DividerItemDecoration(this, llm.getOrientation());
        rcvTodo.addItemDecoration(did);
        rcvTodo.setLayoutManager(llm);
        mSwipe.setOnRefreshListener(this);
        initSet();
    }

    public void initSet() {
        DateForm date = new DateForm(Calendar.getInstance());
        tvDate.setText(date.getMonth() + "월 " + date.getDay() + "일 " + date.getDayOfWeekToString());
        lockRcvAdapter = new LockRcvAdapter(this, dbManager);
        rcvTodo.setAdapter(lockRcvAdapter);
    }

    @Override
    public void onRefresh() {
        lockRcvAdapter.onRefresh();
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
