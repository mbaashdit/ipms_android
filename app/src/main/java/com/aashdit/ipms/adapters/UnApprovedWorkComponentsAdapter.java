package com.aashdit.ipms.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aashdit.ipms.R;
import com.aashdit.ipms.models.PlanableWorks;
import com.aashdit.ipms.util.Utility;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Manabendu on 31/07/20
 */
public class UnApprovedWorkComponentsAdapter extends RecyclerView.Adapter<UnApprovedWorkComponentsAdapter.WorkComponentsViewHolder> {

    private static final String TAG = UnApprovedWorkComponentsAdapter.class.getSimpleName();
    public boolean isLongPressed;
    public boolean isSelected;
    OnWorkComponentListener workComponentListener;
    private Context mContext;
    private List<PlanableWorks> works;
    private String type;


    public UnApprovedWorkComponentsAdapter(Context context, List<PlanableWorks> data, String type) {
        this.mContext = context;
        this.works = data;
        this.type = type;
    }

    @NonNull
    @Override
    public WorkComponentsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cell_unapproved_work_components, viewGroup, false);

        return new WorkComponentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkComponentsViewHolder holder, int position) {
        // include binding logic here

        PlanableWorks pw = works.get(position);
        holder.mTvWcTitle.setText(pw.workComponentName);
        holder.mTvWcPhaseCount.setText(String.valueOf(pw.phaseCount));
        holder.mTvEstStartDate.setText("Est.Start Date : " + pw.estimatedStartDate);
        holder.mTvEstEndDate.setText("Est.End Date : " + pw.estimatedEndDate);
//        holder.mTvWcTotalDuration.setText(""+pw.duration);

        if (pw.duration.equals("")){
            holder.mTvWcTotalDuration.setText("0");
        }else {
            holder.mTvWcTotalDuration.setText("" + pw.duration);
        }

        if (!pw.estimatedStartDate.equals("")) {
            List<String> startDate = Arrays.asList(pw.estimatedStartDate.split("/"));
            String day = startDate.get(0);
            String _month = startDate.get(1);
            String year = startDate.get(2);
            String month = Utility.convertMonthToWord(_month);


            holder.mTvStartDay.setText(day);
            holder.mTvStartMonth.setText(month);
            holder.mTvStartYear.setText(year);
        }

        if (!pw.estimatedEndDate.equals("")) {
            List<String> endDate = Arrays.asList(pw.estimatedEndDate.split("/"));
            String day = endDate.get(0);
            String _month = endDate.get(1);
            String year = endDate.get(2);
            String month = Utility.convertMonthToWord(_month);


            holder.mTvEndDay.setText(day);
            holder.mTvEndMonth.setText(month);
            holder.mTvEndYear.setText(year);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isLongPressed) {
                    workComponentListener.UnapprovedWorkComponentClick(type, position, pw.workComponentName, false);
                } else {

                    holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            isSelected = b;
                            if (b){
                                workComponentListener.enableMultiSelect(position, pw, b);
                                holder.mIvWcStatus.setImageResource(R.drawable.ic_done);
                            }else {
                                holder.mIvWcStatus.setImageResource(R.drawable.ic_progress);
                                workComponentListener.enableMultiSelect(position, pw, b);
                            }
                        }
                    });
                }
            }
        });


        if (pw.phaseCount > 0) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (workComponentListener != null) {
                        isLongPressed = true;
                        isSelected = true;
                        workComponentListener.UnapprovedWorkComponentClick(type, position, pw.workComponentName, true);
                        workComponentListener.enableMultiSelect(position, pw, isSelected);
//                    if (isSelected){
//                        holder.mIvWcStatus.setImageResource(R.drawable.ic_done);
//                    }else {
//                        holder.mIvWcStatus.setImageResource(R.drawable.ic_progress);
//                    }
                        holder.mCheckBox.setChecked(true);
                        return true;
                    }
                    return false;
                }
            });
        }

        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    holder.mIvWcStatus.setImageResource(R.drawable.ic_done);
                    workComponentListener.enableMultiSelect(position, pw, b);
                }else {
                    holder.mIvWcStatus.setImageResource(R.drawable.ic_progress);
                    workComponentListener.enableMultiSelect(position, pw, b);
                }
            }
        });

        if (!isLongPressed){
            holder.mIvWcStatus.setImageResource(R.drawable.ic_progress);
            holder.mCheckBox.setChecked(false);
        }
    }


    @Override
    public int getItemCount() {
        return works.size();
    }

    public void setWorkComponentListener(OnWorkComponentListener workComponentListener) {
        this.workComponentListener = workComponentListener;
    }

    public interface OnWorkComponentListener {
        void UnapprovedWorkComponentClick(String type, int position, String compName, boolean isLOngClick);

        void enableMultiSelect(int position, PlanableWorks work, boolean isSelected);
    }

    public static class WorkComponentsViewHolder extends RecyclerView.ViewHolder {
        TextView mTvWcTitle;
        TextView mTvEstStartDate;
        TextView mTvEstEndDate;
        TextView mTvWcPhaseCount;
        TextView mTvWcTotalDuration;
        TextView mTvDuration;

        TextView mTvStartDay;
        TextView mTvStartMonth;
        TextView mTvStartYear;

        TextView mTvEndDay;
        TextView mTvEndMonth;
        TextView mTvEndYear;

        ImageView mIvWcStatus;
        CheckBox mCheckBox;

        public WorkComponentsViewHolder(View itemView) {
            super(itemView);
            mTvWcTitle = itemView.findViewById(R.id.tv_wc_title);
            mTvEstStartDate = itemView.findViewById(R.id.tv_est_start_date);
            mTvEstEndDate = itemView.findViewById(R.id.tv_est_end_date);
            mTvWcPhaseCount = itemView.findViewById(R.id.tv_wc_phase_count);
            mTvDuration = itemView.findViewById(R.id.tv_wc_duration);
            mTvDuration.setVisibility(View.GONE);
            mTvWcTotalDuration = itemView.findViewById(R.id.tv_wc_total_duration_count);

            mTvStartDay = itemView.findViewById(R.id.tv_start_day);
            mTvStartMonth = itemView.findViewById(R.id.tv_start_mon);
            mTvStartYear = itemView.findViewById(R.id.tv_start_year);

            mTvEndDay = itemView.findViewById(R.id.tv_end_day);
            mTvEndMonth = itemView.findViewById(R.id.tv_end_mon);
            mTvEndYear = itemView.findViewById(R.id.tv_end_year);

            mIvWcStatus = itemView.findViewById(R.id.iv_wc_icon);
            mCheckBox = itemView.findViewById(R.id.checkbox_selected);

        }
    }
}