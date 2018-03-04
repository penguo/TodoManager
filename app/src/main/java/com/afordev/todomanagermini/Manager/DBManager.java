package com.afordev.todomanagermini.Manager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

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
            instance = new DBManager(mContext, "todo.db", null, 4);
        } else {
            instance.setContext(mContext);
        }
        return instance;
    }

    private DBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    public void resetDB() {
        db = getWritableDatabase();
        db.execSQL("DROP TABLE Todo;");
        onCreate(db);
        db.close();
        Toast.makeText(context, "DB가 초기화되었습니다.", Toast.LENGTH_SHORT).show();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Todo ( " +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Title TEXT, " +
                "Date Integer," +
                "Tags TEXT," +
                "Checked Integer, " +
                "Type Integer, " +
                "IsTimeActivated Integer); ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (newVersion) {
            case (4):
                onCreate(db);
                break;
        }
    }

    public void insertTodo(DataTodo data) {
        db = getWritableDatabase();
        long second = data.getDate().getSecond();
        db.execSQL(" INSERT INTO Todo VALUES ( " +
                " null, " +
                "'" + data.getTitle() + "', " +
                second + ", " +
                "'" + data.getTags() + "', " +
                data.getChecked() + ", " +
                data.getType() + ", " +
                data.getIsTimeActivated() + ");");
        db.close();
    }

    public void updateTodo(DataTodo data) {
        db = getWritableDatabase();
        long second = data.getDate().getSecond();
        db.execSQL(" UPDATE Todo SET " +
                "Title = '" + data.getTitle() + "', " +
                "Date = " + second + ", " +
                "Checked = " + data.getChecked() + ", " +
                "Tags = '" + data.getTags() + "', " +
                "Type = " + data.getType() + ", " +
                "IsTimeActivated = " + data.getIsTimeActivated() + " " +
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
                    cursor.getLong(2),
                    cursor.getString(3),
                    cursor.getInt(4),
                    cursor.getInt(5),
                    cursor.getInt(6));
        }
        cursor.close();
        return data;
    }

    public ArrayList<DataTodo> getTodoList(final DateForm date) {
        db = getReadableDatabase();
        ArrayList<DataTodo> list = new ArrayList<>();
        DateForm temp = new DateForm(date.getSecond());
        temp.setHour(0);
        temp.setMinute(0);
        long secondL = temp.getSecond();
        temp.addDate(1);
        long secondR = temp.getSecond();
        Cursor cursor = db.rawQuery("SELECT * FROM Todo " +
                "WHERE Date >= " + secondL + " " +
                "AND Date < " + secondR + ";", null);
        while (cursor.moveToNext()) {
            DataTodo data = new DataTodo(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getLong(2),
                    cursor.getString(3),
                    cursor.getInt(4),
                    cursor.getInt(5),
                    cursor.getInt(6));
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
                    cursor.getLong(2),
                    cursor.getString(3),
                    cursor.getInt(4),
                    cursor.getInt(5),
                    cursor.getInt(6));
            list.add(data);
        }
        cursor.close();
        return list;
    }

    public ArrayList<DataTodo> getMissedHalfStarList(final DateForm date) {
        db = getReadableDatabase();
        ArrayList<DataTodo> list = new ArrayList<>();
        DateForm temp = new DateForm(date.getSecond());
        temp.setHour(0);
        temp.setMinute(0);
        long secondL = temp.getSecond();
        temp.addDate(1);
        long secondR = temp.getSecond();
        Cursor cursor = db.rawQuery("SELECT * FROM Todo " +
                "WHERE Date >= " + secondL + " " +
                "AND Date < " + secondR + " " +
                "AND Type = 1 " +
                "AND Checked = 0;", null);
        while (cursor.moveToNext()) {
            DataTodo data = new DataTodo(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getLong(2),
                    cursor.getString(3),
                    cursor.getInt(4),
                    cursor.getInt(5),
                    cursor.getInt(6));
            list.add(data);
        }
        cursor.close();
        return list;
    }

    public ArrayList<DataTodo> getMissedStarList(final DateForm date) {
        db = getReadableDatabase();
        DateForm temp = new DateForm(date.getSecond());
        temp.setHour(0);
        temp.setMinute(0);
        long second = temp.getSecond();
        ArrayList<DataTodo> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM Todo " +
                "Where Date < " + second + " " +
                "AND Type = 2 " +
                "AND Checked = 0;", null);
        while (cursor.moveToNext()) {
            DataTodo data = new DataTodo(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getLong(2),
                    cursor.getString(3),
                    cursor.getInt(4),
                    cursor.getInt(5),
                    cursor.getInt(6));
            list.add(data);
        }
        cursor.close();
        return list;
    }
}