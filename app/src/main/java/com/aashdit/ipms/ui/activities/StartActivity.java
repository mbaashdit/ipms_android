package com.aashdit.ipms.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.aashdit.ipms.R;
import com.aashdit.ipms.databinding.ActivityStartBinding;
import com.aashdit.ipms.util.SharedPrefManager;

public class StartActivity extends AppCompatActivity {

    private static final String TAG = "StartActivity";

    private ActivityStartBinding bi;
    private SharedPrefManager sp;
    private Handler handler;
    private boolean isLogin;
    private Runnable runnable = this::moveToLogin;
    private void moveToLogin() {
        startActivity(new Intent(StartActivity.this, LoginActivity.class));
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = ActivityStartBinding.inflate(getLayoutInflater());
        setContentView(bi.getRoot());

        sp = SharedPrefManager.getInstance(this);
        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/Montserrat-SemiBold.ttf");
        bi.btnStarted.setTypeface(typeface);
        isLogin = sp.getBoolData("EMPTY");

        handler = new Handler();
        int SPLASH_TIME = 2000;
        handler.postDelayed(runnable, SPLASH_TIME);

        if (isLogin){
            bi.btnStarted.setVisibility(View.GONE);
        }else {
            bi.btnStarted.setVisibility(View.VISIBLE);
        }


        bi.btnStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp.setBoolData("EMPTY",true);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}
