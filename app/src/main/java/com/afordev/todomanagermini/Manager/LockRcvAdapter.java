package com.afordev.todomanagermini.Manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.preference.PreferenceManager;
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

public class LockRcvAdapter extends RecyclerView.Adapter<LockRcvAdapter.ViewHolder> {
    private Context mContext;
    private DBManager dbManager;
    private ArrayList<DataTodo> dataList;
    private DateForm date;
    private DataTodo temp;
    private boolean isDoubleClick, isViewChecked;

    public LockRcvAdapter(Context mContext, DBManager dbManager) {
        this.mContext = mContext;
        this.dbManager = dbManager;
        this.date = new DateForm(Calendar.getInstance());

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        isDoubleClick = prefs.getBoolean(Manager.PREF_DOUBLE_CLICK, false);
        isViewChecked = prefs.getBoolean(Manager.PREF_VIEW_CHECKED, false);

        setData();
    }

    public void setData(){
        dataList = dbManager.getTodoList(date);
        if (!isViewChecked) {
            ArrayList<DataTodo> list = new ArrayList<>();
            for (int i = 0; i < dataList.size(); i++) {
                if (dataList.get(i).getTimeChecked().isNull()) {
                    list.add(dataList.get(i));
                }
            }
            dataList = list;
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lock, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvTag;
        private ImageView ivCheck, ivIcon, ivImportance;
        private LinearLayout layout, layoutText;

        public ViewHolder(final View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.item_todo_tv_title);
            ivCheck = itemView.findViewById(R.id.item_todo_iv_left);
            ivIcon = itemView.findViewById(R.id.item_todo_iv_icon);
            ivImportance = itemView.findViewById(R.id.item_todo_iv_importance);
            layout = itemView.findViewById(R.id.item_todo_layout);
            tvTag = itemView.findViewById(R.id.item_todo_tv_tag);
            layoutText = itemView.findViewById(R.id.item_todo_layout_text);

            ivImportance.setVisibility(View.GONE);

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isDoubleClick || (temp != null && temp.equals(dataList.get(getAdapterPosition())))) {
                        if (dataList.get(getAdapterPosition()).getTimeChecked().isNull()) {
                            dataList.get(getAdapterPosition()).setTimeChecked(new DateForm(Manager.getCurrentTime()));
                        } else {
                            dataList.get(getAdapterPosition()).setTimeChecked(null);
                        }
                        dbManager.updateTodo(dataList.get(getAdapterPosition()));
                        notifyItemChanged(getAdapterPosition());
                        notifyItemMoved(getAdapterPosition(), getSortedPosition(getAdapterPosition()));
                        temp = null;
                    } else {
                        temp = dataList.get(getAdapterPosition());
                    }
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        DataTodo data = dataList.get(position);
        holder.layout.setVisibility(View.VISIBLE);
        holder.tvTitle.setText(data.getTitle());

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
            holder.tvTag.setVisibility(View.GONE);
        } else {
            holder.tvTag.setVisibility(View.VISIBLE);
            holder.tvTag.setText(sb.toString());
        }
        switch (data.getImportance()) {
            case (1):
                holder.layout.setBackgroundResource(R.drawable.btn_star_half);
                break;
            case (2):
                holder.layout.setBackgroundResource(R.drawable.btn_star);
                break;
            case (0):
            default:
                holder.layout.setBackgroundResource(R.drawable.btn_basic);
                break;
        }
        if (data.getTimeChecked().isNull()) {
            if (data.getTimeDead().compareTo(new DateForm(Calendar.getInstance())) < 0) {
                holder.ivCheck.setImageResource(R.drawable.ic_close);
            } else {
                holder.ivCheck.setImageResource(R.drawable.ic_delay);
            }
            holder.tvTitle.setPaintFlags((holder.tvTitle.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.layoutText.setAlpha(1);
        } else {
            if (data.getTimeChecked().compareTo(new DateForm(Calendar.getInstance())) <= 0) {
                holder.layout.setBackgroundResource(R.drawable.btn_basic);
                holder.ivCheck.setImageResource(R.drawable.ic_check_true);
                holder.tvTitle.setPaintFlags((holder.tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG));
                holder.layoutText.setAlpha((float) 0.5);
            } else {
                holder.ivCheck.setImageResource(R.drawable.ic_delay);
            }
        }
    }

    public void onRefresh() {
        setData();
        notifyDataSetChanged();
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
            if (list.get(i).getTimeChecked().isNull()) {
                list0.add(list.get(i));
            } else {
                list1.add(list.get(i));
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
}