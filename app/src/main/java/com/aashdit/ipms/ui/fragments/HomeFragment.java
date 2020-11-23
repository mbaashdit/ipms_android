package com.aashdit.ipms.ui.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aashdit.ipms.R;
import com.aashdit.ipms.adapters.DashboardAdapter;
import com.aashdit.ipms.models.Dashboard;
import com.aashdit.ipms.models.TempBill;
import com.aashdit.ipms.util.Constants;
import com.aashdit.ipms.util.MyDistrictValueFormatter;
import com.aashdit.ipms.util.SharedPrefManager;
import com.aashdit.ipms.util.XYMarkerView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Manabendu on 21/07/20
 */
public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private RecyclerView mRvDashboard;
    private PieChart pieChart;
    private BarChart chart;
    private LineChart lineChart;
    private RelativeLayout mRlFilter;
    private SharedPrefManager sp;
    private String[] projects = {"Delayed Projects", "In Progress Projects"};
    private float[] progress = {15.00f, 85.00f};

    private NestedScrollView mNestedScrollDashboard;
    private TextView mTvProjectName;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mNestedScrollDashboard = view.findViewById(R.id.nested_scroll_dash);
        mTvProjectName= view.findViewById(R.id.tv_app_label);

        sp = SharedPrefManager.getInstance(getActivity());
        if (sp.getStringData(Constants.USER_ROLE_CODE).equals("ROLE_AGENCY")){
            mNestedScrollDashboard.setVisibility(View.GONE);
            mTvProjectName.setVisibility(View.VISIBLE);
        }else {
            mNestedScrollDashboard.setVisibility(View.VISIBLE);
            mTvProjectName.setVisibility(View.GONE);
        }

        mRvDashboard = view.findViewById(R.id.rv_dashboard);
        mRvDashboard.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        pieChart = view.findViewById(R.id.chart);
        chart = view.findViewById(R.id.barchart);
        lineChart = view.findViewById(R.id.chart_fund);

        mRlFilter = view.findViewById(R.id.rl_filter);
        mRlFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupFilterBottomsheet();
            }
        });


        ArrayList<TempBill> billArrayList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            billArrayList.add(new TempBill("Construction of kalinga model residential school", "IPMS00" + i, "4000000"));
        }

        ValueFormatter xAxisFormatter = new MyDistrictValueFormatter(chart);    //new DayAxisValueFormatter(chart);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setTypeface(tfLight);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(xAxisFormatter);


        XYMarkerView mv = new XYMarkerView(getActivity(), xAxisFormatter);
        mv.setChartView(chart); // For bounds control
        chart.setMarker(mv); // Set the marker to the chart

        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0, 2));
        barEntries.add(new BarEntry(1, 2));
        barEntries.add(new BarEntry(2, 3));
        barEntries.add(new BarEntry(3, 4));
        barEntries.add(new BarEntry(4, 17));
        barEntries.add(new BarEntry(5, 6));
        barEntries.add(new BarEntry(6, 25));
        barEntries.add(new BarEntry(7, 20));
        barEntries.add(new BarEntry(8, 2));
        barEntries.add(new BarEntry(9, 3));
        barEntries.add(new BarEntry(10, 4));
        barEntries.add(new BarEntry(11, 2));
        barEntries.add(new BarEntry(12, 6));
        barEntries.add(new BarEntry(13, 25));
        barEntries.add(new BarEntry(14, 20));
        barEntries.add(new BarEntry(15, 2));
        barEntries.add(new BarEntry(16, 3));
        barEntries.add(new BarEntry(17, 4));
        barEntries.add(new BarEntry(18, 2));
        barEntries.add(new BarEntry(19, 6));
        barEntries.add(new BarEntry(20, 25));
        barEntries.add(new BarEntry(21, 20));
        barEntries.add(new BarEntry(22, 2));
        barEntries.add(new BarEntry(23, 3));
        barEntries.add(new BarEntry(24, 4));
        barEntries.add(new BarEntry(25, 2));
        barEntries.add(new BarEntry(26, 6));
        barEntries.add(new BarEntry(27, 25));
        barEntries.add(new BarEntry(28, 20));
        barEntries.add(new BarEntry(29, 25));
//        barEntries.add(new BarEntry(30, 20));

        BarDataSet dataSet = new BarDataSet(barEntries, "Ongoing Projects: District Wise");
//        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(16.0f);

        BarData barData = new BarData(dataSet);
        barData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {

//                if (value > 0){
//                    return super.getFormattedValue(value);
//                }else{
                return "";
//                }
            }
        });
        chart.setFitBars(true);
        chart.setData(barData);
        chart.getDescription().setEnabled(false);
        chart.getDescription().setText("Bar Chart");
        chart.animateY(3000);

//        RunningBillsAdapter adapter = new RunningBillsAdapter(getActivity(),billArrayList);

        List<Dashboard> dashboards = new ArrayList<>();
        Dashboard dashboard1 = new Dashboard();
        dashboard1.setDashCount(10);
        dashboard1.setDashId(1);
        dashboard1.setDashName("Ongoing Projects");

        Dashboard dashboard2 = new Dashboard();
        dashboard2.setDashId(2);
        dashboard2.setDashCount(12);
        dashboard2.setDashName("Sanctioned Projects");

        Dashboard dashboard3 = new Dashboard();
        dashboard3.setDashId(3);
        dashboard3.setDashCount(15);
        dashboard3.setDashName("Projects on Hold");

        Dashboard dashboard4 = new Dashboard();
        dashboard4.setDashId(4);
        dashboard4.setDashCount(12);
        dashboard4.setDashName("Projects Delayed");


        dashboards.add(dashboard1);
        dashboards.add(dashboard2);
        dashboards.add(dashboard3);
        dashboards.add(dashboard4);

        DashboardAdapter adapter = new DashboardAdapter(getActivity(), dashboards);
        mRvDashboard.setAdapter(adapter);


        List<PieEntry> entries = new ArrayList<>();
        for (int i = 0; i < projects.length; i++) {
            entries.add(new PieEntry(progress[i], projects[i]));

        }
        PieDataSet set = new PieDataSet(entries, "");
        set.setColors(ColorTemplate.MATERIAL_COLORS);
        PieData data = new PieData(set);
//        pieChart.setHoleColor(getResources().getColor(R.color.colorWhite));
        pieChart.setData(data);
//        pieChart.getLegend().setTextColor(getResources().getColor(R.color.colorWhite));
        pieChart.animateY(2000);
        pieChart.setCenterText("Project Status");
        pieChart.setDescription(null);
        pieChart.setDrawSliceText(false);
        pieChart.invalidate();



    }
    private BottomSheetDialog dialog;
    private void setupFilterBottomsheet() {
        View dialogView = getLayoutInflater().inflate(R.layout.bottomsheet_filter, null);
        dialog = new BottomSheetDialog(getActivity());
        dialog.setContentView(dialogView);

        Spinner finSpinner = dialogView.findViewById(R.id.spn_financial_year);
        Spinner schemeSpinner = dialogView.findViewById(R.id.spn_schemes);
        Spinner distSpinner = dialogView.findViewById(R.id.spn_district);
        Spinner projectSpinner = dialogView.findViewById(R.id.spn_projects);
        ImageView mIvClose = dialogView.findViewById(R.id.iv_sheet_filter_close);
        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        Button mBtnFilter = dialogView.findViewById(R.id.btn_filter);
        mBtnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        dialog.show();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

}
