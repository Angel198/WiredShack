package com.jaylax.wiredshack.user.managerDetails;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jaylax.wiredshack.ProgressDialog;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ActivityManagerDetailsBinding;
import com.jaylax.wiredshack.model.CommonResponseModel;
import com.jaylax.wiredshack.rest.ApiClient;
import com.jaylax.wiredshack.user.eventDetails.EventDetailsActivity;
import com.jaylax.wiredshack.user.home.HomeRecentEventAdapter;
import com.jaylax.wiredshack.user.home.RecentEventMainModel;
import com.jaylax.wiredshack.utils.Commons;
import com.jaylax.wiredshack.utils.SharePref;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManagerDetailsActivity extends AppCompatActivity {

    ActivityManagerDetailsBinding mBinding;
    Context mContext;
    ProgressDialog progressDialog;
    String mangerId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_manager_details);
        mContext = this;
        progressDialog = new ProgressDialog(mContext);

        if (getIntent().hasExtra("managerId")) {
            mangerId = getIntent().getStringExtra("managerId");
            getManagerDetails();
        }

        mBinding.imgBack.setOnClickListener(view -> onBackPressed());
    }

    private void getManagerDetails() {
        if (Commons.isOnline(mContext)){
            progressDialog.show();
            String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");
            HashMap<String, String> params = new HashMap<>();
            params.put("manager_id", mangerId);
            ApiClient.create().getEventMangerDetails(params).enqueue(new Callback<CommonResponseModel>() {
                @Override
                public void onResponse(Call<CommonResponseModel> call, Response<CommonResponseModel> response) {
                    progressDialog.dismiss();
                    if (response.code() == 200 && response.isSuccessful()) {
                        if (response.body() != null) {
//                            setRecentEventData(response.body().getData());
                            if (!response.body().getStatus().equals("200")){
                                Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                            }
                        }else {
                            Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
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
        }else {
            Commons.showToast(mContext, mContext.getResources().getString(R.string.no_internet_connection));
        }
    }

    private void setRecentEventData(ArrayList<RecentEventMainModel.RecentEventData> list){
        if (list.isEmpty()){
            mBinding.linearRecentEvent.setVisibility(View.GONE);
        }else {
            mBinding.linearRecentEvent.setVisibility(View.VISIBLE);
            mBinding.recyclerRecentEvent.setLayoutManager(new GridLayoutManager(mContext,3));
            mBinding.recyclerRecentEvent.setAdapter(new HomeRecentEventAdapter(mContext, list, data -> {
                Intent intent = new Intent(mContext, EventDetailsActivity.class);
                intent.putExtra("eventId",data.getId());
                mContext.startActivity(intent);
            }));
        }
    }
}