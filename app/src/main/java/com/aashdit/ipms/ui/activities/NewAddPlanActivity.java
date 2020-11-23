package com.aashdit.ipms.ui.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aashdit.ipms.R;
import com.aashdit.ipms.adapters.UnApprovedWorkComponentsAdapter;
import com.aashdit.ipms.adapters.WorkComponentsAdapter;
import com.aashdit.ipms.app.App;
import com.aashdit.ipms.models.FileOffline;
import com.aashdit.ipms.models.PlanableWorks;
import com.aashdit.ipms.models.PlansOffline;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.hbisoft.pickit.PickiT;
import com.hbisoft.pickit.PickiTCallbacks;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;

import static com.aashdit.ipms.util.Constants.DOC;
import static com.aashdit.ipms.util.Constants.DOCX;
import static com.aashdit.ipms.util.Constants.IMAGE;
import static com.aashdit.ipms.util.Constants.PDF;
import static com.aashdit.ipms.util.Constants.PPT;
import static com.aashdit.ipms.util.Constants.PPTX;
import static com.aashdit.ipms.util.Constants.XLS;
import static com.aashdit.ipms.util.Constants.XLSX;

public class NewAddPlanActivity extends AppCompatActivity implements PickiTCallbacks,
        UnApprovedWorkComponentsAdapter.OnWorkComponentListener, WorkComponentsAdapter.OnWorkComponentListener,
        ConnectivityChangeReceiver.ConnectivityReceiverListener, ActionMode.Callback {

    public static final String PROJECT_ID = "projectId";
    public static final String PROJECT_NAME = "projectName";
    public static final String PROJECT_ESD = "estimatedStartDate";
    public static final String PROJECT_EED = "estimatedEndDate";
    private static final String TAG = "NewAddPlanActivity";
    private static final int FILE_SELECT_CODE = 0;
    private static final int REQUEST_CHOOSER = 1234;
    //Declare PickiT
    PickiT pickiT;
    private TextView mTvTitle, mTvProjName;
    private Toolbar toolbar;
    private RecyclerView mRvWorkComponents, mRvUnApprovedComponents;
    private ProgressBar progressBar;
    private ArrayList<PlanableWorks> workArrayList;
    private FloatingActionButton fabUpload, fabDone, fabReUpload;
    private int projectId;
    private String projectName;
    private String token;
    private SharedPrefManager sp;
    private ArrayList<PlanableWorks> apoWorkList;   //Approved Pending Open= disable
    private ArrayList<PlanableWorks> revertedWorks; //Reverted New = enable
    private RelativeLayout mRlNewAddPlanRoot;
    private WorkComponentsAdapter workComponentsAdapter;
    private UnApprovedWorkComponentsAdapter unApprovedWorkComponentsAdapter;
    private String filename;
    private byte[] fileByteArr;
    private RelativeLayout mRlFileUploadContainer;
    private FloatingActionButton fabApprove;

    private ConnectivityChangeReceiver mConnectivityChangeReceiver;
    private boolean isConnected;
    private Realm realm;


    /**
     * To get the logedin user type
     */
    private String userType;

    private String estStartDate, estEndDate;

    private TextView mTvFileName;
    private BottomSheetDialog dialog;
    private ActionMode mActionMode;
    private ArrayList<PlanableWorks> selectedPlanableWork = new ArrayList<>();

    /**
     * Convert a file to byte[]
     */
    public static byte[] getByteArrayFromFile(String filePath) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filePath);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            for (int readNum; (readNum = fis.read(b)) != -1; ) {
                bos.write(b, 0, readNum);
            }
            return bos.toByteArray();
        } catch (Exception e) {
            Log.d("mylog", e.toString());
        }
        return null;
    }

    public static byte[] convertFileToByteArray(File f) {
        byte[] byteArray = null;
        try {
            InputStream inputStream = new FileInputStream(f);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024 * 8];  //10000000
            int bytesRead = 0;

            while ((bytesRead = inputStream.read(b)) != -1) {
                bos.write(b, 0, bytesRead);
            }
            byteArray = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray;
    }

    private static double getFileSizeMegaBytes(File file) {
        return (double) file.length() / (1024 * 1024);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_add_plan);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        sp = SharedPrefManager.getInstance(this);
        ANetwork.setContext(this);
        realm = Realm.getDefaultInstance();
        mConnectivityChangeReceiver = new ConnectivityChangeReceiver();
        mConnectivityChangeReceiver.setConnectivityReceiverListener(this);
        isConnected = mConnectivityChangeReceiver.getConnectionStatus(this);
        registerNetworkBroadcast();
        /**
         *  if the user role is equals to ROLE_AGENCY than do a agency login &
         *  if the user role is equals to ROLE_SECTION_OFFICER than do a SO login and hide file upload
         *  button and don't ask for STORAGE_READ, STORAGE_WRITE Permission.
         * */
        userType = sp.getStringData(Constants.USER_ROLE_CODE);

        projectId = getIntent().getIntExtra(PROJECT_ID, -1);
        projectName = getIntent().getStringExtra(PROJECT_NAME);
        estStartDate = getIntent().getStringExtra(PROJECT_ESD);
        estEndDate = getIntent().getStringExtra(PROJECT_EED);
        sp.setIntData(Constants.PROJECT_ID,projectId);


        mTvFileName = findViewById(R.id.tv_file_name);
        mTvProjName = findViewById(R.id.tv_project_name_bc);
        mTvProjName.setText(projectName);
        mRlFileUploadContainer = findViewById(R.id.rl_file_upload_container);
        mTvTitle = findViewById(R.id.tv_project_name);
        mRlNewAddPlanRoot = findViewById(R.id.rl_new_add_plan);
        fabUpload = findViewById(R.id.fab_upload);
        fabDone = findViewById(R.id.fab_done);
        fabReUpload = findViewById(R.id.fab_reupload);
        progressBar = findViewById(R.id.progressBar);
        fabApprove = findViewById(R.id.fab_approve);

        fabDone.setVisibility(View.GONE);
        fabReUpload.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        fabUpload.setVisibility(View.GONE);
        mTvFileName.setVisibility(View.GONE);
        apoWorkList = new ArrayList<>();
        revertedWorks = new ArrayList<>();

        token = sp.getStringData(Constants.APP_TOKEN);
        mTvTitle.setText(projectName);

        //Initialize PickiT
        //context, listener, activity
        pickiT = new PickiT(this, this, this);

        workArrayList = new ArrayList<>();
        mRvWorkComponents = findViewById(R.id.rv_work_components_list);
        mRvWorkComponents.setItemAnimator(new DefaultItemAnimator());
        mRvWorkComponents.setLayoutManager(new LinearLayoutManager(this));

        mRvUnApprovedComponents = findViewById(R.id.rv_work_unapproved_components_list);
        mRvUnApprovedComponents.setItemAnimator(new DefaultItemAnimator());
        mRvUnApprovedComponents.setLayoutManager(new LinearLayoutManager(this));

        workComponentsAdapter = new WorkComponentsAdapter(this, apoWorkList, Constants.PLAN);
        workComponentsAdapter.setWorkComponentListener(this);
        mRvWorkComponents.setAdapter(workComponentsAdapter);

        unApprovedWorkComponentsAdapter = new UnApprovedWorkComponentsAdapter(this, revertedWorks, Constants.PLAN);
        unApprovedWorkComponentsAdapter.setWorkComponentListener(this);
        mRvUnApprovedComponents.setAdapter(unApprovedWorkComponentsAdapter);

        fabUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                showFileChooser();
                // Create the ACTION_GET_CONTENT Intent
