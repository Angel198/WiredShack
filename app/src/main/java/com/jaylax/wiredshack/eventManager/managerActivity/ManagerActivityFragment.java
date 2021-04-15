package com.jaylax.wiredshack.eventManager.managerActivity;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.FragmentManagerActivityBinding;
import com.jaylax.wiredshack.user.notification.AcceptedRequestAdapter;
import com.jaylax.wiredshack.user.notification.DummyModel;
import com.jaylax.wiredshack.user.notification.ViewSentRequestAdapter;

import java.util.ArrayList;

public class ManagerActivityFragment extends Fragment {

    FragmentManagerActivityBinding mBinding;
    Boolean isRequest = true;
    Boolean isEvents = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.fragment_manager_activity, container, false);
        mBinding.recyclerActivity.setLayoutManager(new LinearLayoutManager(getActivity()));
        setTabLayout();

        mBinding.tvIncomingRequest.setOnClickListener(view -> {
            isRequest = true;
            isEvents = false;
            setTabLayout();

        });

        mBinding.tvEventActivities.setOnClickListener(view -> {
            isRequest = false;
            isEvents = true;
            setTabLayout();
        });

        return mBinding.getRoot();
    }

    private void setTabLayout() {
        if (isRequest){
            mBinding.tvIncomingRequest.setBackgroundResource(R.drawable.back_round_white);
            mBinding.tvIncomingRequest.setTextColor(ContextCompat.getColor(getActivity(),R.color.colorBlackText));

            mBinding.recyclerActivity.setAdapter(new ManagerIncomingRequestAdapter());
        }else {
            mBinding.tvIncomingRequest.setBackgroundResource(R.drawable.back_border_white);
            mBinding.tvIncomingRequest.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));
        }

        if (isEvents){
            mBinding.tvEventActivities.setBackgroundResource(R.drawable.back_round_white);
            mBinding.tvEventActivities.setTextColor(ContextCompat.getColor(getActivity(),R.color.colorBlackText));

            mBinding.recyclerActivity.setAdapter(new ManagerEventActivitiesAdapter());

        }else {
            mBinding.tvEventActivities.setBackgroundResource(R.drawable.back_border_white);
            mBinding.tvEventActivities.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));
        }
    }
}