package com.aashdit.ipms.ui.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aashdit.ipms.BuildConfig;
import com.aashdit.ipms.R;
import com.aashdit.ipms.adapters.PhaseSpinnerAdapter;
import com.aashdit.ipms.adapters.WorkPhaseAdapter;
import com.aashdit.ipms.adapters.WorkPhaseUnApprovedAdapter;
import com.aashdit.ipms.app.App;
import com.aashdit.ipms.models.MasterPhases;
import com.aashdit.ipms.models.WorkPhase;
import com.aashdit.ipms.network.ANetwork;
import com.aashdit.ipms.receiver.ConnectivityChangeReceiver;
import com.aashdit.ipms.util.Constants;
import com.aashdit.ipms.util.SharedPrefManager;
import com.aashdit.ipms.util.Utility;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class WorkPhasesActivity extends AppCompatActivity implements WorkPhaseAdapter.OnPhaseListener,
        View.OnCreateContextMenuListener, WorkPhaseUnApprovedAdapter.OnPhaseLongListener,
        ConnectivityChangeReceiver.ConnectivityReceiverListener {

    private static final String TAG = "WorkPhasesActivity";
    public static String WORK_ID = "WorkPhaseId";
    public static String EST_START_DATE = "est_start_date";
    public static String EST_END_DATE = "est_end_date";
    public static String WP_TITLE = "wp_title";
    public static String PROJ_NAME = "proj_name";
    final Calendar myCalendar = Calendar.getInstance();
    int workPhaseId;
    private RecyclerView mRvWorkPhases;
    private RelativeLayout mRlWorkPhaseRoot;
    private ArrayList<WorkPhase> workPhases;
    private ArrayList<MasterPhases> masterPhases;
    private Toolbar toolbar;
    private String type, token;
    private SharedPrefManager sp;
    private ProgressBar progressBar;
    private int workId;
    private String estimatedStartDate, estimatedEndDate;
    private WorkPhaseAdapter workPhaseAdapter;
    private WorkPhaseUnApprovedAdapter workPhaseUnApprovedAdapter;
    private ImageView mIvAddPhase;
    private String compName, projName;
    private TextView mTvPhaseTitle, mTvNoData;
    private BottomSheetDialog dialog;
    private Switch mSwitchPhase;
    private EditText mEtStartDate, mEtEndDate;
    private Button mBtnAdd, mBtnCancel;
    private int workPlanId;
    private String workPhaseName;

    private String fromAdapter;
    private ConnectivityChangeReceiver mConnectivityChangeReceiver;
    private boolean isConnected;
    /**
     * To get the logedin user type
     */
    private String userType;
    private int itemPosition;
    private boolean isStartDateSelected = false;
    private boolean isEndDateSelected = false;

    private TextView mTvApprovedLbl, mTvUnApprovedLbl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_phases);
        mRvWorkPhases = findViewById(R.id.rv_work_phases);
        mRvWorkPhases.setLayoutManager(new GridLayoutManager(this, 2));
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        ANetwork.setContext(this);

        mConnectivityChangeReceiver = new ConnectivityChangeReceiver();
        mConnectivityChangeReceiver.setConnectivityReceiverListener(this);
        isConnected = mConnectivityChangeReceiver.getConnectionStatus(this);

        registerNetworkBroadcast();
        mTvApprovedLbl = findViewById(R.id.tv_approved);
        mTvUnApprovedLbl = findViewById(R.id.tv_unapproved);
        mTvNoData = findViewById(R.id.tv_no_data);
        mSwitchPhase = findViewById(R.id.switch_phase);
        progressBar = findViewById(R.id.progressBar);
        mRlWorkPhaseRoot = findViewById(R.id.rl_work_phase_root);
        sp = SharedPrefManager.getInstance(this);
        compName = getIntent().getStringExtra(WP_TITLE);
        workId = getIntent().getIntExtra(WORK_ID, -1);
        estimatedEndDate = getIntent().getStringExtra(EST_END_DATE);
        estimatedStartDate = getIntent().getStringExtra(EST_START_DATE);
        projName = getIntent().getStringExtra(PROJ_NAME);
        token = sp.getStringData(Constants.APP_TOKEN);
        fromAdapter = getIntent().getStringExtra("fromAdapter");
        sp.setStringData("FROMADAPTER", fromAdapter);
        workPhases = new ArrayList<>();
        masterPhases = new ArrayList<>();
        mTvPhaseTitle = findViewById(R.id.tv_phase_name);
        mTvPhaseTitle.setText(compName);

        projectEstimatedStartDate = sp.getStringData(Constants.ESD);
        projectEstimatedEndDate = sp.getStringData(Constants.EED);
        TextView mTvCompName = findViewById(R.id.tv_wc_name);
        mTvCompName.setText(compName);
        TextView mTvProjectName = findViewById(R.id.tv_project_name_bc);
        mTvProjectName.setText(projName);

        mIvAddPhase = findViewById(R.id.iv_add_work_phase);
        mIvAddPhase.setVisibility(View.GONE);
        mIvAddPhase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupBottomSheet("add");
            }
        });

        /**
         *  if the user role is equals to ROLE_AGENCY than show fab + button &
         *  if the user role is equals to ROLE_SECTION_OFFICER hide fab + button.
         * */
        userType = sp.getStringData(Constants.USER_ROLE_CODE);


        type = "approved";
        workPhaseAdapter = new WorkPhaseAdapter(this, workPhases);
        workPhaseAdapter.setType(type);
        workPhaseAdapter.setOnPhaseListener(this);

        workPhaseUnApprovedAdapter = new WorkPhaseUnApprovedAdapter(this,workPhases);
        workPhaseUnApprovedAdapter.setOnPhaseLongClick(this);

