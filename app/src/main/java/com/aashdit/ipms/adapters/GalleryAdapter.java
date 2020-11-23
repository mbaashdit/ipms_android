package com.aashdit.ipms.adapters;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aashdit.ipms.R;
import com.aashdit.ipms.models.Progress;
import com.aashdit.ipms.views.CloseDialog;
import com.aashdit.ipms.views.ImageFullScreenDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryHolder> {

    private Context context;
    private List<Progress> progresses;
    private Activity activity;

    public GalleryAdapter(Context context, List<Progress> progresses, Activity activity) {
        this.context = context;
        this.progresses = progresses;
        this.activity = activity;
    }

    @NonNull
    @Override
    public GalleryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cell_gallery_images, parent, false);
        return new GalleryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryHolder holder, int position) {

        Progress progress = progresses.get(position);
        holder.mTvDate.setText(progress.date/*.concat(" ").concat(progress.status)*/);
        holder.mTvStatus.setText(progress.status);
        holder.mTvActualProgress.setText(progress.actualProgress.concat(" %"));
        if (!progress.reason.isEmpty()) {
            holder.mTvReason.setVisibility(View.VISIBLE);
            holder.mTvReasonLbl.setVisibility(View.VISIBLE);
            holder.mTvReason.setText(progress.reason);
        } else {
            holder.mTvReason.setVisibility(View.GONE);
            holder.mTvReasonLbl.setVisibility(View.GONE);
        }
        if (progress.photos != null && progress.photos.size() > 0) {
            bindData(holder.mRvImages, progress.photos);
            holder.mTvPhotoCount.setText(""+progress.photos.size());
        }else {
            holder.mTvPhotoLbl.setVisibility(View.GONE);
            holder.mTvPhotoCount.setVisibility(View.GONE);
        }


        holder.mTvLatitude.setText(progress.latitude);
        holder.mTvLongitude.setText(progress.longitude);

        if (!TextUtils.isEmpty(progress.longitude) && !TextUtils.isEmpty(progress.longitude)) {
            holder.mTvLatitude.setVisibility(View.VISIBLE);
            holder.mTvLongitude.setVisibility(View.VISIBLE);
            holder.mTvLatLbl.setVisibility(View.VISIBLE);
            holder.mTvLngLbl.setVisibility(View.VISIBLE);
            holder.mTvAreaLbl.setVisibility(View.VISIBLE);
            Geocoder gc = new Geocoder(context, Locale.getDefault());
            try {
                List<Address> addresses = gc.getFromLocation(Double.parseDouble(progress.latitude), Double.parseDouble(progress.longitude), 1);
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

    private void bindData(RecyclerView recyclerView, ArrayList<String> images) {
        StatusImageAdapter adapter = new StatusImageAdapter(context, images);
//        adapter.setFullImageListener(new StatusImageAdapter.FullImageListener() {
//            @Override
//            public void showfullscreenImage(String url) {
//                ImageFullScreenDialog imageFullScreenDialog = new ImageFullScreenDialog(context,images.get())
//            }
//        });
        adapter.setFullImageListener(new StatusImageAdapter.FullImageListener() {
            @Override
            public void showfullscreenImage(String url, int pos) {
                new CloseDialog(context,url);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return progresses.size();
    }

    public static class GalleryHolder extends RecyclerView.ViewHolder  {

        ImageView mIvPicture;
        TextView mTvDate, mTvReason, mTvActualProgress, mTvLatitude, mTvLongitude, mTvArea,mTvReasonLbl,
                mTvLatLbl,mTvLngLbl,mTvAreaLbl,mTvStatus,mTvPhotoLbl,mTvPhotoCount;
        RecyclerView mRvImages;

        public GalleryHolder(@NonNull View itemView) {
            super(itemView);
            mTvDate = itemView.findViewById(R.id.tv_cell_progress_date);
            mTvReason = itemView.findViewById(R.id.tv_cell_reason);
            mIvPicture = itemView.findViewById(R.id.iv_gallery_image);
            mRvImages = itemView.findViewById(R.id.rv_images);
            mTvStatus = itemView.findViewById(R.id.tv_cell_progress_status);
            mTvActualProgress = itemView.findViewById(R.id.text_gallery_prog_percent);
            mRvImages.setLayoutManager(new LinearLayoutManager(itemView.getContext(), RecyclerView.HORIZONTAL, false));

            mTvLatitude = itemView.findViewById(R.id.tv_latitude);
            mTvLongitude = itemView.findViewById(R.id.tv_longitude);
            mTvArea = itemView.findViewById(R.id.tv_area);
            mTvReasonLbl = itemView.findViewById(R.id.tv_cell_reason_lbl);


            mTvLatLbl = itemView.findViewById(R.id.tv_latitude_lbl);
            mTvLngLbl = itemView.findViewById(R.id.tv_longitude_lbl);
            mTvAreaLbl = itemView.findViewById(R.id.tv_area_lbl);
            mTvPhotoCount = itemView.findViewById(R.id.tv_photo_count);
            mTvPhotoLbl = itemView.findViewById(R.id.tv_photo_lbl);
        }

    }
}
