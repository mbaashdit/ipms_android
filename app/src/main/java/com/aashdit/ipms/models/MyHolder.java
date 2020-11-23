package com.aashdit.ipms.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.aashdit.ipms.R;
import com.unnamed.b.atv.model.TreeNode;

/**
 * Created by Manabendu on 23/07/20
 */
public class MyHolder extends TreeNode.BaseNodeViewHolder<MyHolder.IconTreeItem> {
    public MyHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, IconTreeItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.layout_profile_node, null, false);
        TextView tvValue = view.findViewById(R.id.node_value);
        tvValue.setText(value.text);

        return view;
    }
    public static class IconTreeItem {
        public int icon;
        public String text;
    }
}
