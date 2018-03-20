package com.afordev.todomanagermini;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afordev.todomanagermini.Manager.DBManager;
import com.afordev.todomanagermini.Manager.LockRcvAdapter;
import com.afordev.todomanagermini.Manager.Manager;
import com.afordev.todomanagermini.SubItem.DateForm;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.request.RequestOptions;

import java.util.Calendar;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class LockActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private RecyclerView rcvTodo;
    private LockRcvAdapter lockRcvAdapter;
    private SwipeRefreshLayout mSwipe;
    private DBManager dbManager = DBManager.getInstance(this);
    private LinearLayout layoutBtnEnter, layoutBtnBack;
    private TextView tvDate;
    private DateForm date;
    private ImageView ivBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        rcvTodo = findViewById(R.id.today_rcv);
        mSwipe = findViewById(R.id.today_swipe);
        layoutBtnEnter = findViewById(R.id.lock_layout_btn_enter);
        layoutBtnBack = findViewById(R.id.lock_layout_btn_back);
        tvDate = findViewById(R.id.lock_tv_date);
        ivBackground = findViewById(R.id.lock_iv_background);
        Glide.with(this)
                .load(R.drawable.lock_background)
                .apply(RequestOptions.bitmapTransform(new MultiTransformation<Bitmap>(new BlurTransformation(25))))
                .into(ivBackground);

        layoutBtnEnter.setOnClickListener(this);
        layoutBtnBack.setOnClickListener(this);
        LinearLayoutManager llm = new LinearLayoutManager(this);
//        DividerItemDecoration did = new DividerItemDecoration(this, llm.getOrientation());
//        rcvTodo.addItemDecoration(did);
        rcvTodo.setLayoutManager(llm);
        mSwipe.setOnRefreshListener(this);
        setData();
    }

    public void initSet() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
    }

    public void setData() {
        date = new DateForm(Calendar.getInstance());
        tvDate.setText(Manager.getDateForm(this, date));
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
