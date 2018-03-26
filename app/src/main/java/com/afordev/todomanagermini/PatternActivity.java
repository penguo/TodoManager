package com.afordev.todomanagermini;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.afordev.todomanagermini.Manager.DBManager;
import com.afordev.todomanagermini.Manager.Manager;
import com.afordev.todomanagermini.Manager.PatternRcvAdapter;

public class PatternActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView rcvPattern;
    private PatternRcvAdapter patternRcvAdapter;
    private DBManager dbManager = DBManager.getInstance(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        rcvPattern = findViewById(R.id.pattern_rcv);
        setData();
    }

    public void setData(){
        patternRcvAdapter = new PatternRcvAdapter(this, dbManager);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rcvPattern.setLayoutManager(llm);
        rcvPattern.setAdapter(patternRcvAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menuxml_pat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case (R.id.menu_add):
                intent = new Intent(PatternActivity.this, AddPatternActivity.class);
                startActivityForResult(intent, Manager.RC_PATTERN_TO_ADDPATTERN);
                return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Manager.RC_PATTERN_TO_ADDPATTERN) {
            if (resultCode == RESULT_OK) {
                setData();
            }
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }
}
