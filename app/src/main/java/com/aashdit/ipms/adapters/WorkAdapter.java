package com.aashdit.ipms.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.aashdit.ipms.R;
import com.aashdit.ipms.models.Work;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Manabendu on 17/06/20
 */
public class WorkAdapter extends RecyclerView.Adapter<WorkAdapter.WorkViewHolder> {

    private static final String TAG = WorkAdapter.class.getSimpleName();
    ProjectListener projectListener;
    private Context mContext;
    private List<Work> workList;
    private ProgressBar progressBar;
    private int pStatus = 0;
    private Handler handler = new Handler();
    List<String> colors;

    public WorkAdapter(Context context, List<Work> data) {
        this.mContext = context;
        this.workList = data;


        colors = new ArrayList<>();

        colors.add("#5E97F6");
        colors.add("#9CCC65");
        colors.add("#FF8A65");
        colors.add("#9E9E9E");
        colors.add("#9FA8DA");
        colors.add("#90A4AE");
        colors.add("#AED581");
        colors.add("#F6BF26");
        colors.add("#FFA726");
        colors.add("#4DD0E1");
        colors.add("#BA68C8");
        colors.add("#A1887F");
    }

    @NonNull
    @Override
    public WorkViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cell_project_new, viewGroup, false);

        return new WorkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkViewHolder holder, int position) {
        // include binding logic here
        Work work = workList.get(position);
//        holder.status.setText(work.status);
        holder.id.setText(work.projectId + "");
        holder.title.setText(work.projectName);
        holder.progressPercentage.setText("50%");
        char letter = work.projectName.charAt(0);

        holder.projectLetter.setText(letter + "");

        Random r = new Random();
        int i1 = r.nextInt(11 - 0) + 0;

//genrating shape with colors

        GradientDrawable draw = new GradientDrawable();
        draw.setShape(GradientDrawable.RECTANGLE);
        draw.setCornerRadius(8.0f);
        draw.setColor(Color.parseColor(colors.get(i1)));

// assigning to textview
        holder.mCvBgColor.setBackground(draw);


//        if (position % 2 == 0){
//            holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.even_color));
//        }else {
//            holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.odd_color));
//        }

//        holder.fabAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                projectListener.onItemClick("plan");
//            }
//        });
//
//        holder.fabProgress.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                projectListener.onItemClick("progress");
//            }
//        });
//
//        holder.fabClose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                projectListener.onItemClick("close");
//            }
//        });

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

        holder.mCvRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (projectListener != null)
                    projectListener.onProjectDetailClick(work);
            }
        });

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (pStatus <= 70) {
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            holder.progressBar.setProgress(pStatus);
//                            holder.progressPercentage.setText(pStatus + " %");
//                        }
//                    });
//                    try {
//                        Thread.sleep(100);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    pStatus++;
//                }
//            }
//        }).start();
    }

    @Override
    public int getItemCount() {
        return workList.size();
    }

    public void setProjectListener(ProjectListener projectListener) {
        this.projectListener = projectListener;
    }

    public interface ProjectListener {
        void onItemClick(String type);

        void onProjectDetailClick(Work work);
    }

    public static class WorkViewHolder extends RecyclerView.ViewHolder {
        TextView title, status, id, progressPercentage, projectLetter;
        ProgressBar progressBar;
        FloatingActionButton fabAdd, fabProgress, fabClose;
        CardView mCvRoot, mCvBgColor;

        public WorkViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_cell_title);
            id = itemView.findViewById(R.id.tv_cell_id);
            mCvBgColor = itemView.findViewById(R.id.cv_letter);
//            status = itemView.findViewById(R.id.tv_cell_status);
//            progressBar = itemView.findViewById(R.id.progressBar);
            progressPercentage = itemView.findViewById(R.id.tv_cell_progress_percentage);
//            fabAdd = itemView.findViewById(R.id.fab_add);
//            fabProgress = itemView.findViewById(R.id.fab_progress);
//            fabClose = itemView.findViewById(R.id.fab_close);
//
            mCvRoot = itemView.findViewById(R.id.cv_root);
            projectLetter = itemView.findViewById(R.id.tv_title_letter);
        }
    }
}