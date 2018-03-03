package com.afordev.todomanagermini.SubItem;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;

/**
 * Created by penguo on 2018-03-03.
 */

public class DateForm implements Parcelable{
    private int year, month, day;
    private int hour, minute, dayofweek;

    public DateForm(long second) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(second * 60000);
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DATE);
        hour = cal.get(Calendar.HOUR_OF_DAY);
        minute = cal.get(Calendar.MINUTE);
        dayofweek = cal.get(Calendar.DAY_OF_WEEK);
    }

    public DateForm(Calendar cal) {
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DATE);
        hour = 0;
        minute = 0;
        dayofweek = cal.get(Calendar.DAY_OF_WEEK);
    }

    public DateForm(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, this.day);
        this.year = year;
        this.month = month;
        this.day = day;
        hour = 0;
        minute = 0;
        this.dayofweek = cal.get(Calendar.DAY_OF_WEEK);
    }

    protected DateForm(Parcel in) {
        year = in.readInt();
        month = in.readInt();
        day = in.readInt();
        hour = in.readInt();
        minute = in.readInt();
        dayofweek = in.readInt();
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
        dayofweek = cal.get(Calendar.DAY_OF_WEEK);
    }

    public long getSecond() {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day, hour, minute);
        return cal.getTimeInMillis() / 60000;
    }

    public void set(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        this.year = year;
        this.month = month;
        this.day = day;
        hour = 0;
        minute = 0;
        this.dayofweek = cal.get(Calendar.DAY_OF_WEEK);
    }

    public int compareTo(DateForm anotherDate) {
        DateForm date1 = new DateForm(this.getSecond());
        date1.setHour(0);
        date1.setMinute(0);
        DateForm date2 = new DateForm(anotherDate.getSecond());
        date2.setHour(0);
        date2.setMinute(0);
        if (date1.getSecond() < date2.getSecond()) {
            return 1;
        } else if (date1.getSecond() > date2.getSecond()) {
            return -1;
        } else {
            return 0;
        }
    }

    public int getDayofweek() {
        return dayofweek;
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
        parcel.writeInt(dayofweek);
    }
}
