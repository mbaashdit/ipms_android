package com.aashdit.ipms.ui.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aashdit.ipms.R;
import com.aashdit.ipms.adapters.MenuAdapter;
import com.aashdit.ipms.databinding.FragmentNavigationBinding;
import com.aashdit.ipms.models.Menu;
import com.aashdit.ipms.ui.activities.SplashActivity;
import com.aashdit.ipms.util.Constants;
import com.aashdit.ipms.util.SharedPrefManager;

import java.util.ArrayList;


public class NavigationFragment extends Fragment implements MenuAdapter.MenuItemClickListener {

    public DrawerLayout mDrawerLayout;
    private TextView mTvUserName;
    private TextView mTvUserRole;
    private TextView mTvClothing;
    private TextView mTvStationary;
    private TextView mTvMore;
    private TextView mTvWishlist;
    private TextView mTvAccount;
    private TextView mTvOrders;
    private TextView mTvContact;
    private TextView mTvFaq;
    private MenuClickListener listener;
    private FragmentNavigationBinding binding;

    private SharedPrefManager sp;

    private ArrayList<Menu> menuArrayList;

    public NavigationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentNavigationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sp = SharedPrefManager.getInstance(getActivity());

        menuArrayList = new ArrayList<>();

        menuArrayList.add(new Menu(R.drawable.dashboard,"Dashboard"));
        menuArrayList.add(new Menu(R.drawable.profile,"Profile"));
        menuArrayList.add(new Menu(R.drawable.projects,"Projects"));
        menuArrayList.add(new Menu(R.drawable.report,"Reports"));
//        menuArrayList.add(new Menu(R.drawable.logout,"Logout"));

        MenuAdapter adapter = new MenuAdapter(getActivity(),menuArrayList);
        adapter.setMenuItemClickListener(this);

        binding.rvMenu.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL,false));
        binding.rvMenu.addItemDecoration(new DividerItemDecoration(binding.rvMenu.getContext(), DividerItemDecoration.VERTICAL));
        binding.rvMenu.setAdapter(adapter);

        binding.tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLogoutDialog();
            }
        });

        binding.tvUserName.setText(sp.getStringData(Constants.USER_NAME));
        binding.tvUserRole.setText(sp.getStringData(Constants.USER_ROLE_DESCRIPTION));
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Do you want logout from IPMS ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        sp.clear();
                        Intent intent = new Intent(getActivity(), SplashActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        getActivity().finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Logout ?");
        alert.show();
    }
    public void setUp(DrawerLayout drawerLayout, MenuClickListener listener) {
        mDrawerLayout = drawerLayout;
        this.listener = listener;
    }

    @Override
    public void onDestroy() {
        mDrawerLayout = null;
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onMenuClick(int pos) {
        listener.onClickMenuItem(pos);
    }

    public interface MenuClickListener {
        void onClickMenuItem(int position);
    }
}
