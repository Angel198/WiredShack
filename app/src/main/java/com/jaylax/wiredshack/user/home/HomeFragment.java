package com.jaylax.wiredshack.user.home;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    FragmentHomeBinding mBinding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        mBinding.recyclerHomeTopStory.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mBinding.recyclerHomeTopStory.setAdapter(new HomeTopStoryAdapter());

        mBinding.recyclerHomeRecentEvent.setLayoutManager(new GridLayoutManager(getActivity(),3));
        mBinding.recyclerHomeRecentEvent.setAdapter(new HomeRecentEventAdapter());
        return mBinding.getRoot();
    }
}