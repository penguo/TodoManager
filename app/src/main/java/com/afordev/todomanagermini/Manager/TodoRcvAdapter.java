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
import android.widget.Switch;
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
    private ArrayList<ArrayList<DataTodo>> arrayLists;
    private ArrayList<DataTodo> dataList;
    private DateForm date;
    private boolean isToday, isAutoSort, isDoubleClick;
    public DataTodo temp;
    public int itemExpandPosition = -1, editPosition = -1;
    private InputMethodManager imm;
    private int header0_pos = -1, header1_pos = -1, header2_pos = -1, headerSize;

    public TodoRcvAdapter(Context mContext, DBManager dbManager, DateForm date) {
        initSet(mContext, dbManager);
        this.date = date;
        setData();
        if (date.compareTo(new DateForm(Calendar.getInstance())) == 0) {
            isToday = true;
        } else {
            isToday = false;
        }
    }

    public void setData() {
        int tempPos = 0;
        headerSize = 0;
        header0_pos = -1;
        header1_pos = -1;
        header2_pos = -1;
        dataList = new ArrayList<>();
        this.arrayLists = dbManager.getSortedList(date);
        if (arrayLists.get(0).size() != 0) {
            header0_pos = tempPos;
            dataList.add(tempPos, new DataTodo(-2));
            tempPos += arrayLists.get(0).size();
            tempPos++;
            dataList.addAll(arrayLists.get(0));
        }
        if (arrayLists.get(1).size() != 0) {
            header1_pos = tempPos;
            dataList.add(tempPos, new DataTodo(-2));
            tempPos += arrayLists.get(1).size();
            tempPos++;
            dataList.addAll(arrayLists.get(1));
        }
        if (arrayLists.get(2).size() != 0) {
            header2_pos = tempPos;
            dataList.add(tempPos, new DataTodo(-2));
            tempPos += arrayLists.get(2).size();
            tempPos++;
            dataList.addAll(arrayLists.get(2));
        }
    }

    public void initSet(Context mContext, DBManager dbManager) {
        this.mContext = mContext;
        this.dbManager = dbManager;
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        isAutoSort = prefs.getBoolean(Manager.PREF_AUTO_SORT, true);
        isDoubleClick = prefs.getBoolean(Manager.PREF_DOUBLE_CLICK, false);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == header0_pos || position == header1_pos || position == header2_pos) {
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
        private TextView tvTitle, tvTags, tvDelay;
        private LinearLayout layout, layoutPlus, layoutText, layoutRight1, layoutRight2;
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
            layoutText = itemView.findViewById(R.id.item_todo_layout_text);
            tvDelay = itemView.findViewById(R.id.item_todo_tv_delay);
            layoutRight1 = itemView.findViewById(R.id.item_todo_layout_right1);
            layoutRight2 = itemView.findViewById(R.id.item_todo_layout_right2);

            layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (isToday) {
                        if (editPosition == -1 && ((MainActivity) mContext).getTemp() == null) {
                            temp = dataList.get(getAdapterPosition()).clone();
                            editPosition = getAdapterPosition();
                            notifyItemChanged(getAdapterPosition());
                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                        } else {
                            Toast.makeText(mContext, "먼저 항목 수정을 마쳐야 합니다.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mContext, "팝업 메뉴", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });
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
                    if (editPosition == -1 && ((MainActivity) mContext).getTemp() == null) {
                        if (isToday) {
                            if (!isDoubleClick || (temp != null && temp.equals(dataList.get(getAdapterPosition())))) {
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
                                if (isAutoSort) {
//                                notifyItemMoved(getAdapterPosition(), getSortedPosition(getAdapterPosition()));
                                }
                                temp = null;
                            } else {
                                temp = dataList.get(getAdapterPosition());
                            }
                            editPosition = -1;
                        } else {
                            temp = dataList.get(getAdapterPosition()).clone();
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
                        temp = dataList.get(getAdapterPosition()).clone();
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
//                            notifyItemMoved(getAdapterPosition(), getSortedPosition(getAdapterPosition()));
                        }
                    }
            }
        }
    }

    class VHEdit extends RecyclerView.ViewHolder implements View.OnClickListener {
        private EditText etTitle;
        private TextView tvTags;
        private ImageView ivEditLeft, ivEditSave;
        private Button btnDelete, btnCancel, btnExpandMenu;
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
            btnExpandMenu = itemView.findViewById(R.id.item_todo_btn_expandmenu);

            ivEditLeft.setOnClickListener(this);
            ivEditSave.setOnClickListener(this);
            btnDelete.setOnClickListener(this);
            btnCancel.setOnClickListener(this);
            btnExpandMenu.setOnClickListener(this);
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
                    dataList.set(getAdapterPosition(), temp);
                    temp = null;
                    editPosition = -1;
                    itemExpandPosition = -1;
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

                case (R.id.item_todo_btn_expandmenu):
                    showExpandMenu();
                    break;

                case (R.id.dialog_em_layout_time):
                    CustomTimePicker.show(mContext, temp, TodoRcvAdapter.this, getAdapterPosition());
                    dialogExpandMenu.dismiss();
                    break;

                case (R.id.dialog_em_layout_tag):
                    Manager.showAddTag((MainActivity) mContext, dbManager, temp, TodoRcvAdapter.this, getAdapterPosition());
                    dialogExpandMenu.dismiss();
                    break;

                case (R.id.dialog_em_layout_autodelay):
                    temp.setTypeValue(0);
                    if (temp.getType() == 2) {
                        temp.setType(0);
                    } else {
                        temp.setType(2);
                    }
                    setDataExpandMenu();
                    break;

                case (R.id.dialog_em_layout_importance):
                    Manager.showImportance(mContext, temp, TodoRcvAdapter.this, getAdapterPosition());
                    dialogExpandMenu.dismiss();
                    break;
                    
                case (R.id.dialog_em_layout_pattern):
                    Toast.makeText(mContext, "Next Version", Toast.LENGTH_SHORT).show();
                    // TODO: 2018-03-26
                    break;
            }
        }

        private AlertDialog dialogExpandMenu;
        private LinearLayout layoutTime, layoutTag, layoutAutoDelay, layoutImportance, layoutPattern;
        private TextView tvTime, tvTag, tvAutoDelay, tvImportance, tvPattern;
        private Switch switchAutoDelay;
        private ImageView ivImportance;

        public void showExpandMenu() {
            LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout layout = (LinearLayout) li.inflate(R.layout.dialog_expandmenu, null);
            layoutTime = layout.findViewById(R.id.dialog_em_layout_time);
            layoutTag = layout.findViewById(R.id.dialog_em_layout_tag);
            layoutAutoDelay = layout.findViewById(R.id.dialog_em_layout_autodelay);
            layoutImportance = layout.findViewById(R.id.dialog_em_layout_importance);
            layoutPattern = layout.findViewById(R.id.dialog_em_layout_pattern);
            tvTime = layout.findViewById(R.id.dialog_em_tv_time);
            tvTag = layout.findViewById(R.id.dialog_em_tv_tag);
            tvAutoDelay = layout.findViewById(R.id.dialog_em_tv_autodelay);
            tvImportance = layout.findViewById(R.id.dialog_em_tv_importance);
            tvPattern = layout.findViewById(R.id.dialog_em_tv_pattern);
            switchAutoDelay = layout.findViewById(R.id.dialog_em_switch_autodelay);
            ivImportance = layout.findViewById(R.id.dialog_em_iv_importance);
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

            setDataExpandMenu();

            layoutTime.setOnClickListener(this);
            layoutTag.setOnClickListener(this);
            layoutAutoDelay.setOnClickListener(this);
            layoutImportance.setOnClickListener(this);
            layoutPattern.setOnClickListener(this);

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
            if (temp.getType() == 2) {
                layoutAutoDelay.setVisibility(View.VISIBLE);
                layoutPattern.setVisibility(View.GONE);
                switchAutoDelay.setChecked(true);
                tvAutoDelay.setText(temp.getTypeValue() + "일 연기됨");
            } else if (temp.getType() == 1) {
                layoutAutoDelay.setVisibility(View.GONE);
                layoutPattern.setVisibility(View.VISIBLE);
                tvPattern.setText(temp.getTypeValue() + "회차");
            } else {
                layoutAutoDelay.setVisibility(View.VISIBLE);
                layoutPattern.setVisibility(View.GONE);
                switchAutoDelay.setChecked(false);
                tvAutoDelay.setText("");
            }
        }
    }

    class VHHeader extends RecyclerView.ViewHolder {
        private TextView tvTitle;

        public VHHeader(final View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.item_header_tv_title);
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
                    sb.append(" ");
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
                    ((VHItem) holder).layoutText.setAlpha(1);
                    break;
                case (1):
                    ((VHItem) holder).layout.setBackgroundResource(R.drawable.btn_basic);
                    ((VHItem) holder).ivCheck.setImageResource(R.drawable.ic_check_true);
                    ((VHItem) holder).tvTitle.setPaintFlags(((VHItem) holder).tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    ((VHItem) holder).layoutText.setAlpha((float) 0.5);
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
                    ((VHItem) holder).layout.setBackgroundResource(R.drawable.btn_basic);
                    ((VHItem) holder).ivCheck.setImageResource(R.drawable.ic_error);
                    break;
            }
            switch (data.getImportance()) {
                case (1):
                    ((VHItem) holder).layoutRight1.setVisibility(View.VISIBLE);
                    ((VHItem) holder).layout.setBackgroundResource(R.drawable.btn_star_half);
                    ((VHItem) holder).ivImportance.setImageResource(R.drawable.ic_star_half);
                    break;
                case (2):
                    ((VHItem) holder).layoutRight1.setVisibility(View.VISIBLE);
                    ((VHItem) holder).layout.setBackgroundResource(R.drawable.btn_star);
                    ((VHItem) holder).ivImportance.setImageResource(R.drawable.ic_star_true);
                    break;
                case (0):
                default:
                    ((VHItem) holder).layoutRight1.setVisibility(View.GONE);
                    ((VHItem) holder).layout.setBackgroundResource(R.drawable.btn_basic);
                    break;
            }
            switch (data.getType()) {
                case (1):
                    ((VHItem) holder).layoutRight2.setVisibility(View.VISIBLE);
                    ((VHItem) holder).ivIcon.setImageResource(R.drawable.ic_puzzle);
                    ((VHItem) holder).tvDelay.setText(data.getTypeValue() + "회");
                    break;
                case (2):
                    ((VHItem) holder).layoutRight2.setVisibility(View.VISIBLE);
                    ((VHItem) holder).ivIcon.setImageResource(R.drawable.ic_delay);
                    ((VHItem) holder).tvDelay.setText(data.getTypeValue() + "일");
                    break;
                case (0):
                default:
                    ((VHItem) holder).layoutRight2.setVisibility(View.GONE);
                    ((VHItem) holder).tvDelay.setText("");
                    break;
            }
        } else if (holder instanceof VHEdit) {
            ((MainActivity) mContext).setViewBottom(false);
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
        } else if (holder instanceof VHHeader) {
            String title;
            if (position == header0_pos) {
                title = "긴급! / 매우 중요!";
            } else if (position == header1_pos) {
                title = "할 일";
            } else if (position == header2_pos) {
                title = "완료";
            } else {
                title = "에러!";
            }
            ((VHHeader) holder).tvTitle.setText(title);
        }
    }

    private void removeItemView(int position) {
        dataList.remove(position);
        notifyItemRemoved(position);
        if (position < header0_pos) {
            header0_pos--;
        }
        if (position < header1_pos) {
            header1_pos--;
        }
        if (position < header2_pos) {
            header2_pos--;
        }
        notifyItemRangeChanged(position, dataList.size()); // 지워진 만큼 다시 채워넣기.
    }

    public void onRefresh() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        isAutoSort = prefs.getBoolean(Manager.PREF_AUTO_SORT, true);
        itemExpandPosition = -1;
        setData();
        notifyDataSetChanged();
    }

