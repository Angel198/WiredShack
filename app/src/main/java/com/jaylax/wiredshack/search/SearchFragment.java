package com.jaylax.wiredshack.search;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.FragmentSearchBinding;
import com.jaylax.wiredshack.home.HomeRecentEventAdapter;

public class SearchFragment extends Fragment {
    FragmentSearchBinding mBinding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false);

        mBinding.recyclerSearchSuggestion.setLayoutManager(new GridLayoutManager(getActivity(),3));
        mBinding.recyclerSearchSuggestion.setAdapter(new SearchSuggestionAdapter());

        return mBinding.getRoot();
    }
}