package com.aashdit.ipms.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aashdit.ipms.R;
import com.aashdit.ipms.models.Menu;

import java.util.List;

/**
 * Created by Manabendu on 17/06/20
 */
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private static final String TAG = MenuAdapter.class.getSimpleName();

    private Context mContext;
    private List<Menu> menuList;


    public MenuAdapter(Context context, List<Menu> data) {
        this.mContext = context;
        this.menuList = data;
    }


    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cell_menu, viewGroup, false);

        return new MenuViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        // include binding logic here
        Menu menu = menuList.get(position);
        holder.title.setText(menu.menuTitle);
        holder.icon.setImageResource(menu.menuIcon);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (menuItemClickListener != null)
                    menuItemClickListener.onMenuClick(position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return menuList.size();
    }


    public static class MenuViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;

        public MenuViewHolder(View itemView) {
            super(itemView);

            icon = itemView.findViewById(R.id.iv_menu_img);
            title = itemView.findViewById(R.id.tv_menu_title);
        }
    }

    MenuItemClickListener menuItemClickListener;

    public void setMenuItemClickListener(MenuItemClickListener menuItemClickListener) {
        this.menuItemClickListener = menuItemClickListener;
    }

    public interface MenuItemClickListener {
        void onMenuClick(int pos);
    }
}