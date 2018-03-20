package com.afordev.todomanagermini.Manager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afordev.todomanagermini.AddPatternActivity;
import com.afordev.todomanagermini.PatternActivity;
import com.afordev.todomanagermini.R;
import com.afordev.todomanagermini.SubItem.DataPattern;

import java.util.ArrayList;

public class PatternRcvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private DBManager dbManager;
    private ArrayList<DataPattern> dataList;

    public PatternRcvAdapter(Context mContext, DBManager dbManager) {
        initSet(mContext, dbManager);
        dataList = dbManager.getPatternList();
    }

    public void initSet(Context mContext, DBManager dbManager) {
        this.mContext = mContext;
        this.dbManager = dbManager;
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
                VHPattern mVHPattern = new VHPattern(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pattern, parent, false));
                return mVHPattern;
        }
    }

    class VHPattern extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvTitle, tvDetail;
        private LinearLayout layoutItem;

        public VHPattern(final View itemView) {
            super(itemView);
            layoutItem = itemView.findViewById(R.id.item_pattern_layout);
            tvTitle = itemView.findViewById(R.id.item_pattern_tv_title);
            tvDetail = itemView.findViewById(R.id.item_pattern_tv_detail);

            layoutItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent;
            switch (view.getId()) {
                case (R.id.item_pattern_layout):
                    intent = new Intent(mContext, AddPatternActivity.class);
                    intent.putExtra("pattern_id", dataList.get(getAdapterPosition()).getId());
                    ((PatternActivity) mContext).startActivityForResult(intent, Manager.RC_PATTERN_TO_ADDPATTERN);
                    break;
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        DataPattern data;
        if (holder instanceof VHPattern) {
            data = dataList.get(position);
            ((VHPattern) holder).tvTitle.setText(data.getTitle());

            String[] sts = data.getDow().split(",");
            String[] dayOfWeek = mContext.getResources().getStringArray(R.array.dayofweek);
            StringBuilder sb = new StringBuilder();
            try {
                for (int i = 0; i < sts.length; i++) {
                    if (!sts[i].equals("")) {
                        sb.append(dayOfWeek[Integer.parseInt(sts[i])]);
                        if (i < sts.length - 1) {
                            sb.append(", ");
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("error", e.toString());
                sb = new StringBuilder();
                sb.append("Error!");
            }
            ((VHPattern) holder).tvDetail.setText("매 주 " + sb.toString() + "요일");
        }
    }

    private void removeItemView(int position) {
        dbManager.deletePattern(dataList.get(position));
        dataList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, dataList.size()); // 지워진 만큼 다시 채워넣기.
    }

}