//    public int getSortedPosition(int position) {
//        ArrayList<DataTodo> list = dataList;
//        list.remove(0);
//        ArrayList<DataTodo> list0 = new ArrayList<>();
//        ArrayList<DataTodo> list1 = new ArrayList<>();
//        ArrayList<DataTodo> list2 = new ArrayList<>();
//        for (int i = 0; i < list.size(); i++) {
//            switch (list.get(i).getImportance()) {
//                case (0):
//                    list0.add(list.get(i));
//                    break;
//                case (1):
//                    list1.add(list.get(i));
//                    break;
//                case (2):
//                    list2.add(list.get(i));
//                    break;
//            }
//        }
//        list = new ArrayList<>();
//        list.addAll(list2);
//        list.addAll(list1);
//        list.addAll(list0);
//        list0 = new ArrayList<>();
//        list1 = new ArrayList<>();
//        for (int i = 0; i < list.size(); i++) {
//            switch (list.get(i).getChecked()) {
//                case (0):
//                    list0.add(list.get(i));
//                    break;
//                case (1):
//                    list1.add(list.get(i));
//                    break;
//            }
//        }
//        list = new ArrayList<>();
//        list.addAll(list0);
//        list.addAll(list1);
//
//        for (int i = 0; i < list.size(); i++) {
//            if (dataList.get(position).getId() == list.get(i).getId()) {
//                dataList = list;
//                this.dataList.add(0, new DataTodo());
//                return i;
//            }
//        }
//        return 0;
//    }

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
        ArrayList<DataTodo> list = new ArrayList<>();
        list.addAll(dataList);
        int tempMinus = 0;
        if (header0_pos != -1) {
            list.remove(header0_pos - tempMinus);
            tempMinus++;
        }
        if (header1_pos != -1) {
            list.remove(header1_pos - tempMinus);
            tempMinus++;
        }
        if (header2_pos != -1) {
            list.remove(header2_pos - tempMinus);
            tempMinus++;
        }
        return list;
    }
}