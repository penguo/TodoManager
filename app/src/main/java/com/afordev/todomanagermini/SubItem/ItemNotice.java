package com.afordev.todomanagermini.SubItem;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afordev.todomanagermini.R;

/**
 * Created by penguo on 2018-03-03.
 */

public class ItemNotice {

    public CardView view;
    private TextView tvTitle;
    private ImageView ivLeft;
    public Button btnCheck, btnIgnore;

    public ItemNotice(Context mContext, View view){
        this.view = view.findViewById(R.id.item_notice_cardview);
        tvTitle = view.findViewById(R.id.item_notice_tv_title);
        ivLeft = view.findViewById(R.id.item_notice_iv_left);
        btnCheck = view.findViewById(R.id.item_notice_btn_check);
        btnIgnore= view.findViewById(R.id.item_notice_btn_ignore);

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    public void setTitle(String st){
        tvTitle.setText(st);
    }

    public void setLeftImage(int resId){
        ivLeft.setImageResource(resId);
    }
}
