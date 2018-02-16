package com.afordev.todomanagermini.SubItem;

import android.util.Log;

import java.util.Calendar;

/**
 * Created by pengu on 2018-02-11.
 */

public class DateForm {

    private int year = 2000, month = 1, day = 1, dayOfWeek = 0;
    private TimeForm time;

    public DateForm(String date) {
        String[] st = date.split(",");
        try {
            year = Integer.parseInt(st[0]);
            month = Integer.parseInt(st[1]);
            day = Integer.parseInt(st[2]);
        } catch (Exception e) {
        }
        try {
            this.time = new TimeForm(Integer.parseInt(st[3]), Integer.parseInt(st[4]));
        } catch (Exception e) {
        }
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day);
        dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
    }

    public DateForm(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day);
        dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
    }

    public DateForm(Calendar cal) {
        this.year = cal.get(Calendar.YEAR);
        this.month = cal.get(Calendar.MONTH) + 1;
        this.day = cal.get(Calendar.DATE);
        dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
    }

    public TimeForm getTime() {
        return time;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public String getDayOfWeekToString() {
        switch (dayOfWeek) {
            case (1):
                return "일요일";
            case (2):
                return "월요일";
            case (3):
                return "화요일";
            case (4):
                return "수요일";
            case (5):
                return "목요일";
            case (6):
                return "금요일";
            case (7):
                return "토요일";
            default:
                return "null";
        }
    }

    public void setTime(TimeForm time) {
        this.time = time;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(year + "년 ");
        sb.append(month + "월 ");
        sb.append(day + "일");
        return sb.toString();
    }

    public String toDBString() {
        StringBuffer sb = new StringBuffer();
        sb.append(year);
        sb.append(",");
        sb.append(month);
        sb.append(",");
        sb.append(day);
        if(time != null){
            sb.append(",");
            sb.append(time.getHour());
            sb.append(",");
            sb.append(time.getMinute());
        }
        Log.e("check", sb.toString());
        return sb.toString();
    }

}
