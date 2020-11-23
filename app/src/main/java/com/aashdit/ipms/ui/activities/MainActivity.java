package com.aashdit.ipms.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.aashdit.ipms.R;
import com.aashdit.ipms.app.App;
import com.aashdit.ipms.receiver.ConnectivityChangeReceiver;
import com.aashdit.ipms.ui.fragments.HomeFragment;
import com.aashdit.ipms.ui.fragments.NavigationFragment;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationFragment.MenuClickListener,
        NavigationView.OnNavigationItemSelectedListener, ConnectivityChangeReceiver.ConnectivityReceiverListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private boolean isConnected;
    private TextView mTVTitle;

    private FragmentManager fragmentManager;
    private Fragment fragment = null;
    private ConnectivityChangeReceiver mConnectivityChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mTVTitle = findViewById(R.id.tv_dash_name);
        mTVTitle.setText("Dashboard");

        mConnectivityChangeReceiver = new ConnectivityChangeReceiver();
        mConnectivityChangeReceiver.setConnectivityReceiverListener(this);
        isConnected = mConnectivityChangeReceiver.getConnectionStatus(this);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationFragment navigationFragment = (NavigationFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        if (navigationFragment != null) {
            navigationFragment.setUp(mDrawerLayout, this);
            setDrawerListener();
        }

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragment = new HomeFragment();
        fragmentTransaction.replace(R.id.frameLayout, fragment).commit();
        registerNetworkBroadcast();
//        Intent intent = new Intent(this, SyncService.class);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForegroundService(intent);
//        } else {
//            startService(intent);
//        }
    }

    private void setDrawerListener() {
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.Open, R.string.Close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerLayout.post(mDrawerToggle::syncState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClickMenuItem(int position) {
        mDrawerLayout.closeDrawer(GravityCompat.START);
        if (position == 1) {
            Intent proIntent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(proIntent);
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        } else if (position == 2) {
            Intent proIntent = new Intent(MainActivity.this, ProjectListActivity.class);
            startActivity(proIntent);
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        } else if (position == 3) {
            Intent billIntent = new Intent(MainActivity.this, BillActivity.class);
            startActivity(billIntent);
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        this.isConnected = isConnected;
        Log.i(TAG, "onNetworkConnectionChanged: "+isConnected);
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
}
