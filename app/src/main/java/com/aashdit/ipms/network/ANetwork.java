package com.aashdit.ipms.network;

import android.content.Context;

import com.androidnetworking.AndroidNetworking;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by Manabendu on 25/07/20
 */
public class ANetwork {

    private ANetwork() {
    }

    public static void setContext(Context context) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        if (context == null) {
            AndroidNetworking.initialize(context, okHttpClient);
        }
    }

}
