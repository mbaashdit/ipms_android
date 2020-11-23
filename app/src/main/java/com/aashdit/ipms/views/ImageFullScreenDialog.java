package com.aashdit.ipms.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.aashdit.ipms.R;
import com.aashdit.ipms.util.Constants;
import com.bumptech.glide.Glide;

import java.util.Objects;

public class ImageFullScreenDialog extends Dialog {


    public Activity c;
    public Dialog d;
//    public String img_url;

    private ImageView imageView,mIvClose;

    public ImageFullScreenDialog(@NonNull Context context, int themeResId,String img_url) {
        super(context, themeResId);
//        super(context);
//        this.c = c;
//        this.d = d;

        d = new Dialog(context, R.style.AppTheme);
        @SuppressLint("InflateParams")
        View dialog = LayoutInflater.from(context).inflate(R.layout.custom_dialog, null);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(d.getWindow()).setBackgroundDrawableResource(R.color.color_alpha);


        imageView = dialog.findViewById(R.id.iv_work_image);
        mIvClose = dialog.findViewById(R.id.iv_close);

        d.show();
        //http://192.168.3.190:8080/ipms/api/workprogress/viewInterimStatusHistoryImages?photoId=1
//        String imgUrl = Constants.BASE_URL+"workprogress/viewInterimStatusHistoryImages?photoId="+img_url;
        Glide.with(context).load(img_url).placeholder(R.drawable.ic_menu_gallery).into(imageView);

        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }


//    public ImageFullScreenDialog(@NonNull Activity a, String url) {
//        super(a);
//        this.c = a;
//        img_url = url;
//    }
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.custom_dialog);
//
//
//    }
}
