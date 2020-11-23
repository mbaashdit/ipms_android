package com.aashdit.ipms.ui.activities;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.aashdit.ipms.R;
import com.aashdit.ipms.databinding.ActivityLoginBinding;
import com.aashdit.ipms.network.Client;
import com.aashdit.ipms.util.Constants;
import com.aashdit.ipms.util.SharedPrefManager;
import com.aashdit.ipms.util.Utility;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private SharedPrefManager sp;
    private ActivityLoginBinding binding;
    private String userName, password, ip;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Test Update

        sp = SharedPrefManager.getInstance(this);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
        AndroidNetworking.initialize(getApplicationContext(),okHttpClient);

        progressBar = findViewById(R.id.progress_circular);
        progressBar.setVisibility(View.GONE);
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userName = binding.etUserId.getText().toString().trim();
                password = binding.etUserPassword.getText().toString().trim();

                callLoginApi2();

            }
        });

        binding.tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signupIntent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(signupIntent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });
        binding.tvForgetPasswordd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent forgetInent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(forgetInent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

//        binding.tvSignup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent registerIntent = new Intent(LoginActivity.this,RegisterActivity.class);
//                startActivity(registerIntent);
//                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
//            }
//        });
    }

    private void callLoginApi2() {

        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post(Constants.BASE_URL+"moblogin")
                .addBodyParameter("userName", userName)
                .addBodyParameter("password", password)
                .addBodyParameter("ip", ip)
                .setTag("Login")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        if (Utility.isStringValid(response)) {
                            try {
                                JSONObject loginObj = new JSONObject(response);
                                if (loginObj.optString("status").equals("SUCCESS")) {
                                    sp.setStringData(Constants.TEMP_USER_ID,userName);
                                    sp.setStringData(Constants.APP_TOKEN, loginObj.optString("token"));
                                    sp.setBoolData(Constants.APP_LOGIN,true);
                                    sp.setStringData(Constants.USER_PASSWORD,password);
                                    JSONArray array = loginObj.optJSONArray("data");
                                    if (array != null && array.length() > 0) {
                                        JSONObject obj = array.optJSONObject(0);
                                        String firstName = obj.optString("firstName");
                                        sp.setStringData(Constants.USER_FIRST_NAME,firstName);
                                        String roleCode = obj.optString("roleCode");
                                        sp.setStringData(Constants.USER_ROLE_CODE,roleCode);
                                        String mobile = obj.optString("mobile");
                                        sp.setStringData(Constants.USER_MOBILE,mobile);
                                        String roleDescription = obj.optString("roleDescription");
                                        sp.setStringData(Constants.USER_ROLE_DESCRIPTION,roleDescription);
                                        String email = obj.optString("email");
                                        sp.setStringData(Constants.USER_EMAIL,email);
                                        String lastname = obj.optString("lastname");
                                        sp.setStringData(Constants.USER_LAST_NAME,lastname);
                                        String userName = obj.optString("userName");
                                        sp.setStringData(Constants.USER_NAME,userName);
                                        int agencyId = obj.optInt("agencyId");
                                        sp.setIntData(Constants.AGENCY_ID,agencyId);
                                        int userId = obj.optInt("userId");
                                        sp.setIntData(Constants.USER_ID,userId);

                                        moveToMainActivity();
                                    }
                                } else if (loginObj.optString("status").equals("FAILURE")) {
                                    Toast.makeText(LoginActivity.this, loginObj.optString("failReason"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: "+anError.getErrorDetail() );
                        progressBar.setVisibility(View.GONE);
                    }
                });
//                .getAsJSONObject(new JSONObjectRequestListener() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        // do anything with response
//                    }
//                    @Override
//                    public void onError(ANError error) {
//                        // handle error
//                    }
//                });

    }

    /**
     * {
     * "data": [
     * {
     * "firstName": "Sahasranshu",
     * "roleCode": "ROLE_AGENCY",
     * "mobile": "8989878787",
     * "agencyId": 1,
     * "roleDescription": "Agency User",
     * "userId": 4,
     * "email": "sahas@aashdit.com",
     * "lastname": "Mishra"
     * }
     * ],
     * "failReason": "",
     * "status": "SUCCESS",
     * "token": "78768841-8caf-45db-9141-f6e9bf4e71fd"
     * }
     */

//    private void callLoginApi() {
//        progressBar.setVisibility(View.VISIBLE);
//        Client.getInstance().getApi().agencyUserLogin(userName, password, ip)
//                .enqueue(new Callback<String>() {
//                    @Override
//                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
//                        if (response.isSuccessful() && response.body() != null) {
//                            progressBar.setVisibility(View.GONE);
//                            if (Utility.isStringValid(response.body())) {
//                                try {
//                                    JSONObject loginObj = new JSONObject(response.body());
//                                    if (loginObj.optString("status").equals("SUCCESS")) {
//                                        sp.setStringData(Constants.APP_TOKEN, loginObj.optString("token"));
//                                        sp.setBoolData(Constants.APP_LOGIN,true);
//                                        sp.setStringData(Constants.USER_PASSWORD,password);
//                                        JSONArray array = loginObj.optJSONArray("data");
//                                        if (array != null && array.length() > 0) {
//                                            JSONObject obj = array.optJSONObject(0);
//                                            String firstName = obj.optString("firstName");
//                                            sp.setStringData(Constants.USER_FIRST_NAME,firstName);
//                                            String roleCode = obj.optString("roleCode");
//                                            sp.setStringData(Constants.USER_ROLE_CODE,roleCode);
//                                            String mobile = obj.optString("mobile");
//                                            sp.setStringData(Constants.USER_MOBILE,mobile);
//                                            String roleDescription = obj.optString("roleDescription");
//                                            sp.setStringData(Constants.USER_ROLE_DESCRIPTION,roleDescription);
//                                            String email = obj.optString("email");
//                                            sp.setStringData(Constants.USER_EMAIL,email);
//                                            String lastname = obj.optString("lastname");
//                                            sp.setStringData(Constants.USER_LAST_NAME,lastname);
//                                            int agencyId = obj.optInt("agencyId");
//                                            sp.setIntData(Constants.AGENCY_ID,agencyId);
//                                            int userId = obj.optInt("userId");
//                                            sp.setIntData(Constants.USER_ID,userId);
//
//                                            moveToMainActivity();
//                                        }
//                                    } else if (loginObj.optString("status").equals("FAILURE")) {
//                                        Toast.makeText(LoginActivity.this, loginObj.optString("failReason"), Toast.LENGTH_SHORT).show();
//                                    }
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
//                        Log.e(TAG, "onFailure: " + t.getLocalizedMessage());
//                        progressBar.setVisibility(View.GONE);
//                    }
//                });
//    }

    private void moveToMainActivity() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        finish();
    }
}
