package com.afordev.todomanagermini.Manager;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.SuperscriptSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afordev.todomanagermini.MainActivity;
import com.afordev.todomanagermini.R;
import com.afordev.todomanagermini.SubItem.DataTodo;
import com.afordev.todomanagermini.SubItem.DateForm;
import com.greenfrvr.hashtagview.HashtagView;

import java.util.ArrayList;

/**
 * Created by penguo on 2018-02-17.
 */

public class Manager {

    public static final int RC_MAIN_TO_MAIN = 1000;
    public static final int RC_MAIN_TO_SETTING = 1001;
    public static final int RC_MAIN_TO_SEARCH = 1002;
    public static final int RC_MAIN_TO_MULTI = 1003;
    public static final int RC_SEARCH_TO_MULTI = 1004;
    public static final int RC_MAIN_TO_PATTERN = 1005;

    public static final String PREF_LOCK_SCREEN = "pref_lock_screen";
    public static final String PREF_AUTO_SORT = "pref_auto_sort";
    public static final String PREF_STAR_NOTICE = "pref_star_notice";
    public static final String PREF_HALF_STAR_NOTICE = "pref_half_star_notice";
    public static int VERSIONCODE = -1;
    public static String VERSIONNAME = "";

    public static void checkService(Context mContext) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Activity.ACTIVITY_SERVICE);
        boolean isServiceOn = false;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("ServiceName".equals(service.service.getClassName())) {
                isServiceOn = true;
            } else {
                isServiceOn = false;
            }
        }
        boolean isPrefLockScreenOn = prefs.getBoolean(PREF_LOCK_SCREEN, false);
        if (isPrefLockScreenOn) {
            if (!isServiceOn) {
                Intent intent = new Intent(mContext, ScreenService.class);
                mContext.startService(intent);
            }
        } else {
            if (isServiceOn) {
                Intent intent = new Intent(mContext, ScreenService.class);
                mContext.stopService(intent);
            }
        }
    }

    public static final HashtagView.DataTransform<String> HASHTAG = new HashtagView.DataTransform<String>() {
        @Override
        public CharSequence prepare(String item) {
            SpannableString spannableString = new SpannableString("#" + item);
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#850097A7")), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new SuperscriptSpan(), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableString;
        }
    };

    public static void showAddTag(final Activity activity, final DataTodo data, final RecyclerView.Adapter<RecyclerView.ViewHolder> rcvAdapter, final int position) {
        LayoutInflater li = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout updateLayout = (LinearLayout) li.inflate(R.layout.dialog_tag, null);
        final HashtagView hashtagView = (HashtagView) updateLayout.findViewById(R.id.dialog_tag_hashtag);
        final AutoCompleteTextView et = (AutoCompleteTextView) updateLayout.findViewById(R.id.dialog_tag_et);
        final ImageButton btnAdd = (ImageButton) updateLayout.findViewById(R.id.dialog_tag_btn_add);
        final ArrayList<String> tags = data.getTagList();
        et.setAdapter(new ArrayAdapter<String>(activity,
                android.R.layout.simple_dropdown_item_1line, tags));
        // TODO: 2018-03-05 adapter의 list를 tags가 아닌, 현재 존재하고 있는 태그들로 바꾸자.

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        AlertDialog dialog;
        hashtagView.setData(tags, Manager.HASHTAG);
        hashtagView.addOnTagClickListener(new HashtagView.TagsClickListener() {
            @Override
            public void onItemClicked(Object item) {
                tags.remove(item);
                data.setTagList(tags);
                hashtagView.setData(tags, Manager.HASHTAG);
            }
        });
        builder.setTitle("분류태그 설정");
        et.setText("");
        builder.setView(updateLayout);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et.getText().toString().equals("")) {
                    Toast.makeText(activity, "내용을 입력하세요.", Toast.LENGTH_SHORT).show();
                } else {
                    if (!tags.contains(et.getText().toString())) {
                        tags.add(et.getText().toString());
                    } else {
                        Toast.makeText(activity, "이미 등록된 태그입니다.", Toast.LENGTH_SHORT).show();
                    }
                }
                et.setText("");
                hashtagView.setData(tags, Manager.HASHTAG);
            }
        });
        builder.setPositiveButton("저장", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!et.getText().toString().equals("")) {
                    if (!tags.contains(et.getText().toString())) {
                        tags.add(et.getText().toString());
                    } else {
                        Toast.makeText(activity, "이미 등록된 태그입니다.", Toast.LENGTH_SHORT).show();
                    }
                }
                data.setTagList(tags);
                if (position != -1) {
                    rcvAdapter.notifyItemChanged(position);
                } else {
                    ((MainActivity) activity).onRefreshBottom();
                }
                dialog.dismiss();
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

    public static String getDateForm(Context mContext, DateForm date) {
        StringBuffer sb = new StringBuffer();
        sb.append((date.getMonth() + 1) + "월 ");
        sb.append(date.getDay() + "일 ");
        sb.append(mContext.getResources().getStringArray(R.array.dayofweek)[date.getDayofweek() - 1] + "요일");
        return sb.toString();
    }

    public static String getTimeForm(DateForm date) {
        StringBuffer sb = new StringBuffer();
        int hour = date.getHour();
        if (hour > 12) {
            sb.append("오후 ");
            if (hour != 12) {
                hour -= 12;
            }
        } else {
            sb.append("오전 ");
        }
        sb.append(hour + "시 ");
        sb.append(date.getMinute() + "분");
        return sb.toString();
    }
}
