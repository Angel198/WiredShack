package com.jaylax.wiredshack.user.managerDetails;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.jaylax.wiredshack.ProgressDialog;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ActivityManagerDetailsBinding;
import com.jaylax.wiredshack.model.CommonResponseModel;
import com.jaylax.wiredshack.model.UserDetailsModel;
import com.jaylax.wiredshack.rest.ApiClient;
import com.jaylax.wiredshack.user.eventDetails.EventDetailsActivity;
import com.jaylax.wiredshack.utils.Commons;
import com.jaylax.wiredshack.utils.SharePref;
import com.jaylax.wiredshack.utils.SpannedGridLayoutManager;

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
    UserDetailsModel userDetailsModel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_manager_details);
        mContext = this;
        progressDialog = new ProgressDialog(mContext);
        userDetailsModel = Commons.convertStringToObject(mContext, SharePref.PREF_USER, UserDetailsModel.class);
        if (userDetailsModel == null) {
            mBinding.imgAccountProfile.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.toplogo));
        } else {
            RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.place_holder).transform(new CenterCrop()).error(R.drawable.place_holder).priority(Priority.HIGH);
            Glide.with(this).load(userDetailsModel.getImage() == null ? "" : userDetailsModel.getImage()).apply(options).into(mBinding.imgAccountProfile);
        }


        if (getIntent().hasExtra("managerId")) {
            mangerId = getIntent().getStringExtra("managerId");
            getManagerDetails();
        }

        if (getIntent().hasExtra("listPos")) {
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
        Glide.with(this).load(image).apply(options).into(mBinding.imgManagerProfile);


        if (managerDetailsData.getUserType() ==null){
            mBinding.clMainManagerDetails.setBackground(ContextCompat.getDrawable(this, R.color.colorBlack20));
        }else {
            if (managerDetailsData.getUserType().equals("2")){
                mBinding.clMainManagerDetails.setBackground(ContextCompat.getDrawable(this, R.color.colorBlack20));
            }else if (managerDetailsData.getUserType().equals("3")){
                mBinding.clMainManagerDetails.setBackground(ContextCompat.getDrawable(this, R.drawable.back_dj_details));
            }else {
                mBinding.clMainManagerDetails.setBackground(ContextCompat.getDrawable(this, R.color.colorBlack20));
            }
        }

        if (managerDetailsData.getAboutMe() == null){
            mBinding.linearProfileAboutMe.setVisibility(View.GONE);
        }else {
            if (managerDetailsData.getAboutMe().isEmpty()){
                mBinding.linearProfileAboutMe.setVisibility(View.GONE);
            }else {
                mBinding.linearProfileAboutMe.setVisibility(View.VISIBLE);
                mBinding.tvManagerAboutMe.setText(managerDetailsData.getAboutMe());
            }
        }


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
            mBinding.recyclerRecentEvent.setVisibility(View.GONE);
        } else {
            mBinding.recyclerRecentEvent.setVisibility(View.VISIBLE);
            RecyclerView.LayoutManager manager ;
            if (managerDetailsData.getRecentEvent().size() > 1){
                manager = new SpannedGridLayoutManager(
                        position -> {
                            // Conditions for 2x2 items
                            if (position % 6 == 0 || position % 6 == 4) {
                                return new SpannedGridLayoutManager.SpanInfo(2, 2);
                            } else {
                                return new SpannedGridLayoutManager.SpanInfo(1, 1);
                            }
                        },
                        3, // number of columns
                        1f // how big is default item
                );
            }else {
                manager = new GridLayoutManager(mContext,2);
            }
            mBinding.recyclerRecentEvent.setHasFixedSize(true);
            mBinding.recyclerRecentEvent.setLayoutManager(manager);
            mBinding.recyclerRecentEvent.setAdapter(new ManagerEventAdapter(mContext, managerDetailsData.getRecentEvent(), data -> {
                Intent intent = new Intent(mContext, EventDetailsActivity.class);
                intent.putExtra("eventId", data.getId());
                mContext.startActivity(intent);
            }));
        }
    }

    private void setClickListener() {
        mBinding.imgBack.setOnClickListener(view -> {
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
        Commons.openLoginScree(mContext);
    }

    @Override
    public void onBackPressed() {
        if (searchDataPos != (-1)) {
            String followFlag = isFollow ? "1" : "0";
            Intent intent = new Intent();
            intent.putExtra("listPos", String.valueOf(searchDataPos));
            intent.putExtra("followFlag", followFlag);
            setResult(Activity.RESULT_OK, intent);
        }
        super.onBackPressed();
    }
}