//                Intent getContentIntent = MyFileUtils.createGetContentIntent();
//
//                Intent intent = Intent.createChooser(getContentIntent, "Select a file");
//                startActivityForResult(intent, REQUEST_CHOOSER);

                Intent intent = Constants.getCustomFileChooserIntent(DOC, DOCX, PDF, IMAGE, XLS, XLSX, PPT, PPTX);
                startActivityForResult(intent, 101);
            }
        });


        if (userType.equals("ROLE_AGENCY")) {
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


            fabApprove.setVisibility(View.GONE);

            getPlannableWorksByProjectId();

        } else if (userType.equals("ROLE_SECTION_OFFICER")) {
            fabUpload.setVisibility(View.GONE);
            fabDone.setVisibility(View.GONE);
            fabReUpload.setVisibility(View.GONE);
            mTvFileName.setVisibility(View.GONE);

            fabApprove.setVisibility(View.VISIBLE);
            getApprovableWorksByProjectId();
            fabApprove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showApproveBottomSheet();
                }
            });
            fabUpload.setVisibility(View.GONE);
            mTvFileName.setVisibility(View.GONE);
        }


        fabDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendPlanForApproval();
            }
        });

        fabReUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabReUpload.setVisibility(View.GONE);
                fabDone.setVisibility(View.GONE);
            }
        });
    }

    private void showApproveBottomSheet() {

        View dialogView = getLayoutInflater().inflate(R.layout.take_action_bottom_sheet, null);
        dialog = new BottomSheetDialog(this);
        dialog.setContentView(dialogView);

        EditText mEtReason = dialogView.findViewById(R.id.et_remark_on_action);
        Button mBtnApprove = dialogView.findViewById(R.id.btn_approve);
        Button mBtnReject = dialogView.findViewById(R.id.btn_reject);
        ImageView mIvClose = dialogView.findViewById(R.id.iv_approve_close);
        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        mBtnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rejectReason = mEtReason.getText().toString().trim();
                if (!TextUtils.isEmpty(rejectReason)) {
                    //TODO call reject api

                    rejectWorkPlan(rejectReason);
                } else {
                    Toast.makeText(NewAddPlanActivity.this, "Rejection Reason can't be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mBtnApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String approveReason = mEtReason.getText().toString().trim();
                //TODO call approve api

                approveWorkPlan(approveReason);

            }
        });

        dialog.show();
    }

    private void rejectWorkPlan(String reason) {
        progressBar.setVisibility(View.VISIBLE);
        String userId = String.valueOf(sp.getIntData(Constants.USER_ID));

        JSONArray jWorkIdArray = new JSONArray();
        for (int i = 0; i < apoWorkList.size(); i++) {
            jWorkIdArray.put(apoWorkList.get(i).workId);
        }

        JSONObject dataObject = new JSONObject();
        try {
            dataObject.put("workIds", jWorkIdArray);
            dataObject.put("remarks", reason);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject mainObject = new JSONObject();
        try {
            mainObject.put("data", dataObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(Constants.BASE_URL.concat("workplan/revertWorkPlans"))
                .addQueryParameter("userId", userId)
                .addQueryParameter("token", token)
                .addJSONObjectBody(mainObject)
                .setTag("revertWorkPlans")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        if (Utility.isStringValid(response)) {
                            if (response.startsWith("{")) {
                                try {
                                    JSONObject object = new JSONObject(response);
                                    String status = object.optString("status");
                                    if (status.equals("SUCCESS")) {
                                        Toast.makeText(NewAddPlanActivity.this, status + "", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        progressBar.setVisibility(View.GONE);
                        Snackbar.make(mRlNewAddPlanRoot, anError.getErrorDetail(), Snackbar.LENGTH_SHORT).show();
                    }
                });


    }

    private void approveWorkPlan(String reason) {
        progressBar.setVisibility(View.VISIBLE);
        String userId = String.valueOf(sp.getIntData(Constants.USER_ID));

        JSONArray jWorkIdArray = new JSONArray();
        for (int i = 0; i < apoWorkList.size(); i++) {
            jWorkIdArray.put(apoWorkList.get(i).workId);
        }

        JSONObject dataObject = new JSONObject();
        try {
            dataObject.put("workIds", jWorkIdArray);
            dataObject.put("remarks", reason);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject mainObject = new JSONObject();
        try {
            mainObject.put("data", dataObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(Constants.BASE_URL.concat("workplan/approveWorkPlans"))
                .addQueryParameter("userId", userId)
                .addQueryParameter("token", token)
                .addJSONObjectBody(mainObject)
                .setTag("approveWorkPlans")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        if (Utility.isStringValid(response)) {
                            if (response.startsWith("{")) {
                                try {
                                    JSONObject object = new JSONObject(response);
                                    String status = object.optString("status");
                                    if (status.equals("SUCCESS")) {
                                        Toast.makeText(NewAddPlanActivity.this, status + "", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        progressBar.setVisibility(View.GONE);
                        Snackbar.make(mRlNewAddPlanRoot, anError.getErrorDetail(), Snackbar.LENGTH_SHORT).show();
                    }
                });


    }

    private void getApprovableWorksByProjectId() {
        progressBar.setVisibility(View.VISIBLE);
        HashMap<String, Object> reqParam = new HashMap<>();
        projectId = sp.getIntData(Constants.PROJECT_ID);
        reqParam.put("pid", String.valueOf(projectId));
        reqParam.put("token", token);

        AndroidNetworking.get(Constants.BASE_URL.concat("workplan/getApprovableWorksByProjectId"))
                .addQueryParameter(reqParam)
                .setTag("ApprovableWorksByProjectId")
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
                                            apoWorkList.add(pw);
                                        }
                                        workComponentsAdapter.notifyDataSetChanged();
                                    }
                                } else {
                                    Snackbar.make(mRlNewAddPlanRoot, jObj.optString("failReason"), Snackbar.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        progressBar.setVisibility(View.GONE);
                        Snackbar.make(mRlNewAddPlanRoot, anError.getErrorDetail(), Snackbar.LENGTH_SHORT).show();
                    }
                });

    }

    //    private ArrayList<PlanableWorks> selectedPlanableWorkForSend =  new ArrayList<>();
    private void sendPlanForApproval() {

        progressBar.setVisibility(View.VISIBLE);
        String userId = String.valueOf(sp.getIntData(Constants.USER_ID));
//        if (isConnected) {
        JSONArray jWorkIdArray = new JSONArray();
        for (int i = 0; i < selectedPlanableWork.size(); i++) {
            jWorkIdArray.put(selectedPlanableWork.get(i).workId);
        }

        JSONObject fileObject = new JSONObject();
        JSONArray fileJsonArray = null;
        try {
            fileJsonArray = new JSONArray(fileByteArr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            fileObject.put("fileName", filename);
            fileObject.put("fileData", fileJsonArray);

            Log.i(TAG, "sendPlaForApproval: \n\n\n " + fileJsonArray + "\n\n\n\n\n");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject dataObject = new JSONObject();
        try {
            dataObject.put("workIds", jWorkIdArray);
            dataObject.put("file", fileObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject outerObject = new JSONObject();
        try {
            outerObject.put("data", dataObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "sendPlaForApproval: \n\n\n\n" + outerObject + "    \n\n\n\n\n");
//        }
        if (isConnected) {
            AndroidNetworking.post(Constants.BASE_URL.concat("workplan/sendWorkPlanForApproval"))
                    .addQueryParameter("userId", userId)
                    .addQueryParameter("token", token)
                    .addJSONObjectBody(outerObject)
                    .setTag("sendWorkPlanForApproval")
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
                                    Toast.makeText(NewAddPlanActivity.this, status + " "+jObj.optString("failReason"), Toast.LENGTH_SHORT).show();

                                    fabDone.setVisibility(View.GONE);
                                    fabReUpload.setVisibility(View.GONE);
                                    fabUpload.setVisibility(View.GONE);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            progressBar.setVisibility(View.GONE);
                            Log.e(TAG, "onError: " + anError.getErrorDetail());
                            Snackbar.make(mRlNewAddPlanRoot, anError.getErrorDetail(), Snackbar.LENGTH_SHORT).show();
//                        Toast.makeText(NewAddPlanActivity.this, "" + anError.getErrorDetail(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            progressBar.setVisibility(View.GONE);
            FileOffline file = new FileOffline();
            file.setFileName(filename);
            file.setFileData(fileByteArr);

            RealmList<Integer> workids = new RealmList<>();
            if (jWorkIdArray.length() > 0) {
                for (int i = 0; i < jWorkIdArray.length(); i++) {
                    workids.add(jWorkIdArray.optInt(i));
                }
            }
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm1) {

                    PlansOffline offline = new PlansOffline(System.currentTimeMillis(), "20.387587", "85.34347", false, outerObject.toString() /*workids, file*/);
                    realm1.insertOrUpdate(offline);
                    Snackbar.make(mRlFileUploadContainer, "Due to connectivity issue files stored locally", Snackbar.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void getPlannableWorksByProjectId() {
        progressBar.setVisibility(View.VISIBLE);
        HashMap<String, Object> reqParam = new HashMap<>();
        projectId = sp.getIntData(Constants.PROJECT_ID);
        reqParam.put("pid", String.valueOf(projectId));
        reqParam.put("token", token);

        AndroidNetworking.get(Constants.BASE_URL.concat("workplan/getPlannableWorksByProjectId"))
                .addQueryParameter(reqParam)
                .setTag("PlannableWorkByProjectId")
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
                                        fabUpload.setVisibility(View.GONE);
                                        mTvFileName.setVisibility(View.GONE);
                                        apoWorkList.clear();
                                        revertedWorks.clear();
                                        for (int i = 0; i < jArray.length(); i++) {
                                            PlanableWorks pw = PlanableWorks.parsePlannableWork(jArray.optJSONObject(i));
                                            if (pw.currentStatus.equals("APPROVED") || pw.currentStatus.equals("PENDING")) {
                                                apoWorkList.add(pw);
                                            } else if (pw.currentStatus.equals("REVERTED") || pw.currentStatus.equals("NEW") || pw.currentStatus.equals("OPEN")) {
                                                revertedWorks.add(pw);
                                            }
                                        }
                                        workComponentsAdapter.notifyDataSetChanged();
                                        unApprovedWorkComponentsAdapter.notifyDataSetChanged();
                                    } else {

                                    }
                                } else {
                                    Snackbar.make(mRlNewAddPlanRoot, jObj.optString("failReason"), Snackbar.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        progressBar.setVisibility(View.GONE);
                        Snackbar.make(mRlNewAddPlanRoot, anError.getErrorDetail(), Snackbar.LENGTH_SHORT).show();
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

    private boolean alreadyLongPressed;

    @Override
    public void workComponentClick(String type, int position, String compName) {
//        alreadyLongPressed = isLongClick;
//        if (isLongClick) {
//            mActionMode = startSupportActionMode(this);
//        } else {

        Intent intent = new Intent(this, WorkPhasesActivity.class);
        intent.putExtra("TYPE", type);
        intent.putExtra("fromAdapter", apoWorkList.get(position).currentStatus);
        intent.putExtra(WorkPhasesActivity.WORK_ID, apoWorkList.get(position).workId);
        intent.putExtra("PHASE_COUNT",apoWorkList.get(position).phaseCount);
        intent.putExtra(WorkPhasesActivity.EST_START_DATE, estStartDate /*planableWorks.get(position).estimatedStartDate*/);
        intent.putExtra(WorkPhasesActivity.EST_END_DATE, estEndDate/*planableWorks.get(position).estimatedEndDate*/);
        intent.putExtra(WorkPhasesActivity.WP_TITLE, apoWorkList.get(position).workComponentName);
        intent.putExtra(WorkPhasesActivity.PROJ_NAME, projectName);
        startActivityForResult(intent,RELOAD_APPROVED_COMPONENT_PHASE);
//        }
    }

    private static final int RELOAD_COMPONENT_PHASE = 12;
    private static final int RELOAD_APPROVED_COMPONENT_PHASE = 13;
    @Override
    public void UnapprovedWorkComponentClick(String type, int position, String compName, boolean isLOngClick) {
        alreadyLongPressed = isLOngClick;
        if (alreadyLongPressed) {
            mActionMode = startSupportActionMode(this);
        } else {
            Intent intent = new Intent(this, WorkPhasesActivity.class);
            intent.putExtra("TYPE", type);
            intent.putExtra("fromAdapter", revertedWorks.get(position).currentStatus);
            intent.putExtra(WorkPhasesActivity.WORK_ID, revertedWorks.get(position).workId);
            intent.putExtra(WorkPhasesActivity.EST_START_DATE, estStartDate /*planableWorks.get(position).estimatedStartDate*/);
            intent.putExtra(WorkPhasesActivity.EST_END_DATE, estEndDate/*planableWorks.get(position).estimatedEndDate*/);
            intent.putExtra(WorkPhasesActivity.WP_TITLE, revertedWorks.get(position).workComponentName);
            intent.putExtra(WorkPhasesActivity.PROJ_NAME, projectName);
            startActivityForResult(intent,RELOAD_COMPONENT_PHASE);
        }
    }

    @Override
    public void enableMultiSelect(int position, PlanableWorks work, boolean isSelected) {
        if (!selectedPlanableWork.contains(work) && isSelected) {
            selectedPlanableWork.add(work);
        } else {
            if (!isSelected) {
                selectedPlanableWork.remove(work);
            }
            unApprovedWorkComponentsAdapter.isSelected = false;
        }
        if (mActionMode != null) {
            mActionMode.setTitle(selectedPlanableWork.size() + " Selected");
            if (selectedPlanableWork.size() == 0) {
                mActionMode.finish();
            }
        }
        mRvUnApprovedComponents.post(new Runnable() {
            @Override
            public void run() {
                unApprovedWorkComponentsAdapter.notifyDataSetChanged();
            }
        });
//        mActionMode.setTitle(selectedPlanableWork.size() + " Selected");
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHOOSER) {
            if (resultCode == RESULT_OK) {
                pickiT.getPath(data.getData(), Build.VERSION.SDK_INT);
                fabUpload.setVisibility(View.VISIBLE);
                mTvFileName.setVisibility(View.VISIBLE);
            }
        }
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                pickiT.getPath(data.getData(), Build.VERSION.SDK_INT);
                fabUpload.setVisibility(View.VISIBLE);
                mTvFileName.setVisibility(View.VISIBLE);
            }
        }
        if (requestCode == RELOAD_COMPONENT_PHASE && resultCode == RESULT_OK) {
            getPlannableWorksByProjectId();
        }
        if (requestCode == RELOAD_APPROVED_COMPONENT_PHASE && resultCode == RESULT_OK) {
            getPlannableWorksByProjectId();
        }
    }

    @Override
    public void PickiTonUriReturned() {

    }

    @Override
    public void PickiTonStartListener() {

    }

    @Override
    public void PickiTonProgressUpdate(int progress) {

    }

    @Override
    public void PickiTonCompleteListener(String path, boolean wasDriveFile, boolean wasUnknownProvider, boolean wasSuccessful, String Reason) {
        Log.i(TAG, "PickiTonCompleteListener: path :::: " + path);

        File file = new File(path);
        Log.i(TAG, "PickiTonCompleteListener: file :::: " + file);
        try {
            FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Path path1 = Paths.get(path);
                byte[] pdfByteArray = Files.readAllBytes(path1);
                Log.i(TAG, "PickiTonCompleteListener: byte[] :::: " + pdfByteArray);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        filename = path.substring(path.lastIndexOf("/") + 1);
        Log.i(TAG, "PickiTonCompleteListener: fileName :::: " + filename);
        double fileSize = getFileSizeMegaBytes(file);
        if (fileSize > 10) {
            Toast.makeText(this, "File Size is too large.Please select below 10 MB", Toast.LENGTH_SHORT).show();
        } else {
            fileByteArr = convertFileToByteArray(file);
        }
        mTvFileName.setText(filename);

    }

    public byte[] read(File file) throws IOException {
//        if (file.length() > MAX_FILE_SIZE) {
//            throw new FileTooBigException(file);
//        }
        ByteArrayOutputStream ous = null;
        InputStream ios = null;
        try {
            byte[] buffer = new byte[1024];
            ous = new ByteArrayOutputStream();
            ios = new FileInputStream(file);
            int read = 0;
            while ((read = ios.read(buffer)) != -1) {
                ous.write(buffer, 0, read);
            }
        } finally {
            try {
                if (ous != null)
                    ous.close();
            } catch (IOException e) {
            }

            try {
                if (ios != null)
                    ios.close();
            } catch (IOException e) {
            }
        }
        return ous.toByteArray();
    }

    @Override
    public void onBackPressed() {
        pickiT.deleteTemporaryFile(this);
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!isChangingConfigurations()) {
            pickiT.deleteTemporaryFile(this);
        }
        unregisterNetworkChanges();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(NewAddPlanActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        this.isConnected = isConnected;
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

        Log.i(TAG, "onResume: new add plan");
        App.getInstance().setConnectivityListener(this);
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.menu_plan_action_mode, menu);
        mode.setTitle("");

        fabUpload.setVisibility(View.VISIBLE);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_send:
                if (fileByteArr == null) {
                    Toast.makeText(this, "Please select an attachment.", Toast.LENGTH_SHORT).show();
                } else {
                    sendPlanForApproval();
                    if (mActionMode != null) {
                        mActionMode.finish();
                    }
                }
                return true;

            default:
                mActionMode.finish();
                fabUpload.setVisibility(View.GONE);
                mTvFileName.setVisibility(View.GONE);
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mActionMode = null;
        unApprovedWorkComponentsAdapter.isLongPressed = false;
        alreadyLongPressed = false;
        unApprovedWorkComponentsAdapter.notifyDataSetChanged();                     //Commented for crashing while long press is active.
        selectedPlanableWork.clear();
        fabUpload.setVisibility(View.GONE);
        mTvFileName.setVisibility(View.GONE);
    }
}