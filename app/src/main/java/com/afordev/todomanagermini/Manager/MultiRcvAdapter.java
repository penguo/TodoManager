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

public class MultiRcvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private DBManager dbManager;
    private ArrayList<DataTodo> dataList, selectList;

    public MultiRcvAdapter(Context mContext, DBManager dbManager, ArrayList<DataTodo> list) {
        initSet(mContext, dbManager);
        this.dataList = list;
    }

    public void initSet(Context mContext, DBManager dbManager) {
        this.mContext = mContext;
        this.dbManager = dbManager;
        selectList = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case (0):
            default:
                VHItem mVHItem = new VHItem(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo, parent, false));
                return mVHItem;
        }
    }

    class VHItem extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvTitle, tvTags;
        private LinearLayout layout;
        private ImageView ivSelect, ivIcon, ivImportance;

        public VHItem(final View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.item_todo_layout);
            tvTitle = itemView.findViewById(R.id.item_todo_tv_title);
            tvTags = itemView.findViewById(R.id.item_todo_tv_tag);
            ivSelect = itemView.findViewById(R.id.item_todo_iv_check);
            ivIcon = itemView.findViewById(R.id.item_todo_iv_icon);
            ivImportance = itemView.findViewById(R.id.item_todo_iv_importance);

            layout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case (R.id.item_todo_layout):
                    if (selectList.contains(dataList.get(getAdapterPosition()))) {
                        selectList.remove(dataList.get(getAdapterPosition()));
                    } else {
                        selectList.add(dataList.get(getAdapterPosition()));
                    }
                    notifyItemChanged(getAdapterPosition());
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
            switch (data.getImportance()) {
                case (1):
                    ((VHItem) holder).ivImportance.setVisibility(View.VISIBLE);
                    ((VHItem) holder).ivImportance.setImageResource(R.drawable.ic_star_half);
                    break;
                case (2):
                    ((VHItem) holder).ivImportance.setVisibility(View.VISIBLE);
                    ((VHItem) holder).ivImportance.setImageResource(R.drawable.ic_star_true);
                    break;
                case (0):
                default:
                    ((VHItem) holder).ivImportance.setVisibility(View.GONE);
                    break;
            }
            switch (data.getChecked()) {
                case (0):
                    ((VHItem) holder).tvTitle.setPaintFlags(((VHItem) holder).tvTitle.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                    ((VHItem) holder).tvTitle.setAlpha(1);
                    break;
                case (1):
                    ((VHItem) holder).tvTitle.setPaintFlags(((VHItem) holder).tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    ((VHItem) holder).tvTitle.setAlpha((float) 0.5);
                    break;
                default:
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
            if (selectList.contains(data)) {
                ((VHItem) holder).ivSelect.setImageResource(R.drawable.ic_check_true);
                ((VHItem) holder).layout.setBackgroundResource(R.drawable.btn_star);
            } else {
                ((VHItem) holder).ivSelect.setImageResource(R.drawable.ic_check_false);
                ((VHItem) holder).layout.setBackgroundResource(R.drawable.btn_basic);
            }
        }
    }

    private void removeItemView(int position) {
        dbManager.deleteTodo(dataList.get(position).getId());
        dataList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, dataList.size()); // 지워진 만큼 다시 채워넣기.
    }

    public ArrayList<DataTodo> getSelectList() {
        return selectList;
    }
}