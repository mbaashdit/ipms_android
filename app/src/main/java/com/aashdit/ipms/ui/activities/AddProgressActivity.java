package com.aashdit.ipms.ui.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aashdit.ipms.R;
import com.aashdit.ipms.adapters.WorkTypeAdapter;
import com.aashdit.ipms.databinding.ActivityAddProgressBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AddProgressActivity extends AppCompatActivity implements WorkTypeAdapter.WorkTypeListener {

    private static final String TAG = "AddProgressActivity";
    final Calendar myCalendar = Calendar.getInstance();
    private ActivityAddProgressBinding binding;
    private ArrayList<String> worksList;
    private int hint = 0;
    private ArrayList<Integer> dynamicView = new ArrayList<>();
    private ArrayList<String> dynamicValue = new ArrayList<>();
    private EditText edittTxt;
    private EditText mEtInterimDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddProgressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.rvWork.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));


        worksList = new ArrayList<>();
        worksList.add("Excavation");
        worksList.add("Plinth");
        worksList.add("Sill");
        worksList.add("Roof");
        worksList.add("Roof Cost");

        WorkTypeAdapter workTypeAdapter = new WorkTypeAdapter(this, worksList);
        workTypeAdapter.setWorkTypeListener(this);
        binding.rvWork.setAdapter(workTypeAdapter);

        binding.ivProgressBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

//        binding.etInterimProgressDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                new DatePickerDialog(AddProgressActivity.this, (datePicker, year, month, day) -> {
//                    myCalendar.set(Calendar.YEAR, year);
//                    myCalendar.set(Calendar.MONTH, month);
//                    myCalendar.set(Calendar.DAY_OF_MONTH, day);
//                    updateInterimProgressDate();
//                }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
//                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
//            }
//        });

        binding.etPlanedStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(AddProgressActivity.this, (datePicker, year, month, day) -> {
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, month);
                    myCalendar.set(Calendar.DAY_OF_MONTH, day);
                    updatePlanedStartDate();
                }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        binding.etActualStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(AddProgressActivity.this, (datePicker, year, month, day) -> {
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, month);
                    myCalendar.set(Calendar.DAY_OF_MONTH, day);
                    updateActualStartDate();
                }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        binding.etPlanedEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(AddProgressActivity.this, (datePicker, year, month, day) -> {
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, month);
                    myCalendar.set(Calendar.DAY_OF_MONTH, day);
                    updatePlanedEndDate();
                }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        binding.etActualEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(AddProgressActivity.this, (datePicker, year, month, day) -> {
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, month);
                    myCalendar.set(Calendar.DAY_OF_MONTH, day);
                    updateActualEndDate();
                }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        binding.tvIntProgDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                createEditTextView();
                new DatePickerDialog(AddProgressActivity.this, (datePicker, year, month, day) -> {
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, month);
                    myCalendar.set(Calendar.DAY_OF_MONTH, day);
                    updateDynamicDate();
                }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        binding.btnSaveProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < binding.llContainer.getChildCount(); i++) {
                    EditText rlRoot = (EditText) binding.llContainer.getChildAt(i);

//                    if (rlRoot != null) {
                        Log.i(TAG, "onClick: " + binding.llContainer.getChildCount());
//                        Log.i(TAG, "onClick: " + rlRoot.getChildAt(i));
//                        if (rlRoot.getChildAt(i) instanceof EditText) {
//                            EditText editText = (EditText) rlRoot.getChildAt(i);
                            Log.i(TAG, "onClick: " + rlRoot.getText().toString());
//                        } else {
//                            Log.i(TAG, "onClick: not a edittext");
//                        }
//                    }
                }
            }
        });

    }

    private void updateActualEndDate() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        binding.etActualEndDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void updatePlanedEndDate() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        binding.etPlanedEndDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateActualStartDate() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        binding.etActualStartDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void updatePlanedStartDate() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        binding.etPlanedStartDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateDynamicDate() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        createViewWithData(sdf.format(myCalendar.getTime()));

    }

    private void createViewWithData(String date) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
//        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.setMargins(0, 10, 0, 10);
//        View rowView = inflater.inflate(R.layout.row_progress, null);
        EditText mEtInterimDate  = new EditText(this);//= rowView.findViewById(R.id.et_interim_date);
        mEtInterimDate.setHint("Add Interim Progress Date" + hint);
        mEtInterimDate.setInputType(InputType.TYPE_CLASS_TEXT);
        mEtInterimDate.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        mEtInterimDate.setBackgroundResource(R.drawable.bg_edittext_normal);
        mEtInterimDate.setClickable(true);
        mEtInterimDate.setFocusable(false);
        mEtInterimDate.setId(hint);
        dynamicView.add(hint);
        binding.llContainer.addView(mEtInterimDate,params);
        hint++;

        mEtInterimDate.setText(date);

//        mEtInterimDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                binding.llContainer.removeView(mEtInterimDate);
//            }
//        });

        mEtInterimDate.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
//                binding.llContainer.removeView(mEtInterimDate);
                showDialog(mEtInterimDate);
                return true;
            }
        });
    }


    protected void createEditTextView() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.setMargins(0, 10, 0, 10);
        View rowView = inflater.inflate(R.layout.row_progress, null);
        mEtInterimDate = rowView.findViewById(R.id.et_interim_date);
        mEtInterimDate.setHint("Add Interim Progress Date" + hint);
//        mEtInterimDate.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_assignment, 0);
        mEtInterimDate.setInputType(InputType.TYPE_CLASS_TEXT);
        mEtInterimDate.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        mEtInterimDate.setBackgroundResource(R.drawable.bg_edittext_normal);
        mEtInterimDate.setLayoutParams(params);
        mEtInterimDate.setClickable(true);
        mEtInterimDate.setFocusable(false);
        mEtInterimDate.setId(hint);
        mEtInterimDate.setTag(hint);
        dynamicView.add(hint);
        binding.llContainer.addView(rowView);
        hint++;

//        edittTxt = new EditText(this);
//        int maxLength = 15;
//        hint++;
//        edittTxt.setHint("Add Interim Progress Date");
//        edittTxt.setLayoutParams(params);
//        edittTxt.setBackgroundResource(R.drawable.bg_edittext_normal);
//        edittTxt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_assignment, 0);
//        edittTxt.setInputType(InputType.TYPE_CLASS_TEXT);
//        edittTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
//        edittTxt.setClickable(true);
//        edittTxt.setFocusable(false);
//        edittTxt.setId(hint);

//        InputFilter[] fArray = new InputFilter[1];
//        fArray[0] = new InputFilter.LengthFilter(maxLength);
//        edittTxt.setFilters(fArray);


        ImageView delete = rowView.findViewById(R.id.iv_delete_date);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    private void showDialog(View view) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to delete ?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    binding.llContainer.removeView(view);
                    Toast.makeText(this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", (dialog, id) -> {
                    //  Action for 'NO' Button
                    dialog.cancel();
                });
        AlertDialog alert = builder.create();
        alert.setTitle("Delete Date");
        alert.show();
    }

    @Override
    public void onWorkSelection() {
        binding.etPlanedStartDate.setText("");
        binding.llContainer.removeAllViews();
        Toast.makeText(this, "clean fields", Toast.LENGTH_SHORT).show();
    }
}