//        if (userType.equals("ROLE_SECTION_OFFICER")) {
//
//            getApprovablePlansByWorkId();
//
//        } else if (userType.equals("ROLE_AGENCY")) {
//            getPhasesFromMaster();
//            getProgressablePlansByWorkId();
//        }


        mSwitchPhase.setVisibility(View.GONE);
        mTvApprovedLbl.setVisibility(View.GONE);
        mTvUnApprovedLbl.setVisibility(View.GONE);
//        mSwitchPhase.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if (b) {
//                    mIvAddPhase.setVisibility(View.VISIBLE);
//                    getEditablePlansByWorkId();
//                } else {
//                    mIvAddPhase.setVisibility(View.GONE);
//                    getProgressablePlansByWorkId();
//                }
//            }
//        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        setResult(RESULT_OK, intent);
        super.onBackPressed();
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
    public void onDestroy() {
        super.onDestroy();
        unregisterNetworkChanges();
    }

    private int phaseCount;

    @Override
    protected void onResume() {
        super.onResume();
        App.getInstance().setConnectivityListener(this);
        Log.i(TAG, "onResume: ::::::::::::: workphase");

        if (isConnected) {
            if (userType.equals("ROLE_SECTION_OFFICER")) {

                getApprovablePlansByWorkId();

            } else if (userType.equals("ROLE_AGENCY")) {
                getPhasesFromMaster();
                if (fromAdapter.equals("REVERTED") || fromAdapter.equals("NEW") || fromAdapter.equals("PENDING") || fromAdapter.equals("OPEN")) {
                    getEditablePlansByWorkId();
                }
                if (fromAdapter.equals("APPROVED")) {
                    getProgressablePlansByWorkId();
                    phaseCount = getIntent().getIntExtra("PHASE_COUNT", -1);
                    if (phaseCount == 0) {
                        mIvAddPhase.setVisibility(View.VISIBLE);
                    } else {
                        mIvAddPhase.setVisibility(View.GONE);
                    }
                }
            }
        } else {
            Toast.makeText(this, getResources().getString(R.string.internet_issue), Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * this api is for Agency User
     */
    private void getProgressablePlansByWorkId() {
        progressBar.setVisibility(View.VISIBLE);
        workPhases.clear();
        HashMap<String, Object> reqParam = new HashMap<>();
        reqParam.put("wid", String.valueOf(workId));
        reqParam.put("token", token);

        AndroidNetworking.get(BuildConfig.BASE_URL.concat("workprogress/getProgressablePlansByWorkId"))
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
                                    workPhases.clear();
                                    JSONArray jArray = jObj.optJSONArray("data");
                                    if (jArray != null && jArray.length() > 0) {
                                        mTvNoData.setVisibility(View.GONE);
                                        for (int i = 0; i < jArray.length(); i++) {
                                            WorkPhase wp = WorkPhase.parsePlansByWorkId(jArray.optJSONObject(i));
                                            workPhases.add(wp);
                                        }

                                        if (workPhases.size() > 0) {
                                            mSwitchPhase.setVisibility(View.GONE);
                                            mTvApprovedLbl.setVisibility(View.GONE);
                                            mTvUnApprovedLbl.setVisibility(View.GONE);
                                            type = "approved";
                                            mRvWorkPhases.setAdapter(workPhaseAdapter);
                                            workPhaseAdapter.setType(type);
                                            workPhaseAdapter.notifyDataSetChanged();
                                        }else {
                                            mSwitchPhase.setVisibility(View.VISIBLE);
                                            mTvApprovedLbl.setVisibility(View.VISIBLE);
                                            mTvUnApprovedLbl.setVisibility(View.VISIBLE);
                                            Toast.makeText(WorkPhasesActivity.this, "Approved phases", Toast.LENGTH_SHORT).show();
                                        }
                                    }else {
                                        mTvNoData.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    Snackbar.make(mRlWorkPhaseRoot, jObj.optString("failReason"), Snackbar.LENGTH_SHORT).show();
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

    /**
     * this api is for Agency User
     */
    private void getEditablePlansByWorkId() {
        progressBar.setVisibility(View.VISIBLE);
        workPhases.clear();
        HashMap<String, Object> reqParam = new HashMap<>();
        reqParam.put("wid", String.valueOf(workId));
        reqParam.put("token", token);

        AndroidNetworking.get(BuildConfig.BASE_URL.concat("workplan/getEditablePlansByWorkId"))
                .addQueryParameter(reqParam)
                .setTag("PlannableWorkByWorkId")
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
                                    workPhases.clear();
                                    JSONArray jArray = jObj.optJSONArray("data");
                                    if (jArray != null && jArray.length() > 0) {
                                        mTvNoData.setVisibility(View.GONE);
                                        for (int i = 0; i < jArray.length(); i++) {
                                            WorkPhase wp = WorkPhase.parsePlansByWorkId(jArray.optJSONObject(i));
                                            workPhases.add(wp);
                                        }
                                        if (workPhases.get(0).status.equals("PENDING") ){
                                            mIvAddPhase.setVisibility(View.GONE);
                                        }else {
                                            mIvAddPhase.setVisibility(View.VISIBLE);
                                        }
                                        if (workPhases.size() > 0) {
                                            type = "unapproved";
                                            mRvWorkPhases.setAdapter(workPhaseUnApprovedAdapter);
                                            workPhaseUnApprovedAdapter.notifyDataSetChanged();

                                        }else {
                                            Toast.makeText(WorkPhasesActivity.this, "No Unapproved phases", Toast.LENGTH_SHORT).show();
                                        }
                                    }else {

                                        if (jArray != null && jArray.length() >= 0) {
                                            mIvAddPhase.setVisibility(View.VISIBLE);
                                        } else {
                                            mIvAddPhase.setVisibility(View.GONE);
                                        }
                                        mTvNoData.setVisibility(View.VISIBLE);
                                    }

                                } else {
                                    Snackbar.make(mRlWorkPhaseRoot, jObj.optString("failReason"), Snackbar.LENGTH_SHORT).show();
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

    /**
     * this api is for Section Officer
     */
    private void getApprovablePlansByWorkId() {
        workPhases.clear();
        HashMap<String, Object> reqParam = new HashMap<>();
        reqParam.put("wid", String.valueOf(workId));
        reqParam.put("token", token);


        AndroidNetworking.get(BuildConfig.BASE_URL.concat("workplan/getApprovablePlansByWorkId"))
                .addQueryParameter(reqParam)
                .setTag("getApprovablePlansByWorkId")
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
                                            WorkPhase wp = WorkPhase.parsePlansByWorkId(jArray.optJSONObject(i));
                                            workPhases.add(wp);
                                        }
                                        type = "approved";
                                        workPhaseAdapter.notifyDataSetChanged();
                                    }
                                } else {
                                    Snackbar.make(mRlWorkPhaseRoot, jObj.optString("failReason"), Snackbar.LENGTH_SHORT).show();
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

    /**
     * this api is to get all the phases from master
     */
    private void getPhasesFromMaster() {
        AndroidNetworking.get(BuildConfig.BASE_URL.concat("master/getAllWorkPhases"))
                .addQueryParameter("token", token)
                .setTag("getAllWorkPhasesMaster")
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
                                        MasterPhases selectPhase = new MasterPhases();
                                        selectPhase.isActive = true;
                                        selectPhase.phaseCode = "0";
                                        selectPhase.phaseDesc = "";
                                        selectPhase.phaseName = "Select";
                                        selectPhase.phaseId = -1;
                                        masterPhases.add(selectPhase);
                                        for (int i = 0; i < jArray.length(); i++) {
                                            MasterPhases mp = MasterPhases.parseMasterPhase(jArray.optJSONObject(i));
                                            if (mp.isActive) {
                                                masterPhases.add(mp);
                                            }
                                        }
                                    }
                                } else {
                                    Snackbar.make(mRlWorkPhaseRoot, jObj.optString("failReason"), Snackbar.LENGTH_SHORT).show();
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

    int selectedWPid;
    private String projectEstimatedStartDate,projectEstimatedEndDate;
    private void setupBottomSheet(String type) {

        View dialogView = getLayoutInflater().inflate(R.layout.bottom_sheet_add_phase, null);
        dialog = new BottomSheetDialog(this);
        dialog.setContentView(dialogView);

        TextView mTvEstStartDate = dialogView.findViewById(R.id.tv_est_date);
        TextView mTvEstEndDate = dialogView.findViewById(R.id.tv_eet_date);

        TextView mTvPhaseUpdate = dialogView.findViewById(R.id.tv_phase_update);
        EditText mEtRemarkPhase = dialogView.findViewById(R.id.et_remark_phase);
        mEtRemarkPhase.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                if (mEtRemarkPhase.hasFocus()) {
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

        RelativeLayout mRlStartDate = dialogView.findViewById(R.id.rl_start_date);
        RelativeLayout mRlEndDate = dialogView.findViewById(R.id.rl_end_date);
        TextView remarlLbl = dialogView.findViewById(R.id.tv_remark_lbl);
        TextView eedLbl = dialogView.findViewById(R.id.tv_eet_date_lbl);
        TextView esdLbl = dialogView.findViewById(R.id.tv_est_date_lbl);

        ImageView mIvEsd = dialogView.findViewById(R.id.iv_esd);
        ImageView mIvEed = dialogView.findViewById(R.id.iv_eed);
        ImageView mIvClose = dialogView.findViewById(R.id.iv_sheet_add_phase_close);

        Spinner mSpnWorkPhases = dialogView.findViewById(R.id.spn_work_phases);
        PhaseSpinnerAdapter aa = new PhaseSpinnerAdapter(this, masterPhases);
        mSpnWorkPhases.setAdapter(aa);
        mSpnWorkPhases.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                workPhaseId = masterPhases.get(i).phaseId;
//                workId = workPhases.get(i).workId;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                workPhaseId = masterPhases.get(1).phaseId;
//                workId = workPhases.get(0).workId;
            }
        });

//        stringDateToLong(estimatedEndDate);

        mBtnAdd = dialogView.findViewById(R.id.btn_add_phase);

        final DatePickerDialog datePickerDialog = new DatePickerDialog(WorkPhasesActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);

                String myFormat = "dd/MM/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                isStartDateSelected = true;
                mTvEstStartDate.setText(sdf.format(myCalendar.getTime()));
            }
        }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(Utility.stringDateToLong(projectEstimatedStartDate));
        datePickerDialog.getDatePicker().setMaxDate(Utility.stringDateToLong(projectEstimatedEndDate));
//        datePickerDialog.getDatePicker().setMinDate(Utility.stringDateToLong(estimatedStartDate));
//        datePickerDialog.getDatePicker().setMaxDate(Utility.stringDateToLong(estimatedEndDate));

        final DatePickerDialog datePickerDialog2 = new DatePickerDialog(WorkPhasesActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);

                String myFormat = "dd/MM/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                isEndDateSelected = true;
                mTvEstEndDate.setText(sdf.format(myCalendar.getTime()));
            }
        }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog2.getDatePicker().setMinDate(Utility.stringDateToLong(projectEstimatedStartDate));
        datePickerDialog2.getDatePicker().setMaxDate(Utility.stringDateToLong(projectEstimatedEndDate));

        if (type.equals("add")) {
            mBtnAdd.setText("ADD");
            remarlLbl.setVisibility(View.GONE);
            eedLbl.setVisibility(View.GONE);
            esdLbl.setVisibility(View.GONE);
//            datePickerDialog.getDatePicker().setMinDate(Utility.stringDateToLong(estimatedStartDate));
//            datePickerDialog.getDatePicker().setMaxDate(Utility.stringDateToLong(estimatedEndDate));


        } else {
            mBtnAdd.setText("UPDATE");
//            mTvPhaseUpdate.setText(workPhaseName);
            mSpnWorkPhases.setVisibility(View.VISIBLE);
            mTvEstStartDate.setText(estimatedStartDate);
            mTvEstEndDate.setText(estimatedEndDate);
            mEtRemarkPhase.setText(remark);

            eedLbl.setVisibility(View.VISIBLE);
            remarlLbl.setVisibility(View.VISIBLE);
            esdLbl.setVisibility(View.VISIBLE);

            for (int i = 0; i < masterPhases.size(); i++) {

                if (workPhaseId == masterPhases.get(i).phaseId) {
                    selectedWPid = i;
                    Log.i(TAG, "setupBottomSheet: inside if  " + workPhaseId + " " + masterPhases.get(i).phaseId);

                    Log.i(TAG, "setupBottomSheet: inside if  " + (workPhaseId == masterPhases.get(i).phaseId));
                    mSpnWorkPhases.setSelection(selectedWPid);
                }
            }
            aa.notifyDataSetChanged();

        }


        mRlStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
        mRlEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog2.show();
            }
        });
        mIvEsd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });

        mIvEed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog2.show();
            }
        });

        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String startDate = mTvEstStartDate.getText().toString().trim();
                String endDate = mTvEstEndDate.getText().toString().trim();
                String remark = mEtRemarkPhase.getText().toString().trim();
                if (!TextUtils.isEmpty(startDate) && !TextUtils.isEmpty(endDate) &&
                        workId != -1 && workPhaseId != -1) {
                    Log.i(TAG, "onClick: " + startDate);
                    Log.i(TAG, "onClick: " + endDate);
                    Log.i(TAG, "onClick: workId " + workId);
                    Log.i(TAG, "onClick: workPhaseId " + workPhaseId);
                    if (type.equals("add")) {
                        if (!isStartDateSelected){
                            Toast.makeText(WorkPhasesActivity.this, "Please select the Estimated Start Date", Toast.LENGTH_SHORT).show();
                        }else if (!isEndDateSelected){
                            Toast.makeText(WorkPhasesActivity.this, "Please select the Estimated End Date", Toast.LENGTH_SHORT).show();
                        }else {
                            if (isStartDateSelected && isEndDateSelected) {
                                JSONObject jObj = new JSONObject();
                                try {
                                    jObj.put("workPhaseId", workPhaseId);
                                    jObj.put("estimatedStartDate", startDate);
                                    jObj.put("estimatedEndDate", endDate);
                                    jObj.put("workId", workId);
                                    jObj.put("remarks", remark);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                JSONObject outerObject = new JSONObject();
                                try {
                                    outerObject.put("data", jObj);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (isConnected) {
                                    addPlan(outerObject);
                                } else {
                                    Toast.makeText(WorkPhasesActivity.this, getResources().getString(R.string.internet_issue), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(WorkPhasesActivity.this, "Please select the Estimated Start Date/End Date", Toast.LENGTH_SHORT).show();
                            }
                        }


                    } else {
                        if (workPlanId != -1) {
                            JSONObject jObj = new JSONObject();
                            try {
                                jObj.put("workPlanId", workPlanId);
                                jObj.put("workPhaseId", workPhaseId);
                                jObj.put("estimatedStartDate", startDate);
                                jObj.put("estimatedEndDate", endDate);
                                jObj.put("workId", workId);
                                jObj.put("remarks", remark);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            JSONObject outerObject = new JSONObject();
                            try {
                                outerObject.put("data", jObj);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (isConnected) {
                                addPlan(outerObject);       //Actually here we are updating
                            } else {
                                Toast.makeText(WorkPhasesActivity.this, getResources().getString(R.string.internet_issue), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                } else {
                    Toast.makeText(WorkPhasesActivity.this, "Please select a work phase", Toast.LENGTH_SHORT).show();
                }


            }
        });
        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void addPlan(JSONObject outerObject) {
        Log.i(TAG, "addPlan: " + outerObject);
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .build();
        AndroidNetworking.post(BuildConfig.BASE_URL.concat("workplan/saveWorkPlan"))
                .addQueryParameter("userId", String.valueOf(sp.getIntData(Constants.USER_ID)))
                .addQueryParameter("token", sp.getStringData(Constants.APP_TOKEN))
                .addJSONObjectBody(outerObject)
                .setTag("SaveWorkPlan")
                .setPriority(Priority.HIGH)
                .setOkHttpClient(okHttpClient) // passing a custom okHttpClient
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
                                    JSONObject obj = jObj.optJSONObject("data");
                                    if (obj != null) {
                                    }
                                    getEditablePlansByWorkId();
                                    Toast.makeText(WorkPhasesActivity.this, status + " " + jObj.optString("failReason"), Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {
                                    dialog.dismiss();
                                    Toast.makeText(WorkPhasesActivity.this, status + " " + jObj.optString("failReason"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(WorkPhasesActivity.this, "Some Problem", Toast.LENGTH_SHORT).show();
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

    private String remark;
    private static int REFRESH_PHASE_UPDATE_CODE = 22;
    @Override
    public void onPhaseClick(int position, View view, WorkPhase wp, boolean isLongClick, int actionType) {


        if (userType.equals("ROLE_SECTION_OFFICER")) {

        } else {
            if (wp.status.equals("NEW")) {//REVERTED
                workPlanId = wp.workPlanId;
                workPhaseId = wp.workPhaseId;
                estimatedStartDate = wp.estimatedStartDate;
                estimatedEndDate = wp.estimatedEndDate;
                remark = wp.remarks;
                workId = wp.workId;
                workPhaseName = wp.workPhaseName;
                itemPosition = position;

                if (isLongClick) {
                    if (actionType == 0) {
                        setupBottomSheet("Update");
                    } else if (actionType == 1) {
                        Toast.makeText(this, "Delete", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Intent intent = new Intent(this, WorkDetailActivity.class);
                intent.putExtra("workPlanId", wp.workPlanId);
                intent.putExtra("projName", projName);
                intent.putExtra("compName", compName);
                intent.putExtra("phaseName", wp.workPhaseName);
                startActivityForResult(intent, REFRESH_PHASE_UPDATE_CODE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REFRESH_PHASE_UPDATE_CODE && resultCode == RESULT_OK) {
            String fromAdapter = sp.getStringData("FROMADAPTER");
            if (userType.equals("ROLE_AGENCY")) {
                getPhasesFromMaster();
                if (fromAdapter.equals("REVERTED") || fromAdapter.equals("NEW") || fromAdapter.equals("PENDING") || fromAdapter.equals("OPEN")) {
                    getEditablePlansByWorkId();
                }
                if (fromAdapter.equals("APPROVED")) {
                    getProgressablePlansByWorkId();
                    phaseCount = getIntent().getIntExtra("PHASE_COUNT", -1);
                    if (phaseCount == 0) {
                        mIvAddPhase.setVisibility(View.VISIBLE);
                    } else {
                        mIvAddPhase.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select The Action");
        menu.add(0, v.getId(), 0, "Update")
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        setupBottomSheet("Update");
                        return true;
                    }
                });
    }

    @Override
    public void onUnapprovedPhaseClick(int position, View view, WorkPhase wp, boolean isLongClick, int actionType) {
        if (wp.status.equals("NEW")  || wp.status.equals("REVERTED")) {
            workPlanId = wp.workPlanId;
            workPhaseId = wp.workPhaseId;
            estimatedStartDate = wp.estimatedStartDate;
            estimatedEndDate = wp.estimatedEndDate;
            workId = wp.workId;
            remark = wp.remarks;
            workPhaseName = wp.workPhaseName;
            itemPosition = position;

            if (isLongClick) {
                if (actionType == 0) {
                    setupBottomSheet("Update");
                } else if (actionType == 1) {
                    Toast.makeText(this, "Delete", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (wp.status.equals("PENDING")) {
            Toast.makeText(this, "You can't update this because it's in pending phase", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        this.isConnected = isConnected;
    }
}
