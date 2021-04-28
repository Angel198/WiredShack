package com.jaylax.wiredshack.user.account;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.jaylax.wiredshack.EditProfileActivity;
import com.jaylax.wiredshack.ProgressDialog;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.FragmentAccountBinding;
import com.jaylax.wiredshack.model.UserDetailsModel;
import com.jaylax.wiredshack.rest.ApiClient;
import com.jaylax.wiredshack.user.dashboard.DashboardActivity;
import com.jaylax.wiredshack.user.following.UserFollowingActivity;
import com.jaylax.wiredshack.user.home.HomeRecentEventAdapter;
import com.jaylax.wiredshack.user.wishlist.UserWishListActivity;
import com.jaylax.wiredshack.utils.Commons;
import com.jaylax.wiredshack.utils.SharePref;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountFragment extends Fragment {

    FragmentAccountBinding mBinding;
    Context mContext;
    ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_account, container, false);
        mContext = getActivity();
        progressDialog = new ProgressDialog(mContext);

        mBinding.recyclerAccountFollowingEvents.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mBinding.recyclerAccountFollowingEvents.setAdapter(new HomeRecentEventAdapter(mContext, new ArrayList<>(), data -> {

        }));

        mBinding.tvAccountEditProfile.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            Objects.requireNonNull(getActivity()).startActivity(intent);
        });

        mBinding.tvAccountWishList.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), UserWishListActivity.class);
            Objects.requireNonNull(getActivity()).startActivity(intent);
        });

        mBinding.linearAccountFollowing.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), UserFollowingActivity.class);
            Objects.requireNonNull(getActivity()).startActivity(intent);
        });

        mBinding.imgAccountLogout.setOnClickListener(view -> {
           showLogoutDialog();
        });
        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        getUserDetails();
    }

    private void getUserDetails() {
        if (Commons.isOnline(mContext)) {
            progressDialog.show();
            String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");
            ApiClient.create().userDetails(header).enqueue(new Callback<UserDetailsModel>() {
                @Override
                public void onResponse(Call<UserDetailsModel> call, Response<UserDetailsModel> response) {
                    progressDialog.dismiss();
                    if (response.code() == 200 && response.isSuccessful()) {
                        if (response.body() != null) {
                            SharePref.getInstance(mContext).save(SharePref.PREF_USER, Commons.convertObjectToString(response.body()));
                            setUserData();
                        }
                    } else {
                        Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                    }
                }

                @Override
                public void onFailure(Call<UserDetailsModel> call, Throwable t) {
                    progressDialog.dismiss();
                    Commons.showToast(mContext, getResources().getString(R.string.something_wants_wrong));
                }
            });
        } else {
            Commons.showToast(mContext, getResources().getString(R.string.no_internet_connection));
        }
    }

    private void setUserData() {
        UserDetailsModel userDetailsModel = Commons.convertStringToObject(mContext, SharePref.PREF_USER, UserDetailsModel.class);
        RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.place_holder).transform(new CenterCrop()).error(R.drawable.place_holder).priority(Priority.HIGH);
        Glide.with(this).load(userDetailsModel.getImage() == null ? "" : userDetailsModel.getImage()).apply(options).into(mBinding.imgAccountProfile);
        Glide.with(this).load(userDetailsModel.getCoverImage() == null ? "" : userDetailsModel.getCoverImage()).apply(options).into(mBinding.imgAccountCover);

        mBinding.tvAccountProfileName.setText(userDetailsModel.getName());

        String followingCount = "0";
        if (userDetailsModel.getFollowing() != null){
            if (!userDetailsModel.getFollowing().isEmpty()) {
                followingCount = userDetailsModel.getFollowing();
            }
        }
        mBinding.tvFollowCount.setText(followingCount);

    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getResources().getString(R.string.logout_msg));
        builder.setCancelable(false);
        builder.setPositiveButton(getResources().getString(R.string.txt_cancel), (dialogInterface, i) -> {

        });
        builder.setNegativeButton(getResources().getString(R.string.logout), (dialogInterface, i) -> {
           SharePref.getInstance(getActivity()).clearAll();
            Intent intent = new Intent(getActivity(), DashboardActivity.class);
            Objects.requireNonNull(getActivity()).startActivity(intent);
            Objects.requireNonNull(getActivity()).finishAffinity();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
    }
}