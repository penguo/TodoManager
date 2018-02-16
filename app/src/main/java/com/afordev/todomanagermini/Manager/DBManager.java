package com.afordev.todomanagermini.Manager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.afordev.todomanagermini.SubItem.DataTodo;
import com.afordev.todomanagermini.SubItem.DateForm;

import java.util.ArrayList;

/**
 * Created by pengu on 2018-02-11.
 */

public class DBManager extends SQLiteOpenHelper {

    SQLiteDatabase db;
    Context context;

    public DBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Todo ( " +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Title TEXT, " +
                "Date TEXT," +
                "Tags TEXT," +
                "Checked Integer, " +
                "Type Integer); ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (newVersion) {
            case (1):
                break;
        }
    }

    public void reset() {
        db = getWritableDatabase();
        db.execSQL(" DROP TABLE Todo; ");
        onCreate(db);
        db.close();
    }

    public void insertTodo(DataTodo data) {
        db = getWritableDatabase();
        db.execSQL(" INSERT INTO Todo VALUES ( " +
                " null, " +
                "'" + data.getTitle() + "', " +
                "'" + data.getDate().toDBString() + "', " +
                "'" + data.getTags() + "', " +
                data.getChecked() + "," +
                data.getType() + ");");
        db.close();
    }

    public void updateTodo(DataTodo data) {
        db = getWritableDatabase();
        db.execSQL(" UPDATE Todo SET " +
                "Title = '" + data.getTitle() + "', " +
                "Date = '" + data.getDate().toDBString() + "', " +
                "Tags = '" + data.getTags() + "', " +
                "Checked = " + data.getChecked() + ", " +
                "Type = " + data.getType() + " " +
                "WHERE _id = " + data.getId() + " ; ");
        db.close();
    }

    public void deleteTodo(int id) {
        db = getWritableDatabase();
        db.execSQL("DELETE FROM Todo WHERE _id = " + id + ";");
        db.close();
    }

    public DataTodo getTodo(int id) {
        db = getReadableDatabase();
        DataTodo data = new DataTodo();
        Cursor cursor = db.rawQuery("SELECT * FROM Todo WHERE _id = " + id + ";", null);
        while (cursor.moveToNext()) {
            data = new DataTodo(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getInt(4),
                    cursor.getInt(5));
        }
        cursor.close();
        return data;
    }

    public ArrayList<DataTodo> getTodoList(DateForm date) {
        db = getReadableDatabase();
        ArrayList<DataTodo> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM Todo WHERE Date = '" + date.toDBString() + "';", null);
        while (cursor.moveToNext()) {
            DataTodo data = new DataTodo(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getInt(4),
                    cursor.getInt(5));
            list.add(data);
        }
        cursor.close();

        ArrayList<DataTodo> list0 = new ArrayList<>();
        ArrayList<DataTodo> list1 = new ArrayList<>();
        ArrayList<DataTodo> list2 = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            switch (list.get(i).getType()) {
                case (0):
                    list0.add(list.get(i));
                    break;
                case (1):
                    list1.add(list.get(i));
                    break;
                case (2):
                    list2.add(list.get(i));
                    break;
            }
        }
        list = new ArrayList<>();
        list.addAll(list2);
        list.addAll(list1);
        list.addAll(list0);
        list0 = new ArrayList<>();
        list1 = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            switch (list.get(i).getChecked()) {
                case (0):
                    list0.add(list.get(i));
                    break;
                case (1):
                    list1.add(list.get(i));
                    break;
            }
        }
        list = new ArrayList<>();
        list.addAll(list0);
        list.addAll(list1);
        return list;
    }
}