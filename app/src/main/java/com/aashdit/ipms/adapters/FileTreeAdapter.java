package com.aashdit.ipms.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aashdit.ipms.R;

import moe.leer.tree2view.TreeUtils;
import moe.leer.tree2view.adapter.TreeAdapter;
import moe.leer.tree2view.module.DefaultTreeNode;

/**
 * Created by Manabendu on 23/07/20
 */
public class FileTreeAdapter extends TreeAdapter<FileTreeAdapter.ViewHolder> {

    public FileTreeAdapter(Context context, DefaultTreeNode<ViewHolder> root, int resourceId) {
        super(context, root, resourceId);
    }

    @Override
    public void toggle(Object... objects) {

    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        //your code here
        //...
        //call padding for a better UI
//        setPadding(holder.arrowIcon, depth, -1);
        //toggle your view's status here
//        toggle(node, holder);


        if (mNodesList == null) {
            mNodesList = TreeUtils.getVisibleNodesD(super.mRoot);
            notifyDataSetChanged();
        }
        DefaultTreeNode node = mNodesList.get(i);
        ViewHolder holder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(mResourceId, viewGroup, false);
            holder = new ViewHolder();
            holder.tv = (TextView) convertView.findViewById(R.id.default_tree_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv.setText(node.getElement().toString());
        int depth = node.getDepth();
        //set view indent
        setPadding(holder.tv, depth, -1);
        //toggle
        //toggle(holder, node);
        return convertView;
    }

    class ViewHolder {
        TextView tv;
    }
}
