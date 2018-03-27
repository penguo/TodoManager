package com.afordev.todomanagermini.Dialog;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.afordev.todomanagermini.R;
import com.afordev.todomanagermini.SubItem.DataTodo;

/**
 * Created by penguo on 2018-03-28.
 */

public class ImportancePicker {

    private Context mContext;
    private DialogExpandMenu dialogEM;

    public ImportancePicker(Context mContext, DialogExpandMenu dialogEM) {
        this.mContext = mContext;
        this.dialogEM = dialogEM;
    }

    public void show(final DataTodo data) {
        LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) li.inflate(R.layout.dialog_importance, null);
        ImageButton btn0 = layout.findViewById(R.id.dialog_imp_btn_0);
        ImageButton btn1 = layout.findViewById(R.id.dialog_imp_btn_1);
        ImageButton btn2 = layout.findViewById(R.id.dialog_imp_btn_2);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle("중요도 설정");
        builder.setView(layout);
        final AlertDialog dialog = builder.create();

        btn0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data.setImportance(0);
                dialog.dismiss();
                dialogEM.refreshParentForFinish();
            }
        });
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data.setImportance(1);
                dialog.dismiss();
                dialogEM.refreshParentForFinish();
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data.setImportance(2);
                dialog.dismiss();
                dialogEM.refreshParentForFinish();
            }
        });
        dialog.show();
    }

}
