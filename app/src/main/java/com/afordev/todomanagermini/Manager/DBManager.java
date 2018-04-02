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

/**
 * Created by pengu on 2018-02-11.
 */

public class DBManager extends SQLiteOpenHelper {

    private static DBManager instance;
    SQLiteDatabase db;
    Context context;

    private DBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    public static DBManager getInstance(Context mContext) {
        if (instance == null) {
            instance = new DBManager(mContext, "todo.db", null, 11);
        } else {
            instance.setContext(mContext);
        }
        return instance;
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
                "PatternTodoId Integer DEFAULT -1, " +
                "Title TEXT, " +
                "Tags TEXT," +
                "StartTime Integer, " +
                "DeadTime Integer DEFAULT -1, " +
                "CheckedTime Integer DEFAULT -1, " +
                "Importance Integer DEFAULT 0," +
                "Turn Integer DEFAULT 0," +
                "Option Text); ");
        db.execSQL("CREATE TABLE Pattern ( " +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "PatternTodoId Integer, " +
                "Memo TEXT, " +
                "StartTime Integer, " +
                "EndTime Integer," +
                "DayOfWeek Text," +
                "RecentlyTime Integer DEFAULT 0," +
                "Option Text); ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (newVersion) {
            case (11):
                break;
        }
    }

    private DataTodo getTodoWithCursor(Cursor cursor, DateForm timePivot) {
        DataTodo data = new DataTodo(cursor.getInt(0),
                cursor.getInt(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getLong(4),
                cursor.getLong(5),
                cursor.getLong(6),
                cursor.getInt(7),
                cursor.getString(8),
                cursor.getInt(9),
                timePivot);
        return data;
    }

    public void insertTodo(DataTodo data) {
        db = getWritableDatabase();
        db.execSQL(" INSERT INTO Todo VALUES ( " +
                " null, " +
                data.getPatternTodoId() + ", " +
                "'" + data.getTitle() + "', " +
                "'" + data.getTags() + "', " +
                data.getTimeStart().getTime() + ", " +
                data.getTimeDead().getTime() + ", " +
                data.getTimeChecked().getTime() + ", " +
                data.getImportance() + ", " +
                data.getTurn() + ", " +
                "'" + data.getOption() + "'); ");
        db.close();
    }

    public void updateTodo(DataTodo data) {
        db = getWritableDatabase();
        db.execSQL(" UPDATE Todo SET " +
                "Title = '" + data.getTitle() + "', " +
                "Tags = '" + data.getTags() + "', " +
                "StartTime = " + data.getTimeStart().getTime() + ", " +
                "DeadTime = " + data.getTimeDead().getTime() + ", " +
                "CheckedTime = " + data.getTimeChecked().getTime() + ", " +
                "Importance = " + data.getImportance() + ", " +
                "Turn = " + data.getTurn() + ", " +
                "Option = '" + data.getOption() + "' " +
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
        Cursor cursor = db.rawQuery("SELECT * FROM Todo WHERE _id = " + id + ";", null);
        cursor.moveToLast();
        DataTodo data = getTodoWithCursor(cursor, null);
        cursor.close();
        return data;
    }

    public ArrayList<DataTodo> getTodoList(final DateForm date) {
        db = getReadableDatabase();
        ArrayList<DataTodo> list = new ArrayList<>();
        DateForm temp = date.clone();
        temp.setHour(0);
        temp.setMinute(0);
        long timeL = temp.getTime();
        temp.addDate(1);
        long timeR = temp.getTime();
        Cursor cursor = db.rawQuery("SELECT * FROM Todo " +
                "WHERE (DeadTime > " + timeL + " OR DeadTime = -1) " +
                "AND StartTime < " + timeR + ";", null);
        while (cursor.moveToNext()) {
            list.add(getTodoWithCursor(cursor, date));
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
            if (list.get(i).getTimeChecked() == null) {
                list0.add(list.get(i));
            } else {
                list1.add(list.get(i));
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
        DateForm temp = date.clone();
        temp.setHour(0);
        temp.setMinute(0);
        long timeL = temp.getTime();
        temp.addDate(1);
        long timeR = temp.getTime();
        Cursor cursor = db.rawQuery("SELECT * FROM Todo " +
                "WHERE (DeadTime > " + timeL + " OR DeadTime = -1) " +
                "AND StartTime < " + timeR + " " +
                "AND (CheckedTime = -1 OR (CheckedTime >= " + timeL + "));", null);
        while (cursor.moveToNext()) {
            list.add(getTodoWithCursor(cursor, date));
        }
        cursor.close();

        int i;
        ArrayList<ArrayList<DataTodo>> arrayLists = new ArrayList<>();
        arrayLists.add(new ArrayList<DataTodo>()); // 매우 중요
        arrayLists.add(new ArrayList<DataTodo>()); // 미완료
        arrayLists.add(new ArrayList<DataTodo>()); // 퍼즐들 따로 정렬
        arrayLists.add(new ArrayList<DataTodo>()); // 완료
        for (i = 0; i < list.size(); i++) {
            if (list.get(i).getPatternTodoId() != -1) {
                arrayLists.get(2).add(list.get(i));
            } else if (!list.get(i).getTimeChecked().isNull()) {
                arrayLists.get(3).add(list.get(i));
            }else if(list.get(i).getImportance() >= 2){
                arrayLists.get(0).add(list.get(i));
            }else{
                arrayLists.get(1).add(list.get(i));
            }
        }
        for (i = 0; i < arrayLists.size(); i++) {
            sort_importance(arrayLists.get(i));
            sort_checked(arrayLists.get(i));
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

    private void sort_checked(ArrayList<DataTodo> list) {
        ArrayList<ArrayList<DataTodo>> arrayLists = new ArrayList<>();
        arrayLists.add(new ArrayList<DataTodo>());
        arrayLists.add(new ArrayList<DataTodo>());
        int i;
        for (i = 0; i < list.size(); i++) {
            if (list.get(i).getTimeChecked() == null) {
                arrayLists.get(0).add(list.get(i));
            } else {
                arrayLists.get(1).add(list.get(i));
            }
        }
        list.clear();
        list.addAll(arrayLists.get(0));
        list.addAll(arrayLists.get(1));
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
                query = "SELECT * FROM Todo;";
                break;
        }
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            list.add(getTodoWithCursor(cursor, null));
        }
        cursor.close();
        return list;
    }

    public ArrayList<DataTodo> getMissedHalfStarList(final DateForm date) {
        db = getReadableDatabase();
        ArrayList<DataTodo> list = new ArrayList<>();
        DateForm temp = new DateForm(date.getTime());
        temp.setHour(0);
        temp.setMinute(0);
        long secondL = temp.getTime();
        temp.addDate(1);
        long secondR = temp.getTime();
        Cursor cursor = db.rawQuery("SELECT * FROM Todo " +
                "WHERE Date >= " + secondL + " " +
                "AND Date < " + secondR + " " +
                "AND Type = 1 " +
                "AND Checked = 0;", null);
        while (cursor.moveToNext()) {
            list.add(getTodoWithCursor(cursor, date));
        }
        cursor.close();
        return list;
    }

    public ArrayList<DataTodo> getMissedStarList(final DateForm date) {
        db = getReadableDatabase();
        DateForm temp = new DateForm(date.getTime());
        temp.setHour(0);
        temp.setMinute(0);
        long second = temp.getTime();
        ArrayList<DataTodo> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM Todo " +
                "Where Date > 0 " +
                "AND Date < " + second + " " +
                "AND Type = 2 " +
                "AND Checked = 0;", null);
        while (cursor.moveToNext()) {
            list.add(getTodoWithCursor(cursor, date));
        }
        cursor.close();
        return list;
    }


    public void insertPattern(DataPattern dataPattern) {
        db = getWritableDatabase();

        DataTodo dataTodo = dataPattern.getPatternTodo();

        db.execSQL(" INSERT INTO Todo VALUES ( " +
                " null, " +
                "-2, " +
                "'" + dataTodo.getTitle() + "', " +
                "'" + dataTodo.getTags() + "', " +
                dataTodo.getTimeStart().getTime() + ", " +
                dataTodo.getTimeDead().getTime() + ", " +
                "-1, " +
                dataTodo.getImportance() + ", " +
                "'" + dataTodo.getOption() + "'); ");

        db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT _id FROM Todo WHERE PatternTodoId = -2;", null);
        cursor.moveToLast();
        int dataTodoId = cursor.getInt(0);
        cursor.close();

        db.execSQL(" INSERT INTO Pattern VALUES ( " +
                " null, " +
                dataTodoId + ", " +
                "'" + dataPattern.getMemo() + "', " +
                dataPattern.getTimeStart().getTime() + ", " +
                dataPattern.getTimeEnd().getTime() + ", " +
                "'" + dataPattern.getDayOfWeek() + "', " +
                dataPattern.getTimeRecently() + ", " +
                "'" + dataPattern.getOption() + ");");
        db.close();
    }

    public void updatePattern(DataPattern dataPattern) {
        db = getWritableDatabase();

        DataTodo dataTodo = dataPattern.getPatternTodo();
        db.execSQL(" UPDATE Todo SET " +
                "Title = '" + dataTodo.getTitle() + "', " +
                "Tags = '" + dataTodo.getTags() + "', " +
                "StartTime = " + dataTodo.getTimeStart().getTime() + ", " +
                "DeadTime = " + dataTodo.getTimeDead().getTime() + ", " +
                "CheckedTime = " + dataTodo.getTimeChecked().getTime() + ", " +
                "Importance = " + dataTodo.getImportance() + ", " +
                "Turn = " + dataTodo.getTurn() + ", " +
                "Option = '" + dataTodo.getOption() + "' " +
                "WHERE _id = " + dataTodo.getId() + " ; ");
        db.execSQL(" UPDATE Pattern SET " +
                "Memo = '" + dataPattern.getMemo() + "', " +
                "StartTime = " + dataPattern.getTimeStart().getTime() + ", " +
                "EndTime = " + dataPattern.getTimeEnd().getTime() + ", " +
                "DayOfWeek = '" + dataPattern.getDayOfWeek() + "', " +
                "RecentlyTime = " + dataPattern.getTimeRecently().getTime() + ", " +
                "Option = '" + dataPattern.getOption() + "' " +
                "WHERE _id = " + dataPattern.getId() + " ; ");
        db.close();
    }

    public void deletePattern(DataPattern data) {
        db = getWritableDatabase();
        db.execSQL("DELETE FROM Pattern WHERE _id = " + data.getId() + ";");
        db.execSQL("DELETE FROM Todo WHERE _id = " + data.getPatternTodo().getId() + ";");
        db.close();
    }

    public DataPattern getPattern(int id) {
        db = getReadableDatabase();
        DataPattern data = new DataPattern();
        Cursor cursor = db.rawQuery("SELECT * FROM Pattern WHERE _id = " + id + ";", null);
        while (cursor.moveToNext()) {
            data = new DataPattern(cursor.getInt(0),
                    new DataTodo(cursor.getInt(1), null),
                    cursor.getString(2),
                    cursor.getLong(3),
                    cursor.getLong(4),
                    cursor.getString(5),
                    cursor.getLong(6),
                    cursor.getString(7));
        }
        cursor = db.rawQuery("SELECT * FROM Todo WHERE _id = " + data.getPatternTodo().getId() + ";", null);
        while (cursor.moveToNext()) {
            data.setPatternTodo(getTodoWithCursor(cursor, null));
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
                    new DataTodo(cursor.getInt(1), null),
                    cursor.getString(2),
                    cursor.getLong(3),
                    cursor.getLong(4),
                    cursor.getString(5),
                    cursor.getLong(6),
                    cursor.getString(7));
            Cursor cursor2 = db.rawQuery("SELECT * FROM Todo WHERE _id = " + data.getPatternTodo().getId() + ";", null);
            while (cursor2.moveToNext()) {
                data.setPatternTodo(getTodoWithCursor(cursor2, null));
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
//        db = getReadableDatabase();
//
//        ArrayList<DataTodo> list = new ArrayList<>();
//        DateForm date = new DateForm(Calendar.getInstance());
//        date.setHour(0);
//        date.setMinute(0);
//        date.addDate(-1);
//        long secondL = date.getTime();
//        date.addDate(1);
//        long secondR = date.getTime();
//        Cursor cursor = db.rawQuery("SELECT * FROM Todo " +
//                "WHERE Date >= " + secondL + " " +
//                "AND Date < " + secondR + " " +
//                "AND Type = 2 " +
//                "AND Checked = 0;", null);
//        while (cursor.moveToNext()) {
//            DataTodo data = new DataTodo(cursor.getInt(0),
//                    cursor.getInt(1),
//                    cursor.getString(2),
//                    cursor.getString(3),
//                    cursor.getLong(4),
//                    cursor.getLong(5),
//                    cursor.getLong(6),
//                    cursor.getInt(7),
//                    cursor.getString(8));
//            list.add(data);
//        }
//        cursor.close();
//
//        db = getWritableDatabase();
//        for (int i = 0; i < list.size(); i++) {
//            db.execSQL(" UPDATE Todo SET " +
//                    "Checked = 2 " +
//                    "WHERE _id = " + list.get(i).getId() + " ; ");
//        }
//        db.close();
//        DataTodo temp;
//        for (int i = 0; i < list.size(); i++) {
//            temp = list.get(i);
//            temp.getDate().addDate(1);
//            temp.setTypeValue(temp.getTypeValue() + 1);
//            temp.setChecked(0);
//        }
//        db = getWritableDatabase();
//        for (int i = 0; i < list.size(); i++) {
//            temp = list.get(i);
//            long second = temp.getDate().getSecond();
//            long secondDeadline;
//            if (temp.getDateDeadline() != null) {
//                secondDeadline = temp.getDateDeadline().getSecond();
//            } else {
//                secondDeadline = -1;
//            }
//            db.execSQL(" INSERT INTO Todo VALUES ( " +
//                    " null, " +
//                    "'" + temp.getTitle() + "', " +
//                    second + ", " +
//                    "'" + temp.getTags() + "', " +
//                    temp.getChecked() + ", " +
//                    temp.getType() + ", " +
//                    temp.getIsTimeActivated() + ", " +
//                    temp.getImportance() + ", " +
//                    temp.getPatternId() + ", " +
//                    temp.getTypeValue() + ", " +
//                    secondDeadline + ");");
//        }
//        db.close();
//
//        db = getReadableDatabase();
//        SQLiteDatabase db2 = getWritableDatabase();
//        date = new DateForm(Calendar.getInstance());
//        date.setHour(0);
//        date.setMinute(0);
//        cursor = db.rawQuery("SELECT _id, TodoId FROM Pattern " +
//                "WHERE DateStart <= " + date.getTime() + " " +
//                "AND DateEnd > " + date.getTime() + " " +
//                "AND DayOfWeek Like '%" + (date.getDayofweek() - 1) + "%' " +
//                "AND RecentlyDate < " + date.getTime() + " ;", null);
//        while (cursor.moveToNext()) {
//            Cursor cursor2 = db.rawQuery("SELECT * FROM Todo WHERE _id = " + cursor.getInt(1) + ";", null);
//            cursor2.moveToLast();
//            temp = new DataTodo(cursor.getInt(0),
//                    cursor.getInt(1),
//                    cursor.getString(2),
//                    cursor.getString(3),
//                    cursor.getLong(4),
//                    cursor.getLong(5),
//                    cursor.getLong(6),
//                    cursor.getInt(7),
//                    cursor.getString(8));
//            temp.getDate().setYear(date.getYear());
//            temp.getDate().setMonth(date.getMonth());
//            temp.getDate().setDay(date.getDay());
//            temp.setTypeValue(temp.getTypeValue() + 1);
//            long secondDeadline;
//            if (temp.getDateDeadline() != null) {
//                secondDeadline = temp.getDateDeadline().getSecond();
//            } else {
//                secondDeadline = -1;
//            }
//            db2.execSQL(" INSERT INTO Todo VALUES ( " +
//                    " null, " +
//                    "'" + temp.getTitle() + "', " +
//                    temp.getDate().getSecond() + ", " +
//                    "'" + temp.getTags() + "', " +
//                    temp.getChecked() + ", " +
//                    temp.getType() + ", " +
//                    temp.getIsTimeActivated() + ", " +
//                    temp.getImportance() + ", " +
//                    temp.getPatternId() + ", " +
//                    temp.getTypeValue() + ", " +
//                    secondDeadline + ");");
//            db2.execSQL(" UPDATE Pattern SET " +
//                    "RecentlyDate = " + date.getTime() + " " +
//                    "WHERE _id = " + cursor.getInt(0) + " ; ");
//            db2.execSQL(" UPDATE Todo SET " +
//                    "TypeValue = " + temp.getTypeValue() + " " +
//                    "WHERE _id = " + cursor.getInt(1) + " ; ");
//            cursor2.close();
//        }
//        db.close();
//        db2.close();
//        cursor.close();
    }
}