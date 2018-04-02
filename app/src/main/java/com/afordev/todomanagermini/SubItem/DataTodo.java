package com.afordev.todomanagermini.SubItem;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by penguo on 2018-02-11.
 */

public class DataTodo implements Parcelable, Cloneable {
    private int id, patternTodoId, importance, turn;
    private DateForm timeStart, timeDead, timeChecked, timePivot;
    private String title, tags, option;

    public DataTodo(int id, DateForm date) {
        this.id = id;
        this.patternTodoId = -1;
        this.title = "";
        this.tags = "";
        DateForm tempDate;
        if(date == null){
            tempDate = new DateForm(Calendar.getInstance());
        }else {
            tempDate = date.clone();
        }
        tempDate.setHour(0);
        tempDate.setMinute(0);
        this.timeStart = tempDate.clone();
        tempDate.addDate(1);
        this.timeDead = tempDate.clone();
        this.timeChecked = null;
        this.importance = 0;
        this.option = "";
        this.turn = 0;
        this.timePivot = null;
        setOption();
    }

    public DataTodo(int id,
                    int patternTodoId,
                    String title,
                    String tags,
                    long timeStart,
                    long timeDead,
                    long timeChecked,
                    int importance,
                    String option,
                    int turn,
                    DateForm timePivot) {
        this.id = id;
        this.patternTodoId = patternTodoId;
        this.title = title;
        this.tags = tags;
        this.timeStart = new DateForm(timeStart);
        this.timeDead = new DateForm(timeDead);
        this.timeChecked = new DateForm(timeChecked);
        this.importance = importance;
        this.option = option;
        this.turn = turn;
        this.timePivot = timePivot;
        setOption();
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

    protected DataTodo(Parcel in) {
        id = in.readInt();
        patternTodoId = in.readInt();
        title = in.readString();
        tags = in.readString();
        timeStart = in.readParcelable(DateForm.class.getClassLoader());
        timeDead = in.readParcelable(DateForm.class.getClassLoader());
        timeChecked = in.readParcelable(DateForm.class.getClassLoader());
        importance = in.readInt();
        option = in.readString();
        turn = in.readInt();
        timePivot = in.readParcelable(DateForm.class.getClassLoader());
        setOption();
    }

    private void setOption() {
        if (option.equals("")) {
            return;
        }
        String[] list = option.split(",");
        for (int i = 0; i < list.length; i++) {
            String[] st = list[i].split(":");
            switch(st[0]){
                case(""):
                    break;
            }
        }
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
        parcel.writeInt(id);
        parcel.writeInt(patternTodoId);
        parcel.writeString(title);
        parcel.writeString(tags);
        parcel.writeParcelable(timeStart, i);
        parcel.writeParcelable(timeDead, i);
        parcel.writeParcelable(timeChecked, i);
        parcel.writeInt(importance);
        parcel.writeString(option);
        parcel.writeInt(turn);
        parcel.writeParcelable(timePivot, i);
    }

    public DataTodo clone() {
        DataTodo obj = null;
        try {
            obj = (DataTodo) super.clone();
        } catch (Exception e) {
        }
        return obj;
    }

    public String getOption() {
        return option;
    }

    public DateForm getTimeStart() {
        if (timeStart != null) {
            return timeStart;
        } else {
            return new DateForm(-1);
        }
    }

    public DateForm getTimeChecked() {
        if (timeChecked != null) {
            return timeChecked;
        } else {
            return new DateForm(-1);
        }
    }

    public DateForm getTimeDead() {
        if (timeDead != null) {
            return timeDead;
        } else {
            return new DateForm(-1);
        }
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getImportance() {
        return importance;
    }

    public String getTags() {
        return tags;
    }

    public int getPatternTodoId() {
        return patternTodoId;
    }

    public void setTimeStart(DateForm timeStart) {
        this.timeStart = timeStart;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public void setPatternTodoId(int patternTodoId) {
        this.patternTodoId = patternTodoId;
    }

    public void setTimeChecked(DateForm timeChecked) {
        this.timeChecked = timeChecked;
    }

    public void setTimeDead(DateForm timeDead) {
        this.timeDead = timeDead;
    }

    public DateForm getTimePivot() {
        return timePivot;
    }

    public void setTimePivot(DateForm timePivot) {
        this.timePivot = timePivot;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

}
