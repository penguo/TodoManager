package com.afordev.todomanagermini;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

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
//        DividerItemDecoration did = new DividerItemDecoration(this, llm.getOrientation());
//        rcvTodo.addItemDecoration(did);
        rcvTodo.setLayoutManager(llm);

        ArrayList<DataTodo> list = getIntent().getParcelableArrayListExtra("list");
        multiRcvAdapter = new MultiRcvAdapter(this, dbManager, list);
        rcvTodo.setAdapter(multiRcvAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menuxml_multi, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.menu_finish):
                setResult(RESULT_OK);
                finish();
                return true;
            case (R.id.menu_multi_tag):
                showTag();
                return true;
            case (R.id.menu_multi_importance):
                showImportance();
                return true;
            case (R.id.menu_multi_time):
                return true;
            case (R.id.menu_multi_delete):
                showDelete();
                return true;
        }
        return false;
    }

    private void showTag() {
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) li.inflate(R.layout.dialog_edittext, null);
        final AutoCompleteTextView et = (AutoCompleteTextView) layout.findViewById(R.id.dialog_tag_et);
        final ArrayList<String> dbTags = dbManager.getTagList();
        et.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, dbTags));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog;

        builder.setTitle("태그 추가, 제거");
        et.setText("");
        builder.setView(layout);

        builder.setNeutralButton("제거", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!et.getText().toString().equals("")) {
                    ArrayList<String> tags;
                    ArrayList<DataTodo> list = multiRcvAdapter.getSelectList();
                    for (int i = 0; i < list.size(); i++) {
                        tags = list.get(i).getTagList();
                        if (tags.contains(et.getText().toString())) {
                            tags.remove(et.getText().toString());
                            list.get(i).setTagList(tags);
                            dbManager.updateTodo(list.get(i));
                        }
                    }
                    multiRcvAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                } else {
                    Toast.makeText(MultiActivity.this, "공백은 입력되지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setPositiveButton("추가", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!et.getText().toString().equals("")) {
                    ArrayList<String> tags;
                    ArrayList<DataTodo> list = multiRcvAdapter.getSelectList();
                    for (int i = 0; i < list.size(); i++) {
                        tags = list.get(i).getTagList();
                        if (!tags.contains(et.getText().toString())) {
                            tags.add(et.getText().toString());
                            list.get(i).setTagList(tags);
                            dbManager.updateTodo(list.get(i));
                        }
                    }
                    multiRcvAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                } else {
                    Toast.makeText(MultiActivity.this, "공백은 입력되지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog = builder.create(); //builder.show()를 create하여 dialog에 저장하는 방식.
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    private void showDelete() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("정말로 삭제하시겠습니까? 모든 내용이 삭제되며 복구되지 않습니다.");
        dialog.setCancelable(true);
        dialog.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                multiRcvAdapter.deleteMultiple();
                Toast.makeText(MultiActivity.this, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
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

    private void showImportance() {
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) li.inflate(R.layout.dialog_importance, null);
        ImageButton btn0 = layout.findViewById(R.id.dialog_imp_btn_0);
        ImageButton btn1 = layout.findViewById(R.id.dialog_imp_btn_1);
        ImageButton btn2 = layout.findViewById(R.id.dialog_imp_btn_2);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("중요도 설정");
        builder.setView(layout);
        final AlertDialog dialog = builder.create();

        btn0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                multiRcvAdapter.setImportanceMultiple(0);
                dialog.dismiss();
            }
        });
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                multiRcvAdapter.setImportanceMultiple(1);
                dialog.dismiss();
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                multiRcvAdapter.setImportanceMultiple(2);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }
}
