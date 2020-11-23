package com.aashdit.ipms.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aashdit.ipms.R;
import com.aashdit.ipms.models.ClickedImage;

import java.util.List;

public class CapturedImageAdapter extends RecyclerView.Adapter<CapturedImageAdapter.CapturedImageHolder> {

    private List<ClickedImage> clickedImages;
    private Context context;

    public CapturedImageAdapter(List<ClickedImage> clickedImages, Context context) {
        this.clickedImages = clickedImages;
        this.context = context;
    }

    @NonNull
    @Override
    public CapturedImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cell_clicked_images,parent,false);
        return new CapturedImageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CapturedImageHolder holder, int position) {
        ClickedImage image = clickedImages.get(position);
        holder.mIvPicture.setImageBitmap(image.getImageCaptured());
        holder.mIvRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeListener.removeImage(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return clickedImages.size();
    }
    RemoveListener removeListener;

    public void setRemoveListener(RemoveListener removeListener) {
        this.removeListener = removeListener;
    }

    public interface RemoveListener{
        void removeImage(int atPosition);
    }

    public static class CapturedImageHolder extends RecyclerView.ViewHolder{


        ImageView mIvPicture,mIvRemove;
        public CapturedImageHolder(@NonNull View itemView) {
            super(itemView);
            mIvPicture = itemView.findViewById(R.id.iv_captured_image);
            mIvRemove = itemView.findViewById(R.id.iv_remove_img);
        }
    }
}
