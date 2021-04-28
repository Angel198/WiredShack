package com.jaylax.wiredshack.user.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jaylax.wiredshack.ProgressDialog;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.FragmentHomeBinding;
import com.jaylax.wiredshack.model.RecentEventMainModel;
import com.jaylax.wiredshack.model.UserDetailsModel;
import com.jaylax.wiredshack.rest.ApiClient;
import com.jaylax.wiredshack.user.eventDetails.EventDetailsActivity;
import com.jaylax.wiredshack.user.managerDetails.ManagerDetailsActivity;
import com.jaylax.wiredshack.utils.Commons;
import com.jaylax.wiredshack.utils.SharePref;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    FragmentHomeBinding mBinding;
    Context context;
    UserDetailsModel userDetailsModel;
    ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);

        context = getActivity();
        progressDialog = new ProgressDialog(context);
        userDetailsModel = Commons.convertStringToObject(context, SharePref.PREF_USER, UserDetailsModel.class);

//        getEventList();

        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        getEventManagerList();
    }
    private void getEventManagerList(){
        if (Commons.isOnline(context)){
            progressDialog.show();
            String header = "Bearer " + SharePref.getInstance(context).get(SharePref.PREF_TOKEN, "");
            ApiClient.create().getEventsManager(header).enqueue(new Callback<ManagerListMainModel>() {
                @Override
                public void onResponse(Call<ManagerListMainModel> call, Response<ManagerListMainModel> response) {
                    progressDialog.dismiss();
                    getEventList();
                    if (response.code() == 200 && response.isSuccessful()) {
                        if (response.body() != null) {
                            setEventManagerData(response.body().getData());
                            if (!response.body().getStatus().equals("200")){
                                Commons.showToast(context, getResources().getString(R.string.please_try_after_some_time));
                            }
                        }else {
                            Commons.showToast(context, getResources().getString(R.string.please_try_after_some_time));
                        }
                    } else {
                        Commons.showToast(context, getResources().getString(R.string.please_try_after_some_time));
                    }
                }

                @Override
                public void onFailure(Call<ManagerListMainModel> call, Throwable t) {
                    progressDialog.dismiss();
                    getEventList();
                    Commons.showToast(context, getResources().getString(R.string.something_wants_wrong));
                }
            });
        }else {
            Commons.showToast(context, context.getResources().getString(R.string.no_internet_connection));
        }
    }

    private void setEventManagerData(ArrayList<ManagerListMainModel.ManagerListData> list) {
        if (list.isEmpty()){
            mBinding.linearHomeManager.setVisibility(View.GONE);
        }else {
            mBinding.linearHomeManager.setVisibility(View.VISIBLE);
            mBinding.recyclerHomeManager.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            mBinding.recyclerHomeManager.setAdapter(new HomeTopStoryAdapter(context, list, data -> {
                Intent intent = new Intent(context, ManagerDetailsActivity.class);
                intent.putExtra("managerId",data.getId());
                context.startActivity(intent);
            }));
        }
    }

    private void getEventList() {
        if (Commons.isOnline(context)){
            progressDialog.show();
            String header = "Bearer " + SharePref.getInstance(context).get(SharePref.PREF_TOKEN, "");
            ApiClient.create().getRecentEventsUser(header).enqueue(new Callback<RecentEventMainModel>() {
                @Override
                public void onResponse(Call<RecentEventMainModel> call, Response<RecentEventMainModel> response) {
                    progressDialog.dismiss();
                    if (response.code() == 200 && response.isSuccessful()) {
                        if (response.body() != null) {
                            setRecentEventData(response.body().getData());
                            if (!response.body().getStatus().equals("200")){
                                Commons.showToast(context, getResources().getString(R.string.please_try_after_some_time));
                            }
                        }else {
                            Commons.showToast(context, getResources().getString(R.string.please_try_after_some_time));
                        }
                    } else {
                        Commons.showToast(context, getResources().getString(R.string.please_try_after_some_time));
                    }
                }

                @Override
                public void onFailure(Call<RecentEventMainModel> call, Throwable t) {
                    progressDialog.dismiss();
                    Commons.showToast(context, getResources().getString(R.string.something_wants_wrong));
                }
            });
        }else {
            Commons.showToast(context, context.getResources().getString(R.string.no_internet_connection));
        }
    }

    private void setRecentEventData(ArrayList<RecentEventMainModel.RecentEventData> list){
        if (list.isEmpty()){
            mBinding.linearHomeRecentEvent.setVisibility(View.GONE);
        }else {
            mBinding.linearHomeRecentEvent.setVisibility(View.VISIBLE);
            mBinding.recyclerHomeRecentEvent.setLayoutManager(new GridLayoutManager(getActivity(),3));
            mBinding.recyclerHomeRecentEvent.setAdapter(new HomeRecentEventAdapter(context, list, data -> {
                Intent intent = new Intent(context, EventDetailsActivity.class);
                intent.putExtra("eventId",data.getId());
                context.startActivity(intent);
            },true));
        }
    }
}