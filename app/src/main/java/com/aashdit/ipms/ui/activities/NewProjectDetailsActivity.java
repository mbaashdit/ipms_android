package com.aashdit.ipms.ui.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.aashdit.ipms.R;
import com.aashdit.ipms.app.App;
import com.aashdit.ipms.models.Work;
import com.aashdit.ipms.receiver.ConnectivityChangeReceiver;
import com.aashdit.ipms.util.Constants;
import com.aashdit.ipms.util.SharedPrefManager;
import com.aashdit.ipms.util.Utility;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NewProjectDetailsActivity extends AppCompatActivity implements
        ConnectivityChangeReceiver.ConnectivityReceiverListener{

    public static final String WORK = "WORK";
    private static final String TAG = "NewProjectDetailsActivi";
    private ImageView mIvBack;
    private TextView mTvProjectName;
    private TextView mTvProjectId;
    private TextView mTvSchemeName;
    private TextView mTvProjectDesc;
    private TextView mTvEstStartDate;
    private TextView mTvEstEndDate;
    private TextView mTvFy;
    private TextView mtvShowComponents;
    private TextView mTvEstCost, mTvRevEstCost;
    private RelativeLayout mRlShowComponent;
    private SharedPrefManager sp;
    private RelativeLayout mRlNewProjectDetailsRoot;

    private Work selectedWork;

    private ConnectivityChangeReceiver mConnectivityChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_project_details);

        selectedWork = getIntent().getParcelableExtra(WORK);
        sp = SharedPrefManager.getInstance(this);
        mConnectivityChangeReceiver = new ConnectivityChangeReceiver();
        mConnectivityChangeReceiver.setConnectivityReceiverListener(this);
        isInternet = mConnectivityChangeReceiver.getConnectionStatus(this);
        registerNetworkBroadcast();
        mIvBack = findViewById(R.id.iv_back_prj_detail);
        mTvProjectName = findViewById(R.id.tv_comp_name);
        mTvSchemeName = findViewById(R.id.tv_scheme_name);
        mTvProjectId = findViewById(R.id.tv_comp_id);
        mTvProjectDesc = findViewById(R.id.tv_project_description);
        mRlShowComponent = findViewById(R.id.rl_show_component);
        mTvEstStartDate = findViewById(R.id.tv_pd_est_start_date);
        mTvEstEndDate = findViewById(R.id.tv_pd_est_end_date);
        mTvFy = findViewById(R.id.tv_fy);
        mTvEstCost = findViewById(R.id.tv_est_budge);
        mTvRevEstCost = findViewById(R.id.tv_revised_budge);
        mtvShowComponents = findViewById(R.id.tv_show_components);
        mRlNewProjectDetailsRoot = findViewById(R.id.rl_new_project_detail_root);
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        if (selectedWork != null) {
            mTvProjectName.setText(selectedWork.projectName);
            mTvProjectId.setText(selectedWork.projectCode);
            mTvSchemeName.setText(selectedWork.schemeName);
            mTvProjectDesc.setText(selectedWork.projectDescription);
            mTvEstStartDate.setText(Utility.convertTO_dd_MMM_YYYY(selectedWork.estmiatedStartDate));//selectedWork.estmiatedStartDate
            mTvEstEndDate.setText(Utility.convertTO_dd_MMM_YYYY(selectedWork.estimatedEndDate));
            mTvFy.setText(selectedWork.financialYear);
            Utility.stringDateToLong(selectedWork.estmiatedStartDate);
            Utility.stringDateToLong(selectedWork.estimatedEndDate);

            Log.i(TAG, "onCreate: EC ::: " + selectedWork.estimatedBudget);


            mTvEstCost.setText(Utility.rupeeFormat(selectedWork.estimatedBudget));
            mTvRevEstCost.setText(Utility.rupeeFormat(selectedWork.revisedEstimatedBudget));
        }

        mtvShowComponents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent planIntent = new Intent(NewProjectDetailsActivity.this, NewAddPlanActivity.class);
                if (selectedWork != null) {
                    if (isInternet) {
                        Log.i(TAG, "onClick: " + selectedWork.projectId);
                        planIntent.putExtra(NewAddPlanActivity.PROJECT_ID, selectedWork.projectId);
                        planIntent.putExtra(NewAddPlanActivity.PROJECT_NAME, selectedWork.projectName);
                        planIntent.putExtra(NewAddPlanActivity.PROJECT_ESD, selectedWork.estmiatedStartDate);
                        planIntent.putExtra(NewAddPlanActivity.PROJECT_EED, selectedWork.estimatedEndDate);
                        sp.setStringData(Constants.ESD, selectedWork.estmiatedStartDate);
                        sp.setStringData(Constants.EED, selectedWork.estimatedEndDate);
                        sp.setStringData(Constants.PROJECT_NAME, selectedWork.projectName);
                        sp.setIntData(Constants.PROJECT_ID, selectedWork.projectId);
                        startActivity(planIntent);
                    }else {
                        Snackbar.make(mRlNewProjectDetailsRoot,getResources().getString(R.string.internet_issue), Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private boolean isInternet;
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        isInternet = isConnected;
    }

    private void registerNetworkBroadcast() {
        registerReceiver(mConnectivityChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mConnectivityChangeReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        App.getInstance().setConnectivityListener(this);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterNetworkChanges();
    }
}
