package com.afordev.todomanagermini.Manager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Paint;
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
        initSet(mContext, dbManager);
        this.date = date;
        this.dataList = dbManager.getTodoList(date);
        this.dataList.add(0, new DataTodo());
        if (date.compareTo(new DateForm(Calendar.getInstance())) == 0) {
            isToday = true;
        } else {
            isToday = false;
        }
    }

    public void initSet(Context mContext, DBManager dbManager) {
        this.mContext = mContext;
        this.dbManager = dbManager;
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
        if (position == 0) {
            return -1;
        }
        if (editPosition == position) {
            return 1;
        } else {
            return 0;
        }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case (-1):
                VHHeader mVHHeader = new VHHeader(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header, parent, false));
                return mVHHeader;
            case (1):
                VHEdit mVHEdit = new VHEdit(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_edit, parent, false));
                return mVHEdit;
            case (0):
            default:
                VHItem mVHItem = new VHItem(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo, parent, false));
                return mVHItem;
        }
    }

    class VHItem extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvTitle, tvTags;
        private LinearLayout layout, layoutPlus;
        private ImageView ivCheck, ivIcon, ivImportance;
        private Button btnPDelete, btnPEdit, btnPDelay, btnPCheck;

        public VHItem(final View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.item_todo_layout);
            tvTitle = itemView.findViewById(R.id.item_todo_tv_title);
            tvTags = itemView.findViewById(R.id.item_todo_tv_tag);
            ivCheck = itemView.findViewById(R.id.item_todo_iv_check);
            ivIcon = itemView.findViewById(R.id.item_todo_iv_icon);
            ivImportance = itemView.findViewById(R.id.item_todo_iv_importance);
            layoutPlus = itemView.findViewById(R.id.item_todo_layout_plus);
            btnPDelete = itemView.findViewById(R.id.item_todo_btn_pdelete);
            btnPEdit = itemView.findViewById(R.id.item_todo_btn_pedit);
            btnPDelay = itemView.findViewById(R.id.item_todo_btn_pdelay);
            btnPCheck = itemView.findViewById(R.id.item_todo_btn_pcheck);

            layout.setOnClickListener(this);
            if (isToday) {
                layout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (editPosition == -1 && ((MainActivity) mContext).getTemp() == null) {
                            temp = dataList.get(getAdapterPosition());
                            editPosition = getAdapterPosition();
                            notifyItemChanged(getAdapterPosition());
                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                        } else {
                            Toast.makeText(mContext, "먼저 항목 수정을 마쳐야 합니다.", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                });
            }
            btnPDelete.setOnClickListener(this);
            btnPEdit.setOnClickListener(this);
            btnPDelay.setOnClickListener(this);
            btnPCheck.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
            switch (view.getId()) {
                case (R.id.item_todo_layout):
                    if (editPosition == -1 && ((MainActivity) mContext).getTemp() == null) {
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
                            temp = dataList.get(getAdapterPosition());
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

                case (R.id.item_todo_btn_pdelete):
                    dialog.setMessage("정말로 삭제하시겠습니까? 모든 내용이 삭제되며 복구되지 않습니다.");
                    dialog.setCancelable(true);
                    dialog.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dbManager.deleteTodo(dataList.get(getAdapterPosition()).getId());
                            itemExpandPosition = -1;
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

                case (R.id.item_todo_btn_pedit):
                    if (editPosition == -1 && ((MainActivity) mContext).getTemp() == null) {
                        temp = dataList.get(getAdapterPosition());
                        editPosition = getAdapterPosition();
                        notifyItemChanged(getAdapterPosition());
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    } else {
                        Toast.makeText(mContext, "먼저 항목 수정을 마쳐야 합니다.", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case (R.id.item_todo_btn_pdelay):
                    dateDelayOption();
                    break;

                case (R.id.item_todo_btn_pcheck):
                    if (editPosition != -1) {
                        Toast.makeText(mContext, "먼저 항목 수정을 마쳐야 합니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        switch (dataList.get(getAdapterPosition()).getChecked()) {
                            case (0):
                                dataList.get(getAdapterPosition()).setChecked(1);
                                break;
                            case (1):
                                dataList.get(getAdapterPosition()).setChecked(0);
                                break;
                            default:
                                Toast.makeText(mContext, "ERROR.", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        dbManager.updateTodo(dataList.get(getAdapterPosition()));
                        itemExpandPosition = -1;
                        notifyItemChanged(getAdapterPosition());
                        if (isAutoSort) {
                            notifyItemMoved(getAdapterPosition(), getSortedPosition(getAdapterPosition()));
                        }
                    }
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
                    switch (temp.getImportance()) {
                        case (0):
                            temp.setImportance(1);
                            break;
                        case (1):
                            temp.setImportance(2);
                            break;
                        case (2):
                            temp.setImportance(0);
                            break;
                    }
                    notifyItemChanged(getAdapterPosition());
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
                    CustomTimePicker.show(mContext, temp, TodoRcvAdapter.this, getAdapterPosition());
                    break;

                case (R.id.item_todo_btn_tag):
                    Manager.showAddTag((MainActivity) mContext, temp, TodoRcvAdapter.this, getAdapterPosition());
                    break;
            }
        }
    }

    class VHHeader extends RecyclerView.ViewHolder {
        private TextView tvTitle;

        public VHHeader(final View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.item_header_tv_title);

            tvTitle.setText("할 일");
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        DataTodo data;
        if (holder instanceof VHItem) {
            data = dataList.get(position);
            if (itemExpandPosition == position) {
                ((VHItem) holder).layoutPlus.setVisibility(View.VISIBLE);
            } else {
                ((VHItem) holder).layoutPlus.setVisibility(View.GONE);
            }
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
            switch (data.getImportance()) {
                case (1):
                    ((VHItem) holder).layout.setBackgroundResource(R.drawable.btn_star_half);
                    ((VHItem) holder).ivImportance.setVisibility(View.VISIBLE);
                    ((VHItem) holder).ivImportance.setImageResource(R.drawable.ic_star_half);
                    break;
                case (2):
                    ((VHItem) holder).layout.setBackgroundResource(R.drawable.btn_star);
                    ((VHItem) holder).ivImportance.setVisibility(View.VISIBLE);
                    ((VHItem) holder).ivImportance.setImageResource(R.drawable.ic_star_true);
                    break;
                case (3):
                    ((VHItem) holder).layout.setBackgroundResource(R.drawable.btn_basic);
                    ((VHItem) holder).ivImportance.setVisibility(View.VISIBLE);
                    ((VHItem) holder).ivImportance.setImageResource(R.drawable.ic_error);
                    break;
                case (0):
                default:
                    ((VHItem) holder).layout.setBackgroundResource(R.drawable.btn_basic);
                    ((VHItem) holder).ivImportance.setVisibility(View.GONE);
                    break;
            }
            switch (data.getChecked()) {
                case (0):
                    ((VHItem) holder).ivCheck.setImageResource(R.drawable.ic_check_false);
                    ((VHItem) holder).tvTitle.setPaintFlags(((VHItem) holder).tvTitle.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                    ((VHItem) holder).tvTitle.setAlpha(1);
                    break;
                case (1):
                    ((VHItem) holder).layout.setBackgroundResource(R.drawable.btn_basic);
                    ((VHItem) holder).ivCheck.setImageResource(R.drawable.ic_check_true);
                    ((VHItem) holder).tvTitle.setPaintFlags(((VHItem) holder).tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    ((VHItem) holder).tvTitle.setAlpha((float) 0.5);
                    break;
                default:
                    ((VHItem) holder).layout.setBackgroundResource(R.drawable.btn_basic);
                    ((VHItem) holder).ivCheck.setImageResource(R.drawable.ic_error);
                    break;
            }
            switch (data.getType()) {
                case (1):
                    ((VHItem) holder).ivIcon.setVisibility(View.VISIBLE);
                    ((VHItem) holder).ivIcon.setImageResource(R.drawable.ic_puzzle);
                    break;
                case (2):
                    ((VHItem) holder).ivIcon.setVisibility(View.VISIBLE);
                    ((VHItem) holder).ivIcon.setImageResource(R.drawable.ic_delay);
                    break;
                case (0):
                default:
                    ((VHItem) holder).ivIcon.setVisibility(View.GONE);
                    break;
            }
        } else if (holder instanceof VHEdit) {
            ((MainActivity) mContext).setViewBottom(false);
            data = temp; // TODO: 2018-03-07 대기가 길어져서 메모리에서 사라져서 temp가 null일때 널포인트에러 발생
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
            switch (data.getImportance()) {
                case (1):
                    ((VHEdit) holder).ivEditLeft.setImageResource(R.drawable.ic_star_half);
                    break;
                case (2):
                    ((VHEdit) holder).ivEditLeft.setImageResource(R.drawable.ic_star_true);
                    break;
                case (3):
                    ((VHEdit) holder).ivEditLeft.setImageResource(R.drawable.ic_error);
                    break;
                case (0):
                default:
                    ((VHEdit) holder).ivEditLeft.setImageResource(R.drawable.ic_star_false);
                    break;
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
        this.dataList.add(0, new DataTodo());
        notifyDataSetChanged();
    }

    public int getSortedPosition(int position) {
        ArrayList<DataTodo> list = dataList;
        list.remove(0);
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

        for (int i = 0; i < list.size(); i++) {
            if (dataList.get(position).getId() == list.get(i).getId()) {
                dataList = list;
                this.dataList.add(0, new DataTodo());
                return i;
            }
        }
        return 0;
    }

    public void dateDelayOption() {
        DatePickerDialog dpDialog = new DatePickerDialog(mContext, listenerDate,
                temp.getDate().getYear(),
                temp.getDate().getMonth(),
                temp.getDate().getDay());
        dpDialog.show();
    }

    private DatePickerDialog.OnDateSetListener listenerDate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            temp.getDate().set(year, monthOfYear, dayOfMonth);
            dbManager.updateTodo(temp);
            onRefresh();
        }
    };

    public ArrayList<DataTodo> getDataList() {
        return dataList;
    }
}