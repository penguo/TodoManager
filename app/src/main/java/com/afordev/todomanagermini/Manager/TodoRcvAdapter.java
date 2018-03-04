package com.afordev.todomanagermini.Manager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.afordev.todomanagermini.MainActivity;
import com.afordev.todomanagermini.R;
import com.afordev.todomanagermini.SubItem.DataTodo;
import com.afordev.todomanagermini.SubItem.DateForm;

import java.util.ArrayList;
import java.util.Calendar;

public class TodoRcvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private DBManager dbManager;
    private ArrayList<DataTodo> dataList;
    private DateForm date;
    private boolean isToday, isAutoSort;
    public DataTodo temp;
    public int itemExpandPosition = -1, editPosition = -1;
    private InputMethodManager imm;

    public TodoRcvAdapter(Context mContext, DBManager dbManager, DateForm date) {
        this.mContext = mContext;
        this.dbManager = dbManager;
        this.date = date;
        this.dataList = dbManager.getTodoList(date);
        if (date.compareTo(new DateForm(Calendar.getInstance())) == 0) {
            isToday = true;
        } else {
            isToday = false;
        }
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        isAutoSort = prefs.getBoolean(Manager.PREF_AUTO_SORT, true);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (editPosition == position) {
            return 1;
        } else {
            return 0;
        }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case (1):
                VHEdit mVHEdit = new VHEdit(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_edit, parent, false));
                return mVHEdit;
            case (0):
            default:
                VHItem mVHItem = new VHItem(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo2, parent, false));
                return mVHItem;
        }
    }

    class VHItem extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvTitle, tvTags;
        private LinearLayout layout;
        private ImageView ivCheck;

        public VHItem(final View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.item_todo_layout);
            tvTitle = itemView.findViewById(R.id.item_todo_tv_title);
            tvTags = itemView.findViewById(R.id.item_todo_tv_tag);
            ivCheck = itemView.findViewById(R.id.item_todo_iv_check);
            layout.setOnClickListener(this);
            layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (editPosition == -1) {
                        temp = dataList.get(getAdapterPosition());
                        editPosition = getAdapterPosition();
                        notifyItemChanged(getAdapterPosition());
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    } else {
                        Toast.makeText(mContext, "먼저 항목 수정을 마쳐야 합니다.", Toast.LENGTH_SHORT).show();
                    }
                    return false;
                }
            });
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case (R.id.item_todo_layout):
                    if (editPosition == -1) {
                        if (isToday) {
                            temp = dataList.get(getAdapterPosition());
                            editPosition = getAdapterPosition();
                            switch (temp.getChecked()) {
                                case (0):
                                    temp.setChecked(1);
                                    break;
                                case (1):
                                    temp.setChecked(0);
                                    break;
                                default:
                                    Toast.makeText(mContext, "ERROR.", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            dbManager.updateTodo(temp);
                            notifyItemChanged(getAdapterPosition());
                            if (isAutoSort) {
                                notifyItemMoved(getAdapterPosition(), getSortedPosition(getAdapterPosition()));
                            }
                            temp = null;
                            editPosition = -1;
                        } else {
                            int i = itemExpandPosition;
                            if (itemExpandPosition == getAdapterPosition()) {
                                itemExpandPosition = -1;
                                notifyItemChanged(getAdapterPosition());
                            } else {
                                itemExpandPosition = getAdapterPosition();
                                if (i >= 0) {
                                    notifyItemChanged(i);
                                }
                                notifyItemChanged(itemExpandPosition);
                            }
                        }
                    } else {
                        Toast.makeText(mContext, "먼저 항목 수정을 마쳐야 합니다.", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    }

    class VHEdit extends RecyclerView.ViewHolder implements View.OnClickListener {
        private EditText etTitle;
        private TextView tvTags;
        private ImageView ivEditLeft, ivEditSave;
        private Button btnDelete, btnTag, btnTime, btnCancel;
        private ConstraintLayout layoutNew;

        public VHEdit(final View itemView) {
            super(itemView);
            layoutNew = itemView.findViewById(R.id.item_todo_layout_new);
            layoutNew.setVisibility(View.GONE);
            etTitle = itemView.findViewById(R.id.item_todo_et_edit_title);
            tvTags = itemView.findViewById(R.id.item_todo_tv_edit_tag);
            ivEditLeft = itemView.findViewById(R.id.item_todo_iv_edit_left);
            ivEditSave = itemView.findViewById(R.id.item_todo_iv_edit_save);
            btnDelete = itemView.findViewById(R.id.item_todo_btn_delete);
            btnTag = itemView.findViewById(R.id.item_todo_btn_tag);
            btnTime = itemView.findViewById(R.id.item_todo_btn_time);
            btnCancel = itemView.findViewById(R.id.item_todo_btn_cancel);
            ivEditLeft.setOnClickListener(this);
            ivEditSave.setOnClickListener(this);
            btnDelete.setOnClickListener(this);
            btnCancel.setOnClickListener(this);
            btnTime.setOnClickListener(this);
            btnTag.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
            switch (view.getId()) {
                case (R.id.item_todo_iv_edit_left):
                    Toast.makeText(mContext, "Next Version...", Toast.LENGTH_SHORT).show();
                    break;

                case (R.id.item_todo_iv_edit_save):
                    temp.setTitle(etTitle.getText().toString());
                    dbManager.updateTodo(temp);
                    temp = null;
                    editPosition = -1;
                    notifyItemChanged(getAdapterPosition());
                    imm.hideSoftInputFromWindow(etTitle.getWindowToken(), 0);
                    ((MainActivity) mContext).setViewBottom(true);
                    break;

                case (R.id.item_todo_btn_delete):
                    dialog.setMessage("정말로 삭제하시겠습니까? 모든 내용이 삭제되며 복구되지 않습니다.");
                    dialog.setCancelable(true);
                    dialog.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            imm.hideSoftInputFromWindow(etTitle.getWindowToken(), 0);
                            dbManager.deleteTodo(temp.getId());
                            temp = null;
                            editPosition = -1;
                            ((MainActivity) mContext).setViewBottom(true);
                            removeItemView(getAdapterPosition());
                        }
                    });
                    dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                    break;

                case (R.id.item_todo_btn_cancel):
                    dialog.setMessage("작업 중인 정보가 사라집니다.");
                    dialog.setCancelable(true);
                    dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            temp = null;
                            editPosition = -1;
                            notifyItemChanged(getAdapterPosition());
                            imm.hideSoftInputFromWindow(etTitle.getWindowToken(), 0);
                            ((MainActivity) mContext).setViewBottom(true);
                            dialog.dismiss();
                        }
                    });
                    dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                    break;

                case (R.id.item_todo_btn_time):
                    timeSelectOption();
                    break;

                case (R.id.item_todo_btn_tag):
                    Manager.showAddTag((MainActivity) mContext, temp, TodoRcvAdapter.this, getAdapterPosition());
                    break;
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        DataTodo data;
        if (holder instanceof VHItem) {
            data = dataList.get(position);
            ((VHItem) holder).tvTitle.setText(data.getTitle());
            if (data.getIsTimeActivated() == 0 && data.getTags().equals("")) {
                ((VHItem) holder).tvTags.setVisibility(View.GONE);
            } else {
                ((VHItem) holder).tvTags.setVisibility(View.VISIBLE);
                StringBuffer sb = new StringBuffer();
                if (data.getIsTimeActivated() == 1) {
                    sb.append(Manager.getTimeForm(data.getDate()));
                }
                if (data.getIsTimeActivated() == 1 && !data.getTags().equals("")) {
                    sb.append(", ");
                }
                if (!data.getTags().equals("")) {
                    ArrayList<String> st = data.getTagList();
                    for (int i = 0; i < st.size(); i++) {
                        sb.append("#" + st.get(i) + " ");
                    }
                }
                ((VHItem) holder).tvTags.setText(sb.toString());
            }
            switch (data.getChecked()) {
                case (0):
                    ((VHItem) holder).ivCheck.setImageResource(R.drawable.ic_check_false);
                    ((VHItem) holder).tvTitle.setPaintFlags(((VHItem) holder).tvTitle.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                    ((VHItem) holder).layout.setAlpha(1);
                    break;
                case (1):
                    ((VHItem) holder).ivCheck.setImageResource(R.drawable.ic_check_true);
                    ((VHItem) holder).tvTitle.setPaintFlags(((VHItem) holder).tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    ((VHItem) holder).layout.setBackgroundResource(R.drawable.btn_basic);
                    ((VHItem) holder).layout.setAlpha((float) 0.5);
                    break;
                default:
                    ((VHItem) holder).ivCheck.setImageResource(R.drawable.ic_error);
                    break;
            }
        } else {
            ((MainActivity) mContext).setViewBottom(false);
            data = temp;
            ((VHEdit) holder).etTitle.setText(data.getTitle());
            ((VHEdit) holder).etTitle.requestFocus();
            if (data.getIsTimeActivated() == 0 && data.getTags().equals("")) {
                ((VHEdit) holder).tvTags.setVisibility(View.GONE);
            } else {
                ((VHEdit) holder).tvTags.setVisibility(View.VISIBLE);
                StringBuffer sb = new StringBuffer();
                if (data.getIsTimeActivated() == 1) {
                    sb.append(Manager.getTimeForm(data.getDate()));
                }
                if (data.getIsTimeActivated() == 1 && !data.getTags().equals("")) {
                    sb.append(", ");
                }
                if (!data.getTags().equals("")) {
                    ArrayList<String> st = data.getTagList();
                    for (int i = 0; i < st.size(); i++) {
                        sb.append("#" + st.get(i) + " ");
                    }
                }
                ((VHEdit) holder).tvTags.setText(sb.toString());
            }
        }
    }

    private void removeItemView(int position) {
        dbManager.deleteTodo(dataList.get(position).getId());
        dataList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, dataList.size()); // 지워진 만큼 다시 채워넣기.
    }

    public void onRefresh() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        isAutoSort = prefs.getBoolean(Manager.PREF_AUTO_SORT, true);
        this.dataList = dbManager.getTodoList(date);
        notifyDataSetChanged();
    }

    public int getSortedPosition(int position) {
        ArrayList<DataTodo> list = dataList;

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

        for (int i = 0; i < list.size(); i++) {
            if (dataList.get(position).getId() == list.get(i).getId()) {
                dataList = list;
                return i;
            }
        }
        return 0;
    }

    public void timeSelectOption() {
        TimePickerDialog dialog = new TimePickerDialog(mContext, listenerTime,
                temp.getDate().getHour(),
                temp.getDate().getMinute(), false);

        dialog.show();
    }

    private TimePickerDialog.OnTimeSetListener listenerTime = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            temp.getDate().setHour(hourOfDay);
            temp.getDate().setMinute(minute);
        }
    };

}