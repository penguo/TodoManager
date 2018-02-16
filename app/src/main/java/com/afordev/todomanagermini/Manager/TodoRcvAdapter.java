package com.afordev.todomanagermini.Manager;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.afordev.todomanagermini.R;
import com.afordev.todomanagermini.SubItem.DataTodo;
import com.afordev.todomanagermini.SubItem.DateForm;
import com.afordev.todomanagermini.SubItem.TimeForm;

import java.util.ArrayList;
import java.util.Calendar;

public class TodoRcvAdapter extends RecyclerView.Adapter<TodoRcvAdapter.ViewHolder> {
    private Context mContext;
    private DBManager dbManager;
    private ArrayList<DataTodo> dataList;
    private DateForm date;
    private InputMethodManager imm;
    public int editModePosition = -1;
    private boolean isLockScreen, isMenuExpand;
    private DataTodo newDataTodo;

    public TodoRcvAdapter(Context mContext, DBManager dbManager, DateForm date, boolean isLockScreen) {
        this.mContext = mContext;
        this.dbManager = dbManager;
        this.dataList = dbManager.getTodoList(date);
        this.date = date;
        this.isLockScreen = isLockScreen;
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public int getItemCount() {
        if (!isLockScreen) {
            return dataList.size() + 1;
        } else {
            return dataList.size();
        }
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvTag;
        private EditText etTitle;
        private ImageView ivLeft, ivCheck, ivEditLeft, ivEditSave;
        private ConstraintLayout layoutNew;
        private LinearLayout layout, layoutEdit, layoutExpand;
        private Button btnDelete, btnTag, btnTime, btnCancel;

        public ViewHolder(final View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.item_todo_tv_title);
            etTitle = itemView.findViewById(R.id.item_todo_et_edit_title);
            ivLeft = itemView.findViewById(R.id.item_todo_iv_left);
            ivCheck = itemView.findViewById(R.id.item_todo_iv_check);
            ivEditLeft = itemView.findViewById(R.id.item_todo_iv_edit_left);
            ivEditSave = itemView.findViewById(R.id.item_todo_iv_edit_save);
            layout = itemView.findViewById(R.id.item_todo_layout);
            layoutNew = itemView.findViewById(R.id.item_todo_layout_new);
            layoutEdit = itemView.findViewById(R.id.item_todo_layout_edit);
            layoutExpand = itemView.findViewById(R.id.item_todo_layout_expand);
            btnDelete = itemView.findViewById(R.id.item_todo_btn_delete);
            btnTag = itemView.findViewById(R.id.item_todo_btn_tag);
            btnTime = itemView.findViewById(R.id.item_todo_btn_time);
            btnCancel = itemView.findViewById(R.id.item_todo_btn_cancel);
            tvTag = itemView.findViewById(R.id.item_todo_tv_tag);

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (editModePosition != -1) {
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
                        notifyItemChanged(getAdapterPosition());
                        notifyItemMoved(getAdapterPosition(), getSortedPosition(getAdapterPosition()));
                    }
                }
            });
            if (!isLockScreen) {
                layout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (editModePosition == -1) {
                            isMenuExpand = false;
                            editModePosition = getAdapterPosition();
                            notifyItemChanged(getAdapterPosition());
                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                            return false;
                        } else {
                            Toast.makeText(mContext, "먼저 항목 수정을 마쳐야 합니다.", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    }
                });
            }
            layoutNew.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isMenuExpand = false;
                    newDataTodo = new DataTodo(-1, "", date.toDBString(), "", 0, 0);
                    editModePosition = getAdapterPosition();
                    notifyItemChanged(getAdapterPosition());
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                }
            });
            ivLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getAdapterPosition() == dataList.size()) {
                        Toast.makeText(mContext, "ERROR.", Toast.LENGTH_SHORT).show();
                    } else if (editModePosition != -1) {
                        Toast.makeText(mContext, "먼저 항목 수정을 마쳐야 합니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        switch (dataList.get(getAdapterPosition()).getType()) {
                            case (0):
                                dataList.get(getAdapterPosition()).setType(1);
                                break;
                            case (1):
                                dataList.get(getAdapterPosition()).setType(2);
                                break;
                            case (2):
                                dataList.get(getAdapterPosition()).setType(0);
                                break;
                            default:
                                Toast.makeText(mContext, "ERROR.", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        dbManager.updateTodo(dataList.get(getAdapterPosition()));
                        notifyItemChanged(getAdapterPosition());
                        notifyItemMoved(getAdapterPosition(), getSortedPosition(getAdapterPosition()));
                    }
                }
            });
            ivEditLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isMenuExpand) {
                        isMenuExpand = true;
                        notifyItemChanged(editModePosition);
                        imm.hideSoftInputFromWindow(etTitle.getWindowToken(), 0);
                    } else {
                        isMenuExpand = false;
                        notifyItemChanged(editModePosition);
                        imm.hideSoftInputFromWindow(etTitle.getWindowToken(), 0);
                    }
                }
            });
            ivEditSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DataTodo data;
                    if (editModePosition == dataList.size()) {
                        data = newDataTodo;
                        data.setTitle(etTitle.getText().toString());
                        dbManager.insertTodo(data);
                        onRefresh();
                    } else {
                        data = dataList.get(editModePosition);
                        data.setTitle(etTitle.getText().toString());
                        dbManager.updateTodo(data);
                        notifyItemChanged(editModePosition);
                    }
                    editModePosition = -1;
                    imm.hideSoftInputFromWindow(etTitle.getWindowToken(), 0);
                }
            });
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getAdapterPosition() != dataList.size()) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                        dialog.setMessage("정말로 삭제하시겠습니까? 모든 내용이 삭제되며 복구되지 않습니다.");
                        dialog.setCancelable(true);
                        dialog.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                imm.hideSoftInputFromWindow(etTitle.getWindowToken(), 0);
                                editModePosition = -1;
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
                    } else {
                        editModePosition = -1;
                        notifyItemChanged(getAdapterPosition());
                    }
                }
            });
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getAdapterPosition() != dataList.size()) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                        dialog.setMessage("작업 중인 정보가 사라집니다.");
                        dialog.setCancelable(true);
                        dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int i = editModePosition;
                                editModePosition = -1;
                                notifyItemChanged(i);
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
                    } else {
                        editModePosition = -1;
                        notifyItemChanged(getAdapterPosition());
                    }
                }
            });
            btnTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (editModePosition != dataList.size()) {
                        TimeSelectOption(dataList.get(editModePosition).getDate().getTime());
                    } else {
                        TimeSelectOption(newDataTodo.getDate().getTime());
                    }
                }
            });
            btnTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(mContext, "다음 버전에 추가될 예정.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (editModePosition == position) {
            holder.layout.setVisibility(View.GONE);
            holder.layoutNew.setVisibility(View.GONE);
            holder.layoutEdit.setVisibility(View.VISIBLE);
            if (position == dataList.size()) { // new
                holder.etTitle.setText("");
                holder.btnDelete.setVisibility(View.GONE);
            } else {
                DataTodo data = dataList.get(position);
                holder.etTitle.setText(data.getTitle());
                holder.btnDelete.setVisibility(View.VISIBLE);
            }
            holder.etTitle.requestFocus();
            if (!isMenuExpand) {
                holder.layoutExpand.setVisibility(View.GONE);
            } else {
                holder.layoutExpand.setVisibility(View.VISIBLE);
            }
        } else {
            holder.layoutEdit.setVisibility(View.GONE);
            if (position == dataList.size()) {
                holder.layout.setVisibility(View.GONE);
                holder.layoutNew.setVisibility(View.VISIBLE);
            } else {
                holder.layout.setVisibility(View.VISIBLE);
                holder.layoutNew.setVisibility(View.GONE);

                DataTodo data = dataList.get(position);
                holder.tvTitle.setText(data.getTitle());
                if (data.getDate().getTime() == null && data.getTags().equals("")) {
                    holder.tvTag.setVisibility(View.GONE);
                } else {
                    holder.tvTag.setVisibility(View.VISIBLE);
                    StringBuffer sb = new StringBuffer();
                    if (data.getDate().getTime() != null) {
                        sb.append(data.getDate().getTime().toString());
                    }
                    if (data.getDate().getTime() != null && !data.getTags().equals("")) {
                        sb.append(", ");
                    }
                    if (!data.getTags().equals("")) {
                        String[] st = data.getTagList();
                        for (int i = 0; i < st.length; i++) {
                            sb.append("#" + st[i] + " ");
                        }
                    }
                    holder.tvTag.setText(sb.toString());
                }
                switch (data.getType()) {
                    case (0):
                        holder.ivLeft.setImageResource(R.drawable.ic_star_false);
                        holder.layout.setBackgroundResource(R.drawable.btn_basic);
                        break;
                    case (1):
                        holder.ivLeft.setImageResource(R.drawable.ic_star_half);
                        holder.layout.setBackgroundResource(R.drawable.btn_star_half);
                        break;
                    case (2):
                        holder.ivLeft.setImageResource(R.drawable.ic_star_true);
                        holder.layout.setBackgroundResource(R.drawable.btn_star);
                        break;
                    default:
                        holder.ivLeft.setImageResource(R.drawable.ic_error);
                        break;
                }
                switch (data.getChecked()) {
                    case (0):
                        holder.ivCheck.setImageResource(R.drawable.ic_check_false);
                        holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                        holder.layout.setAlpha(1);
                        break;
                    case (1):
                        holder.ivCheck.setImageResource(R.drawable.ic_check_true);
                        holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        holder.layout.setBackgroundResource(R.drawable.btn_basic);
                        holder.layout.setAlpha((float) 0.5);
                        break;
                    default:
                        holder.ivCheck.setImageResource(R.drawable.ic_error);
                        break;
                }
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


    public void TimeSelectOption(TimeForm timeForm) {
        TimePickerDialog dialog;
        if (timeForm != null) {
            dialog = new TimePickerDialog(mContext, listenerTime, timeForm.getHour(), timeForm.getMinute(), false);
        } else {
            Calendar cal = Calendar.getInstance();
            dialog = new TimePickerDialog(mContext, listenerTime, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false);
        }
        dialog.show();
    }

    private TimePickerDialog.OnTimeSetListener listenerTime = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if (editModePosition != dataList.size()) {
                dataList.get(editModePosition).getDate().setTime(new TimeForm(hourOfDay, minute));
                dbManager.updateTodo(dataList.get(editModePosition));
            } else {
                newDataTodo.getDate().setTime(new TimeForm(hourOfDay, minute));
            }
        }
    };
}