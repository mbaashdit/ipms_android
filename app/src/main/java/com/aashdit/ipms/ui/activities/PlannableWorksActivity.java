package com.aashdit.ipms.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.aashdit.ipms.BuildConfig;
import com.aashdit.ipms.R;
import com.aashdit.ipms.models.PlanableWorks;
import com.aashdit.ipms.util.Constants;
import com.aashdit.ipms.util.SharedPrefManager;
import com.aashdit.ipms.util.Utility;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class PlannableWorksActivity extends AppCompatActivity {


    public static final String PROJECT_ID = "projectId";
    public static final String PROJECT_NAME = "projectName";
    private static final String TAG = "PlannableWorksActivity";
    private RelativeLayout mRlPlannableWorkRoot;
    private int projectId;
    private String projectName;
    private String token;
    private SharedPrefManager sp;
    private ArrayList<PlanableWorks> planableWorks;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plannable_works);

        projectId = getIntent().getIntExtra(PROJECT_ID, -1);
        projectName = getIntent().getStringExtra(PROJECT_NAME);
        mRlPlannableWorkRoot = findViewById(R.id.rl_planable_work_root);
        progressBar = findViewById(R.id.progressBar);

        sp = SharedPrefManager.getInstance(this);
        token = sp.getStringData(Constants.APP_TOKEN);
        planableWorks = new ArrayList<>();


        getPlannableWorksByProjectId();
    }

    private void getPlannableWorksByProjectId() {
        progressBar.setVisibility(View.VISIBLE);
        HashMap<String, Object> reqParam = new HashMap<>();
        reqParam.put("pid", projectId);
        reqParam.put("token", token);

        AndroidNetworking.get(BuildConfig.BASE_URL.concat("workplan/getPlannableWorksByProjectId"))
                .addQueryParameter(reqParam)
                .setTag("PlannableWorkByProjectI")
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
                                    JSONArray jArray = jObj.optJSONArray("data");
                                    if (jArray != null && jArray.length() > 0) {
                                        for (int i = 0; i < jArray.length(); i++) {
                                            PlanableWorks pw = PlanableWorks.parsePlannableWork(jArray.optJSONObject(i));
                                            planableWorks.add(pw);
                                        }
                                    }
                                }else {
                                    Snackbar.make(mRlPlannableWorkRoot,jObj.optString("failReason"),Snackbar.LENGTH_SHORT).show();
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
