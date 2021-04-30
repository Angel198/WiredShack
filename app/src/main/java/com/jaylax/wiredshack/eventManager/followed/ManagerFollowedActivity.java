package com.jaylax.wiredshack.eventManager.followed;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.jaylax.wiredshack.ProgressDialog;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ActivityManagerFollowedBinding;
import com.jaylax.wiredshack.eventManager.dashboard.DashboardEventManagerActivity;
import com.jaylax.wiredshack.eventManager.home.ManagerRecentEventsAdapter;
import com.jaylax.wiredshack.model.RecentEventMainModel;
import com.jaylax.wiredshack.model.UserDetailsModel;
import com.jaylax.wiredshack.rest.ApiClient;
import com.jaylax.wiredshack.user.following.UserFollowingAdapter;
import com.jaylax.wiredshack.utils.Commons;
import com.jaylax.wiredshack.utils.SharePref;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManagerFollowedActivity extends AppCompatActivity {

    ActivityManagerFollowedBinding mBinding;
    Context mContext;
    ProgressDialog progressDialog;
    UserDetailsModel userDetailsModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_manager_followed);
        mContext = this;
        progressDialog = new ProgressDialog(Objects.requireNonNull(mContext));
        userDetailsModel = Commons.convertStringToObject(mContext, SharePref.PREF_USER, UserDetailsModel.class);

        getFollowedList();
        mBinding.imgBack.setOnClickListener(view -> onBackPressed());

    }

    private void getFollowedList() {
        if (Commons.isOnline(mContext)) {
            progressDialog.show();
            String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");
            ApiClient.create().getFollowedData(header).enqueue(new Callback<ManagerFollowedMainModel>() {
                @Override
                public void onResponse(Call<ManagerFollowedMainModel> call, Response<ManagerFollowedMainModel> response) {
                    progressDialog.dismiss();
                    if (response.code() == 200 && response.isSuccessful()) {
                        if (response.body() != null) {
                            setFollowedData(response.body().getData());
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
                public void onFailure(Call<ManagerFollowedMainModel> call, Throwable t) {
                    progressDialog.dismiss();
                    Commons.showToast(mContext, getResources().getString(R.string.something_wants_wrong));
                }
            });
        } else {
            Commons.showToast(mContext, mContext.getResources().getString(R.string.no_internet_connection));
        }
    }

    private void setFollowedData(ArrayList<ManagerFollowedMainModel.ManagerFollowedData> list) {
        if (list.isEmpty()) {
            mBinding.recyclerManagerFollowed.setVisibility(View.GONE);
        } else {
            mBinding.tvUserFollowingCount.setText(getResources().getString(R.string.followed_count, String.valueOf(list.size())));
            mBinding.recyclerManagerFollowed.setVisibility(View.VISIBLE);
            mBinding.recyclerManagerFollowed.setLayoutManager(new LinearLayoutManager(this));
            mBinding.recyclerManagerFollowed.setAdapter(new ManagerFollowedAdapter(mContext, list));
        }
    }

}