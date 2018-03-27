package com.afordev.todomanagermini.Dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afordev.todomanagermini.Manager.DBManager;
import com.afordev.todomanagermini.Manager.Manager;
import com.afordev.todomanagermini.R;
import com.afordev.todomanagermini.SubItem.DataTodo;
import com.greenfrvr.hashtagview.HashtagView;

import java.util.ArrayList;

/**
 * Created by penguo on 2018-03-28.
 */

public class TagPicker {

    private Context mContext;
    private DialogExpandMenu dialogEM;
    private DBManager dbManager;

    public TagPicker(Context mContext, DialogExpandMenu dialogEM) {
        this.mContext = mContext;
        this.dialogEM = dialogEM;
        this.dbManager = DBManager.getInstance(mContext);
    }

    public void show(final DataTodo data) {
        LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout updateLayout = (LinearLayout) li.inflate(R.layout.dialog_tag, null);
        final HashtagView hashtagView = (HashtagView) updateLayout.findViewById(R.id.dialog_tag_hashtag);
        final AutoCompleteTextView et = (AutoCompleteTextView) updateLayout.findViewById(R.id.dialog_tag_et);
        final ImageButton btnAdd = (ImageButton) updateLayout.findViewById(R.id.dialog_tag_btn_add);
        final ArrayList<String> dbTags = dbManager.getTagList();
        et.setAdapter(new ArrayAdapter<String>(mContext,
                R.layout.simple_dropdown_item_tag, dbTags));
        final ArrayList<String> tags = data.getTagList();

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        AlertDialog dialog;
        hashtagView.setData(tags, Manager.HASHTAG);
        hashtagView.addOnTagClickListener(new HashtagView.TagsClickListener() {
            @Override
            public void onItemClicked(Object item) {
                tags.remove(item);
                data.setTagList(tags);
                hashtagView.setData(tags, Manager.HASHTAG);
            }
        });
        builder.setTitle("분류태그 설정");
        et.setText("");
        builder.setView(updateLayout);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et.getText().toString().equals("")) {
                    Toast.makeText(mContext, "내용을 입력하세요.", Toast.LENGTH_SHORT).show();
                } else {
                    if (!tags.contains(et.getText().toString())) {
                        tags.add(et.getText().toString());
                    } else {
                        Toast.makeText(mContext, "이미 등록된 태그입니다.", Toast.LENGTH_SHORT).show();
                    }
                }
                et.setText("");
                hashtagView.setData(tags, Manager.HASHTAG);
            }
        });
        builder.setPositiveButton("저장", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!et.getText().toString().equals("")) {
                    if (!tags.contains(et.getText().toString())) {
                        tags.add(et.getText().toString());
                    } else {
                        Toast.makeText(mContext, "이미 등록된 태그입니다.", Toast.LENGTH_SHORT).show();
                    }
                }
                data.setTagList(tags);
                dialog.dismiss();
                dialogEM.refreshParentForFinish();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog = builder.create(); //builder.show()를 create하여 dialog에 저장하는 방식.
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }


}
