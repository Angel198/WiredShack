package com.jaylax.wiredshack.user.account;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jaylax.wiredshack.EditProfileActivity;
import com.jaylax.wiredshack.LoginActivity;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.FragmentAccountBinding;
import com.jaylax.wiredshack.user.following.UserFollowingActivity;
import com.jaylax.wiredshack.user.home.HomeRecentEventAdapter;

import java.util.Objects;

public class AccountFragment extends Fragment {

    FragmentAccountBinding mBinding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_account, container, false);
        mBinding.recyclerAccountFollowingEvents.setLayoutManager(new GridLayoutManager(getActivity(),3));
        mBinding.recyclerAccountFollowingEvents.setAdapter(new HomeRecentEventAdapter());

        mBinding.tvAccountEditProfile.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            Objects.requireNonNull(getActivity()).startActivity(intent);
        });

        mBinding.linearAccountFollowing.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), UserFollowingActivity.class);
            Objects.requireNonNull(getActivity()).startActivity(intent);
        });

        mBinding.imgAccountLogin.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            Objects.requireNonNull(getActivity()).startActivity(intent);
        });
        return mBinding.getRoot();
    }
}