package com.jaylax.wiredshack.user.managerDetails;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.jaylax.wiredshack.LoginActivity;
import com.jaylax.wiredshack.ProgressDialog;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ActivityManagerDetailsBinding;
import com.jaylax.wiredshack.model.CommonResponseModel;
import com.jaylax.wiredshack.rest.ApiClient;
import com.jaylax.wiredshack.user.eventDetails.EventDetailsActivity;
import com.jaylax.wiredshack.user.home.HomeRecentEventAdapter;
import com.jaylax.wiredshack.utils.Commons;
import com.jaylax.wiredshack.utils.SharePref;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManagerDetailsActivity extends AppCompatActivity {

    ActivityManagerDetailsBinding mBinding;
    Context mContext;
    ProgressDialog progressDialog;
    String mangerId = "";
    boolean isFollow = false;
    int followCount = 0;
    int searchDataPos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_manager_details);
        mContext = this;
        progressDialog = new ProgressDialog(mContext);

        if (getIntent().hasExtra("managerId")) {
            mangerId = getIntent().getStringExtra("managerId");
            getManagerDetails();
        }

        if (getIntent().hasExtra("listPos")){
            searchDataPos = Integer.parseInt(getIntent().getStringExtra("listPos"));
        }
        setClickListener();
    }

    private void getManagerDetails() {
        if (Commons.isOnline(mContext)) {
            String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");
            HashMap<String, String> params = new HashMap<>();
            params.put("manager_id", mangerId);
            Call<ManagerDetailsMainModel> call = null;

            if (SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "").toString().isEmpty()) {
                call = ApiClient.create().getEventMangerDetails(params);
            } else {
                call = ApiClient.create().getEventMangerDetailsToken(header, params);
            }
            if (call != null) {
                progressDialog.show();
                call.enqueue(new Callback<ManagerDetailsMainModel>() {
                    @Override
                    public void onResponse(Call<ManagerDetailsMainModel> call, Response<ManagerDetailsMainModel> response) {
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
                    public void onFailure(Call<ManagerDetailsMainModel> call, Throwable t) {
                        progressDialog.dismiss();
                        Commons.showToast(mContext, getResources().getString(R.string.something_wants_wrong));
                    }
                });
            }
        } else {
            Commons.showToast(mContext, mContext.getResources().getString(R.string.no_internet_connection));
        }
    }

    private void setRecentEventData(ManagerDetailsMainModel.ManagerDetailsData managerDetailsData) {
        RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.place_holder).transform(new CenterCrop()).error(R.drawable.place_holder).priority(Priority.HIGH);
        String image = managerDetailsData.getManagerImage() == null ? "" : managerDetailsData.getManagerImage();
        Glide.with(this).load(image).apply(options).into(mBinding.imgAccountProfile);
        Glide.with(this).load(managerDetailsData.getManagerCoverImage() == null ? "" : managerDetailsData.getManagerCoverImage()).apply(options).into(mBinding.imgAccountCover);

        /*if (managerDetailsData.getUserType() ==null){
            mBinding.imgAccountProfile.setBackground(ContextCompat.getDrawable(this, R.drawable.back_manager_profile));
        }else {
            if (managerDetailsData.getUserType().equals("2")){
                mBinding.imgAccountProfile.setBackground(ContextCompat.getDrawable(this, R.drawable.back_manager_profile));
            }else if (managerDetailsData.getUserType().equals("3")){
                mBinding.imgAccountProfile.setBackground(ContextCompat.getDrawable(this, R.drawable.back_dj_profile));
            }else {
                mBinding.imgAccountProfile.setBackground(ContextCompat.getDrawable(this, R.drawable.back_manager_profile));
            }
        }*/

        mBinding.tvEventManagerName.setText(managerDetailsData.getManagerName());

        if (managerDetailsData.getFollow() == null) {
            isFollow = false;
        } else {
            if (managerDetailsData.getFollow().equals("1")) {
                isFollow = true;
            } else {
                isFollow = false;
            }
        }

        if (managerDetailsData.getFollowedCount() == null || managerDetailsData.getFollowedCount().isEmpty()) {
            followCount = 0;
        } else {
            followCount = Integer.parseInt(managerDetailsData.getFollowedCount());
        }

        setFollowUI();

        if (managerDetailsData.getRecentEvent().isEmpty()) {
            mBinding.linearRecentEvent.setVisibility(View.GONE);
        } else {
            mBinding.linearRecentEvent.setVisibility(View.VISIBLE);
            mBinding.recyclerRecentEvent.setLayoutManager(new GridLayoutManager(mContext, 3));
            mBinding.recyclerRecentEvent.setAdapter(new HomeRecentEventAdapter(mContext, managerDetailsData.getRecentEvent(), data -> {
                Intent intent = new Intent(mContext, EventDetailsActivity.class);
                intent.putExtra("eventId", data.getId());
                mContext.startActivity(intent);
            }));
        }
    }

    private void setClickListener() {
        mBinding.imgBack.setOnClickListener(view ->{
            onBackPressed();
        });

        mBinding.tvFollow.setOnClickListener(view -> {
            if (SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "").toString().isEmpty()) {
                openLogin();
            } else {
                if (Commons.isOnline(mContext)) {
                    progressDialog.show();
                    HashMap<String, String> params = new HashMap<>();
                    params.put("manager_id", mangerId);

                    String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");
                    ApiClient.create().followManager(header, params).enqueue(new Callback<CommonResponseModel>() {
                        @Override
                        public void onResponse(Call<CommonResponseModel> call, Response<CommonResponseModel> response) {
                            progressDialog.dismiss();
                            if (response.code() == 200 && response.isSuccessful()) {
                                if (response.body() != null) {
                                    if (response.body().getStatus().equals("200")) {
                                        if (isFollow) {
                                            isFollow = false;
                                            followCount = followCount - 1;
                                        } else {
                                            isFollow = true;
                                            followCount = followCount + 1;
                                        }
                                        setFollowUI();
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
        });
    }

    private void setFollowUI() {
        if (isFollow) {
            mBinding.tvFollow.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.back_border_white));
            mBinding.tvFollow.setText(mContext.getResources().getString(R.string.following));
        } else {
            mBinding.tvFollow.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.back_follow));
            mBinding.tvFollow.setText(mContext.getResources().getString(R.string.follow));
        }

        mBinding.tvManagerFollowCount.setText(String.valueOf(followCount));
    }

    private void openLogin() {
        Intent intent = new Intent(mContext, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (searchDataPos != (-1)){
            String followFlag = isFollow ? "1": "0";
            Intent intent = new Intent();
            intent.putExtra("listPos",String.valueOf(searchDataPos));
            intent.putExtra("followFlag",followFlag);
            setResult(Activity.RESULT_OK,intent);
        }
        super.onBackPressed();
    }
}