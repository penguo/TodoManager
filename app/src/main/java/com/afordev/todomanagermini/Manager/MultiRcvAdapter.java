package com.afordev.todomanagermini.Manager;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
        private LinearLayout layout, layoutText;
        private ImageView ivSelect, ivIcon, ivImportance;

        public VHItem(final View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.item_todo_layout);
            tvTitle = itemView.findViewById(R.id.item_todo_tv_title);
            tvTags = itemView.findViewById(R.id.item_todo_tv_tag);
            ivSelect = itemView.findViewById(R.id.item_todo_iv_left);
            ivIcon = itemView.findViewById(R.id.item_todo_iv_icon);
            ivImportance = itemView.findViewById(R.id.item_todo_iv_importance);
            layoutText = itemView.findViewById(R.id.item_todo_layout_text);

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

            StringBuilder sb = new StringBuilder();
            if (data.getTimeDead().compareTo(new DateForm(Calendar.getInstance())) == 0) {
                sb.append(Manager.getTimeForm(data.getTimeDead()));
            }
            if (!data.getTags().equals("")) {
                if (!sb.toString().equals("")) {
                    sb.append(", ");
                }
                ArrayList<String> st = data.getTagList();
                for (int i = 0; i < st.size(); i++) {
                    sb.append("#" + st.get(i) + " ");
                }
            }
            if (sb.toString().equals("")) {
                ((VHItem) holder).tvTags.setVisibility(View.GONE);
            } else {
                ((VHItem) holder).tvTags.setVisibility(View.VISIBLE);
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
                    ((VHItem) holder).ivImportance.setVisibility(View.INVISIBLE);
                    break;
            }
            if (data.getTimeChecked().isNull()) {
                ((VHItem) holder).tvTitle.setPaintFlags(((VHItem) holder).tvTitle.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                ((VHItem) holder).layoutText.setAlpha(1);
            } else {
                if (data.getTimeChecked().compareTo(new DateForm(Calendar.getInstance())) <= 0) {
                    ((VHItem) holder).tvTitle.setPaintFlags(((VHItem) holder).tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    ((VHItem) holder).layoutText.setAlpha((float) 0.5);
                } else {
                }
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

    public void deleteMultiple() {
        for (int i = 0; i < selectList.size(); i++) {
            dataList.remove(selectList.get(i));
            dbManager.deleteTodo(selectList.get(i).getId());
        }
        selectList.clear();
        notifyDataSetChanged();
    }

    public void setImportanceMultiple(int input) {
        for (int i = 0; i < selectList.size(); i++) {
            selectList.get(i).setImportance(input);
            dbManager.updateTodo(selectList.get(i));
        }
        notifyDataSetChanged();
    }
}