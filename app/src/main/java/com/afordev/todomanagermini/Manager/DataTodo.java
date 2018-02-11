package com.afordev.todomanagermini.Manager;

/**
 * Created by penguo on 2018-02-11.
 */

public class DataTodo {
    private String title;
    private DateForm date;
    private int id, checked, type;

    public DataTodo() {
        this.id = -1;
        this.title = "null";
        this.date = new DateForm("2000,1,1");
        this.checked = 0;
        this.type = 0;
    }

    public DataTodo(int id, String title, String date, int checked, int type) {
        this.id = id;
        this.title = title;
        this.date = new DateForm(date);
        this.checked = checked;
        this.type = type;
    }

    public DateForm getDate() {
        return date;
    }

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public int getChecked() {
        return checked;
    }

    public void setDate(DateForm date) {
        this.date = date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setChecked(int checked) {
        this.checked = checked;
    }
}
