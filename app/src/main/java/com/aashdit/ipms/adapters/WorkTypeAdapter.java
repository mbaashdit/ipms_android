package com.aashdit.ipms.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aashdit.ipms.R;

import java.util.List;

/**
 * Created by Manabendu on 30/06/20
 */
public class WorkTypeAdapter extends RecyclerView.Adapter<WorkTypeAdapter.WorkTypeViewHolder> {

    private static final String TAG = WorkTypeAdapter.class.getSimpleName();

    private Context mContext;
    private List<String> works;

    private static int SELECTED_POSITION = 0;


    public WorkTypeAdapter(Context context, List<String> data) {
        this.mContext = context;
        this.works = data;
    }


    @NonNull
    @Override
    public WorkTypeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cell_work, viewGroup, false);

        return new WorkTypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkTypeViewHolder holder, int position) {

        String work = works.get(position);
        holder.mTvWorkTitle.setText(work);

        holder.mRlRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SELECTED_POSITION = position;
                notifyDataSetChanged();
                workTypeListener.onWorkSelection();
            }
        });

        if (SELECTED_POSITION == position){
            holder.mRlRoot.setBackgroundColor(mContext.getResources().getColor(R.color.selected_cell_background));
        }else {
            holder.mRlRoot.setBackgroundColor(mContext.getResources().getColor(R.color.unselected_cell_background));
        }
    }

    @Override
    public int getItemCount() {
        return works.size();
    }

    public static class WorkTypeViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout mRlRoot;
        TextView mTvWorkTitle;
        public WorkTypeViewHolder(View itemView) {
            super(itemView);

            mTvWorkTitle = itemView.findViewById(R.id.tv_work_title);
            mRlRoot = itemView.findViewById(R.id.rl_root_bg);
        }
    }
    WorkTypeListener workTypeListener;

    public void setWorkTypeListener(WorkTypeListener workTypeListener) {
        this.workTypeListener = workTypeListener;
    }

    public interface WorkTypeListener{
        void onWorkSelection();
    }

}