package com.aashdit.ipms.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.aashdit.ipms.receiver.ConnectivityChangeReceiver;

/**
 * Created by Manabendu on 24/08/20
 */
//public class SyncService extends Service implements ConnectivityChangeReceiver.ConnectivityReceiverListener {
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//
//        Bundle extras = intent.getExtras();
//        if (extras != null) {
//            boolean isNetworkConnected = extras.getBoolean("isNetworkConnected");
//            Toast.makeText(this, "Service started :: " + isNetworkConnected, Toast.LENGTH_SHORT).show();
//        }
//
//        return START_STICKY;
//    }
//
//
//    @Override
//    public void onNetworkConnectionChanged(boolean isConnected) {
//        Toast.makeText(this, isConnected + " From Service", Toast.LENGTH_SHORT).show();
//    }
//}
