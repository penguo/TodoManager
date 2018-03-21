package com.afordev.todomanagermini.Manager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.afordev.todomanagermini.SubItem.DataPattern;
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
            instance = new DBManager(mContext, "todo.db", null, 7);
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
        db.execSQL("DROP TABLE Pattern;");
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
                "IsTimeActivated Integer, " +
                "Importance Integer DEFAULT 0," +
                "PatternId Integer DEFAULT -1," +
                "AutoDelay Integer DEFAULT -1); ");
        db.execSQL("CREATE TABLE Pattern ( " +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Title TEXT, " +
                "DateStart Integer, " +
                "DateEnd Integer," +
                "DayOfWeek Text," +
                "TodoId Integer); ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (newVersion) {
            case (5):
                db.execSQL("ALTER TABLE Todo ADD COLUMN Importance Integer DEFAULT 0;");
                break;
            case (6):
                db.execSQL("ALTER TABLE Todo ADD COLUMN PatternId Integer DEFAULT -1;");
                db.execSQL("CREATE TABLE Pattern ( " +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "Title TEXT, " +
                        "DateStart Integer, " +
                        "DateEnd Integer," +
                        "DayOfWeek Text," +
                        "TodoId Integer); ");
                break;
            case (7):
                db.execSQL("ALTER TABLE Todo ADD COLUMN AutoDelay Integer DEFAULT -1;");
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
                data.getIsTimeActivated() + ", " +
                data.getImportance() + ", " +
                data.getPatternId() + ", " +
                data.getAutoDelay() + ");");
        db.close();
    }

    public void updateTodo(DataTodo data) {
        db = getWritableDatabase();
        long second = data.getDate().getSecond();
        db.execSQL(" UPDATE Todo SET " +
                "Title = '" + data.getTitle() + "', " +
                "Date = " + second + ", " +
                "Tags = '" + data.getTags() + "', " +
                "Checked = " + data.getChecked() + ", " +
                "Type = " + data.getType() + ", " +
                "IsTimeActivated = " + data.getIsTimeActivated() + ", " +
                "Importance = " + data.getImportance() + ", " +
                "PatternId = " + data.getPatternId() + ", " +
                "AutoDelay = " + data.getAutoDelay() + " " +
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
                    cursor.getInt(6),
                    cursor.getInt(7),
                    cursor.getInt(8),
                    cursor.getInt(9));
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
                    cursor.getInt(6),
                    cursor.getInt(7),
                    cursor.getInt(8),
                    cursor.getInt(9));
            list.add(data);
        }
        cursor.close();

        ArrayList<DataTodo> list0 = new ArrayList<>();
        ArrayList<DataTodo> list1 = new ArrayList<>();
        ArrayList<DataTodo> list2 = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            switch (list.get(i).getImportance()) {
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

    public ArrayList<ArrayList<DataTodo>> getSortedList(final DateForm date) {
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
                    cursor.getInt(6),
                    cursor.getInt(7),
                    cursor.getInt(8),
                    cursor.getInt(9));
            list.add(data);
        }
        cursor.close();

        int i;
        ArrayList<ArrayList<DataTodo>> arrayLists = new ArrayList<>();
        arrayLists.add(new ArrayList<DataTodo>());
        arrayLists.add(new ArrayList<DataTodo>());
        arrayLists.add(new ArrayList<DataTodo>());
        for (i = 0; i < list.size(); i++) {
            if (list.get(i).getImportance() >= 2 && list.get(i).getChecked() == 0) { // 매우 중요
                arrayLists.get(0).add(list.get(i));
            } else if (list.get(i).getChecked() == 0) { // 미완료
                arrayLists.get(1).add(list.get(i));
            } else if (list.get(i).getChecked() >= 1) { // 완료, 딜레이(2) 등
                arrayLists.get(2).add(list.get(i));
            } else {
                arrayLists.get(2).add(list.get(i));
            }
        }
        for (i = 0; i < arrayLists.size(); i++) {
            sort_importance(arrayLists.get(i));
        }
        return arrayLists;
    }

    private void sort_importance(ArrayList<DataTodo> list) {
        ArrayList<ArrayList<DataTodo>> arrayLists = new ArrayList<>();
        arrayLists.add(new ArrayList<DataTodo>());
        arrayLists.add(new ArrayList<DataTodo>());
        arrayLists.add(new ArrayList<DataTodo>());
        int i;
        for (i = 0; i < list.size(); i++) {
            switch (list.get(i).getImportance()) {
                case (2):
                    arrayLists.get(2).add(list.get(i));
                    break;
                case (1):
                    arrayLists.get(1).add(list.get(i));
                    break;
                case (0):
                default:
                    arrayLists.get(0).add(list.get(i));
                    break;
            }
        }
        list.clear();
        list.addAll(arrayLists.get(2));
        list.addAll(arrayLists.get(1));
        list.addAll(arrayLists.get(0));
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
                    cursor.getInt(6),
                    cursor.getInt(7),
                    cursor.getInt(8),
                    cursor.getInt(9));
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
                    cursor.getInt(6),
                    cursor.getInt(7),
                    cursor.getInt(8),
                    cursor.getInt(9));
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
                "Where Date > 0 " +
                "AND Date < " + second + " " +
                "AND Type = 2 " +
                "AND Checked = 0;", null);
        while (cursor.moveToNext()) {
            DataTodo data = new DataTodo(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getLong(2),
                    cursor.getString(3),
                    cursor.getInt(4),
                    cursor.getInt(5),
                    cursor.getInt(6),
                    cursor.getInt(7),
                    cursor.getInt(8),
                    cursor.getInt(9));
            list.add(data);
        }
        cursor.close();
        return list;
    }


    public void insertPattern(DataPattern dataPattern) {
        db = getWritableDatabase();

        DataTodo dataTodo = dataPattern.getDataTodo();

        db.execSQL(" INSERT INTO Todo VALUES ( " +
                " null, " +
                "'" + dataTodo.getTitle() + "', " +
                "-1, " +
                "'" + dataTodo.getTags() + "', " +
                dataTodo.getChecked() + ", " +
                dataTodo.getImportance() + ", " +
                dataTodo.getType() + ", " +
                dataTodo.getIsTimeActivated() + ", " +
                dataTodo.getPatternId() + ", " +
                dataTodo.getAutoDelay() + ");");

        db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Todo ;", null);
        cursor.moveToLast();
        int dataTodoId = cursor.getInt(0);

        long secondStart = dataPattern.getDateStart().getSecond();
        long secondEnd = dataPattern.getDateEnd().getSecond();
        db.execSQL(" INSERT INTO Pattern VALUES ( " +
                " null, " +
                "'" + dataPattern.getTitle() + "', " +
                secondStart + ", " +
                secondEnd + ", " +
                "'" + dataPattern.getDow() + "', " +
                dataTodoId + ");");
        db.close();
    }

    public void updatePattern(DataPattern dataPattern) {
        db = getWritableDatabase();

        DataTodo dataTodo = dataPattern.getDataTodo();
        db.execSQL(" UPDATE Todo SET " +
                "Title = '" + dataTodo.getTitle() + "', " +
                "Date = -1, " +
                "Checked = " + dataTodo.getChecked() + ", " +
                "Tags = '" + dataTodo.getTags() + "', " +
                "Importance = " + dataTodo.getImportance() + ", " +
                "Type = " + dataTodo.getType() + ", " +
                "IsTimeActivated = " + dataTodo.getIsTimeActivated() + ", " +
                "AutoDelay = " + dataTodo.getAutoDelay() + " " +
                "WHERE _id = " + dataTodo.getId() + " ; ");
        db.execSQL(" UPDATE Pattern SET " +
                "Title = '" + dataPattern.getTitle() + "', " +
                "DateStart = " + dataPattern.getDateStart().getSecond() + ", " +
                "DateEnd = " + dataPattern.getDateEnd().getSecond() + ", " +
                "DayOfWeek = '" + dataPattern.getDow() + "' " +
                "WHERE _id = " + dataPattern.getId() + " ; ");
        db.close();
    }

    public void deletePattern(DataPattern data) {
        db = getWritableDatabase();
        db.execSQL("DELETE FROM Pattern WHERE _id = " + data.getId() + ";");
        db.execSQL("DELETE FROM Todo WHERE _id = " + data.getDataTodo().getId() + ";");
        db.close();
    }

    public DataPattern getPattern(int id) {
        db = getReadableDatabase();
        DataPattern data = new DataPattern();
        Cursor cursor = db.rawQuery("SELECT * FROM Pattern WHERE _id = " + id + ";", null);
        while (cursor.moveToNext()) {
            data = new DataPattern(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getLong(2),
                    cursor.getLong(3),
                    cursor.getString(4),
                    new DataTodo(cursor.getInt(5)));
        }
        cursor = db.rawQuery("SELECT * FROM Todo WHERE _id = " + data.getDataTodo().getId() + ";", null);
        while (cursor.moveToNext()) {
            DataTodo dataTodo = new DataTodo(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getLong(2),
                    cursor.getString(3),
                    cursor.getInt(4),
                    cursor.getInt(5),
                    cursor.getInt(6),
                    cursor.getInt(7),
                    cursor.getInt(8),
                    cursor.getInt(9));
            data.setDataTodo(dataTodo);
        }
        cursor.close();
        return data;
    }

    public ArrayList<DataPattern> getPatternList() {
        db = getReadableDatabase();
        ArrayList<DataPattern> dataList = new ArrayList<>();
        DataPattern data;
        Cursor cursor = db.rawQuery("SELECT * FROM Pattern;", null);
        while (cursor.moveToNext()) {
            data = new DataPattern(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getLong(2),
                    cursor.getLong(3),
                    cursor.getString(4),
                    new DataTodo(cursor.getInt(5)));
            Cursor cursor2 = db.rawQuery("SELECT * FROM Todo WHERE _id = " + data.getDataTodo().getId() + ";", null);
            while (cursor2.moveToNext()) {
                DataTodo dataTodo = new DataTodo(cursor2.getInt(0),
                        cursor2.getString(1),
                        cursor2.getLong(2),
                        cursor2.getString(3),
                        cursor2.getInt(4),
                        cursor2.getInt(5),
                        cursor2.getInt(6),
                        cursor2.getInt(7),
                        cursor2.getInt(8),
                        cursor2.getInt(9));
                data.setDataTodo(dataTodo);
            }
            cursor2.close();
            dataList.add(data);
        }
        cursor.close();
        return dataList;
    }


    public ArrayList<String> getTagList() {
        db = getReadableDatabase();
        ArrayList<String> list = new ArrayList<>();
        String[] strings;
        int i;
        Cursor cursor = db.rawQuery("SELECT Tags FROM Todo ;", null);
        while (cursor.moveToNext()) {
            strings = cursor.getString(0).split(",");
            for (i = 0; i < strings.length; i++) {
                if (!list.contains(strings[i])) {
                    list.add(strings[i]);
                }
            }
            if (list.contains("")) {
                list.remove("");
            }
        }
        cursor.close();
        return list;
    }


    public void checkToday() {
        db = getReadableDatabase();

        ArrayList<DataTodo> list = new ArrayList<>();
        DateForm date = new DateForm(Calendar.getInstance());
        date.setHour(0);
        date.setMinute(0);
        date.addDate(-1);
        long secondL = date.getSecond();
        date.addDate(1);
        long secondR = date.getSecond();
        Cursor cursor = db.rawQuery("SELECT * FROM Todo " +
                "WHERE Date >= " + secondL + " " +
                "AND Date < " + secondR + " " +
                "AND AutoDelay > -1 " +
                "AND Checked = 0;", null);
        while (cursor.moveToNext()) {
            DataTodo data = new DataTodo(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getLong(2),
                    cursor.getString(3),
                    cursor.getInt(4),
                    cursor.getInt(5),
                    cursor.getInt(6),
                    cursor.getInt(7),
                    cursor.getInt(8),
                    cursor.getInt(9));
            list.add(data);
        }
        cursor.close();

        db = getWritableDatabase();
        for (int i = 0; i < list.size(); i++) {
            db.execSQL(" UPDATE Todo SET " +
                    "Checked = 2 " +
                    "WHERE _id = " + list.get(i).getId() + " ; ");
        }
        db.close();
        DataTodo temp;
        for (int i = 0; i < list.size(); i++) {
            temp = list.get(i);
            temp.getDate().addDate(1);
            temp.setAutoDelay(temp.getAutoDelay() + 1);
            temp.setType(2);
            temp.setChecked(0);
        }
        db = getWritableDatabase();
        for (int i = 0; i < list.size(); i++) {
            temp = list.get(i);
            long second = temp.getDate().getSecond();
            db.execSQL(" INSERT INTO Todo VALUES ( " +
                    " null, " +
                    "'" + temp.getTitle() + "', " +
                    second + ", " +
                    "'" + temp.getTags() + "', " +
                    temp.getChecked() + ", " +
                    temp.getType() + ", " +
                    temp.getIsTimeActivated() + ", " +
                    temp.getImportance() + ", " +
                    temp.getPatternId() + ", " +
                    temp.getAutoDelay() + ");");
        }
        db.close();
    }
}