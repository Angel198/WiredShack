package com.jaylax.wiredshack.eventManager.eventdetails;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jaylax.wiredshack.ProgressDialog;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.FragmentManagerEventDetailsBinding;
import com.jaylax.wiredshack.eventManager.editEvent.ManagerEditEventActivity;
import com.jaylax.wiredshack.model.UserDetailsModel;
import com.jaylax.wiredshack.user.eventDetails.EventCommentAdapter;
import com.jaylax.wiredshack.utils.Commons;
import com.jaylax.wiredshack.utils.SharePref;

import java.util.ArrayList;

public class ManagerEventDetailsFragment extends Fragment {

    FragmentManagerEventDetailsBinding mBinding;
    Context context;
    UserDetailsModel userDetailsModel;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()),R.layout.fragment_manager_event_details,container,false);
        context = getActivity();
        progressDialog = new ProgressDialog(context);
        userDetailsModel = Commons.convertStringToObject(context, SharePref.PREF_USER, UserDetailsModel.class);

        setCLickListener();

        mBinding.recyclerEventUser.setLayoutManager(new GridLayoutManager(getActivity(),4));
        mBinding.recyclerEventUser.setAdapter(new EventImagesAdapter(getActivity(),false,new ArrayList<>()));
        mBinding.recyclerEventComment.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBinding.recyclerEventComment.setAdapter(new EventCommentAdapter(context,new ArrayList<>()));

        return mBinding.getRoot();
    }

    private void setCLickListener() {
        mBinding.tvEventEdit.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), ManagerEditEventActivity.class);
            getActivity().startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getEventList();
    }

    private void getEventList() {
        /*if (Commons.isOnline(context)){
            progressDialog.show();
            String header = "Bearer " + SharePref.getInstance(context).get(SharePref.PREF_TOKEN, "");
            ApiClient.create().getRecentEventsUser(header).enqueue(new Callback<RecentEventMainModel>() {
                @Override
                public void onResponse(Call<RecentEventMainModel> call, Response<RecentEventMainModel> response) {
                    progressDialog.dismiss();
                    if (response.code() == 200 && response.isSuccessful()) {
                        if (response.body() != null) {

                        }
                    } else {
                        Commons.showToast(context, getResources().getString(R.string.please_try_after_some_time));
                    }
                }

                @Override
                public void onFailure(Call<RecentEventMainModel> call, Throwable t) {
                    progressDialog.dismiss();
                    Commons.showToast(context, getResources().getString(R.string.something_wants_wrong));
                }
            });
        }else {
            Commons.showToast(context, context.getResources().getString(R.string.no_internet_connection));
        }*/
    }
}