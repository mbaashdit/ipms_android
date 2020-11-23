package com.aashdit.ipms.views;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.aashdit.ipms.R;
import com.bumptech.glide.Glide;

import java.util.Calendar;
import java.util.Objects;

public class CloseDialog {

    private Dialog closeStoreDialog;
    private EditText mStoreCash, mStore_damaged_item, mStoreCloseTime, mStoreComment;

    private ImageView imageView,mIvClose;

    public CloseDialog(Context context,String img_url) {
        closeStoreDialog = new Dialog(context, R.style.AppTheme);
        @SuppressLint("InflateParams")
        View dialog = LayoutInflater.from(context).inflate(R.layout.custom_dialog, null);
        closeStoreDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(closeStoreDialog.getWindow()).setBackgroundDrawableResource(R.color.color_alpha);
        imageView = dialog.findViewById(R.id.iv_work_image);
        mIvClose = dialog.findViewById(R.id.iv_close);

        closeStoreDialog.setContentView(dialog);
        closeStoreDialog.setCancelable(true);
        closeStoreDialog.setCanceledOnTouchOutside(true);
        closeStoreDialog.show();
        Glide.with(context).load(img_url).placeholder(R.drawable.ic_menu_gallery).into(imageView);
        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeStoreDialog.dismiss();
            }
        });
    }

    private void hideCloseDialog() {
        closeStoreDialog.dismiss();
    }

}
