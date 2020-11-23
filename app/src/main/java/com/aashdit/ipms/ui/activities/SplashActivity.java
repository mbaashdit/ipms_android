package com.aashdit.ipms.ui.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.aashdit.ipms.R;
import com.aashdit.ipms.app.App;
import com.aashdit.ipms.network.ANetwork;
import com.aashdit.ipms.receiver.ConnectivityChangeReceiver;
import com.aashdit.ipms.util.Constants;
import com.aashdit.ipms.util.SharedPrefManager;
import com.aashdit.ipms.util.Utility;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SplashActivity extends AppCompatActivity implements ConnectivityChangeReceiver.ConnectivityReceiverListener {

    private static final String TAG = SplashActivity.class.getSimpleName();

    private Handler handler;
    private Runnable runnable = this::moveToLogin;
    private boolean isLogin;
    private SharedPrefManager sp;
    private String userName, password, ip;
    private ConnectivityChangeReceiver mConnectivityChangeReceiver;
    private boolean isConnected;
    private RelativeLayout mRlSplashRoot;
    private void moveToLogin() {
        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        mRlSplashRoot = findViewById(R.id.rl_splash_root);
        ANetwork.setContext(this);

        mConnectivityChangeReceiver = new ConnectivityChangeReceiver();
        mConnectivityChangeReceiver.setConnectivityReceiverListener(this);
        isConnected = mConnectivityChangeReceiver.getConnectionStatus(this);
        registerNetworkBroadcast();
        sp = SharedPrefManager.getInstance(this);
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        isLogin = sp.getBoolData(Constants.APP_LOGIN);
        if (isLogin) {
            userName = sp.getStringData(Constants.TEMP_USER_ID);
            password = sp.getStringData(Constants.USER_PASSWORD);
            callLoginApi();
        } else {
            handler = new Handler();
            int SPLASH_TIME = 2000;
            handler.postDelayed(runnable, SPLASH_TIME);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        App.getInstance().setConnectivityListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterNetworkChanges();
//        handler.removeCallbacks(runnable);
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
    private void callLoginApi() {


        AndroidNetworking.post(Constants.BASE_URL + "moblogin")
                .addBodyParameter("userName", userName)
                .addBodyParameter("password", password)
                .addBodyParameter("ip", ip)
                .setTag("Login")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        if (Utility.isStringValid(response)) {
                            try {
                                JSONObject loginObj = new JSONObject(response);
                                if (loginObj.optString("status").equals("SUCCESS")) {
                                    sp.setStringData(Constants.APP_TOKEN, loginObj.optString("token"));
                                    sp.setBoolData(Constants.APP_LOGIN, true);
                                    sp.setStringData(Constants.USER_PASSWORD, password);
                                    JSONArray array = loginObj.optJSONArray("data");
                                    if (array != null && array.length() > 0) {
                                        JSONObject obj = array.optJSONObject(0);
                                        String firstName = obj.optString("firstName");
                                        sp.setStringData(Constants.USER_FIRST_NAME, firstName);
                                        String roleCode = obj.optString("roleCode");
                                        sp.setStringData(Constants.USER_ROLE_CODE, roleCode);
                                        String mobile = obj.optString("mobile");
                                        sp.setStringData(Constants.USER_MOBILE, mobile);
                                        String roleDescription = obj.optString("roleDescription");
                                        sp.setStringData(Constants.USER_ROLE_DESCRIPTION, roleDescription);
                                        String email = obj.optString("email");
                                        sp.setStringData(Constants.USER_EMAIL, email);
                                        String lastname = obj.optString("lastname");
                                        sp.setStringData(Constants.USER_LAST_NAME, lastname);
                                        String userName = obj.optString("userName");
                                        sp.setStringData(Constants.USER_NAME,userName);
                                        int agencyId = obj.optInt("agencyId");
                                        sp.setIntData(Constants.AGENCY_ID, agencyId);
                                        int userId = obj.optInt("userId");
                                        sp.setIntData(Constants.USER_ID, userId);

                                        moveToMainActivity();
                                    }
                                } else if (loginObj.optString("status").equals("FAILURE")) {
                                    Toast.makeText(SplashActivity.this, loginObj.optString("failReason"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Snackbar.make(mRlSplashRoot,anError.getErrorDetail(), Snackbar.LENGTH_SHORT).show();
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });

    }

    private void moveToMainActivity() {
        Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(mainIntent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        finish();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        this.isConnected = isConnected;
    }
}
