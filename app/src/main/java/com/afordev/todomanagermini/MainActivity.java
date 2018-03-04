package com.afordev.todomanagermini;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.afordev.todomanagermini.Manager.DBManager;
import com.afordev.todomanagermini.Manager.Manager;
import com.afordev.todomanagermini.Manager.TodoRcvAdapter;
import com.afordev.todomanagermini.SubItem.DataTodo;
import com.afordev.todomanagermini.SubItem.DateForm;
import com.afordev.todomanagermini.SubItem.ItemNotice;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private Toolbar mToolbar;
    private RecyclerView rcvTodo;
    public TodoRcvAdapter todoRcvAdapter;
    private SwipeRefreshLayout mSwipe;
    private DateForm date;
    private DBManager dbManager = DBManager.getInstance(this);
    private boolean isToday;
    private ItemNotice notice;
    private SharedPreferences prefs;
    private InputMethodManager imm;
    private TextView version;

    public View viewBottom;
    private DataTodo temp;
    private ConstraintLayout layoutNew;
    private LinearLayout layoutEdit;
    private Button btnDelete, btnTag, btnTime, btnCancel;
    private EditText etTitle;
    private ImageView ivEditLeft, ivEditSave;
    private TextView tvTags;

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
        version = findViewById(R.id.main_tv_version);
        version.setText(Manager.VERSIONNAME + " b" + Manager.VERSIONCODE);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

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

        // Bottom item
        viewBottom = findViewById(R.id.today_bottom);
        temp = null;
        layoutNew = findViewById(R.id.item_todo_layout_new);
        layoutEdit = findViewById(R.id.item_todo_layout_edit);
        btnDelete = findViewById(R.id.item_todo_btn_delete);
        btnTag = findViewById(R.id.item_todo_btn_tag);
        btnTime = findViewById(R.id.item_todo_btn_time);
        btnCancel = findViewById(R.id.item_todo_btn_cancel);
        etTitle = findViewById(R.id.item_todo_et_edit_title);
        ivEditLeft = findViewById(R.id.item_todo_iv_edit_left);
        ivEditSave = findViewById(R.id.item_todo_iv_edit_save);
        tvTags = findViewById(R.id.item_todo_tv_edit_tag);
        layoutNew.setOnClickListener(this);
        btnTag.setOnClickListener(this);
        btnTime.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        ivEditLeft.setOnClickListener(this);
        ivEditSave.setOnClickListener(this);
        btnDelete.setVisibility(View.GONE);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    public void setData() {
        Manager.checkService(this);
        try {
            date = new DateForm(getIntent().getLongExtra("date", Calendar.getInstance().getTimeInMillis() / 60000));
        } catch (NullPointerException e) {
            date = new DateForm(Calendar.getInstance());
        }
        Calendar cal = Calendar.getInstance();
        if (date.getYear() == cal.get(Calendar.YEAR) &&
                date.getMonth() == cal.get(Calendar.MONTH) &&
                date.getDay() == cal.get(Calendar.DATE)) {
            isToday = true;
            mToolbar.setTitle("오늘의 할 일");
            noticeStar();
        } else {
            isToday = false;
            mToolbar.setTitle(Manager.getDateForm(this, date));
        }
        todoRcvAdapter = new TodoRcvAdapter(this, dbManager, date);
        rcvTodo.setAdapter(todoRcvAdapter);
        onRefreshBottom();
    }

    public void onRefreshBottom() {
        if (temp == null) {
            layoutNew.setVisibility(View.VISIBLE);
            layoutEdit.setVisibility(View.GONE);
        } else {
            layoutNew.setVisibility(View.GONE);
            layoutEdit.setVisibility(View.VISIBLE);
            etTitle.setText(temp.getTitle());
            etTitle.requestFocus();
            if (temp.getIsTimeActivated() == 0 && temp.getTags().equals("")) {
                tvTags.setVisibility(View.GONE);
            } else {
                tvTags.setVisibility(View.VISIBLE);
                StringBuffer sb = new StringBuffer();
                if (temp.getIsTimeActivated() == 1) {
                    sb.append(Manager.getTimeForm(temp.getDate()));
                }
                if (temp.getIsTimeActivated() == 1 && !temp.getTags().equals("")) {
                    sb.append(", ");
                }
                if (!temp.getTags().equals("")) {
                    ArrayList<String> st = temp.getTagList();
                    for (int i = 0; i < st.size(); i++) {
                        sb.append("#" + st.get(i) + " ");
                    }
                }
                tvTags.setText(sb.toString());
            }
        }
    }

    public void setViewBottom(final boolean view){
        final Handler mHandler = new Handler();
        final Animation animBtC = AnimationUtils.loadAnimation(
                this,R.anim.bottom_to_center);
        if(view){
            mHandler.postDelayed(new Runnable()  {
                public void run() {
                    viewBottom.setVisibility(View.VISIBLE);
                    viewBottom.setAnimation(animBtC);
                }
            }, 200);
        }else{
            viewBottom.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.item_todo_layout_new):
                temp = new DataTodo(date);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                onRefreshBottom();
                break;

            case (R.id.item_todo_iv_edit_left):
                Toast.makeText(this, "Next Version...", Toast.LENGTH_SHORT).show();
                break;

            case (R.id.item_todo_iv_edit_save):
                temp.setTitle(etTitle.getText().toString());
                dbManager.insertTodo(temp);
                onRefresh();
                imm.hideSoftInputFromWindow(etTitle.getWindowToken(), 0);
                break;

            case (R.id.item_todo_btn_cancel):
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setMessage("작업 중인 정보가 사라집니다.");
                dialog.setCancelable(true);
                dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        imm.hideSoftInputFromWindow(etTitle.getWindowToken(), 0);
                        temp = null;
                        onRefreshBottom();
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
                break;

            case (R.id.item_todo_btn_time):
                timeSelectOption();
                break;

            case (R.id.item_todo_btn_tag):
                Manager.showAddTag(this, temp, todoRcvAdapter, -1);
                break;
        }
    }

    public boolean noticeStar() {
        notice = new ItemNotice(this, findViewById(R.id.today_notice));
        boolean isStarNotice = prefs.getBoolean(Manager.PREF_STAR_NOTICE, false);
        String ignoreDate = prefs.getString("ignore_star", ",,");
        if (isStarNotice) {
            try {
                String[] strings = ignoreDate.split(",");
                DateForm date = new DateForm(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]), Integer.parseInt(strings[2]));
                if (date.compareTo(this.date) == 0) {
                    notice.view.setVisibility(View.GONE);
                    return false;
                }
            } catch (Exception e) {
                Log.e("eror", "ddd");
            }
            final ArrayList<DataTodo> missedStar = dbManager.getMissedStarList(date);
            if (missedStar.size() == 0) {
                notice.view.setVisibility(View.GONE);
            } else {
                notice.view.setVisibility(View.VISIBLE);
                notice.setTitle(missedStar.size() + "개의 '매우 중요' 일정이 완료되지 않았습니다.");
                notice.setLeftImage(R.drawable.ic_error);
                notice.btnCheck.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                        intent.putExtra("list", missedStar);
                        startActivityForResult(intent, Manager.RC_MAIN_TO_SEARCH);
                    }
                });
                notice.btnIgnore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("ignore_star", date.getYear() + "," + date.getMonth() + "," + date.getDay());
                        editor.apply();
                        notice.view.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, date.getYear() + "," + date.getMonth() + "," + date.getDay() + " 하루 동안 보이지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            notice.view.setVisibility(View.GONE);
            return false;
        }
        return true;
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
            case (R.id.menu_multi):
                intent = new Intent(MainActivity.this, MultiActivity.class);
                startActivityForResult(intent, Manager.RC_MAIN_TO_SETTING);
                return true;
        }
        return false;
    }

    @Override
    public void onRefresh() {
        todoRcvAdapter.onRefresh();
        mSwipe.setRefreshing(false);
    }

    @Override
    public void onBackPressed() {
        if (todoRcvAdapter.temp == null || this.temp == null) {
            super.onBackPressed();
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("작업 중인 정보가 사라집니다.");
            dialog.setCancelable(true);
            dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    todoRcvAdapter.temp = null;
                    todoRcvAdapter.notifyItemChanged(todoRcvAdapter.editPosition);
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
        DatePickerDialog dpDialog = new DatePickerDialog(this, listenerDate,
                date.getYear(),
                date.getMonth(),
                date.getDay());
        dpDialog.show();
    }

    private DatePickerDialog.OnDateSetListener listenerDate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            DateForm temp = new DateForm(year, monthOfYear, dayOfMonth);
            if (temp.compareTo(date) == 0 && date.compareTo(new DateForm(Calendar.getInstance())) == 0) {
                if (!isToday) {
                    finish();
                }
            } else {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.putExtra("date", temp.getSecond());
                if (!isToday) {
                    finish();
                }
                startActivityForResult(intent, Manager.RC_MAIN_TO_MAIN);
            }
        }
    };

    public void timeSelectOption() {
        TimePickerDialog dialog = new TimePickerDialog(this, listenerTime,
                temp.getDate().getHour(),
                temp.getDate().getMinute(), false);

        dialog.show();
    }

    private TimePickerDialog.OnTimeSetListener listenerTime = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            temp.getDate().setHour(hourOfDay);
            temp.getDate().setMinute(minute);
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Manager.RC_MAIN_TO_SETTING) {
            if (resultCode == RESULT_OK) {
                setData();
            }
        }
        if (requestCode == Manager.RC_MAIN_TO_SEARCH) {
            if (resultCode == RESULT_OK) {
                setData();
            }
        }
        if (requestCode == Manager.RC_MAIN_TO_MAIN) {
            setData();
        }
    }
}
