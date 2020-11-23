package com.aashdit.ipms.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.aashdit.ipms.R;
import com.aashdit.ipms.util.Constants;
import com.aashdit.ipms.util.SharedPrefManager;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    private Toolbar toolbar;
    private EditText mEtFirstName,mEtLastName,mEtEmail,mEtPhone,mEtJobDescription,mEtUserName;
    private TextView mTvDesc;
    private SharedPrefManager sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sp = SharedPrefManager.getInstance(this);

        toolbar = findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mEtFirstName  =findViewById(R.id.et_profile_first_name);
        mEtLastName  =findViewById(R.id.et_profile_last_name);
        mEtEmail  =findViewById(R.id.et_profile_email);
        mEtPhone  =findViewById(R.id.et_profile_mobile_no);
        mEtUserName = findViewById(R.id.et_profile_user_name);
//        mTvDesc = findViewById(R.id.tv_profile_job_description);
        mEtJobDescription =  findViewById(R.id.et_profile_job_description);

        mEtFirstName.setEnabled(false);
        mEtLastName.setEnabled(false);
        mEtEmail.setEnabled(false);
        mEtPhone.setEnabled(false);
        mEtUserName.setEnabled(false);
        mEtJobDescription.setEnabled(false);

        mEtFirstName.setText(sp.getStringData(Constants.USER_FIRST_NAME));
        mEtLastName.setText(sp.getStringData(Constants.USER_LAST_NAME));
        mEtUserName.setText(sp.getStringData(Constants.USER_NAME));
        mEtEmail.setText(sp.getStringData(Constants.USER_EMAIL));
        mEtPhone.setText(sp.getStringData(Constants.USER_MOBILE));
        mEtJobDescription.setText(sp.getStringData(Constants.USER_ROLE_DESCRIPTION));

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

}
