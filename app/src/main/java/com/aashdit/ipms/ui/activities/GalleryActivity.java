package com.aashdit.ipms.ui.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aashdit.ipms.BuildConfig;
import com.aashdit.ipms.R;
import com.aashdit.ipms.adapters.GalleryAdapter;
import com.aashdit.ipms.adapters.ProgressAdapter;
import com.aashdit.ipms.models.Progress;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    private static final String TAG = "GalleryActivity";

    private Toolbar toolbar;
    private RecyclerView mRvGallery;
    private int workPlanId;
    private String token;

    private ProgressBar progressBar;
    private SharedPrefManager sp;
    private int userId;
    private ArrayList<Progress> progressArrayList = new ArrayList<>();
    private GalleryAdapter galleryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        toolbar = findViewById(R.id.toolbar_gallery);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        sp = SharedPrefManager.getInstance(this);
        token = sp.getStringData(Constants.APP_TOKEN);
        workPlanId = getIntent().getIntExtra("workPlanId", -1);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        mRvGallery = findViewById(R.id.rv_gallery);
        mRvGallery.setLayoutManager(new LinearLayoutManager(this));
        getProgressDetailsByPlanId();

        galleryAdapter = new GalleryAdapter(this, progressArrayList,this);
        mRvGallery.setAdapter(galleryAdapter);
        userId = sp.getIntData(Constants.USER_ID);

    }


    private void getProgressDetailsByPlanId() {
        progressBar.setVisibility(View.VISIBLE);
        HashMap<String, Object> reqParam = new HashMap<>();
        reqParam.put("wpid", String.valueOf(workPlanId));
        reqParam.put("token", token);

        AndroidNetworking.get(BuildConfig.BASE_URL.concat("workprogress/getProgressDetailsByPlanId"))
                .addQueryParameter(reqParam)
                .setTag("ProgressableWorkByWorkId")
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
                                    JSONObject dataObj = jObj.optJSONObject("data");
                                    if (dataObj != null) {
                                        String duration = dataObj.optString("duration");

                                        String esd = dataObj.optString("estimatedStartDate");
                                        String eed = dataObj.optString("estimatedEndDate");

                                        if (!esd.equals("")) {
                                            List<String> startDate = Arrays.asList(esd.split("/"));
                                            String day = startDate.get(0);
                                            String _month = startDate.get(1);
                                            String year = startDate.get(2);

                                            String month = Utility.convertMonthToWord(_month);

                                        }
                                        if (!eed.equals("")) {
                                            List<String> startDate = Arrays.asList(eed.split("/"));
                                            String day = startDate.get(0);
                                            String _month = startDate.get(1);
                                            String year = startDate.get(2);

                                            String month = Utility.convertMonthToWord(_month);

                                        }


                                        JSONArray statusArray = dataObj.optJSONArray("statusHistory");
                                        if (statusArray != null && statusArray.length() > 0) {
                                            progressArrayList.clear();
                                            for (int i = 0; i < statusArray.length(); i++) {
                                                Progress pro = Progress.parseProgress(statusArray.optJSONObject(i));
                                                progressArrayList.add(pro);
                                                galleryAdapter.notifyDataSetChanged();
                                            }

                                        }
                                        String progress = dataObj.optString("progress");
                                        String currentstatus = dataObj.optString("currentStatus");

                                    }
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


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}