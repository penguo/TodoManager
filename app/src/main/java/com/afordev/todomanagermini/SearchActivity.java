package com.afordev.todomanagermini;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afordev.todomanagermini.Manager.DBManager;
import com.afordev.todomanagermini.Manager.SearchRcvAdapter;
import com.afordev.todomanagermini.SubItem.DataTodo;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Toolbar mToolbar;
    private EditText etSearch;
    private Spinner spinner;
    private ArrayList<String> items;
    private DBManager dbManager = DBManager.getInstance(this);
    private RecyclerView rcvSearch;
    private SearchRcvAdapter searchRcvAdapter;
    private ImageView ivSearch;
    private int selectedItemPos;
    private LinearLayout layoutTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        layoutTop = findViewById(R.id.search_layout_top);
        etSearch = findViewById(R.id.search_et_word);
        etSearch.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search();
                }
                return false;
            }
        });
        spinner = findViewById(R.id.search_spinner);
        rcvSearch = findViewById(R.id.search_rcv);
        ivSearch = findViewById(R.id.search_iv_search);

        setData();
    }

    public void setData() {
        ArrayList<DataTodo> dataList = getIntent().getParcelableArrayListExtra("list");
        searchRcvAdapter = new SearchRcvAdapter(this, dbManager, -1, "");
        LinearLayoutManager llm = new LinearLayoutManager(this);
        DividerItemDecoration did = new DividerItemDecoration(this, llm.getOrientation());
        rcvSearch.addItemDecoration(did);
        rcvSearch.setLayoutManager(llm);
        rcvSearch.setAdapter(searchRcvAdapter);
        if (dataList == null) {
            items = new ArrayList<>();
            items.add("전체");
            items.add("제목");
            items.add("태그");
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(
                    this,
                    R.layout.support_simple_spinner_dropdown_item,
                    items);
            spinner.setAdapter(spinnerAdapter);
            spinner.setSelection(0);
            spinner.setOnItemSelectedListener(this);
            ivSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    search();
                }
            });
        } else {
            layoutTop.setVisibility(View.GONE);
            searchRcvAdapter = new SearchRcvAdapter(this, dbManager, dataList);
            rcvSearch.setAdapter(searchRcvAdapter);
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        selectedItemPos = i;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    public void search() {
        if (etSearch.getText().toString().length() < 2) {
            Toast.makeText(this, "최소 2자 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
        } else {
            searchRcvAdapter = new SearchRcvAdapter(this, dbManager, selectedItemPos, etSearch.getText().toString());
            rcvSearch.setAdapter(searchRcvAdapter);
            StringBuffer sb = new StringBuffer();
            switch (selectedItemPos) {
                case (0):
                    break;
                case (1):
                    sb.append("제목에서의 ");
                    break;
                case (2):
                    sb.append("태그에서의 ");
                    break;
            }
            sb.append("'" + etSearch.getText().toString() + "' 검색 결과");
            mToolbar.setTitle(sb.toString());
            etSearch.setText("");
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
        }
    }
}
