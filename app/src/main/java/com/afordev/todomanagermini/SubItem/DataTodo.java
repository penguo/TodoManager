package com.afordev.todomanagermini.SubItem;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by penguo on 2018-02-11.
 */

public class DataTodo implements Parcelable {
    private String title, tags;
    private DateForm date;
    private int id, checked, importance, type, isTimeActivated;

    public DataTodo() {
        this.id = -1;
        this.title = "";
        this.date = new DateForm(Calendar.getInstance());
        this.tags = "";
        this.checked = 0;
        this.importance = 0;
        this.type = 0;
        this.isTimeActivated = 0;
    }

    public DataTodo(DateForm date) {
        this.id = -1;
        this.title = "";
        this.date = new DateForm(date.getSecond());
        this.date.setHour(0);
        this.date.setMinute(0);
        this.tags = "";
        this.checked = 0;
        this.type = 0;
        this.isTimeActivated = 0;
        this.importance = 0;
    }

    public DataTodo(int id, String title, long second, String tags, int checked, int type, int isTimeActivited, int importance) {
        this.id = id;
        this.title = title;
        this.date = new DateForm(second);
        this.tags = tags;
        this.checked = checked;
        this.type = type;
        this.isTimeActivated = isTimeActivited;
        this.importance = importance;
    }

    protected DataTodo(Parcel in) {
        title = in.readString();
        tags = in.readString();
        date = in.readParcelable(DateForm.class.getClassLoader());
        id = in.readInt();
        checked = in.readInt();
        type = in.readInt();
        isTimeActivated = in.readInt();
        importance = in.readInt();
    }

    public static final Creator<DataTodo> CREATOR = new Creator<DataTodo>() {
        @Override
        public DataTodo createFromParcel(Parcel in) {
            return new DataTodo(in);
        }

        @Override
        public DataTodo[] newArray(int size) {
            return new DataTodo[size];
        }
    };

    public int getIsTimeActivated() {
        return isTimeActivated;
    }

    public DateForm getDate() {
        return date;
    }

    public int getId() {
        return id;
    }

    public int getImportance() {
        return importance;
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

    public String getTags() {
        return tags;
    }

    public ArrayList<String> getTagList() {
        ArrayList<String> list = new ArrayList<>();
        String[] st = tags.split(",");
        for (int i = 0; i < st.length; i++) {
            list.add(st[i]);
        }
        if (list.contains("")) {
            list.remove("");
        }
        return list;
    }

    public void setIsTimeActivated(int isTimeActivated) {
        this.isTimeActivated = isTimeActivated;
    }

    public void setDate(DateForm date) {
        this.date = date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setImportance(int importance) {
        this.importance = importance;
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

    public void setTags(String tags) {
        this.tags = tags;
    }

    public void setTagList(ArrayList<String> list) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i < list.size() - 1) {
                sb.append(",");
            }
        }
        this.tags = sb.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(tags);
        parcel.writeParcelable(date, i);
        parcel.writeInt(id);
        parcel.writeInt(checked);
        parcel.writeInt(type);
        parcel.writeInt(isTimeActivated);
        parcel.writeInt(importance);
    }

}
