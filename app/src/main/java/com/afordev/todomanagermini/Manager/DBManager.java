package com.afordev.todomanagermini.Manager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.afordev.todomanagermini.SubItem.DataTodo;
import com.afordev.todomanagermini.SubItem.DateForm;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by pengu on 2018-02-11.
 */

public class DBManager extends SQLiteOpenHelper {

    private static DBManager instance;
    SQLiteDatabase db;
    Context context;

    public static DBManager getInstance(Context mContext) {
        if (instance == null) {
            instance = new DBManager(mContext, "todo.db", null, 1);
        } else {
            instance.setContext(mContext);
        }
        return instance;
    }

    private DBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    public void setContext(Context context) {
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
                "Checked = " + data.getChecked() + ", " +
                "Tags = '" + data.getTags() + "', " +
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
        Cursor cursor = db.rawQuery("SELECT * FROM Todo WHERE Date Like '" + date.toDBString() + "%';", null);
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

    public ArrayList<DataTodo> searchTodo(int select, String word) {
        db = getReadableDatabase();
        ArrayList<DataTodo> list = new ArrayList<>();
        String query;
        switch (select) {
            case (0):
                query = "SELECT * FROM Todo WHERE Title Like '%" + word + "%' OR Tags Like '%" + word + "%';";
                break;
            case (1):
                query = "SELECT * FROM Todo WHERE Title Like '%" + word + "%';";
                break;
            case (2):
                query = "SELECT * FROM Todo WHERE Tags Like '%" + word + "%';";
                break;
            default:
                query = "SELECT * FROM Todo Where Date = 'NOTHING FOUNDED';";
                break;
        }
        Cursor cursor = db.rawQuery(query, null);
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
        return list;
    }
}