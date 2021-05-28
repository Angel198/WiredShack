package com.jaylax.wiredshack.eventManager.managerActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.jaylax.wiredshack.ProgressDialog;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.FragmentManagerActivityBinding;
import com.jaylax.wiredshack.model.CommonResponseModel;
import com.jaylax.wiredshack.model.UserDetailsModel;
import com.jaylax.wiredshack.rest.ApiClient;
import com.jaylax.wiredshack.utils.Commons;
import com.jaylax.wiredshack.utils.SharePref;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManagerActivityFragment extends Fragment {

    FragmentManagerActivityBinding mBinding;
    Boolean isRequest = true;
    Boolean isEvents = false;
    Context mContext;
    ProgressDialog progressDialog;
    UserDetailsModel userDetailsModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.fragment_manager_activity, container, false);
        mBinding.recyclerActivity.setLayoutManager(new LinearLayoutManager(getActivity()));
        mContext = getActivity();
        progressDialog = new ProgressDialog(Objects.requireNonNull(mContext));
        userDetailsModel = Commons.convertStringToObject(mContext, SharePref.PREF_USER, UserDetailsModel.class);


        userDetailsModel = Commons.convertStringToObject(mContext, SharePref.PREF_USER, UserDetailsModel.class);

        RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.place_holder).transform(new CenterCrop()).error(R.drawable.place_holder).priority(Priority.HIGH);
        Glide.with(this).load(userDetailsModel.getImage() == null ? "" : userDetailsModel.getImage()).apply(options).into(mBinding.imgAccountProfile);

        getIncomingRequest();
        setTabLayout();

        mBinding.tvIncomingRequest.setOnClickListener(view -> {
            if (!isRequest) {
                mBinding.recyclerActivity.setVisibility(View.GONE);
                getIncomingRequest();
            }
            isRequest = true;
            isEvents = false;
            setTabLayout();

        });

        mBinding.tvEventActivities.setOnClickListener(view -> {
            if (!isEvents) {
                mBinding.recyclerActivity.setVisibility(View.GONE);
                getAccountActivity();
            }
            isRequest = false;
            isEvents = true;
            setTabLayout();
        });

        return mBinding.getRoot();
    }

    private void setTabLayout() {
        if (isRequest) {
            mBinding.tvIncomingRequest.setBackgroundResource(R.drawable.back_pink_select);
        } else {
            mBinding.tvIncomingRequest.setBackgroundResource(0);
        }

        if (isEvents) {
            mBinding.tvEventActivities.setBackgroundResource(R.drawable.back_pink_select);
        } else {
            mBinding.tvEventActivities.setBackgroundResource(0);
        }
    }

    private void getIncomingRequest() {
        if (Commons.isOnline(mContext)) {
            progressDialog.show();
            String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");
            ApiClient.create().incomingRequest(header).enqueue(new Callback<IncomingRequestMainModel>() {
                @Override
                public void onResponse(Call<IncomingRequestMainModel> call, Response<IncomingRequestMainModel> response) {
                    progressDialog.dismiss();
                    if (response.code() == 200 && response.isSuccessful()) {
                        if (response.body() != null) {
                            setIncomingRequest(response.body().getData());
                            if (!response.body().getStatus().equals("200")) {
                                Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                            }
                        } else {
                            setIncomingRequest(new ArrayList<>());
                            Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                        }
                    } else {
                        setIncomingRequest(new ArrayList<>());
                        Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                    }
                }

                @Override
                public void onFailure(Call<IncomingRequestMainModel> call, Throwable t) {
                    progressDialog.dismiss();
                    setIncomingRequest(new ArrayList<>());
                    Commons.showToast(mContext, getResources().getString(R.string.something_wants_wrong));
                }
            });
        } else {
            Commons.showToast(mContext, mContext.getResources().getString(R.string.no_internet_connection));
        }
    }

    private void setIncomingRequest(ArrayList<IncomingRequestMainModel.IncomingRequest> list) {
        if (list.isEmpty()) {
            mBinding.recyclerActivity.setVisibility(View.GONE);
        } else {
            mBinding.recyclerActivity.setVisibility(View.VISIBLE);
            mBinding.recyclerActivity.setAdapter(new ManagerIncomingRequestAdapter(mContext, list, (pos, data, flag) -> {
                changeRequestStatus(data, flag);
            }));
        }
    }

    private void getAccountActivity() {
        if (Commons.isOnline(mContext)) {
            progressDialog.show();
            String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");
            ApiClient.create().getAccountActivity(header).enqueue(new Callback<ManagerActivityMainModel>() {
                @Override
                public void onResponse(Call<ManagerActivityMainModel> call, Response<ManagerActivityMainModel> response) {
                    progressDialog.dismiss();
                    if (response.code() == 200 && response.isSuccessful()) {
                        if (response.body() != null) {
                            setEventActivity(response.body().getData());
                            if (!response.body().getStatus().equals("200")) {
                                Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                            }
                        } else {
                            setEventActivity(new ArrayList<>());
                            Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                        }
                    } else {
                        setEventActivity(new ArrayList<>());
                        Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                    }
                }

                @Override
                public void onFailure(Call<ManagerActivityMainModel> call, Throwable t) {
                    progressDialog.dismiss();
                    setEventActivity(new ArrayList<>());
                    Commons.showToast(mContext, getResources().getString(R.string.something_wants_wrong));
                }
            });
        } else {
            Commons.showToast(mContext, mContext.getResources().getString(R.string.no_internet_connection));
        }
    }

    private void setEventActivity(ArrayList<ManagerActivityMainModel.ManagerActivityData> list) {
        if (list.isEmpty()) {
            mBinding.recyclerActivity.setVisibility(View.GONE);
        } else {
            mBinding.recyclerActivity.setVisibility(View.VISIBLE);
            mBinding.recyclerActivity.setAdapter(new ManagerEventActivitiesAdapter(mContext, list));
        }
    }

    private void changeRequestStatus(IncomingRequestMainModel.IncomingRequest data, String flag) {
        if (Commons.isOnline(mContext)) {
            progressDialog.show();
            String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");

            String statusFlag = "";
            if (flag.equals("accept")) {
                statusFlag = "1";
            } else {
                statusFlag = "0";
            }
            HashMap<String, String> params = new HashMap<>();
            params.put("event_id", data.getEventId());
            params.put("userid", data.getUserId());
            params.put("status", statusFlag);


            ApiClient.create().approveCancelRequest(header, params).enqueue(new Callback<CommonResponseModel>() {
                @Override
                public void onResponse(Call<CommonResponseModel> call, Response<CommonResponseModel> response) {
                    progressDialog.dismiss();
                    if (response.code() == 200 && response.isSuccessful()) {
                        if (response.body() != null) {
                            if (response.body().getStatus().equals("200")) {
                                getIncomingRequest();
                            } else {
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
                public void onFailure(Call<CommonResponseModel> call, Throwable t) {
                    progressDialog.dismiss();
                    Commons.showToast(mContext, getResources().getString(R.string.something_wants_wrong));
                }
            });
        } else {
            Commons.showToast(mContext, mContext.getResources().getString(R.string.no_internet_connection));
        }
    }
}