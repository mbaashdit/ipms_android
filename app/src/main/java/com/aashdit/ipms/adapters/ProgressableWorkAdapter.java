package com.aashdit.ipms.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aashdit.ipms.R;
import com.aashdit.ipms.models.ProgressableWork;

import java.util.List;

/**
 * Created by Manabendu on 06/08/20
 */
public class ProgressableWorkAdapter extends RecyclerView.Adapter<ProgressableWorkAdapter.ProgressableViewHolder> {

    private static final String TAG = ProgressableWorkAdapter.class.getSimpleName();

    private Context mContext;
    private List<ProgressableWork> mData;
    private String type;


    public ProgressableWorkAdapter(Context context, List<ProgressableWork> data, String type) {
        this.mContext = context;
        this.mData = data;
        this.type = type;
    }


    @NonNull
    @Override
    public ProgressableViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cell_work_components, viewGroup, false);

        return new ProgressableViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ProgressableViewHolder holder, int position) {
        // include binding logic here
        ProgressableWork pw = mData.get(position);
        holder.mTvWcTitle.setText(pw.workComponentName);
        holder.mTvWcPhaseCount.setText("Phase Count : ".concat(String.valueOf(pw.phaseCount)));
        holder.mTvEstStartDate.setText("Est.Start Date : " + pw.estimatedStartDate);
        holder.mTvEstEndDate.setText("Est.End Date : " + pw.estimatedEndDate);
        holder.mTvDuration.setText("Duration : " + pw.duration);
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                workComponentListener.workComponentClick(type,position,pw.workComponentName);
//            }
//        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (workProgressListener != null)
                    workProgressListener.onProgressClick(pw);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ProgressableViewHolder extends RecyclerView.ViewHolder {
        TextView mTvWcTitle;
        TextView mTvEstStartDate;
        TextView mTvEstEndDate;
        TextView mTvWcPhaseCount;
        TextView mTvDuration;

        public ProgressableViewHolder(View itemView) {
            super(itemView);
            mTvWcTitle = itemView.findViewById(R.id.tv_wc_title);
            mTvEstStartDate = itemView.findViewById(R.id.tv_est_start_date);
            mTvEstEndDate = itemView.findViewById(R.id.tv_est_end_date);
            mTvWcPhaseCount = itemView.findViewById(R.id.tv_wc_phase_count);
            mTvDuration = itemView.findViewById(R.id.tv_wc_duration);
        }
    }

    WorkProgressListener workProgressListener;

    public void setWorkProgressListener(WorkProgressListener workProgressListener) {
        this.workProgressListener = workProgressListener;
    }

    public interface WorkProgressListener {
        void onProgressClick(ProgressableWork progressableWork);
    }
}