package com.jaylax.wiredshack.user.wishlist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jaylax.wiredshack.ProgressDialog;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ActivityUserWishListBinding;
import com.jaylax.wiredshack.eventManager.followed.ManagerFollowedAdapter;
import com.jaylax.wiredshack.eventManager.followed.ManagerFollowedMainModel;
import com.jaylax.wiredshack.model.CommonResponseModel;
import com.jaylax.wiredshack.model.UserDetailsModel;
import com.jaylax.wiredshack.rest.ApiClient;
import com.jaylax.wiredshack.user.eventDetails.EventDetailsActivity;
import com.jaylax.wiredshack.user.home.HomeRecentEventAdapter;
import com.jaylax.wiredshack.utils.Commons;
import com.jaylax.wiredshack.utils.SharePref;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserWishListActivity extends AppCompatActivity {

    ActivityUserWishListBinding mBinding;
    Context mContext;
    ProgressDialog progressDialog;
    UserDetailsModel userDetailsModel;
    ArrayList<UserWishListMainModel.UserWishList> wishList = new ArrayList<>();
    UserWishListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_user_wish_list);
        mContext = this;
        progressDialog = new ProgressDialog(Objects.requireNonNull(mContext));
        userDetailsModel = Commons.convertStringToObject(mContext, SharePref.PREF_USER, UserDetailsModel.class);

        mBinding.imgBack.setOnClickListener(view -> onBackPressed());
        mBinding.recyclerUserWishList.setLayoutManager(new GridLayoutManager(mContext,3));
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWishList();
    }

    private void getWishList() {
        if (Commons.isOnline(mContext)) {
            progressDialog.show();
            String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");
            ApiClient.create().getWishList(header).enqueue(new Callback<UserWishListMainModel>() {
                @Override
                public void onResponse(Call<UserWishListMainModel> call, Response<UserWishListMainModel> response) {
                    progressDialog.dismiss();
                    if (response.code() == 200 && response.isSuccessful()) {
                        if (response.body() != null) {
                            setWithListData(response.body().getData());
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
                public void onFailure(Call<UserWishListMainModel> call, Throwable t) {
                    progressDialog.dismiss();
                    Commons.showToast(mContext, getResources().getString(R.string.something_wants_wrong));
                }
            });
        } else {
            Commons.showToast(mContext, mContext.getResources().getString(R.string.no_internet_connection));
        }
    }

    private void setWithListData(ArrayList<UserWishListMainModel.UserWishList> list) {
        wishList = list;
        if (list.isEmpty()) {
            mBinding.recyclerUserWishList.setVisibility(View.GONE);
        } else {
            mBinding.recyclerUserWishList.setVisibility(View.VISIBLE);
            adapter = new UserWishListAdapter(mContext, list, new UserWishListAdapter.WishListEventClick() {
                @Override
                public void onEventClick(UserWishListMainModel.UserWishList data) {
                    Intent intent = new Intent(mContext, EventDetailsActivity.class);
                    intent.putExtra("eventId",data.getId());
                    startActivity(intent);
                }

                @Override
                public void onLikeEventClick(int pos, UserWishListMainModel.UserWishList data) {
                    likeDislikeEvent(pos,data);
                }
            });
            mBinding.recyclerUserWishList.setAdapter(adapter);
        }
    }

    private void likeDislikeEvent(int pos, UserWishListMainModel.UserWishList data) {
        if (Commons.isOnline(mContext)) {
//                    progressDialog.show();
            HashMap<String, String> params = new HashMap<>();
            params.put("event_id", data.getId());

            String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");
            ApiClient.create().likeEvent(header, params).enqueue(new Callback<CommonResponseModel>() {
                @Override
                public void onResponse(Call<CommonResponseModel> call, Response<CommonResponseModel> response) {
//                            progressDialog.dismiss();
                    if (response.code() == 200 && response.isSuccessful()) {
                        if (response.body() != null) {
                            if (response.body().getStatus().equals("200")) {
                                if (data.getLike().equals("1")) {
                                    wishList.get(pos).setLike("0");
                                } else {
                                    wishList.get(pos).setLike("1");
                                }
                                adapter.notifyDataSetChanged();
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
//                            progressDialog.dismiss();
                    Commons.showToast(mContext, getResources().getString(R.string.something_wants_wrong));
                }
            });
        } else {
            Commons.showToast(mContext, getResources().getString(R.string.no_internet_connection));
        }
    }
}