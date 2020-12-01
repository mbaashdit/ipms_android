package com.aashdit.ipms.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aashdit.ipms.BuildConfig;
import com.aashdit.ipms.R;
import com.aashdit.ipms.adapters.ProgressableWorkAdapter;
import com.aashdit.ipms.adapters.WorkComponentsAdapter;
import com.aashdit.ipms.models.PlanableWorks;
import com.aashdit.ipms.models.ProgressableWork;
import com.aashdit.ipms.models.Work;
import com.aashdit.ipms.util.Constants;
import com.aashdit.ipms.util.SharedPrefManager;
import com.aashdit.ipms.util.Utility;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NewAddProgressActivity extends AppCompatActivity implements WorkComponentsAdapter.OnWorkComponentListener, ProgressableWorkAdapter.WorkProgressListener {


    public static final String PROJECT_ID = "projectId";
    public static final String PROJECT_NAME = "projectName";
    private static final String TAG = "NewAddProgressActivity";
    private Toolbar toolbar;
    private RecyclerView mRvComponentsList;
    private ArrayList<ProgressableWork> progressableWorks;
    private SharedPrefManager sp;
    private int projectId;
    private String projectName;
    private ProgressableWorkAdapter progressableWorkAdapter;
    private TextView mTvProjectName;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_add_progress);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        projectId = getIntent().getIntExtra(PROJECT_ID, -1);
        projectName = getIntent().getStringExtra(PROJECT_NAME);

        sp = SharedPrefManager.getInstance(this);
        progressableWorks = new ArrayList<>();
        mRvComponentsList = findViewById(R.id.rv_work_components_list);
        mRvComponentsList.setLayoutManager(new LinearLayoutManager(this));

        mTvProjectName = findViewById(R.id.tv_project_name);
        mTvProjectName.setText(projectName);

        for (int i = 0; i < 5; i++) {
//            workArrayList.add(new Work());
        }
        progressableWorkAdapter = new ProgressableWorkAdapter(this, progressableWorks, Constants.PROGRESS);
        progressableWorkAdapter.setWorkProgressListener(this);
//        workComponentsAdapter.setWorkComponentListener(this);
        mRvComponentsList.setAdapter(progressableWorkAdapter);

        getProgressableWork();

    }

    private void getProgressableWork() {
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.get(BuildConfig.BASE_URL.concat("workprogress/getProgressableWorksByProjectId"))
                .addQueryParameter("pid",String.valueOf(projectId))
                .addQueryParameter("token",sp.getStringData(Constants.APP_TOKEN))
                .setTag("PlannableWorkByProjectId")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        if (Utility.isStringValid(response)) {
                            if (response.startsWith("{")){
                                try {
                                    JSONObject responseObj = new JSONObject(response);
                                    String status = responseObj.optString("status");
                                    if (status.equals("SUCCESS")) {
                                        JSONArray jsonArray = responseObj.optJSONArray("data");
                                        if (jsonArray != null && jsonArray.length() > 0) {
                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                ProgressableWork work = ProgressableWork.parseProgressableWork(jsonArray.optJSONObject(i));
                                                progressableWorks.add(work);
                                            }
                                            progressableWorkAdapter.notifyDataSetChanged();
                                        }else {
                                            Toast.makeText(NewAddProgressActivity.this, "No Planable work found", Toast.LENGTH_SHORT).show();
                                        }

                                    } else if (status.equals("FAILURE")) {
                                        Toast.makeText(NewAddProgressActivity.this, responseObj.optString("failReason"), Toast.LENGTH_SHORT).show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }else {
                            try {
                                JSONArray responseArray = new JSONArray(response);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        progressBar.setVisibility(View.GONE);
                        Log.e(TAG, "onError: "+anError.getErrorDetail() );
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onProgressClick(ProgressableWork progressableWork) {
        Intent intent = new Intent(this, WorkPhasesActivity.class);
        intent.putExtra("TYPE", "PROGRESS");
        intent.putExtra("COMP_NAME",progressableWork.workComponentName);
        intent.putExtra(WorkPhasesActivity.WORK_ID,progressableWork.workId);
        startActivity(intent);
    }

    @Override
    public void workComponentClick(String type, int position, String compName) {
        Intent intent = new Intent(this, WorkPhasesActivity.class);
        intent.putExtra("TYPE", type);
        intent.putExtra("COMP_NAME",compName);
        startActivity(intent);
    }
}
