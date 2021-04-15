package com.jaylax.wiredshack.user.notification;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.FragmentNotificationBinding;

import java.util.ArrayList;

public class NotificationFragment extends Fragment {

    FragmentNotificationBinding mBinding;
    Boolean isAccept = true;
    Boolean isViewRequest = false;
    ArrayList<DummyModel> dummyList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),R.layout.fragment_notification,container,false);

        mBinding.recyclerActivity.setLayoutManager(new LinearLayoutManager(getActivity()));
        setTabLayout();

        mBinding.tvAcceptedRequest.setOnClickListener(view -> {
            isAccept = true;
            isViewRequest = false;
            setTabLayout();

        });

        mBinding.tvViewSentRequest.setOnClickListener(view -> {
            isAccept = false;
            isViewRequest = true;
            setTabLayout();
        });
        return mBinding.getRoot();
    }

    private void setTabLayout() {
        if (isAccept){
            mBinding.tvAcceptedRequest.setBackgroundResource(R.drawable.back_round_white);
            mBinding.tvAcceptedRequest.setTextColor(ContextCompat.getColor(getActivity(),R.color.colorBlackText));

            dummyList = new ArrayList<>();
            dummyList.add(new DummyModel("Today",""));
            dummyList.add(new DummyModel("",""));
            dummyList.add(new DummyModel("",""));
            dummyList.add(new DummyModel("",""));
            dummyList.add(new DummyModel("This Week",""));
            dummyList.add(new DummyModel("",""));
            dummyList.add(new DummyModel("",""));

            mBinding.recyclerActivity.setAdapter(new AcceptedRequestAdapter(dummyList));
        }else {
            mBinding.tvAcceptedRequest.setBackgroundResource(R.drawable.back_border_white);
            mBinding.tvAcceptedRequest.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));
        }

        if (isViewRequest){
            mBinding.tvViewSentRequest.setBackgroundResource(R.drawable.back_round_white);
            mBinding.tvViewSentRequest.setTextColor(ContextCompat.getColor(getActivity(),R.color.colorBlackText));

            mBinding.recyclerActivity.setAdapter(new ViewSentRequestAdapter());

        }else {
            mBinding.tvViewSentRequest.setBackgroundResource(R.drawable.back_border_white);
            mBinding.tvViewSentRequest.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));
        }
    }
}