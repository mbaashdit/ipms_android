package com.aashdit.ipms.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aashdit.ipms.BuildConfig;
import com.aashdit.ipms.R;
import com.aashdit.ipms.util.Constants;
import com.bumptech.glide.Glide;

import java.util.List;

public class StatusImageAdapter extends RecyclerView.Adapter<StatusImageAdapter.StatusHolder> {

    private Context context;
    private List<String> images;

    public StatusImageAdapter(Context context, List<String> images) {
        this.context = context;
        this.images = images;
    }

    @NonNull
    @Override
    public StatusHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cell_clicked_images, parent, false);
        return new StatusHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusHolder holder, int position) {

        String imageUrl = images.get(position);
//        Glide.with(context).load("https://miro.medium.com/max/1200/1*mk1-6aYaf_Bes1E3Imhc0A.jpeg").into(holder.mIvImage);
        if (!imageUrl.isEmpty()) {
            Glide.with(context).load(BuildConfig.BASE_URL+"workprogress/viewInterimStatusHistoryImages?photoId="+imageUrl).into(holder.mIvImage);
        }
        holder.mIvClose.setVisibility(View.GONE);
        holder.mIvImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullImageListener.showfullscreenImage(BuildConfig.BASE_URL+"workprogress/viewInterimStatusHistoryImages?photoId="+imageUrl,position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    static class StatusHolder extends RecyclerView.ViewHolder {

        ImageView mIvImage,mIvClose;

        public StatusHolder(@NonNull View itemView) {
            super(itemView);
            mIvImage = itemView.findViewById(R.id.iv_captured_image);
            mIvClose = itemView.findViewById(R.id.iv_remove_img);
        }
    }

    FullImageListener fullImageListener;

    public void setFullImageListener(FullImageListener fullImageListener) {
        this.fullImageListener = fullImageListener;
    }

    public interface FullImageListener {
        void showfullscreenImage(String url, int pos);
    }
}
