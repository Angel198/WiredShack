package com.jaylax.wiredshack.eventManager.followed;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.jaylax.wiredshack.ProgressDialog;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ActivityManagerFollowedBinding;
import com.jaylax.wiredshack.model.UserDetailsModel;
import com.jaylax.wiredshack.rest.ApiClient;
import com.jaylax.wiredshack.user.following.UserFollowingMainModel;
import com.jaylax.wiredshack.utils.Commons;
import com.jaylax.wiredshack.utils.SharePref;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManagerFollowedActivity extends AppCompatActivity {

    ActivityManagerFollowedBinding mBinding;
    Context mContext;
    ProgressDialog progressDialog;
    UserDetailsModel userDetailsModel;
    ManagerFollowedAdapter adapter = null;
    ArrayList<ManagerFollowedMainModel.ManagerFollowedData> followedList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_manager_followed);
        mContext = this;
        progressDialog = new ProgressDialog(Objects.requireNonNull(mContext));
        userDetailsModel = Commons.convertStringToObject(mContext, SharePref.PREF_USER, UserDetailsModel.class);
        RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.place_holder).transform(new CenterCrop()).error(R.drawable.place_holder).priority(Priority.HIGH);
        Glide.with(this).load(userDetailsModel.getImage() == null ? "" : userDetailsModel.getImage()).apply(options).into(mBinding.imgAccountProfile);

        getFollowedList();
        mBinding.imgBack.setOnClickListener(view -> onBackPressed());
        mBinding.edtSearchManager.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (adapter != null) {
                    adapter.getFilter().filter(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mBinding.imgUserFollowingSort.setOnClickListener(view -> {
            if (!followedList.isEmpty() && adapter != null) {
                Collections.sort(followedList, (userFollowingData, t1) -> userFollowingData.getUserName().compareToIgnoreCase(t1.getUserName()));

                adapter.notifyDataSetChanged();
            }
        });
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
            followedList = list;
            mBinding.tvUserFollowingCount.setText(getResources().getString(R.string.followed_count, String.valueOf(list.size())));
            mBinding.recyclerManagerFollowed.setVisibility(View.VISIBLE);
            adapter = new ManagerFollowedAdapter(mContext, followedList);
            mBinding.recyclerManagerFollowed.setLayoutManager(new LinearLayoutManager(this));
            mBinding.recyclerManagerFollowed.setAdapter(adapter);
        }
    }

}