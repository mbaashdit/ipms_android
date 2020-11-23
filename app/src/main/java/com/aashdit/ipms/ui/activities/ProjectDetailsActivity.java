package com.aashdit.ipms.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.aashdit.ipms.databinding.ActivityProjectDetailsBinding;
import com.aashdit.ipms.models.Work;

public class ProjectDetailsActivity extends AppCompatActivity {

    private static final String TAG = "ProjectDetailsActivity";
    public static String WORK = "work";
    private ActivityProjectDetailsBinding binding;
    private Work work;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProjectDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbarProTitle);

        work = getIntent().getParcelableExtra(WORK);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            if (work != null) {
                binding.tvTitle.setText(work.projectName);
            }
        }

        binding.btnAddPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        binding.btnAddProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent progIntent = new Intent(ProjectDetailsActivity.this, AddProgressActivity.class);
                startActivity(progIntent);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
