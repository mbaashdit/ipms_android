package com.aashdit.ipms.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.aashdit.ipms.R;
import com.aashdit.ipms.databinding.ActivityThankYouBinding;

public class ThankYouActivity extends AppCompatActivity {

    private static final String TAG = "ThankYouActivity";

    private ActivityThankYouBinding bi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = ActivityThankYouBinding.inflate(getLayoutInflater());
        setContentView(bi.getRoot());

        bi.ivHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent homeIntent = new Intent(ThankYouActivity.this,MainActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(homeIntent);
            }
        });
    }
}
