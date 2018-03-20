package com.afordev.todomanagermini.SubItem;

import java.util.Calendar;

/**
 * Created by penguo on 2018-03-20.
 */

public class DataPattern {
    private int id;
    private String title, dow;
    private DateForm dateStart, dateEnd;
    private DataTodo dataTodo;

    public DataPattern(){
        this.id = -1;
        this.title = "";
        this.dateStart = new DateForm(Calendar.getInstance());
        this.dateEnd = new DateForm(Calendar.getInstance());
        this.dow = "";
        this.dataTodo = new DataTodo();
    }

    public DataPattern(int id, String title, long secondSt, long secondEnd, String dow, DataTodo dataTodo){
        this.id = id;
        this.title = title;
        this.dateStart = new DateForm(secondSt);
        this.dateEnd = new DateForm(secondEnd);
        this.dow = dow;
        this.dataTodo = dataTodo;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public DataTodo getDataTodo() {
        return dataTodo;
    }

    public DateForm getDateEnd() {
        return dateEnd;
    }

    public DateForm getDateStart() {
        return dateStart;
    }

    public String getDow() {
        return dow;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDataTodo(DataTodo dataTodo) {
        this.dataTodo = dataTodo;
    }

    public void setDateEnd(DateForm dateEnd) {
        this.dateEnd = dateEnd;
    }

    public void setDateStart(DateForm dateStart) {
        this.dateStart = dateStart;
    }

    public void setDow(String dow) {
        this.dow = dow;
    }
}
