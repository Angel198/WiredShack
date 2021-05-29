package com.jaylax.wiredshack.user.following;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.jaylax.wiredshack.ProgressDialog;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ActivityUserFollowingBinding;
import com.jaylax.wiredshack.eventManager.followed.ManagerFollowedAdapter;
import com.jaylax.wiredshack.eventManager.followed.ManagerFollowedMainModel;
import com.jaylax.wiredshack.model.CommonResponseModel;
import com.jaylax.wiredshack.model.UserDetailsModel;
import com.jaylax.wiredshack.rest.ApiClient;
import com.jaylax.wiredshack.user.search.SearchFragment;
import com.jaylax.wiredshack.utils.Commons;
import com.jaylax.wiredshack.utils.SharePref;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserFollowingActivity extends AppCompatActivity {

    ActivityUserFollowingBinding mBinding;
    Context mContext;
    ProgressDialog progressDialog;
    UserDetailsModel userDetailsModel;
    ArrayList<UserFollowingMainModel.UserFollowingData> mainList = new ArrayList<>();
    ArrayList<UserFollowingMainModel.UserFollowingData> filteredList = new ArrayList<>();
    UserFollowingAdapter adapter = null;
    int followingCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_user_following);
        mContext = this;
        progressDialog = new ProgressDialog(Objects.requireNonNull(mContext));
        userDetailsModel = Commons.convertStringToObject(mContext, SharePref.PREF_USER, UserDetailsModel.class);
        RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.place_holder).transform(new CenterCrop()).error(R.drawable.place_holder).priority(Priority.HIGH);
        Glide.with(this).load(userDetailsModel.getImage() == null ? "" : userDetailsModel.getImage()).apply(options).into(mBinding.imgAccountProfile);

        getFollowingList();
        if (userDetailsModel.getFollowing() != null) {
            if (!userDetailsModel.getFollowing().isEmpty()) {
                followingCount = Integer.parseInt(userDetailsModel.getFollowing());
            }
        }
        mBinding.imgBack.setOnClickListener(view -> {
            onBackPressed();
        });

        mBinding.edtSearchManager.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                /*if (adapter != null) {
                    adapter.getFilter().filter(charSequence.toString());
                }*/

                String searchTxt = mBinding.edtSearchManager.getText().toString().toLowerCase();
                if (searchTxt.isEmpty()) {
                    setFollowedData(mainList);
                } else {
                    ArrayList<UserFollowingMainModel.UserFollowingData> listFilter = new ArrayList<>();
                    for (UserFollowingMainModel.UserFollowingData data : mainList) {
                        if (data.getManagerName().toLowerCase().contains(searchTxt)) {
                            listFilter.add(data);
                        }
                    }

                    setFollowedData(listFilter);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mBinding.imgUserFollowingSort.setOnClickListener(view -> {
            if (!filteredList.isEmpty() && adapter != null) {
                Collections.sort(filteredList, (userFollowingData, t1) -> userFollowingData.getManagerName().compareToIgnoreCase(t1.getManagerName()));

                adapter.notifyDataSetChanged();
            }
        });
    }

    private void getFollowingList() {
        if (Commons.isOnline(mContext)) {
            progressDialog.show();
            String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");
            ApiClient.create().getFollowingData(header).enqueue(new Callback<UserFollowingMainModel>() {
                @Override
                public void onResponse(Call<UserFollowingMainModel> call, Response<UserFollowingMainModel> response) {
                    progressDialog.dismiss();
                    if (response.code() == 200 && response.isSuccessful()) {
                        if (response.body() != null) {
//                            setFollowedData(response.body().getData());
                            mainList = response.body().getData();
                            for (UserFollowingMainModel.UserFollowingData data : mainList) {
                                data.setIsFollow("1");
                            }
                            setFollowedData(mainList);
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
                public void onFailure(Call<UserFollowingMainModel> call, Throwable t) {
                    progressDialog.dismiss();
                    Commons.showToast(mContext, getResources().getString(R.string.something_wants_wrong));
                }
            });
        } else {
            Commons.showToast(mContext, mContext.getResources().getString(R.string.no_internet_connection));
        }
    }

    private void setFollowedData(ArrayList<UserFollowingMainModel.UserFollowingData> list) {
        if (list.isEmpty()) {
            mBinding.recyclerUserFollowing.setVisibility(View.GONE);
        } else {
            filteredList = new ArrayList<>();
            filteredList = list;
            /*for (UserFollowingMainModel.UserFollowingData data : filteredList) {
                data.setIsFollow("1");
            }*/

            adapter = new UserFollowingAdapter(mContext, filteredList, this::followUnfollowManager);
//            followingCount = mainList.size();
            mBinding.tvUserFollowingCount.setText(String.valueOf(followingCount));
            mBinding.recyclerUserFollowing.setVisibility(View.VISIBLE);
            mBinding.recyclerUserFollowing.setLayoutManager(new LinearLayoutManager(this));
            mBinding.recyclerUserFollowing.setAdapter(adapter);
        }
    }

    private void followUnfollowManager(int pos, UserFollowingMainModel.UserFollowingData data) {
        if (Commons.isOnline(mContext)) {
            progressDialog.show();
            HashMap<String, String> params = new HashMap<>();
            params.put("manager_id", data.getId());

            String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");
            ApiClient.create().followManager(header, params).enqueue(new Callback<CommonResponseModel>() {
                @Override
                public void onResponse(Call<CommonResponseModel> call, Response<CommonResponseModel> response) {
                    progressDialog.dismiss();
                    if (response.code() == 200 && response.isSuccessful()) {
                        if (response.body() != null) {
                            if (response.body().getStatus().equals("200")) {
                                if (filteredList.get(pos).getIsFollow().equals("1")) {
                                    filteredList.get(pos).setIsFollow("0");
                                    followingCount = followingCount - 1;
                                } else {
                                    filteredList.get(pos).setIsFollow("1");
                                    followingCount = followingCount + 1;
                                }
                                if (adapter != null) {
                                    adapter.notifyDataSetChanged();
                                }
                                updateMainList(filteredList.get(pos));
                                mBinding.tvUserFollowingCount.setText(String.valueOf(followingCount));
                            } else {
                                String msg = "";
                                if (response.body().getMessage().isEmpty()) {
                                    msg = getResources().getString(R.string.please_try_after_some_time);
                                } else {
                                    msg = response.body().getMessage();
                                }
                                Commons.showToast(mContext, msg);
                            }
                        }
                    } else {
                        Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                    }
                }

                @Override
                public void onFailure(Call<CommonResponseModel> call, Throwable t) {
                    progressDialog.dismiss();
                    Commons.showToast(mContext, getResources().getString(R.string.something_wants_wrong));
                }
            });
        } else {
            Commons.showToast(mContext, getResources().getString(R.string.no_internet_connection));
        }
    }

    private void updateMainList(UserFollowingMainModel.UserFollowingData userFollowingData) {
        for (UserFollowingMainModel.UserFollowingData data : mainList) {
            if (data.getId().equals(userFollowingData.getId())) {
                data.setIsFollow(userFollowingData.getIsFollow());
            }
        }
    }
}