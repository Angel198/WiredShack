package com.jaylax.wiredshack.eventManager.eventdetails;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.FragmentManagerEventDetailsBinding;
import com.jaylax.wiredshack.eventManager.editEvent.ManagerEditEventActivity;
import com.jaylax.wiredshack.user.eventDetails.EventCommentAdapter;

public class ManagerEventDetailsFragment extends Fragment {

    FragmentManagerEventDetailsBinding mBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()),R.layout.fragment_manager_event_details,container,false);

        mBinding.recyclerEventUser.setLayoutManager(new GridLayoutManager(getActivity(),4));
        mBinding.recyclerEventUser.setAdapter(new EventParticipateUserAdapter(false));
        mBinding.recyclerEventComment.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBinding.recyclerEventComment.setAdapter(new EventCommentAdapter());

        mBinding.tvEventEdit.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), ManagerEditEventActivity.class);
            getActivity().startActivity(intent);
        });
        return mBinding.getRoot();
    }
}