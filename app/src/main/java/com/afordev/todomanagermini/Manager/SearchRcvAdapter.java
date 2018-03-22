package com.afordev.todomanagermini.Manager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.afordev.todomanagermini.R;
import com.afordev.todomanagermini.SearchActivity;
import com.afordev.todomanagermini.SubItem.DataTodo;

import java.util.ArrayList;

public class SearchRcvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private DBManager dbManager;
    private ArrayList<DataTodo> dataList;
    private boolean isAutoSort;
    public DataTodo temp;
    public int itemExpandPosition = -1, editPosition = -1;
    private InputMethodManager imm;

    public SearchRcvAdapter(Context mContext, DBManager dbManager, ArrayList<DataTodo> list) {
        initSet(mContext, dbManager);
        this.dataList = list;
    }

    public SearchRcvAdapter(Context mContext, DBManager dbManager, int select, String word) {
        initSet(mContext, dbManager);
        this.dataList = dbManager.searchTodo(select, word);
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
                VHItem mVHItem = new VHItem(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo, parent, false));
                return mVHItem;
        }
    }

    class VHItem extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvTitle, tvTags, tvDelay;
        private LinearLayout layout, layoutPlus, layoutText;
        private ImageView ivCheck, ivIcon, ivImportance;
        private Button btnPDelete, btnPEdit, btnPDelay, btnPCheck;

        public VHItem(final View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.item_todo_layout);
            tvTitle = itemView.findViewById(R.id.item_todo_tv_title);
            tvTags = itemView.findViewById(R.id.item_todo_tv_tag);
            ivCheck = itemView.findViewById(R.id.item_todo_iv_left);
            ivIcon = itemView.findViewById(R.id.item_todo_iv_icon);
            ivImportance = itemView.findViewById(R.id.item_todo_iv_importance);
            layoutPlus = itemView.findViewById(R.id.item_todo_layout_plus);
            btnPDelete = itemView.findViewById(R.id.item_todo_btn_pdelete);
            btnPEdit = itemView.findViewById(R.id.item_todo_btn_pedit);
            btnPDelay = itemView.findViewById(R.id.item_todo_btn_pdelay);
            btnPCheck = itemView.findViewById(R.id.item_todo_btn_pcheck);
            tvDelay = itemView.findViewById(R.id.item_todo_tv_delay);
            layoutText = itemView.findViewById(R.id.item_todo_layout_text);

            layout.setOnClickListener(this);
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
                    if (editPosition == -1) {
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
                    if (editPosition == -1) {
                        temp = dataList.get(getAdapterPosition()).clone();
                        editPosition = getAdapterPosition();
                        notifyItemChanged(getAdapterPosition());
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    } else {
                        Toast.makeText(mContext, "먼저 항목 수정을 마쳐야 합니다.", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case (R.id.item_todo_btn_pdelay):
                    dateSelectOption();
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
        private Button btnDelete, btnCancel;
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
            btnCancel = itemView.findViewById(R.id.item_todo_btn_cancel);
            ivEditLeft.setOnClickListener(this);
            ivEditSave.setOnClickListener(this);
            btnDelete.setOnClickListener(this);
            btnCancel.setOnClickListener(this);
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
                    dataList.set(getAdapterPosition(), temp);
                    temp = null;
                    editPosition = -1;
                    notifyItemChanged(getAdapterPosition());
                    imm.hideSoftInputFromWindow(etTitle.getWindowToken(), 0);
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

                case (R.id.item_todo_btn_expandmenu):
                    showExpandMenu();
                    break;

                case (R.id.dialog_em_layout_time):
                    CustomTimePicker.show(mContext, temp, SearchRcvAdapter.this, getAdapterPosition());
                    dialogExpandMenu.dismiss();
                    break;

                case (R.id.dialog_em_layout_tag):
                    Manager.showAddTag((SearchActivity) mContext, dbManager, temp, SearchRcvAdapter.this, getAdapterPosition());
                    dialogExpandMenu.dismiss();
                    break;

                case (R.id.dialog_em_layout_autodelay):
                    if (temp.getAutoDelay() == -1) {
                        temp.setAutoDelay(0);
                        setDataExpandMenu();
                    } else {
                        temp.setAutoDelay(-1);
                        setDataExpandMenu();
                    }
                    break;

                case (R.id.dialog_em_layout_importance):
                    Manager.showImportance(mContext, temp, SearchRcvAdapter.this, getAdapterPosition());
                    dialogExpandMenu.dismiss();
                    break;
            }
        }

        private AlertDialog dialogExpandMenu;
        private LinearLayout layoutTime, layoutTag, layoutAutoDelay, layoutImportance;
        private TextView tvTime, tvTag, tvAutoDelay, tvImportance;
        private Switch switchAutoDelay;
        private ImageView ivImportance;

        public void showExpandMenu() {
            LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout layout = (LinearLayout) li.inflate(R.layout.dialog_expandmenu, null);
            layoutTime = layout.findViewById(R.id.dialog_em_layout_time);
            layoutTag = layout.findViewById(R.id.dialog_em_layout_tag);
            layoutAutoDelay = layout.findViewById(R.id.dialog_em_layout_autodelay);
            layoutImportance = layout.findViewById(R.id.dialog_em_layout_importance);
            tvTime = layout.findViewById(R.id.dialog_em_tv_time);
            tvTag = layout.findViewById(R.id.dialog_em_tv_tag);
            tvAutoDelay = layout.findViewById(R.id.dialog_em_tv_autodelay);
            tvImportance = layout.findViewById(R.id.dialog_em_tv_importance);
            switchAutoDelay = layout.findViewById(R.id.dialog_em_switch_autodelay);
            ivImportance = layout.findViewById(R.id.dialog_em_iv_importance);
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

            setDataExpandMenu();

            layoutTime.setOnClickListener(this);
            layoutTag.setOnClickListener(this);
            layoutAutoDelay.setOnClickListener(this);
            layoutImportance.setOnClickListener(this);

            builder.setView(layout);
            builder.setPositiveButton("완료", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            dialogExpandMenu = builder.create(); //builder.show()를 create하여 dialog에 저장하는 방식.
            dialogExpandMenu.show();
        }

        public void setDataExpandMenu() {
            switch (temp.getImportance()) {
                case (0):
                    tvImportance.setText("보통");
                    ivImportance.setImageResource(R.drawable.ic_star_false);
                    break;
                case (1):
                    tvImportance.setText("중요");
                    ivImportance.setImageResource(R.drawable.ic_star_half);
                    break;
                case (2):
                    tvImportance.setText("매우 중요");
                    ivImportance.setImageResource(R.drawable.ic_star_true);
                    break;
                default:
                    tvImportance.setText("ERROR");
                    ivImportance.setImageResource(R.drawable.ic_star_false);
                    break;
            }
            if (temp.getIsTimeActivated() == 1) {
                tvTime.setText(Manager.getTimeForm(temp.getDate()));
            } else {
                tvTime.setText("하루 종일");
            }
            StringBuilder sb = new StringBuilder();
            ArrayList<String> st = temp.getTagList();
            for (int i = 0; i < st.size(); i++) {
                sb.append("#" + st.get(i) + " ");
            }
            tvTag.setText(sb.toString());
            if (temp.getAutoDelay() == -1) {
                switchAutoDelay.setChecked(false);
                tvAutoDelay.setText("");
            } else {
                switchAutoDelay.setChecked(true);
                tvAutoDelay.setText(temp.getAutoDelay() + "일 연기됨");
            }
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
            StringBuilder sb = new StringBuilder();
            sb.append(Manager.getDateForm(mContext, data.getDate()) + " ");
            if (data.getIsTimeActivated() == 1) {
                sb.append(Manager.getTimeForm(data.getDate()));
            }
            if (data.getIsTimeActivated() == 1 && !data.getTags().equals("")) {
                sb.append(" ");
            }
            if (!data.getTags().equals("")) {
                ArrayList<String> st = data.getTagList();
                for (int i = 0; i < st.size(); i++) {
                    sb.append("#" + st.get(i) + " ");
                }
            }
            ((VHItem) holder).tvTags.setText(sb.toString());
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
                case (0):
                default:
                    ((VHItem) holder).layout.setBackgroundResource(R.drawable.btn_basic);
                    ((VHItem) holder).ivImportance.setVisibility(View.INVISIBLE);
                    break;
            }
            switch (data.getChecked()) {
                case (0):
                    ((VHItem) holder).ivCheck.setImageResource(R.drawable.ic_check_false);
                    ((VHItem) holder).tvTitle.setPaintFlags(((VHItem) holder).tvTitle.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                    ((VHItem) holder).layoutText.setAlpha(1);
                    break;
                case (1):
                    ((VHItem) holder).ivCheck.setImageResource(R.drawable.ic_check_true);
                    ((VHItem) holder).tvTitle.setPaintFlags(((VHItem) holder).tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    ((VHItem) holder).layout.setBackgroundResource(R.drawable.btn_basic);
                    ((VHItem) holder).layoutText.setAlpha((float) 0.5);
                    ((VHItem) holder).btnPEdit.setVisibility(View.GONE);
                    ((VHItem) holder).btnPDelay.setVisibility(View.GONE);
                    ((VHItem) holder).btnPCheck.setVisibility(View.GONE);
                    break;
                case (2):
                    ((VHItem) holder).layout.setBackgroundResource(R.drawable.btn_basic);
                    ((VHItem) holder).ivCheck.setImageResource(R.drawable.ic_delay);
                    ((VHItem) holder).tvTitle.setPaintFlags(((VHItem) holder).tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    ((VHItem) holder).layoutText.setAlpha((float) 0.5);
                    ((VHItem) holder).btnPEdit.setVisibility(View.GONE);
                    ((VHItem) holder).btnPDelay.setVisibility(View.GONE);
                    ((VHItem) holder).btnPCheck.setVisibility(View.GONE);
                    break;
                default:
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
                    ((VHItem) holder).ivIcon.setVisibility(View.INVISIBLE);
                    break;
            }
            if (data.getAutoDelay() > 0) {
                ((VHItem) holder).tvDelay.setText(data.getAutoDelay() + "");
            } else {
                ((VHItem) holder).tvDelay.setText("");
            }
        } else if (holder instanceof VHEdit) {
            if (temp != null) {
                data = temp;
            } else {
                data = new DataTodo();
                data.setTitle("유효기간이 만료되어 초기화되었습니다. 취소 후 시작해주세요.");
                // TODO: 2018-03-21 초기화될 경우 해결방안 - 초기화 안되게 하면 더 좋음.
            }
            ((VHEdit) holder).etTitle.setText(data.getTitle());
            ((VHEdit) holder).etTitle.requestFocus();
            if (data.getIsTimeActivated() == 0 && data.getTags().equals("")) {
                ((VHEdit) holder).tvTags.setVisibility(View.INVISIBLE);
            } else {
                ((VHEdit) holder).tvTags.setVisibility(View.VISIBLE);
                StringBuffer sb = new StringBuffer();
                sb.append(Manager.getDateForm(mContext, data.getDate()) + " ");

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

    public int getSortedPosition(int position) {
        ArrayList<DataTodo> list = dataList;

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
                return i;
            }
        }
        return 0;
    }

    public void dateSelectOption() {
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
        }
    };

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

    public ArrayList<DataTodo> getDataList() {
        return dataList;
    }
}