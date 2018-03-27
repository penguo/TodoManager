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
import android.util.Log;
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
import java.util.Calendar;

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
    public static final int RC_PATTERN_TO_ADDPATTERN = 1006;

    public static final String PREF_LOCK_SCREEN = "pref_lock_screen";
    public static final String PREF_AUTO_SORT = "pref_auto_sort";
    public static final String PREF_STAR_NOTICE = "pref_star_notice";
    public static final String PREF_HALF_STAR_NOTICE = "pref_half_star_notice";
    public static final String PREF_DOUBLE_CLICK = "pref_double_click";
    public static final String PREF_VIEW_CHECKED = "pref_view_checked";
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

    public static String getDateForm(Context mContext, DateForm date) {
        StringBuilder sb = new StringBuilder();
        DateForm pivot = new DateForm(Calendar.getInstance());
        if (date.getYear() != pivot.getYear()) {
            sb.append(date.getYear() + "년 ");
        }
        sb.append((date.getMonth() + 1) + "월 ");
        sb.append(date.getDay() + "일 ");
        String[] sts = mContext.getResources().getStringArray(R.array.dayofweek);
        sb.append(sts[date.getDayofweek() - 1]);
        sb.append("요일");
        return sb.toString();
    }

    public static String getTimeForm(DateForm date) {
        StringBuilder sb = new StringBuilder();
        int hour = date.getHour();
        if (hour > 12) {
            sb.append("오후 ");
            if (hour != 12) {
                hour -= 12;
            }
        } else if (hour == 12) {
            sb.append("오후 ");
        } else {
            sb.append("오전 ");
        }
        sb.append(hour + "시 ");
        sb.append(date.getMinute() + "분");
        return sb.toString();
    }
}
