package com.jaylax.wiredshack.eventManager.managerAccount;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
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
import com.jaylax.wiredshack.databinding.FragmentManagerAccountBinding;
import com.jaylax.wiredshack.eventManager.dashboard.DashboardEventManagerActivity;
import com.jaylax.wiredshack.eventManager.home.ManagerRecentEventsAdapter;
import com.jaylax.wiredshack.eventManager.managerActivity.ManagerEventActivitiesAdapter;
import com.jaylax.wiredshack.eventManager.managerActivity.ManagerIncomingRequestAdapter;
import com.jaylax.wiredshack.model.UserDetailsModel;
import com.jaylax.wiredshack.rest.Api;
import com.jaylax.wiredshack.rest.ApiClient;
import com.jaylax.wiredshack.user.dashboard.DashboardActivity;
import com.jaylax.wiredshack.model.RecentEventMainModel;
import com.jaylax.wiredshack.utils.Commons;
import com.jaylax.wiredshack.utils.SharePref;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManagerAccountFragment extends Fragment {

    FragmentManagerAccountBinding mBinding;
    Context mContext;
    ProgressDialog progressDialog;

    Boolean isRecent = true;
    Boolean isPast = false;

    static final String RECENT_EVENT = "recent";
    static final String PAST_EVENT = "past";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()),R.layout.fragment_manager_account,container,false);
        mContext = getActivity();
        progressDialog = new ProgressDialog(Objects.requireNonNull(mContext));

        mBinding.tvAccountEditProfile.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            getActivity().startActivity(intent);
        });


        setClickListener();
        return mBinding.getRoot();
    }

    private void setClickListener() {
        mBinding.imgAccountLogout.setOnClickListener(view -> showLogoutDialog());

        mBinding.tvRecentEvent.setOnClickListener(view -> {
            if (!isRecent){
                getManagerEventList(RECENT_EVENT);
            }
            isRecent = true;
            isPast = false;
            setTabLayout();

        });

        mBinding.tvPastEvent.setOnClickListener(view -> {
            if (!isPast){
                getManagerEventList(PAST_EVENT);
            }
            isRecent = false;
            isPast = true;
            setTabLayout();
        });
    }

    private void setTabLayout() {
        if (isRecent){
            mBinding.tvRecentEvent.setBackgroundResource(R.drawable.back_round_white);
            mBinding.tvRecentEvent.setTextColor(ContextCompat.getColor(getActivity(),R.color.colorBlackText));
        }else {
            mBinding.tvRecentEvent.setBackgroundResource(R.drawable.back_border_white);
            mBinding.tvRecentEvent.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));
        }

        if (isPast){
            mBinding.tvPastEvent.setBackgroundResource(R.drawable.back_round_white);
            mBinding.tvPastEvent.setTextColor(ContextCompat.getColor(getActivity(),R.color.colorBlackText));
        }else {
            mBinding.tvPastEvent.setBackgroundResource(R.drawable.back_border_white);
            mBinding.tvPastEvent.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isRecent = true;
        isPast = false;
        setTabLayout();
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
                    getManagerEventList(RECENT_EVENT);
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
                    getManagerEventList(RECENT_EVENT);
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
    }

    private void getManagerEventList(String type) {
        if (Commons.isOnline(mContext)){
            String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");

            Call<RecentEventMainModel> call = null;

            if (type.equals(RECENT_EVENT)){
                call = ApiClient.create().getRecentEventsManager(header);
            }else {
                call = ApiClient.create().getPastEventsManager(header);
            }
            if (call != null) {
                progressDialog.show();
                call.enqueue(new Callback<RecentEventMainModel>() {
                    @Override
                    public void onResponse(Call<RecentEventMainModel> call, Response<RecentEventMainModel> response) {
                        progressDialog.dismiss();
                        if (response.code() == 200 && response.isSuccessful()) {
                            if (response.body() != null) {
                                setRecentEventData(response.body().getData(),type);
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
                    public void onFailure(Call<RecentEventMainModel> call, Throwable t) {
                        progressDialog.dismiss();
                        Commons.showToast(mContext, getResources().getString(R.string.something_wants_wrong));
                    }
                });
            }
        }else {
            Commons.showToast(mContext, mContext.getResources().getString(R.string.no_internet_connection));
        }
    }

    private void setRecentEventData(ArrayList<RecentEventMainModel.RecentEventData> list, String type){
        if (list.isEmpty()){
            mBinding.recyclerHomeRecentEventEvents.setVisibility(View.GONE);
        }else {
            mBinding.recyclerHomeRecentEventEvents.setVisibility(View.VISIBLE);
            mBinding.recyclerHomeRecentEventEvents.setLayoutManager(new GridLayoutManager(getActivity(),3));
            mBinding.recyclerHomeRecentEventEvents.setAdapter(new ManagerRecentEventsAdapter(mContext, list, (data, lisType) -> {
                DashboardEventManagerActivity.redirectToEditEvent(data.getId(),getActivity());
            },false,type));
        }
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