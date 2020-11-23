package com.aashdit.ipms.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.aashdit.ipms.ui.fragments.CompletedFragment;
import com.aashdit.ipms.ui.fragments.OnGoingFragment;

import java.util.Objects;

/**
 * Created by Manabendu on 17/06/20
 */
public class ProjTabAdapter extends FragmentPagerAdapter {

    private String[] titles = {"OnGoing", "Completed"};

    public ProjTabAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0){
            fragment = new OnGoingFragment();
        }else if (position == 1){
            fragment = new CompletedFragment();
        }
        return Objects.requireNonNull(fragment);
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
