package com.aashdit.ipms.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aashdit.ipms.adapters.BillsAdapter;
import com.aashdit.ipms.databinding.ActivityBillBinding;
import com.aashdit.ipms.models.TempBill;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class BillActivity extends AppCompatActivity implements BillsAdapter.OnNavigateListener {

    private static final String TAG = "BillActivity";

    private ActivityBillBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBillBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        binding.btnGenerateBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(binding.rlBillRoot, "Move to Create Bill Page", BaseTransientBottomBar.LENGTH_LONG).show();
                Intent genIntent = new Intent(BillActivity.this,GenerateBillActivity.class);
                startActivity(genIntent);
            }
        });

        binding.ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(binding.rlBillRoot, "Move to Create Bill Page", BaseTransientBottomBar.LENGTH_LONG).show();
                Intent genIntent = new Intent(BillActivity.this,GenerateBillActivity.class);
                genIntent.putExtra(GenerateBillActivity.TYPE,"ADD");
                startActivity(genIntent);
            }
        });

        ArrayList<TempBill> billArrayList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            billArrayList.add(new TempBill("Construction of kalinga model residential school","IPMS00"+i,"4000000"));
        }

        BillsAdapter adapter = new BillsAdapter(this,billArrayList);
        adapter.setOnNavigateListener(this);
        binding.rvBills.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,false));
        binding.rvBills.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNavigationClick(String type) {
        Intent updateIntent = new Intent(this, GenerateBillActivity.class);
        updateIntent.putExtra(GenerateBillActivity.TYPE,type);
        startActivity(updateIntent);
    }
}
