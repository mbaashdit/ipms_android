package com.aashdit.ipms.adapters;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aashdit.ipms.R;
import com.aashdit.ipms.models.Progress;
import com.bumptech.glide.Glide;
import com.github.vipulasri.timelineview.TimelineView;

import java.util.List;
import java.util.Locale;

/**
 * Created by Manabendu on 01/08/20
 */
public class ProgressAdapter extends RecyclerView.Adapter<ProgressAdapter.ProgressViewHolder> {

//    private static final String TAG = ProgressAdapter.class.getSimpleName();

    private Context mContext;
    private List<Progress> progresses;


    public ProgressAdapter(Context context, List<Progress> data) {
        this.mContext = context;
        this.progresses = data;
    }


    @NonNull
    @Override
    public ProgressViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cell_progress_work, viewGroup, false);

        return new ProgressViewHolder(view,viewType);
    }


    @Override
    public void onBindViewHolder(@NonNull ProgressViewHolder holder, int position) {
        // include binding logic here
        Progress p = progresses.get(position);
        if (p.reason.equals("")) {
            holder.mTvReason.setVisibility(View.GONE);
            holder.mTvReasonLbl.setVisibility(View.GONE);
        } else {
            holder.mTvReason.setVisibility(View.VISIBLE);
            holder.mTvReasonLbl.setVisibility(View.VISIBLE);
            holder.mTvReason.setText(p.reason);
        }
        if (p.status.equals("Inprogress")) {
            holder.mTvProgPercentage.setText(p.actualProgress.concat(" %"));
            holder.mTvProgPercentage.setVisibility(View.VISIBLE);
            holder.mTvActualProgress.setVisibility(View.VISIBLE);
        } else {
            holder.mTvProgPercentage.setVisibility(View.GONE);
            holder.mTvActualProgress.setVisibility(View.GONE);
        }
        holder.mTvDate.setText(p.status.concat(" on ").concat(p.date));
        holder.mTvLatitude.setText(p.latitude);
        holder.mTvLongitude.setText(p.longitude);
        Glide.with(mContext).load("").into(holder.mIvProgressImage);


        if (!TextUtils.isEmpty(p.longitude) && !TextUtils.isEmpty(p.longitude)) {
            holder.mTvLatitude.setVisibility(View.VISIBLE);
            holder.mTvLongitude.setVisibility(View.VISIBLE);
            holder.mTvLatLbl.setVisibility(View.VISIBLE);
            holder.mTvLngLbl.setVisibility(View.VISIBLE);
            holder.mTvAreaLbl.setVisibility(View.VISIBLE);
            Geocoder gc = new Geocoder(mContext, Locale.getDefault());
            try {
                List<Address> addresses = gc.getFromLocation(Double.parseDouble(p.latitude), Double.parseDouble(p.longitude), 1);
                StringBuilder sb = new StringBuilder();
                if (addresses.size() > 0) {
                    Address address = addresses.get(0);
                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        sb.append(address.getAddressLine(i)).append("\n");
                    }
                    if (address.getAddressLine(0) != null)
                        holder.mTvArea.setText(address.getAddressLine(0));

                    notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            holder.mTvLatitude.setVisibility(View.GONE);
            holder.mTvLongitude.setVisibility(View.GONE);
            holder.mTvLatLbl.setVisibility(View.GONE);
            holder.mTvLngLbl.setVisibility(View.GONE);
            holder.mTvAreaLbl.setVisibility(View.GONE);
        }

    }


    @Override
    public int getItemCount() {
        return progresses.size();
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position, getItemCount());
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public TimelineView mTimelineView;
        TextView mTvDate, mTvReason, mTvProgPercentage, mTvLatitude, mTvLongitude, mTvArea,
                mTvActualProgress,mTvReasonLbl,mTvLatLbl,mTvLngLbl,mTvAreaLbl;
        ImageView mIvProgressImage;

        public ProgressViewHolder(View itemView, int viewType) {
            super(itemView);
            mTimelineView = itemView.findViewById(R.id.timeline);
            mTimelineView.initLine(viewType);
            mTvDate = itemView.findViewById(R.id.text_timeline_date);
            mTvReason = itemView.findViewById(R.id.text_timeline_title);
            mTvProgPercentage = itemView.findViewById(R.id.text_timeline_prog_percent);
            mIvProgressImage = itemView.findViewById(R.id.cell_image_uploaded);

            mTvLatitude = itemView.findViewById(R.id.tv_latitude);
            mTvLongitude = itemView.findViewById(R.id.tv_longitude);
            mTvArea = itemView.findViewById(R.id.tv_area);

            mTvActualProgress = itemView.findViewById(R.id.tv_cell_act_progress_lbl);
            mTvReasonLbl = itemView.findViewById(R.id.tv_cell_reason_lbl);

            mTvLatLbl = itemView.findViewById(R.id.tv_latitude_lbl);
            mTvLngLbl = itemView.findViewById(R.id.tv_longitude_lbl);
            mTvAreaLbl = itemView.findViewById(R.id.tv_area_lbl);
        }
    }
}