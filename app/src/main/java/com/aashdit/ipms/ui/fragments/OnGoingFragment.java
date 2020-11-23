package com.aashdit.ipms.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aashdit.ipms.R;
import com.aashdit.ipms.adapters.WorkAdapter;
import com.aashdit.ipms.databinding.FragmentOngoingBinding;
import com.aashdit.ipms.models.Work;

import java.util.ArrayList;

/**
 * Created by Manabendu on 17/06/20
 */
public class OnGoingFragment extends Fragment {

    private static final String TAG = "OnGoingFragment";

    FragmentOngoingBinding binding;
    ArrayList<Work> works;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOngoingBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        works = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
//            works.add(new Work("Title "+i,"Id "+i,"Status "+i));
        }

        WorkAdapter adapter = new WorkAdapter(getActivity(),works);
        binding.rvOngoing.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL,false));
        binding.rvOngoing.setAdapter(adapter);
    }
}
