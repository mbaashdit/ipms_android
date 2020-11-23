package com.aashdit.ipms.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aashdit.ipms.R;
import com.aashdit.ipms.models.Dashboard;

import java.util.List;

/**
 * Created by Manabendu on 02/09/20
 */
public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.DashboardViewHolder> {

    private static final String TAG = DashboardAdapter.class.getSimpleName();

    private Context mContext;
    private List<Dashboard> mData;


    public DashboardAdapter(Context context, List<Dashboard> data) {
        this.mContext = context;
        this.mData = data;
    }


    @NonNull
    @Override
    public DashboardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cell_single_dashboard, viewGroup, false);

        return new DashboardViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull DashboardViewHolder holder, int position) {
        // include binding logic here
        Dashboard dashboard = mData.get(position);
        holder.mTvProjectCount.setText(""+dashboard.getDashCount());
        holder.mTvProjectName.setText(dashboard.getDashName());

    }


    @Override
    public int getItemCount() {
        return mData.size();
    }


    public static class DashboardViewHolder extends RecyclerView.ViewHolder {
        TextView mTvProjectCount,mTvProjectName;
        public DashboardViewHolder(View itemView) {
            super(itemView);

            mTvProjectCount = itemView.findViewById(R.id.tv_project_count);
            mTvProjectName = itemView.findViewById(R.id.tv_cell_project_name);
        }
    }
}