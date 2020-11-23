package com.aashdit.ipms.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.aashdit.ipms.R;
import com.aashdit.ipms.models.WorkPhase;
import com.aashdit.ipms.util.Utility;
import com.sasank.roundedhorizontalprogress.RoundedHorizontalProgressBar;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Manabendu on 31/07/20
 */
public class WorkPhaseUnApprovedAdapter extends RecyclerView.Adapter<WorkPhaseUnApprovedAdapter.WorkPhaseViewHolder> {

    private static final String TAG = WorkPhaseUnApprovedAdapter.class.getSimpleName();
    OnPhaseLongListener onPhaseListener;
    private Context mContext;
    private List<WorkPhase> mData;
    private String type;


    public WorkPhaseUnApprovedAdapter(Context context, List<WorkPhase> data) {
        this.mContext = context;
        this.mData = data;
    }

    public void setType(String type) {
        this.type = type;
    }

    @NonNull
    @Override
    public WorkPhaseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cell_work_phases, viewGroup, false);

        return new WorkPhaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkPhaseViewHolder holder, int position) {

        WorkPhase wp = mData.get(position);
        holder.roundedHorizontalProgressBar.animateProgress(2000, 0, 80); // (animationDuration, oldProgress, newProgress)

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        if (!wp.currentStatus.equals("PENDING")){

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    final CharSequence[] items = {"Update"}; //,"Delete"

                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                    builder.setTitle("Select The Action");
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            onPhaseLongClick.onUnapprovedPhaseClick(position, holder.itemView, wp, true, item);
                        }
                    });
                    builder.show();
                    return true;
                }
            });
        }

        if (wp.actualProgress != null){
            holder.roundedHorizontalActualProgressBar.setVisibility(View.VISIBLE);
            holder.mTvActualProgress.setVisibility(View.VISIBLE);
            try{
                int progress = Integer.parseInt(wp.actualProgress);
                holder.mTvActualProgress.setVisibility(View.VISIBLE);
                holder.roundedHorizontalActualProgressBar.setVisibility(View.VISIBLE);
                if (progress > 100) {
                    holder.mTvActualProgress.setText("Actual "+progress + " %");
                    holder.roundedHorizontalActualProgressBar.setProgressColors(mContext.getResources().getColor(R.color.colorProgressbgColor),
                            mContext.getResources().getColor(R.color.colorProgressRedColor));
                    holder.roundedHorizontalActualProgressBar.animateProgress(2000, 0, progress);
                } else {
                    holder.mTvActualProgress.setText("Actual "+progress + " %");
                    holder.roundedHorizontalActualProgressBar.animateProgress(2000, 0, progress); // (animationDuration, oldProgress, newProgress)
                }
            }catch (Exception e){
                e.printStackTrace();
                holder.mTvActualProgress.setVisibility(View.GONE);
                holder.roundedHorizontalActualProgressBar.setVisibility(View.GONE);
            }

        }
        holder.mTvWorkPhaseName.setText(wp.workPhaseName);
        String duration = wp.duration;
        holder.mTvWorkPhaseDuration.setText(duration);
        holder.mTvWorkPhaseStatus.setText(wp.status);


        if (!wp.estimatedStartDate.equals("")) {
            List<String> startDate = Arrays.asList(wp.estimatedStartDate.split("/"));
            String day = startDate.get(0);
            String _month = startDate.get(1);
            String year = startDate.get(2);
            String month = Utility.convertMonthToWord(_month);

            holder.mTvStartDay.setText(day);
            holder.mTvStartMonth.setText(month);
            holder.mTvStartYear.setText(year);
        } else {
            holder.mTvStartDay.setText("N/A");
            holder.mTvStartMonth.setText("N/A");
            holder.mTvStartYear.setText("N/A");
        }

        if (!wp.estimatedEndDate.equals("")) {
            List<String> endDate = Arrays.asList(wp.estimatedEndDate.split("/"));
            String day = endDate.get(0);
            String _month = endDate.get(1);
            String year = endDate.get(2);

            String month = Utility.convertMonthToWord(_month);

            holder.mTvEndDay.setText(day);
            holder.mTvEndMonth.setText(month);
            holder.mTvEndYear.setText(year);
        } else {
            holder.mTvEndDay.setText("N/A");
            holder.mTvEndMonth.setText("N/A");
            holder.mTvEndYear.setText("N/A");
        }


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    OnPhaseLongListener onPhaseLongClick;

    public void setOnPhaseLongClick(OnPhaseLongListener onPhaseLongClick) {
        this.onPhaseLongClick = onPhaseLongClick;
    }

    public interface OnPhaseLongListener {
        void onUnapprovedPhaseClick(int pos, View view, WorkPhase wp, boolean isLongClick, int selectedAction);
    }

    public static class WorkPhaseViewHolder extends RecyclerView.ViewHolder {


        TextView mTvWorkPhaseName;
        TextView mTvWorkPhaseDuration;
        TextView mTvWorkPhaseStatus;
        TextView mTvStartDay, mTvStartMonth, mTvStartYear;
        TextView mTvEndDay, mTvEndMonth, mTvEndYear;

        RoundedHorizontalProgressBar roundedHorizontalProgressBar,roundedHorizontalActualProgressBar;
        CardView mCvRoot;
        RelativeLayout mRlDateRoot;

        TextView mTvProgressText,mTvActualProgress;

        public WorkPhaseViewHolder(View itemView) {
            super(itemView);

            mTvWorkPhaseName = itemView.findViewById(R.id.tv_work_phase_name);
            mTvWorkPhaseDuration = itemView.findViewById(R.id.tv_work_phase_durtion);
            mTvWorkPhaseStatus = itemView.findViewById(R.id.tv_phase_status);
            mTvStartDay = itemView.findViewById(R.id.tv_start_day_of_phase);
            mTvStartMonth = itemView.findViewById(R.id.tv_start_month_of_phase);
            mTvStartYear = itemView.findViewById(R.id.tv_start_year_of_phase);

            mTvEndDay = itemView.findViewById(R.id.tv_end_day_of_phase);
            mTvEndMonth = itemView.findViewById(R.id.tv_end_month_of_phase);
            mTvEndYear = itemView.findViewById(R.id.tv_end_year_of_phase);
            mCvRoot = itemView.findViewById(R.id.cv_root);
            mRlDateRoot = itemView.findViewById(R.id.rl_duration_container);

            roundedHorizontalProgressBar = itemView.findViewById(R.id.progress_bar);
            roundedHorizontalProgressBar.setVisibility(View.GONE);
            mTvProgressText = itemView.findViewById(R.id.tv_progress_text);

            mTvProgressText.setVisibility(View.GONE);

            roundedHorizontalActualProgressBar = itemView.findViewById(R.id.progress_actual_bar);
            roundedHorizontalActualProgressBar.setVisibility(View.GONE);
            mTvActualProgress = itemView.findViewById(R.id.tv_actual_progress_text);
            mTvActualProgress.setVisibility(View.GONE);
        }

    }
}