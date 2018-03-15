package com.afordev.todomanagermini;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.afordev.todomanagermini.Manager.DBManager;
import com.afordev.todomanagermini.Manager.MultiRcvAdapter;
import com.afordev.todomanagermini.SubItem.DataTodo;

import java.util.ArrayList;

public class MultiActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView rcvTodo;
    private DBManager dbManager;
    public MultiRcvAdapter multiRcvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        dbManager = DBManager.getInstance(this);
        rcvTodo = findViewById(R.id.multi_rcv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        DividerItemDecoration did = new DividerItemDecoration(this, llm.getOrientation());
        rcvTodo.addItemDecoration(did);
        rcvTodo.setLayoutManager(llm);

        ArrayList<DataTodo> list = getIntent().getParcelableArrayListExtra("list");
        multiRcvAdapter = new MultiRcvAdapter(this, dbManager, list);
        rcvTodo.setAdapter(multiRcvAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_multi, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.menu_finish):
                return true;
        }
        return false;
    }

}
