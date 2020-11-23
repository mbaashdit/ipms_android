package com.aashdit.ipms.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aashdit.ipms.R;
import com.aashdit.ipms.models.MasterPhases;
import com.aashdit.ipms.models.WorkPhase;

import java.util.List;

/**
 * Created by Manabendu on 06/08/20
 */
public class PhaseSpinnerAdapter extends BaseAdapter {

    private Activity activity;
    private List<MasterPhases> resultArrayList;
    private LayoutInflater inflater;

    public PhaseSpinnerAdapter(Activity activity, List<MasterPhases> resultArrayList) {
        this.activity = activity;
        this.resultArrayList = resultArrayList;

        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return resultArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return resultArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.spinner_work_phase, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MasterPhases currentItem = (MasterPhases) getItem(position);
        viewHolder.bindData(currentItem);


        return convertView;

    }

    private class ViewHolder {
        TextView itemName;

        public ViewHolder(View view) {
            itemName = view.findViewById(R.id.text1);
        }

        public void bindData(MasterPhases currentItem) {
            itemName.setText(currentItem.phaseName);
        }
    }


}
