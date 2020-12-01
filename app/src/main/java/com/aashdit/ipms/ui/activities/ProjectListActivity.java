package com.aashdit.ipms.ui.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aashdit.ipms.BuildConfig;
import com.aashdit.ipms.R;
import com.aashdit.ipms.adapters.ProjTabAdapter;
import com.aashdit.ipms.adapters.WorkAdapter;
import com.aashdit.ipms.app.App;
import com.aashdit.ipms.databinding.ActivityProjectListBinding;
import com.aashdit.ipms.models.Work;
import com.aashdit.ipms.network.ANetwork;
import com.aashdit.ipms.receiver.ConnectivityChangeReceiver;
import com.aashdit.ipms.util.Constants;
import com.aashdit.ipms.util.SharedPrefManager;
import com.aashdit.ipms.util.Utility;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProjectListActivity extends AppCompatActivity implements WorkAdapter.ProjectListener,
        ConnectivityChangeReceiver.ConnectivityReceiverListener {

    private static final String TAG = "ProjectListActivity";
    ArrayList<Work> works;
    private ActivityProjectListBinding binding;
    private ProjTabAdapter tabAdapter;
    private BottomSheetDialog dialog;
    private BottomSheetBehavior sheetBehavior;
    private SharedPrefManager sp;
    private ProgressBar progressBar;
    private WorkAdapter adapter;

    private ConnectivityChangeReceiver mConnectivityChangeReceiver;
    private boolean isConnected;
    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProjectListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sp = SharedPrefManager.getInstance(this);
        userType = sp.getStringData(Constants.USER_ROLE_CODE);

        ANetwork.setContext(this);

        mConnectivityChangeReceiver = new ConnectivityChangeReceiver();
        mConnectivityChangeReceiver.setConnectivityReceiverListener(this);
        isConnected = mConnectivityChangeReceiver.getConnectionStatus(this);

        registerNetworkBroadcast();
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        progressBar = findViewById(R.id.progress_circular);
        progressBar.setVisibility(View.GONE);

        works = new ArrayList<>();
        getProjectList();

        adapter = new WorkAdapter(this, works);
        adapter.setProjectListener(this);
        binding.rvProjectsList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        binding.rvProjectsList.setAdapter(adapter);
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
    private void getProjectList() {
        progressBar.setVisibility(View.VISIBLE);

        String userId = String.valueOf(sp.getIntData(Constants.USER_ID));
        String token = sp.getStringData(Constants.APP_TOKEN);
        AndroidNetworking.get(BuildConfig.BASE_URL.concat("getProjectDetailsByUserId"))
                .addQueryParameter("userId", userId)
                .addQueryParameter("token", token)
                .setTag("ProjectList")
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
                                    JSONArray jsonArray = jObj.optJSONArray("data");
                                    if (jsonArray != null && jsonArray.length() > 0) {
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            Work work = Work.parseProjects(jsonArray.optJSONObject(i));
                                            works.add(work);

                                        }
                                        adapter.notifyDataSetChanged();
                                    }

                                } else if (status.equals("FAILURE")) {
                                    Toast.makeText(ProjectListActivity.this, jObj.optString("failReason"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }


                    @Override
                    public void onError(ANError anError) {
                        progressBar.setVisibility(View.GONE);
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(String type) {
        if (type.equals("plan")) {
            Intent addPlanIntent = new Intent(this, AddPlanActivity.class);
            startActivity(addPlanIntent);
        } else if (type.equals("progress")) {
            Intent addProgressIntent = new Intent(this, AddProgressActivity.class);

            startActivity(addProgressIntent);
        } else if (type.equals("close")) {
            Toast.makeText(this, "Project Closed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onProjectDetailClick(Work work) {
//        Intent detailsIntent = new Intent(this, ProjectDetailsActivity.class);
//        detailsIntent.putExtra(ProjectDetailsActivity.WORK,work);
//        startActivity(detailsIntent);
        if (userType.equals("ROLE_AGENCY")) {
//            setupBottomSheet(work);
            if (isConnected) {
                Intent compDetailIntent = new Intent(ProjectListActivity.this, NewProjectDetailsActivity.class);
                compDetailIntent.putExtra(NewProjectDetailsActivity.WORK, work);
                startActivity(compDetailIntent);
            }else {
                Snackbar.make(binding.rlProjectListRoot,getResources().getString(R.string.internet_issue),Snackbar.LENGTH_SHORT).show();
            }
        }else {
            if (isConnected) {
                Intent intent = new Intent(this, NewAddPlanActivity.class);
                intent.putExtra(NewAddPlanActivity.PROJECT_NAME, work.projectName);
                intent.putExtra(NewAddPlanActivity.PROJECT_ID, work.projectId);
                startActivity(intent);
            }else {
                Snackbar.make(binding.rlProjectListRoot,getResources().getString(R.string.internet_issue),Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void setupBottomSheet(Work work) {
        View dialogView = getLayoutInflater().inflate(R.layout.bottom_sheet, null);
        dialog = new BottomSheetDialog(this);
        dialog.setContentView(dialogView);
//        sheetBehavior = BottomSheetBehavior.from(dialogView);

//        dialogView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
//                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
////                    btn_bottom_sheet.setText("Close sheet");
//                } else {
//                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
////                    btn_bottom_sheet.setText("Expand sheet");
//                }
//            }
//        });

        Button btnAddPlan = dialogView.findViewById(R.id.bs_btn_add_plan);
        Button btnAddProgress = dialogView.findViewById(R.id.bs_btn_add_progress);

        TextView mTvFinancialYear = dialogView.findViewById(R.id.tv_financial_year);
        TextView mTvProjDesc = dialogView.findViewById(R.id.tv_project_desc);
        TextView title = dialogView.findViewById(R.id.tv_title);
        TextView project_id = dialogView.findViewById(R.id.tv_id);
        TextView project_progress = dialogView.findViewById(R.id.tv_progress_percentage);
        title.setText(work.projectName);
        project_id.setText("Project ID : " + work.projectId);
        project_progress.setText("50%");
        mTvFinancialYear.setText(work.financialYear);
        mTvProjDesc.setText(work.projectDescription);

        btnAddPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent planIntent = new Intent(ProjectListActivity.this, NewAddPlanActivity.class);
                planIntent.putExtra(NewAddPlanActivity.PROJECT_ID, work.projectId);
                planIntent.putExtra(NewAddPlanActivity.PROJECT_NAME, work.projectName);
                startActivity(planIntent);
                dialog.dismiss();
            }
        });

        btnAddProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent progIntent = new Intent(ProjectListActivity.this, NewAddProgressActivity.class);
                progIntent.putExtra(NewAddProgressActivity.PROJECT_ID, work.projectId);
                progIntent.putExtra(NewAddProgressActivity.PROJECT_NAME, work.projectName);
                startActivity(progIntent);
                dialog.dismiss();
            }
        });

//        ImageView camera = dialogView.findViewById(R.id.iv_camera);
//        ImageView gallery = dialogView.findViewById(R.id.iv_gallery);
//        gallery.setOnClickListener(view -> {
//            Intent intent = new Intent();
//            intent.setType("image/*");
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
//        });

//        camera.setOnClickListener(view -> requestCameraPermission());

        dialog.show();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        this.isConnected = isConnected;
//        Toast.makeText(this, "Internet     "+isConnected, Toast.LENGTH_SHORT).show();
    }
}
