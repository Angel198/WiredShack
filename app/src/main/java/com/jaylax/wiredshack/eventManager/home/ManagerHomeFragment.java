package com.jaylax.wiredshack.eventManager.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jaylax.wiredshack.EditProfileActivity;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.FragmentManagerHomeBinding;
import com.jaylax.wiredshack.user.home.HomeRecentEventAdapter;

public class ManagerHomeFragment extends Fragment {

    FragmentManagerHomeBinding mBinding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.fragment_manager_home, container, false);
        mBinding.recyclerHomeRecentEventEvents.setLayoutManager(new GridLayoutManager(getActivity(),3));
        mBinding.recyclerHomeRecentEventEvents.setAdapter(new ManagerRecentEventsAdapter());

        mBinding.tvAccountEditProfile.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            getActivity().startActivity(intent);
        });

        return mBinding.getRoot();
    }
}