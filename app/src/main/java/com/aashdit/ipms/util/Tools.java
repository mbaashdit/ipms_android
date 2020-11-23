package com.aashdit.ipms.util;

import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;

import com.aashdit.ipms.R;


public class Tools {

    public static void setStatus(Activity activity) {
        if (android.os.Build.VERSION.SDK_INT >= 21 && android.os.Build.VERSION.SDK_INT < 23) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.colorBlack));
        } else if (android.os.Build.VERSION.SDK_INT >= 23) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.colorWhite));
        }
    }

    public static void hideStatus(Activity activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }
}
