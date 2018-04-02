package com.afordev.todomanagermini.SubItem;

import java.util.Calendar;

/**
 * Created by penguo on 2018-03-20.
 */

public class DataPattern {
    private int id;
    private String memo, dayOfWeek, option;
    private DateForm timeStart, timeEnd, timeRecently;
    private DataTodo patternTodo;

    public DataPattern(){
        this.id = -1;
        this.patternTodo = new DataTodo(-1, null);
        this.memo = "";
        DateForm tempDate = new DateForm(Calendar.getInstance());
        tempDate.setHour(0);
        tempDate.setMinute(0);
        this.timeStart = tempDate.clone();
        tempDate.addDate(1);
        this.timeEnd = tempDate.clone();
        this.dayOfWeek = "";
        this.timeRecently = null;
        this.option ="";
    }

    public DataPattern(int id,
                       DataTodo patternTodo,
                       String memo,
                       long timeStart,
                       long timeEnd,
                       String dayOfWeek,
                       long timeRecently,
                       String option){
        this.id = id;
        this.patternTodo = patternTodo;
        this.memo = memo;
        this.timeStart = new DateForm(timeStart);
        this.timeEnd = new DateForm(timeEnd);
        this.dayOfWeek = dayOfWeek;
        this.timeRecently = new DateForm(timeRecently);
        this.option = option;
    }

    public int getId() {
        return id;
    }

    public String getMemo() {
        return memo;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public DataTodo getPatternTodo() {
        return patternTodo;
    }

    public DateForm getTimeEnd() {
        return timeEnd;
    }

    public DateForm getTimeRecently() {
        return timeRecently;
    }

    public DateForm getTimeStart() {
        return timeStart;
    }

    public String getOption() {
        return option;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public void setPatternTodo(DataTodo patternTodo) {
        this.patternTodo = patternTodo;
    }

    public void setTimeEnd(DateForm timeEnd) {
        this.timeEnd = timeEnd;
    }

    public void setTimeRecently(DateForm timeRecently) {
        this.timeRecently = timeRecently;
    }

    public void setTimeStart(DateForm timeStart) {
        this.timeStart = timeStart;
    }
}
