package com.afordev.todomanagermini.SubItem;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;

/**
 * Created by penguo on 2018-03-03.
 */

public class DateForm implements Parcelable, Cloneable {
    private int year, month, day;
    private int hour, minute;
    private int dayofweek = -1;
    private boolean isNull = false;

    public DateForm(long time) {
        if (time != -1) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time * 60000);
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
            day = cal.get(Calendar.DATE);
            hour = cal.get(Calendar.HOUR_OF_DAY);
            minute = cal.get(Calendar.MINUTE);
        } else {
            isNull = true;
        }
    }

    public DateForm(Calendar cal) {
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DATE);
        hour = cal.get(Calendar.HOUR);
        minute = cal.get(Calendar.MINUTE);
    }

    public DateForm(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        this.year = year;
        this.month = month;
        this.day = day;
        hour = cal.get(Calendar.HOUR);
        minute = cal.get(Calendar.MINUTE);
    }

    protected DateForm(Parcel in) {
        year = in.readInt();
        month = in.readInt();
        day = in.readInt();
        hour = in.readInt();
        minute = in.readInt();
    }

    public static final Creator<DateForm> CREATOR = new Creator<DateForm>() {
        @Override
        public DateForm createFromParcel(Parcel in) {
            return new DateForm(in);
        }

        @Override
        public DateForm[] newArray(int size) {
            return new DateForm[size];
        }
    };

    public void addDate(int i) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day, hour, minute);
        cal.add(Calendar.DATE, i);
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DATE);
        hour = cal.get(Calendar.HOUR_OF_DAY);
        minute = cal.get(Calendar.MINUTE);
    }

    public long getTime() {
        if (!isNull) {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, day, hour, minute);
            return cal.getTimeInMillis() / 60000;
        } else {
            return -1;
        }
    }

    public void set(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        this.year = year;
        this.month = month;
        this.day = day;
        hour = 0;
        minute = 0;
    }

    public int compareTo(DateForm rightDate) {
        DateForm date1 = this.clone();
        date1.setHour(0);
        date1.setMinute(0);
        DateForm date2 = rightDate.clone();
        date2.setHour(0);
        date2.setMinute(0);
        long diff = date1.getTime() - date2.getTime();
        diff /= (24 * 60);
        return (int) diff;
    }

    public int getDayofweek() {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    public int getMinute() {
        return minute;
    }

    public int getHour() {
        return hour;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(year);
        parcel.writeInt(month);
        parcel.writeInt(day);
        parcel.writeInt(hour);
        parcel.writeInt(minute);
    }

    public boolean isNull() {
        return isNull;
    }

    public DateForm clone() {
        DateForm obj = null;
        try {
            obj = (DateForm) super.clone();
        } catch (Exception e) {
        }
        return obj;
    }
}
