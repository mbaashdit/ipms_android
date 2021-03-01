package com.aashdit.ipms.ui.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aashdit.ipms.BuildConfig;
import com.aashdit.ipms.R;
import com.aashdit.ipms.adapters.CapturedImageAdapter;
import com.aashdit.ipms.adapters.ProgressAdapter;
import com.aashdit.ipms.app.App;
import com.aashdit.ipms.databinding.ActivityWorkDetailBinding;
import com.aashdit.ipms.models.ClickedImage;
import com.aashdit.ipms.models.Progress;
import com.aashdit.ipms.network.ANetwork;
import com.aashdit.ipms.receiver.ConnectivityChangeReceiver;
import com.aashdit.ipms.util.Constants;
import com.aashdit.ipms.util.ImageUtil;
import com.aashdit.ipms.util.SharedPrefManager;
import com.aashdit.ipms.util.Utility;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class WorkDetailActivity extends AppCompatActivity implements LocationListener,
        ConnectivityChangeReceiver.ConnectivityReceiverListener {

    private static final String TAG = "WorkDetailActivity";

    private final int CAMERA_REQ_CODE = 100;
    private final int SETTINGS_REQ_CODE = 101;
    final Calendar myCalendar = Calendar.getInstance();
    private Toolbar toolbar;
    private ImageView mIvPlay;
    private ImageView mIvPause;
    private ImageView mIvStop;
    private ImageView mIvRestart;
    private ImageView mIvPdf;
    private TextView mTvWorkStatus;
    private BottomSheetDialog dialog;
    private RecyclerView mRvProgress;
    private TextView mTvDuration;
    private TextView mTvProgress;

    private ProgressBar progressBar;
    private SharedPrefManager sp;
//    private String estStartDate, estEndDate;
    private String token;
    private int workId, workPlanId;
    private ProgressAdapter progressAdapter;
    private TextView mTvPhaseName;
    private TextView mTvActualProgress;

    private TextView mTvEstStartDay, mTvEstStartMonYear;
    private TextView mTvEstEndDay, mTvEstEndMonYear;

    private TextView mTvFailReason;

    private ArrayList<Progress> progressArrayList = new ArrayList<>();
    private int userId, projectId;

    private ImageView mIvWorkStatus;
    private ImageView mIvActualProgress;

    private AppBarLayout mAppbar;
    private RelativeLayout mRlWorkDetailRoot, mRlCompleted;
    private View view;
    private LinearLayout mLlWorkDetail;
    private RelativeLayout mRlStatusHistory;

    private String projName, compName, phaseName;
    /**
     * STATUS as int
     * <p>
     * 0 = stop
     * 1 = start
     * 2 = pause
     * 3 = restart
     */

    private LinearLayoutManager linearLayoutManager;
    private RelativeLayout mRlWorkDetail;

    private boolean isDateSelected = false;
    private LocationManager locationManager;
    private ConnectivityChangeReceiver mConnectivityChangeReceiver;
    private boolean isConnected;
//    private double latitude, longitude;

    private ActivityWorkDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWorkDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mConnectivityChangeReceiver = new ConnectivityChangeReceiver();
        mConnectivityChangeReceiver.setConnectivityReceiverListener(this);
        isConnected = mConnectivityChangeReceiver.getConnectionStatus(this);

        registerNetworkBroadcast();
        sp = SharedPrefManager.getInstance(this);
        token = sp.getStringData(Constants.APP_TOKEN);
        projectId = sp.getIntData(Constants.PROJECT_ID);
//        boolean status = getIntent().getBooleanExtra("STATUS", false);
//        estStartDate = getIntent().getStringExtra("est_start_date");
//        estEndDate = getIntent().getStringExtra("est_end_date");
        workId = getIntent().getIntExtra("workId", -1);
        workPlanId = getIntent().getIntExtra("workPlanId", -1);

        projName = getIntent().getStringExtra("projName");
        compName = getIntent().getStringExtra("compName");
        phaseName = getIntent().getStringExtra("phaseName");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        mTvFailReason = findViewById(R.id.tv_fail_reason);
        mTvFailReason.setVisibility(View.GONE);
        mAppbar = findViewById(R.id.appBarLayout);
        mRlWorkDetail = findViewById(R.id.rl_work_detail);
        mLlWorkDetail = findViewById(R.id.ll_work_detail);
        ImageView mIvGallery = findViewById(R.id.iv_gallery);

        mRlStatusHistory = findViewById(R.id.rl_status_his_lbl);

        TextView mTvProjNameBc = findViewById(R.id.tv_proj_name);
        mTvProjNameBc.setText(projName);

        TextView mTvCompNameBc = findViewById(R.id.tv_comp_name);
        mTvCompNameBc.setText(compName);

        TextView mTvPhaseNameBc = findViewById(R.id.tv_phase_name);
        mTvPhaseNameBc.setText(phaseName);

        mRlCompleted = findViewById(R.id.rl_completed);

        mRlCompleted.setVisibility(View.GONE);

        mIvPlay = findViewById(R.id.iv_play);
        mIvPause = findViewById(R.id.iv_pause);
        mIvStop = findViewById(R.id.iv_stop);
        mIvRestart = findViewById(R.id.iv_restart);
        view = findViewById(R.id.view_temp);
        mIvPdf = findViewById(R.id.iv_pdf);

        mIvActualProgress = findViewById(R.id.iv_actual_progress);

        mIvWorkStatus = findViewById(R.id.iv_work_status_img);
        mRlWorkDetailRoot = findViewById(R.id.rl_work_detail_root);
        mRlWorkDetailRoot.setVisibility(View.GONE);

        mTvWorkStatus = findViewById(R.id.tv_progress_state);
//        TextView mTvEstStartDate = findViewById(R.id.tv_est_start_date);
//        TextView mTvEstEndDate = findViewById(R.id.monSemiBold3);
        mTvDuration = findViewById(R.id.tv_est_duration);
        mTvProgress = findViewById(R.id.tv_wp_progress);
        mTvPhaseName = findViewById(R.id.tv_project_name);

        mTvActualProgress = findViewById(R.id.tv_actual_progress);
        mTvEstStartDay = findViewById(R.id.tv_esday);
        mTvEstStartMonYear = findViewById(R.id.tv_esmonth_yr);

        mTvEstEndDay = findViewById(R.id.tv_eeday);
        mTvEstEndMonYear = findViewById(R.id.tv_eemonth_yr);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        mRvProgress = findViewById(R.id.progress_recyclerview);
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        mRvProgress.setLayoutManager(linearLayoutManager);

        mIvStop.setVisibility(View.GONE);
        mIvPause.setVisibility(View.GONE);
        mIvRestart.setVisibility(View.GONE);
        mIvPlay.setVisibility(View.GONE);

        startDateInLong = Utility.stringDateToLong(sp.getStringData(Constants.ESD));
        endDateInLong = Utility.stringDateToLong(sp.getStringData(Constants.EED));
        mIvPdf.setOnClickListener(v -> callProgressReportDownloadApi());

        mIvGallery.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(WorkDetailActivity.this, GalleryActivity.class);
            galleryIntent.putExtra("workPlanId", workPlanId);
            startActivity(galleryIntent);
        });

        mIvPause.setOnClickListener(view -> {

            // to pause the work status code 2
//                setupBottomSheet(2);
            setupPauseBottomSheet();
        });

        mIvStop.setOnClickListener(view -> {

            // to stop the work status code 0
//                setupBottomSheet(0);
            setupStopBottomSheet();
        });

        mIvPlay.setOnClickListener(view -> {

            // to start the work status code 1
//                setupBottomSheet(1);
            setupStartBottomsheet();
        });

        mIvRestart.setOnClickListener(view -> {

            // to restart the work status code 3
//                setupBottomSheet(3);
            setupRestartBottomsheet();
        });

        mIvActualProgress.setOnClickListener(view -> setupActualProgress());

        ANetwork.setContext(this);
        if (isConnected) {
            getProgressDetailsByPlanId();
        } else {
            Toast.makeText(this, getResources().getString(R.string.internet_issue), Toast.LENGTH_SHORT).show();
        }

        progressAdapter = new ProgressAdapter(this, progressArrayList);
        mRvProgress.setAdapter(progressAdapter);
        userId = sp.getIntData(Constants.USER_ID);


        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            // do your work now
