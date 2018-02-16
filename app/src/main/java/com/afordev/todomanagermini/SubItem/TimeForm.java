package com.afordev.todomanagermini.SubItem;

/**
 * Created by penguo on 2018-02-17.
 */

public class TimeForm {
    private int hour, minute;

    public TimeForm(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        int h = this.hour;
        if (h <= 12) {
            sb.append("오전 ");
        } else {
            sb.append("오후 ");
            h -= 12;
        }
        sb.append(h + "시 ");
        sb.append(minute + "분");
        return sb.toString();
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }
}
