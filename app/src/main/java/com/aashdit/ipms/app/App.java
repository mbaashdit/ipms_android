package com.aashdit.ipms.app;

import android.app.Application;

import androidx.multidex.MultiDex;

import com.aashdit.ipms.receiver.ConnectivityChangeReceiver;
import com.facebook.stetho.Stetho;

import io.realm.Realm;

/**
 * Created by Manabendu on 18/06/20
 */
public class App extends Application {

    private static final String TAG = "App";
    private static App mInstance;
    public static double latitude = 0.0;
    public static double longitude = 0.0;
    public static String capturedAddress = "";

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        MultiDex.install(this);
        Realm.init(this);
        mInstance = this;
    }

    public static synchronized App getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityChangeReceiver.ConnectivityReceiverListener listener) {
        new ConnectivityChangeReceiver().connectivityReceiverListener = listener;
    }
}
