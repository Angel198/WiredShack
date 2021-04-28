package com.jaylax.wiredshack.eventManager.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.jaylax.wiredshack.ProgressDialog;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.FragmentManagerHomeBinding;
import com.jaylax.wiredshack.eventManager.editEvent.ManagerEditEventActivity;
import com.jaylax.wiredshack.model.RecentEventMainModel;
import com.jaylax.wiredshack.model.UserDetailsModel;
import com.jaylax.wiredshack.rest.ApiClient;
import com.jaylax.wiredshack.utils.Commons;
import com.jaylax.wiredshack.utils.SharePref;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManagerHomeFragment extends Fragment {

    FragmentManagerHomeBinding mBinding;
    Context mContext;
    ProgressDialog progressDialog;
    UserDetailsModel userDetailsModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.fragment_manager_home, container, false);
        mContext = getActivity();
        progressDialog = new ProgressDialog(Objects.requireNonNull(mContext));
        userDetailsModel = Commons.convertStringToObject(mContext, SharePref.PREF_USER, UserDetailsModel.class);

        RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.place_holder).transform(new CenterCrop()).error(R.drawable.place_holder).priority(Priority.HIGH);
        Glide.with(this).load(userDetailsModel.getImage() == null ? "" : userDetailsModel.getImage()).apply(options).into(mBinding.imgAccountProfile);
        Glide.with(this).load(userDetailsModel.getCoverImage() == null ? "" : userDetailsModel.getCoverImage()).apply(options).into(mBinding.imgAccountCover);

        mBinding.tvAccountProfileName.setText(userDetailsModel.getName());
        String followedCount = "0";
        if (userDetailsModel.getFollowed() != null  ){
            if (!userDetailsModel.getFollowed().isEmpty()) {
                followedCount = userDetailsModel.getFollowed();
            }
        }
        mBinding.tvFollowedCount.setText(followedCount);

        setClickListener();

        return mBinding.getRoot();
    }

    private void setClickListener() {
        mBinding.tvAccountAddEvent.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), ManagerEditEventActivity.class);
            mContext.startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getRecentEvents();
    }

    private void getRecentEvents() {
        if (Commons.isOnline(mContext)) {
            progressDialog.show();
            String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");
            ApiClient.create().getRecentEventsManager(header).enqueue(new Callback<RecentEventMainModel>() {
                @Override
                public void onResponse(Call<RecentEventMainModel> call, Response<RecentEventMainModel> response) {
                    progressDialog.dismiss();
                    if (response.code() == 200 && response.isSuccessful()) {
                        if (response.body() != null) {
                            setRecentEventData(response.body().getData());
                            if (!response.body().getStatus().equals("200")) {
                                Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                            }
                        } else {
                            Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                        }
                    } else {
                        Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                    }
                }

                @Override
                public void onFailure(Call<RecentEventMainModel> call, Throwable t) {
                    progressDialog.dismiss();
                    Commons.showToast(mContext, getResources().getString(R.string.something_wants_wrong));
                }
            });
        } else {
            Commons.showToast(mContext, mContext.getResources().getString(R.string.no_internet_connection));
        }
    }

    private void setRecentEventData(ArrayList<RecentEventMainModel.RecentEventData> list){
        if (list.isEmpty()){
            mBinding.linearHomeRecentEvent.setVisibility(View.GONE);
        }else {
            mBinding.linearHomeRecentEvent.setVisibility(View.VISIBLE);

            if (list.size()>6){
                mBinding.tvViewAllRecentEvents.setVisibility(View.VISIBLE);
            }else {
                mBinding.tvViewAllRecentEvents.setVisibility(View.GONE);
            }
            mBinding.recyclerHomeRecentEventEvents.setLayoutManager(new GridLayoutManager(getActivity(),3));
            mBinding.recyclerHomeRecentEventEvents.setAdapter(new ManagerRecentEventsAdapter(mContext, list, data -> {

            },false));
        }
    }
}