//                                Toast.makeText(getApplicationContext(), "All permissions are granted!", Toast.LENGTH_SHORT).show();
//                            handler.postDelayed(runnable, 2000);

                            getLocation();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permenantly, navigate user to app settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }

    private void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            assert locationManager != null;
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
        } catch (SecurityException e) {
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

    private void callProgressReportDownloadApi() {

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            // do you work now
                            if (isConnected) {
                                downloadFile();
                            } else {
                                Toast.makeText(WorkDetailActivity.this, getResources().getString(R.string.internet_issue), Toast.LENGTH_SHORT).show();
                            }
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permenantly, navigate user to app settings
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }

    public void downloadFile() {

        File dir = Environment.getExternalStoragePublicDirectory(Environment
                .DIRECTORY_DOWNLOADS);
        String dirPath = dir.getAbsolutePath();
        Log.i(TAG, "downloadDoc: " + dir);
        Log.i(TAG, "downloadDoc: " + dirPath);

        Date date = new Date(System.currentTimeMillis());
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        String dateFormatted = formatter.format(date);
        String prName;
        String phName;
        if (projName.length() <= 3 || phaseName.length() <= 3) {
            prName = projName;
            phName = phaseName;
        } else {
            prName = projName.substring(0, 3);
            phName = phaseName.substring(0, 3);
        }


        String DownloadUrl = BuildConfig.BASE_URL + "workprogress/getWorkProgressReportByProjectId?projectId=".concat(String.valueOf(projectId));
        DownloadManager.Request request1 = new DownloadManager.Request(Uri.parse(DownloadUrl));
        request1.setDescription("Sample PDF File");   //appears the same in Notification bar while downloading
        request1.setTitle("IPMS Progress Status");
        request1.setMimeType("application/pdf");
        request1.setVisibleInDownloadsUi(true);

        request1.allowScanningByMediaScanner();
        request1.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request1.setDestinationInExternalFilesDir(getApplicationContext(), "/IPMS", "ipms_" + prName + "_" + phName + "_" + dateFormatted + ".pdf");

        DownloadManager manager1 = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Objects.requireNonNull(manager1).enqueue(request1);
        if (DownloadManager.STATUS_SUCCESSFUL == 8) {
            Toast.makeText(this, "Download success", Toast.LENGTH_SHORT).show();
        }
    }

    private void progressPaused(String reason, String date, ArrayList<ClickedImage> base64Strings) {
        progressBar.setVisibility(View.VISIBLE);
        HashMap<String, Object> reqParam = new HashMap<>();
        reqParam.put("userId", String.valueOf(userId));
        reqParam.put("token", token);

        JSONArray tempImgArray = new JSONArray();
        JSONObject reqObj = new JSONObject();
        if (base64Strings != null && base64Strings.size() > 0) {
            for (int i = 0; i < base64Strings.size(); i++) {
                tempImgArray.put(ImageUtil.convert(base64Strings.get(i).getImageCaptured()));
            }
        }
        if ( App.latitude == 0.0 ||  App.longitude == 0.0) {
            Toast.makeText(this, "GETTING LOCATION. PLEASE WAIT", Toast.LENGTH_SHORT).show();
        } else {


            try {
                reqObj.put("workPlanId", workPlanId);
                reqObj.put("actionDate", date);
                reqObj.put("remarks", reason);
                reqObj.put("photos", tempImgArray);
                reqObj.put("latitude", String.valueOf( App.latitude));
                reqObj.put("longitude", String.valueOf( App.longitude));
                reqObj.put("latLngArea", capturedAddress);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONObject dataObj = new JSONObject();
            try {
                dataObj.put("data", reqObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            AndroidNetworking.post(BuildConfig.BASE_URL.concat("workprogress/pauseWorkPlan"))
                    .addQueryParameter(reqParam)
                    .addJSONObjectBody(dataObj)
                    .setTag("stopWorkPlan")
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
                                        dialog.dismiss();
                                        mTvActualProgress.setTextColor(getResources().getColor(R.color.toolbar_title_color));
                                        mIvPause.setVisibility(View.GONE);
                                        mIvRestart.setVisibility(View.VISIBLE);
                                        mIvStop.setVisibility(View.VISIBLE);
                                        mIvPlay.setVisibility(View.GONE);
                                        clickedImages.clear();


                                        mRvProgress.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                            @Override
                                            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                                            if (dy > 0 || dy < 0 && mIvRestart.isShown() && mIvStop.isShown()) {
//                                                fab.hide();
//                                                mIvStop.setVisibility(View.GONE);
//                                                mIvRestart.setVisibility(View.GONE);
//                                            }
                                            }

                                            @Override
                                            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                                            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                                                fab.show();
//                                                mIvStop.setVisibility(View.GONE);
//                                                mIvRestart.setVisibility(View.VISIBLE);
//                                            }
                                                super.onScrollStateChanged(recyclerView, newState);
                                            }
                                        });


                                        getProgressDetailsByPlanId();
                                    } else {
                                        Toast.makeText(WorkDetailActivity.this, jObj.optString("failReason"), Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.e(TAG, "onError: " + anError.getErrorDetail());
                            progressBar.setVisibility(View.GONE);
                        }
                    });
        }
    }

    private void progressStart(String date, ArrayList<ClickedImage> base64Strings) {
        progressBar.setVisibility(View.VISIBLE);
        HashMap<String, Object> reqParam = new HashMap<>();
        reqParam.put("userId", String.valueOf(userId));
        reqParam.put("token", token);

        JSONArray tempImgArray = new JSONArray();
        if (base64Strings != null && base64Strings.size() > 0) {
            for (int i = 0; i < base64Strings.size(); i++) {
                tempImgArray.put(ImageUtil.convert(base64Strings.get(i).getImageCaptured()));
            }
        }

        JSONObject reqObj = new JSONObject();
        try {
            reqObj.put("workPlanId", workPlanId);
            reqObj.put("actionDate", date);
            reqObj.put("photos", tempImgArray);
            reqObj.put("latitude", String.valueOf( App.latitude));
            reqObj.put("longitude", String.valueOf( App.longitude));
            reqObj.put("latLngArea", capturedAddress);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject dataObj = new JSONObject();
        try {
            dataObj.put("data", reqObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(BuildConfig.BASE_URL.concat("workprogress/startWorkPlan"))
                .addQueryParameter(reqParam)
                .addJSONObjectBody(dataObj)
                .setTag("stopWorkPlan")
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
                                    dialog.dismiss();
                                    mIvStop.setVisibility(View.VISIBLE);
                                    mIvPause.setVisibility(View.VISIBLE);
                                    mIvPlay.setVisibility(View.GONE);
                                    mIvRestart.setVisibility(View.GONE);

                                    getProgressDetailsByPlanId();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                        progressBar.setVisibility(View.GONE);
                    }
                });

    }

    private void progressResume(String date, ArrayList<ClickedImage> base64Strings) {
        progressBar.setVisibility(View.VISIBLE);
        HashMap<String, Object> reqParam = new HashMap<>();
        reqParam.put("userId", String.valueOf(userId));
        reqParam.put("token", token);

        JSONArray tempImgArray = new JSONArray();
        if (base64Strings != null && base64Strings.size() > 0) {
            for (int i = 0; i < base64Strings.size(); i++) {
                tempImgArray.put(ImageUtil.convert(base64Strings.get(i).getImageCaptured()));
            }
        }
        JSONObject reqObj = new JSONObject();
        try {
            reqObj.put("workPlanId", workPlanId);
            reqObj.put("actionDate", date);
            reqObj.put("photos", tempImgArray);
            reqObj.put("latitude", String.valueOf( App.latitude));
            reqObj.put("longitude", String.valueOf( App.longitude));
            reqObj.put("latLngArea", capturedAddress);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject dataObj = new JSONObject();
        try {
            dataObj.put("data", reqObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AndroidNetworking.post(BuildConfig.BASE_URL.concat("workprogress/resumeWorkPlan"))
                .addQueryParameter(reqParam)
                .addJSONObjectBody(dataObj)
                .setTag("stopWorkPlan")
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
                                    dialog.dismiss();

                                    mIvStop.setVisibility(View.VISIBLE);
                                    mIvPause.setVisibility(View.VISIBLE);
                                    mIvPlay.setVisibility(View.GONE);
                                    mIvRestart.setVisibility(View.GONE);

                                    getProgressDetailsByPlanId();
                                } else {
                                    Toast.makeText(WorkDetailActivity.this, jObj.optString("failReason"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                        progressBar.setVisibility(View.GONE);
                    }
                });

    }

    private void progressStop(String reason, String date, ArrayList<ClickedImage> base64Strings) {
        progressBar.setVisibility(View.VISIBLE);
        HashMap<String, Object> reqParam = new HashMap<>();
        reqParam.put("userId", String.valueOf(userId));
        reqParam.put("token", token);


        JSONArray tempImgArray = new JSONArray();
        if (base64Strings != null && base64Strings.size() > 0) {
            for (int i = 0; i < base64Strings.size(); i++) {
                tempImgArray.put(ImageUtil.convert(base64Strings.get(i).getImageCaptured()));
            }
        }

        JSONObject reqObj = new JSONObject();
        try {
            reqObj.put("workPlanId", workPlanId);
            reqObj.put("actionDate", date);
            reqObj.put("remarks", reason);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject dataObj = new JSONObject();
        try {
            dataObj.put("data", reqObj);
            reqObj.put("photos", tempImgArray);
            reqObj.put("latitude", String.valueOf( App.latitude));
            reqObj.put("longitude", String.valueOf( App.longitude));
            reqObj.put("latLngArea", capturedAddress);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AndroidNetworking.post(BuildConfig.BASE_URL.concat("workprogress/stopWorkPlan"))
                .addQueryParameter(reqParam)
                .addJSONObjectBody(dataObj)
                .setTag("stopWorkPlan")
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
                                    dialog.dismiss();
                                    mIvRestart.setVisibility(View.GONE);
                                    mIvPlay.setVisibility(View.GONE);
                                    mIvPause.setVisibility(View.GONE);
                                    mIvStop.setVisibility(View.GONE);
                                    mTvActualProgress.setTextColor(getResources().getColor(R.color.colorStopProgress));
                                    mTvWorkStatus.setTextColor(getResources().getColor(R.color.colorStopStatus));
                                    getProgressDetailsByPlanId();
                                } else {
                                    Toast.makeText(WorkDetailActivity.this, jObj.optString("failReason"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void updateActualProgress(String actualProgress) {
        progressBar.setVisibility(View.VISIBLE);
        HashMap<String, Object> reqParam = new HashMap<>();
        reqParam.put("userId", String.valueOf(userId));
        reqParam.put("token", token);

        JSONArray tempImgArray = new JSONArray();
        if (actualProgressImages != null && actualProgressImages.size() > 0) {
            for (int i = 0; i < actualProgressImages.size(); i++) {
                tempImgArray.put(ImageUtil.convert(actualProgressImages.get(i).getImageCaptured()));
            }
        }

        JSONObject reqObj = new JSONObject();
        try {
            reqObj.put("workPlanId", workPlanId);
            reqObj.put("actualProgress", actualProgress);
            reqObj.put("photos", tempImgArray);
            reqObj.put("latitude", String.valueOf( App.latitude));
            reqObj.put("longitude", String.valueOf( App.longitude));
            reqObj.put("latLngArea", capturedAddress);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject dataObj = new JSONObject();
        try {
            dataObj.put("data", reqObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(BuildConfig.BASE_URL.concat("workprogress/updateActualProgress"))
                .addQueryParameter(reqParam)
                .addJSONObjectBody(dataObj)
                .setTag("updateActualProgress")
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
                                    dialog.dismiss();
                                    mIvRestart.setVisibility(View.GONE);
                                    mIvPlay.setVisibility(View.GONE);
                                    mIvPause.setVisibility(View.GONE);
                                    mIvStop.setVisibility(View.GONE);
                                    actualProgressImages.clear();
                                    getProgressDetailsByPlanId();
                                } else {
                                    Toast.makeText(WorkDetailActivity.this, jObj.optString("failReason"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private long startDateInLong;
    private long endDateInLong;
    private String actualProg;

    private void getProgressDetailsByPlanId() {
        clickedImages.clear();
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
                            mRlWorkDetailRoot.setVisibility(View.VISIBLE);
                            try {
                                JSONObject jObj = new JSONObject(response);

                                String status = jObj.optString("status");
                                if (status.equals("SUCCESS")) {
                                    mTvFailReason.setVisibility(View.GONE);
                                    JSONObject dataObj = jObj.optJSONObject("data");
                                    if (dataObj != null) {
                                        String duration = dataObj.optString("duration");
                                        mTvDuration.setText(duration);

                                        String esd = dataObj.optString("estimatedStartDate");
                                        String eed = dataObj.optString("estimatedEndDate");
                                        mTvPhaseName.setText(dataObj.optString("workPhaseName"));

                                        if (!esd.equals("")) {
                                            List<String> startDate = Arrays.asList(esd.split("/"));
                                            String day = startDate.get(0);
                                            String _month = startDate.get(1);
                                            String year = startDate.get(2);

                                            String month = Utility.convertMonthToWord(_month);

                                            mTvEstStartDay.setText(day);
                                            mTvEstStartMonYear.setText(month.concat(", ").concat(year));
                                        }
                                        if (!eed.equals("")) {
                                            List<String> startDate = Arrays.asList(eed.split("/"));
                                            String day = startDate.get(0);
                                            String _month = startDate.get(1);
                                            String year = startDate.get(2);

                                            String month = Utility.convertMonthToWord(_month);

                                            mTvEstEndDay.setText(day);
                                            mTvEstEndMonYear.setText(month.concat(", ").concat(year));
                                        }
                                        actualProg = dataObj.optString("actualProgress");
                                        mTvActualProgress.setText(actualProg.concat(" %"));

                                        JSONArray statusArray = dataObj.optJSONArray("statusHistory");
                                        if (statusArray != null && statusArray.length() > 0) {
                                            progressArrayList.clear();
                                            for (int i = 0; i < statusArray.length(); i++) {
                                                Progress pro = Progress.parseProgress(statusArray.optJSONObject(i));
                                                progressArrayList.add(pro);
                                                progressAdapter.notifyDataSetChanged();
                                            }

                                        }
                                        String progress = dataObj.optString("progress");
                                        mTvProgress.setText(getResources().getString(R.string.work_detail_progress).concat(progress));
                                        String currentstatus = dataObj.optString("currentStatus");


                                        switch (currentstatus) {
                                            case "NOTSTARTED":
                                                mAppbar.setBackgroundColor(getResources().getColor(R.color.colorNotStarted));
                                                mIvWorkStatus.setImageResource(R.drawable.ic_plan_pause);
//                                            mTvWorkStatus.setBackgroundColor(getResources().getColor(R.color.colorYellow));
                                                mIvPlay.setVisibility(View.VISIBLE);
                                                mIvStop.setVisibility(View.GONE);
                                                mIvPause.setVisibility(View.GONE);

                                                mRlStatusHistory.setBackgroundColor(getResources().getColor(R.color.colorNotStarted));

                                                mIvActualProgress.setColorFilter(getResources().getColor(R.color.colorActualProgress));
                                                mIvPdf.setColorFilter(getResources().getColor(R.color.colorActualProgress));

                                                mTvActualProgress.setTextColor(getResources().getColor(R.color.colorActualProgress));
                                                mTvWorkStatus.setTextColor(getResources().getColor(R.color.colorStopStatus));
                                                mIvActualProgress.setVisibility(View.GONE);


                                                mTvEstEndDay.setTextColor(getResources().getColor(R.color.colorActualProgress));
                                                mTvEstEndMonYear.setTextColor(getResources().getColor(R.color.colorPauseStartDay));

                                                mTvEstStartDay.setTextColor(getResources().getColor(R.color.colorActualProgress));
                                                mTvEstStartMonYear.setTextColor(getResources().getColor(R.color.colorPauseStartDay));

                                                mTvPhaseName.setTextColor(getResources().getColor(R.color.colorActualProgress));

                                                view.setBackgroundColor(getResources().getColor(R.color.colorPauseView));
                                                break;
                                            case "INPROGRESS":
                                                mAppbar.setBackgroundColor(getResources().getColor(R.color.colorInProgress));
                                                mIvWorkStatus.setImageResource(R.drawable.ic_inprogress);
//                                            mTvWorkStatus.setBackgroundColor(getResources().getColor(R.color.green));
                                                mIvPlay.setVisibility(View.GONE);
                                                mIvStop.setVisibility(View.VISIBLE);
                                                mIvPause.setVisibility(View.VISIBLE);

                                                mIvActualProgress.setVisibility(View.VISIBLE);

                                                mRlStatusHistory.setBackgroundColor(getResources().getColor(R.color.colorNotStarted));

                                                mIvPdf.setColorFilter(getResources().getColor(R.color.toolbar_title_color));

                                                view.setBackgroundColor(getResources().getColor(R.color.colorProgressView));
                                                break;
                                            case "PAUSED":
                                                mAppbar.setBackgroundColor(getResources().getColor(R.color.colorPause));
                                                mIvWorkStatus.setImageResource(R.drawable.ic_plan_pause);

                                                mIvPlay.setVisibility(View.GONE);
                                                mIvStop.setVisibility(View.GONE);
                                                mIvRestart.setVisibility(View.VISIBLE);

                                                mRlStatusHistory.setBackgroundColor(getResources().getColor(R.color.colorNotStarted));

                                                mIvActualProgress.setColorFilter(getResources().getColor(R.color.colorActualProgress));
                                                mIvPdf.setColorFilter(getResources().getColor(R.color.colorActualProgress));

                                                mTvActualProgress.setTextColor(getResources().getColor(R.color.colorActualProgress));
                                                mTvWorkStatus.setTextColor(getResources().getColor(R.color.colorStopStatus));
                                                mIvActualProgress.setVisibility(View.GONE);

                                                mTvEstEndDay.setTextColor(getResources().getColor(R.color.colorActualProgress));
                                                mTvEstEndMonYear.setTextColor(getResources().getColor(R.color.colorPauseStartDay));

                                                mTvEstStartDay.setTextColor(getResources().getColor(R.color.colorActualProgress));
                                                mTvEstStartMonYear.setTextColor(getResources().getColor(R.color.colorPauseStartDay));

                                                mTvPhaseName.setTextColor(getResources().getColor(R.color.colorActualProgress));
                                                view.setBackgroundColor(getResources().getColor(R.color.colorPauseView));

//                                            mTvWorkStatus.setBackgroundColor(getResources().getColor(R.color.colorYellow));
                                                break;
                                            case "STOPPED":
                                                mAppbar.setBackgroundColor(getResources().getColor(R.color.colorCompleted));
                                                mIvWorkStatus.setImageResource(R.drawable.ic_complete);
                                                mRlCompleted.setVisibility(View.VISIBLE);

                                                mRlStatusHistory.setBackgroundColor(getResources().getColor(R.color.colorStopBg));
                                                mIvPdf.setColorFilter(getResources().getColor(R.color.colorStopProgress));

                                                mTvActualProgress.setTextColor(getResources().getColor(R.color.colorStopProgress));
                                                mTvWorkStatus.setTextColor(getResources().getColor(R.color.colorStopStatus));
                                                mIvActualProgress.setVisibility(View.GONE);

                                                mTvEstEndDay.setTextColor(getResources().getColor(R.color.colorEnddayStop));
                                                mTvEstEndMonYear.setTextColor(getResources().getColor(R.color.colorEndyearStop));

                                                mTvEstStartDay.setTextColor(getResources().getColor(R.color.colorEnddayStop));
                                                mTvEstStartMonYear.setTextColor(getResources().getColor(R.color.colorEndyearStop));

                                                mTvPhaseName.setTextColor(getResources().getColor(R.color.colorEnddayStop));
                                                view.setBackgroundColor(getResources().getColor(R.color.colorCompleteView));
//                                            mTvWorkStatus.setBackgroundColor(getResources().getColor(R.color.close_item_background));
                                                break;
                                        }

                                        mTvWorkStatus.setText(currentstatus);
                                    }
                                }else if (status.equals("FAILURE")){
                                    mTvFailReason.setText(jObj.optString("failReason"));
                                    mTvFailReason.setVisibility(View.VISIBLE);
                                    mAppbar.setVisibility(View.GONE);
                                    mLlWorkDetail.setVisibility(View.GONE);
                                    mRlWorkDetail.setVisibility(View.GONE);
                                    mRlCompleted.setVisibility(View.GONE);
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

    private void setupRestartBottomsheet() {
        View dialogView = getLayoutInflater().inflate(R.layout.bottom_sheet_restart_work, null);
        dialog = new BottomSheetDialog(this);
        dialog.setContentView(dialogView);
        dialogView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        clickedImages.clear();
                        ciadapter.notifyDataSetChanged();
//                        behavior.setAllowUserDragging(false);
                        break;
                    case MotionEvent.ACTION_UP:
//                        behavior.setAllowUserDragging(true);
                        break;
                }
                return true;
            }
        });
        RelativeLayout mRlUpload = dialogView.findViewById(R.id.rl_upload_photo);
        ImageView mIvEsd = dialogView.findViewById(R.id.iv_esd);
        ImageView mIvClose = dialogView.findViewById(R.id.iv_sheet_restart_work_close);
        Button mBtnRestart = dialogView.findViewById(R.id.btn_restart);
        EditText mEtStopReason = dialogView.findViewById(R.id.et_restart_reason);
        TextView mTvDate = dialogView.findViewById(R.id.tv_est_restart_date);
        RelativeLayout mRlResume = dialogView.findViewById(R.id.rl_resume_date);
        RecyclerView mRvSelectedImages = dialogView.findViewById(R.id.rv_selected_photos);
        mRvSelectedImages.setLayoutManager(new LinearLayoutManager(WorkDetailActivity.this, RecyclerView.HORIZONTAL, false));
        ciadapter = new CapturedImageAdapter(clickedImages, WorkDetailActivity.this);
        ciadapter.setRemoveListener(new CapturedImageAdapter.RemoveListener() {
            @Override
            public void removeImage(int atPosition) {
                clickedImages.remove(atPosition);
                ciadapter.notifyDataSetChanged();
            }
        });
        mRvSelectedImages.setAdapter(ciadapter);
        mRlUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCameraPermission();
                CAPTURE_TYPE = 2;
            }
        });

        final DatePickerDialog datePickerDialog = new DatePickerDialog(WorkDetailActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);

                String myFormat = "dd/MM/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                isDateSelected = true;
                mTvDate.setText(sdf.format(myCalendar.getTime()));
            }
        }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.getDatePicker().setMinDate(startDateInLong);
        mIvEsd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });
        mRlResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickedImages.clear();
                ciadapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });


        mBtnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String date = mTvDate.getText().toString().trim();
                if (isProviderEnabled) {
                    if (!TextUtils.isEmpty(date)) {
                        if (isDateSelected) {
                            if (clickedImages != null && clickedImages.size() > 0) {
                                if (App.latitude == 0.0 || App.longitude == 0.0) {
                                    Toast.makeText(WorkDetailActivity.this, "GETTING LOCATION. PLEASE WAIT", Toast.LENGTH_SHORT).show();
                                } else {
                                    if (isConnected) {
                                        progressResume(date, clickedImages);
                                    } else {
                                        Toast.makeText(WorkDetailActivity.this, getResources().getString(R.string.internet_issue), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else {
                                Toast.makeText(WorkDetailActivity.this, "Please capture an image", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(WorkDetailActivity.this, "Please select the date", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(WorkDetailActivity.this, "Please select the date", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(WorkDetailActivity.this, "Please Enable Location", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    private void setupStopBottomSheet() {
        View dialogView = getLayoutInflater().inflate(R.layout.bottom_sheet_stop_work, null);
        dialog = new BottomSheetDialog(this);
        dialog.setContentView(dialogView);
        dialogView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        clickedImages.clear();
                        ciadapter.notifyDataSetChanged();
//                        behavior.setAllowUserDragging(false);
                        break;
                    case MotionEvent.ACTION_UP:
//                        behavior.setAllowUserDragging(true);
                        break;
                }
                return true;
            }
        });
        RelativeLayout mRlUpload = dialogView.findViewById(R.id.rl_upload_photo);
        ImageView mIvEsd = dialogView.findViewById(R.id.iv_esd);
        ImageView mIvClose = dialogView.findViewById(R.id.iv_sheet_start_work_close);
        Button mBtnStop = dialogView.findViewById(R.id.btn_stop);
        EditText mEtStopReason = dialogView.findViewById(R.id.et_stop_reason);
        mEtStopReason.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                if (mEtStopReason.hasFocus()) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_SCROLL:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            return true;
                    }
                }
                return false;
            }
        });
        TextView mTvDate = dialogView.findViewById(R.id.tv_est_stop_date);
        RelativeLayout mRlStopDae = dialogView.findViewById(R.id.rl_stop_date);
        RecyclerView mRvSelectedImages = dialogView.findViewById(R.id.rv_selected_photos);
        mRvSelectedImages.setLayoutManager(new LinearLayoutManager(WorkDetailActivity.this, RecyclerView.HORIZONTAL, false));
        ciadapter = new CapturedImageAdapter(clickedImages, WorkDetailActivity.this);
        ciadapter.setRemoveListener(new CapturedImageAdapter.RemoveListener() {
            @Override
            public void removeImage(int atPosition) {
                clickedImages.remove(atPosition);
                ciadapter.notifyDataSetChanged();
            }
        });
        mRvSelectedImages.setAdapter(ciadapter);
        mRlUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCameraPermission();
                CAPTURE_TYPE = 2;
            }
        });
        final DatePickerDialog datePickerDialog = new DatePickerDialog(WorkDetailActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);

                String myFormat = "dd/MM/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                mTvDate.setText(sdf.format(myCalendar.getTime()));
            }
        }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));

//        datePickerDialog.getDatePicker().setMaxDate(endDateInLong);
        datePickerDialog.getDatePicker().setMinDate(startDateInLong);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

//        datePickerDialog.getDatePicker().setMaxDate(endDateInLong);
//        datePickerDialog.getDatePicker().setMinDate(startDateInLong);

        mBtnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String reason = mEtStopReason.getText().toString().trim();
                String date = mTvDate.getText().toString().trim();
                if (isProviderEnabled) {
                    if (date.equals("Stop Date")) {
                        Toast.makeText(WorkDetailActivity.this, "Please select the date", Toast.LENGTH_SHORT).show();
                    } else if (clickedImages == null) {
                        Toast.makeText(WorkDetailActivity.this, "Please capture an image", Toast.LENGTH_SHORT).show();
                    } else if (clickedImages.size() == 0) {
                        Toast.makeText(WorkDetailActivity.this, "Please capture an image", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(reason)) {
                        Toast.makeText(WorkDetailActivity.this, "Please select the reason", Toast.LENGTH_SHORT).show();
                    } else if (App.latitude == 0.0 || App.longitude == 0.0) {
                        Toast.makeText(WorkDetailActivity.this, "GETTING LOCATION. PLEASE WAIT", Toast.LENGTH_SHORT).show();
                    } else {
                        if (isConnected) {
                            progressStop(reason, date, clickedImages);
                        } else {
                            Toast.makeText(WorkDetailActivity.this, getResources().getString(R.string.internet_issue), Toast.LENGTH_SHORT).show();
                        }
                    }
                }else {
                    Toast.makeText(WorkDetailActivity.this, "Please Enable Location", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mRlStopDae.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        mIvEsd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });
        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickedImages.clear();
                ciadapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void setupStartBottomsheet() {
        View dialogView = getLayoutInflater().inflate(R.layout.bottom_sheet_start_work, null);
        dialog = new BottomSheetDialog(this);
        dialog.setContentView(dialogView);

        dialogView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        clickedImages.clear();
                        ciadapter.notifyDataSetChanged();
//                        behavior.setAllowUserDragging(false);
                        break;
                    case MotionEvent.ACTION_UP:
//                        behavior.setAllowUserDragging(true);
                        break;
                }
                return true;
            }
        });

        RelativeLayout mRlUpload = dialogView.findViewById(R.id.rl_upload_photo);
        ImageView mIvEsd = dialogView.findViewById(R.id.iv_esd);
        ImageView mIvClose = dialogView.findViewById(R.id.iv_sheet_start_work_close);
        Button mBtnStart = dialogView.findViewById(R.id.btn_start);
        EditText mEtStartReason = dialogView.findViewById(R.id.et_start_reason);
        TextView mTvDate = dialogView.findViewById(R.id.tv_est_start_date);
        RelativeLayout mRlStartDate = dialogView.findViewById(R.id.rl_start_date);
        RecyclerView mRvSelectedImages = dialogView.findViewById(R.id.rv_selected_photos);
        mRvSelectedImages.setLayoutManager(new LinearLayoutManager(WorkDetailActivity.this, RecyclerView.HORIZONTAL, false));
        ciadapter = new CapturedImageAdapter(clickedImages, WorkDetailActivity.this);
        ciadapter.setRemoveListener(new CapturedImageAdapter.RemoveListener() {
            @Override
            public void removeImage(int atPosition) {
                clickedImages.remove(atPosition);
                ciadapter.notifyDataSetChanged();
            }
        });
        mRvSelectedImages.setAdapter(ciadapter);
        mRlUpload.setOnClickListener(v -> {
            requestCameraPermission();
            CAPTURE_TYPE = 2;
        });

        final DatePickerDialog datePickerDialog = new DatePickerDialog(WorkDetailActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);

                String myFormat = "dd/MM/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                isDateSelected = true;
                mTvDate.setText(sdf.format(myCalendar.getTime()));
            }
        }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(startDateInLong);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        mBtnStart.setOnClickListener(view -> {
            String reason = mEtStartReason.getText().toString().trim();
            String date = mTvDate.getText().toString().trim();
            if (isProviderEnabled) {
                if (date.equals("Start Date")) {
                    Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
                } else if (isDateSelected) {
                    if (App.latitude == 0.0 || App.longitude == 0.0) {
                        Toast.makeText(this, "GETTING LOCATION. PLEASE WAIT", Toast.LENGTH_SHORT).show();
                    } else {
                        if (clickedImages != null && clickedImages.size() > 0 && App.latitude != 0.0 && App.longitude != 0.0) {
                            if (isConnected) {
                                progressStart(date, clickedImages);
                            } else {
                                Toast.makeText(WorkDetailActivity.this, getResources().getString(R.string.internet_issue), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(WorkDetailActivity.this, "Please capture an image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }else {
                Toast.makeText(WorkDetailActivity.this, "Please Enable Location", Toast.LENGTH_SHORT).show();
            }

        });

        mRlStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
        mIvEsd.setOnClickListener(view -> datePickerDialog.show());
        mIvClose.setOnClickListener(view -> {
            clickedImages.clear();
            ciadapter.notifyDataSetChanged();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void setupActualProgress() {
        View dialogView = getLayoutInflater().inflate(R.layout.bottom_sheet_actual_progress, null);
        dialog = new BottomSheetDialog(this);
        dialog.setContentView(dialogView);
        dialogView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        actualProgressImages.clear();
                        ciadapter.notifyDataSetChanged();
//                        behavior.setAllowUserDragging(false);
                        break;
                    case MotionEvent.ACTION_UP:
//                        behavior.setAllowUserDragging(true);
                        break;
                }
                return true;
            }
        });

        ImageView mIvCapture = dialogView.findViewById(R.id.iv_camera);
        mIvCapture.setOnClickListener(v -> {
            requestCameraPermission();
            CAPTURE_TYPE = 1;
        });
        ImageView mIvClose = dialogView.findViewById(R.id.iv_sheet_update_work_close);
        EditText mEtActualProgress = dialogView.findViewById(R.id.et_actual_progress);
        Button mBtnUpdate = dialogView.findViewById(R.id.btn_update);
        mEtActualProgress.setText(actualProg);
        RecyclerView mRvActualImages = dialogView.findViewById(R.id.rv_selected_actual_progress_photos);
        mRvActualImages.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

//        clickedImages.clear();
        ciadapter = new CapturedImageAdapter(actualProgressImages, WorkDetailActivity.this);
        ciadapter.setRemoveListener(new CapturedImageAdapter.RemoveListener() {
            @Override
            public void removeImage(int atPosition) {
                actualProgressImages.remove(atPosition);
                ciadapter.notifyDataSetChanged();
            }
        });
        mRvActualImages.setAdapter(ciadapter);
        mBtnUpdate.setOnClickListener(view -> {
                    if (isProviderEnabled) {
                        String actualProgress = mEtActualProgress.getText().toString().trim();
                        if (actualProgressImages.size() == 0) {
                            Toast.makeText(this, "Please capture image", Toast.LENGTH_SHORT).show();
                        } else if (TextUtils.isEmpty(actualProgress)) {
                            Toast.makeText(getApplicationContext(), "Please fill the actual progress", Toast.LENGTH_LONG).show();
                        } else if (App.latitude == 0.0 || App.longitude == 0.0) {
                            Toast.makeText(this, "GETTING LOCATION. PLEASE WAIT", Toast.LENGTH_SHORT).show();
                        } else {
                            if (isConnected) {
                                updateActualProgress(actualProgress);
                            } else {
                                Toast.makeText(WorkDetailActivity.this, getResources().getString(R.string.internet_issue), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }else {
                        Toast.makeText(WorkDetailActivity.this, "Please Enable Location", Toast.LENGTH_SHORT).show();
                    }
        });


        mIvClose.setOnClickListener(view -> {
            actualProgressImages.clear();
            ciadapter.notifyDataSetChanged();
            dialog.dismiss();
        });
        dialog.show();
    }

    ImageView mIvUploadedImg;
    private int CAPTURE_TYPE; // 1 = actual , 2 = interim progress
    private void setupPauseBottomSheet() {
        View dialogView = getLayoutInflater().inflate(R.layout.bottom_sheet_pause_work, null);
        dialog = new BottomSheetDialog(this);
        dialog.setContentView(dialogView);
        dialogView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        clickedImages.clear();
                        ciadapter.notifyDataSetChanged();
//                        behavior.setAllowUserDragging(false);
                        break;
                    case MotionEvent.ACTION_UP:
//                        behavior.setAllowUserDragging(true);
                        break;
                }
                return true;
            }
        });
        RecyclerView mRvSelectedImages = dialogView.findViewById(R.id.rv_selected_photos);
        mRvSelectedImages.setLayoutManager(new LinearLayoutManager(WorkDetailActivity.this, RecyclerView.HORIZONTAL, false));
        ImageView mIvEsd = dialogView.findViewById(R.id.iv_esd);
        ImageView mIvClose = dialogView.findViewById(R.id.iv_sheet_pause_work_close);
        mIvUploadedImg = dialogView.findViewById(R.id.id_uploaded_img);
        EditText mEtActualProgress = dialogView.findViewById(R.id.et_actual_progress);
        RelativeLayout mRlPauseDate = dialogView.findViewById(R.id.rl_pause_date);
        TextView mTvDate = dialogView.findViewById(R.id.tv_est_pause_date);
        RelativeLayout mRlPhotoUpload = dialogView.findViewById(R.id.rl_upload_photo);
        mRlPhotoUpload.setOnClickListener(v -> {
            requestCameraPermission();
            CAPTURE_TYPE = 2;
        });

        EditText mEtReason = dialogView.findViewById(R.id.et_pause_reason);
        mEtReason.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                if (mEtReason.hasFocus()) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_SCROLL:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            return true;
                    }
                }
                return false;
            }
        });
        Button mBtnPause = dialogView.findViewById(R.id.btn_pause);

        final DatePickerDialog datePickerDialog = new DatePickerDialog(WorkDetailActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);

                String myFormat = "dd/MM/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                isDateSelected = true;
                mTvDate.setText(sdf.format(myCalendar.getTime()));
            }
        }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(startDateInLong);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        mRlPauseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
        mIvEsd.setOnClickListener(view -> datePickerDialog.show());
        mBtnPause.setOnClickListener(view -> {
            String reason = mEtReason.getText().toString().trim();
            String date = mTvDate.getText().toString().trim();

            if (isProviderEnabled) {
                if (isDateSelected) {
                    if (clickedImages.size() > 0) {
                        if (!TextUtils.isEmpty(reason)) {
                            if (App.latitude == 0.0 || App.longitude == 0.0) {//getLocation();
                                Toast.makeText(WorkDetailActivity.this, "GETTING LOCATION. PLEASE WAIT", Toast.LENGTH_SHORT).show();
                            } else {
                                if (isConnected) {
                                    progressPaused(reason, date, clickedImages);
                                } else {
                                    Toast.makeText(WorkDetailActivity.this, getResources().getString(R.string.internet_issue), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(WorkDetailActivity.this, "Please enter the reason for pause", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(WorkDetailActivity.this, "Please capture image", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(WorkDetailActivity.this, "Please select a date", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(WorkDetailActivity.this, "Please Enable Location", Toast.LENGTH_SHORT).show();
            }

        });

        ciadapter = new CapturedImageAdapter(clickedImages, WorkDetailActivity.this);
        ciadapter.setRemoveListener(new CapturedImageAdapter.RemoveListener() {
            @Override
            public void removeImage(int atPosition) {
                clickedImages.remove(atPosition);
                ciadapter.notifyDataSetChanged();
            }
        });
        mRvSelectedImages.setAdapter(ciadapter);
        mIvClose.setOnClickListener(view -> {
            clickedImages.clear();
            ciadapter.notifyDataSetChanged();
            dialog.dismiss();
        });

        base64Strings.clear();
        for (int i = 0; i < clickedImages.size(); i++) {
            base64Strings.add(ImageUtil.convert(clickedImages.get(i).getImageCaptured()));
        }


        dialog.show();
    }

    private CapturedImageAdapter ciadapter;

    /**
     * Requesting camera permission
     */
    private void requestCameraPermission() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        // permission is granted
                        if (locationManager != null){
                            openCamera();
                        }else {
                            Toast.makeText(WorkDetailActivity.this, "Please Enable GPS", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // check for permanent denial of permission
                        if (response.isPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQ_CODE);
    }

    private ArrayList<ClickedImage> clickedImages = new ArrayList<>();
    private ArrayList<ClickedImage> actualProgressImages = new ArrayList<>();
    private ArrayList<String> base64Strings = new ArrayList<>();


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQ_CODE && resultCode == RESULT_OK && data != null) {
            if (data.getExtras() != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");

                if (photo != null) {
//                String base64String;
                    if (CAPTURE_TYPE == 2) {
                        clickedImages.add(new ClickedImage(photo));
                        ciadapter.notifyDataSetChanged();
//                    base64String = ImageUtil.convert(photo);
                    } else if (CAPTURE_TYPE == 1) {
                        actualProgressImages.add(new ClickedImage(photo));
                        ciadapter.notifyDataSetChanged();
//                    base64String = ImageUtil.convert(photo);
                    }
                }
            }

        }
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(WorkDetailActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();

    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, SETTINGS_REQ_CODE);
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    String capturedAddress = "";
    @Override
    public void onLocationChanged(Location location) {
        isProviderEnabled = true;
        Log.d("Tag", "LatLng===>" + location.getLatitude() + " " + location.getLongitude());

        if (location.getLatitude() != 0.0 && location.getLongitude() != 0.0) {
            App.latitude = location.getLatitude();
            App.longitude = location.getLongitude();

            Geocoder gc = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = gc.getFromLocation( App.latitude,  App.longitude, 1);
                StringBuilder sb = new StringBuilder();
                if (addresses.size() > 0) {
                    Address address = addresses.get(0);
                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        sb.append(address.getAddressLine(i)).append("\n");
                    }
                    if (address.getAddressLine(0) != null)
                        App.capturedAddress=address.getAddressLine(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        isProviderEnabled = true;
//        Toast.makeText(this, "Please Enable GPS Provider", Toast.LENGTH_SHORT).show();
        if(locationManager != null) {
            try {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                assert locationManager != null;
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }
    private boolean isProviderEnabled;

    @Override
    public void onProviderDisabled(String provider) {
        isProviderEnabled = false;
//        Toast.makeText(this, "GPS Provider Disabled", Toast.LENGTH_SHORT).show();
        Snackbar.make(binding.rlWorkDetailRoot,"GPS Provider Disabled",Snackbar.LENGTH_LONG).show();
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            // do your work now
//                                Toast.makeText(getApplicationContext(), "All permissions are granted!", Toast.LENGTH_SHORT).show();
//                            handler.postDelayed(runnable, 2000);

                            getLocation();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permenantly, navigate user to app settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        this.isConnected = isConnected;
    }
}
