package com.aashdit.ipms.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.aashdit.ipms.R;
import com.aashdit.ipms.util.Constants;
import com.aashdit.ipms.util.SharedPrefManager;
import com.aashdit.ipms.util.Utility;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class SaveWorkPlanActivity extends AppCompatActivity {

    private static final String TAG = "SaveWorkPlanActivity";

    private TextView mTvWorkPhaseId;
    private TextView mTvWorkId;
    private TextView mTvStartDate;
    private TextView mTvEndDate;
    private Button mBtnSaveWorkPlan;
    private ProgressBar progressBar;
    private RelativeLayout mRlSaveWorkPlanRoot;

    private int workPhaseId, workId, workPlanId;
    private String startDate, endDate;

    private int userId;
    private String token;

    private SharedPrefManager sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_work_plan);

        mRlSaveWorkPlanRoot = findViewById(R.id.rl_save_work_plan_root);
        mTvWorkPhaseId = findViewById(R.id.tv_work_phase_id);
        mTvWorkId = findViewById(R.id.tv_work_id);
        mTvStartDate = findViewById(R.id.tv_est_start_date);
        mTvEndDate = findViewById(R.id.tv_est_end_date);
        mBtnSaveWorkPlan = findViewById(R.id.btn_save_work);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        sp = SharedPrefManager.getInstance(this);
        userId = sp.getIntData(Constants.USER_ID);
        token = sp.getStringData(Constants.APP_TOKEN);

        workId = getIntent().getIntExtra("workPhaseId", -1);
        workPhaseId = getIntent().getIntExtra("workId", -1);
        workPlanId = getIntent().getIntExtra("workPlanId", -1);
        startDate = getIntent().getStringExtra("est_start_date");
        endDate = getIntent().getStringExtra("est_end_date");

        mTvWorkPhaseId.setText("Work Phase Id : " + workPhaseId);
        mTvWorkId.setText("Work Id : " + workId);
        mTvStartDate.setText("Est. Start Date" + startDate);
        mTvEndDate.setText("Est. End Date" + endDate);

        mTvEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO show calender
            }
        });

        mTvStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO show calender
            }
        });

        mBtnSaveWorkPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startDate = "";
//                endDate = "";
                saveWorkApi();
            }
        });

    }

    private void saveWorkApi() {
        progressBar.setVisibility(View.VISIBLE);
        HashMap<String, Object> reqParam = new HashMap<>();
        reqParam.put("userId", String.valueOf(userId));
        reqParam.put("token", token);

        JSONObject jsonObject = new JSONObject();
        if (workPlanId == -1) {
            try {
                jsonObject.put("workPhaseId", workPhaseId);
                jsonObject.put("workId", workId);
                jsonObject.put("estimatedStartDate", startDate);
                jsonObject.put("estimatedEndDate", endDate);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                jsonObject.put("workPlanId", workPlanId);
                jsonObject.put("workPhaseId", workPhaseId);
                jsonObject.put("workId", workId);
                jsonObject.put("estimatedStartDate", startDate);
                jsonObject.put("estimatedEndDate", endDate);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        AndroidNetworking.post(Constants.BASE_URL.concat("workplan/saveWorkPlan"))
                .addQueryParameter(reqParam)
                .addPathParameter(jsonObject)
                .setTag("SaveWorkPlan")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        if (Utility.isStringValid(response)) {
                            try {
                                JSONObject jObj = new JSONObject(response);
                                String status = jObj.optString("status");
                                if (status.equals("SUCCESS")) {
                                    Snackbar.make(mRlSaveWorkPlanRoot, status, Snackbar.LENGTH_SHORT).show();
                                    JSONObject obj = jObj.optJSONObject("data");
                                    if (obj != null) {
//                                        workPlanId = obj.optInt("workPlanId");
                                    }

                                } else {
                                    Snackbar.make(mRlSaveWorkPlanRoot, jObj.optString("failReason"), Snackbar.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }
}
