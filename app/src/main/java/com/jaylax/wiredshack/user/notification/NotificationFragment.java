package com.jaylax.wiredshack.user.notification;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.jaylax.wiredshack.ProgressDialog;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.FragmentNotificationBinding;
import com.jaylax.wiredshack.eventManager.managerActivity.IncomingRequestMainModel;
import com.jaylax.wiredshack.model.CommonResponseModel;
import com.jaylax.wiredshack.model.UserDetailsModel;
import com.jaylax.wiredshack.rest.ApiClient;
import com.jaylax.wiredshack.user.eventDetails.EventDetailsActivity;
import com.jaylax.wiredshack.utils.Commons;
import com.jaylax.wiredshack.utils.SharePref;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationFragment extends Fragment {

    FragmentNotificationBinding mBinding;
    Boolean isAccept = true;
    Boolean isViewRequest = false;
    ArrayList<DummyModel> dummyList = new ArrayList<>();
    Context mContext;
    ProgressDialog progressDialog;
    UserDetailsModel userDetailsModel;

    ArrayList<AcceptedEventMainModel.AcceptedData> acceptedList = new ArrayList<>();

    ArrayList<PendingRequestMainModel.PendingRequestData> pendingList = new ArrayList<>();
    ViewSentRequestAdapter requestAdapter = null;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_notification, container, false);
        mBinding.recyclerActivity.setLayoutManager(new LinearLayoutManager(getActivity()));

        mContext = getActivity();
        progressDialog = new ProgressDialog(Objects.requireNonNull(mContext));
        userDetailsModel = Commons.convertStringToObject(mContext, SharePref.PREF_USER, UserDetailsModel.class);

        RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.place_holder).transform(new CenterCrop()).error(R.drawable.place_holder).priority(Priority.HIGH);
        Glide.with(this).load(userDetailsModel.getImage() == null ? "" : userDetailsModel.getImage()).apply(options).into(mBinding.imgAccountProfile);

        getAcceptedEventData();
        setTabLayout();

        setClickListener();
        return mBinding.getRoot();
    }

    private void setClickListener() {
        mBinding.tvAcceptedRequest.setOnClickListener(view -> {
            if (!isAccept) {
                mBinding.recyclerActivity.setVisibility(View.GONE);
                getAcceptedEventData();
            }
            isAccept = true;
            isViewRequest = false;
            setTabLayout();

        });

        mBinding.tvViewSentRequest.setOnClickListener(view -> {
            if (!isViewRequest) {
                mBinding.recyclerActivity.setVisibility(View.GONE);
                getSentRequestListData();
            }
            isAccept = false;
            isViewRequest = true;
            setTabLayout();
        });
    }

    private void setTabLayout() {
        if (isAccept) {
            mBinding.tvAcceptedRequest.setBackgroundResource(R.drawable.back_pink_select);
        } else {
            mBinding.tvAcceptedRequest.setBackgroundResource(R.drawable.back_round_black);
        }

        if (isViewRequest) {
            mBinding.tvViewSentRequest.setBackgroundResource(R.drawable.back_pink_select);
        } else {
            mBinding.tvViewSentRequest.setBackgroundResource(R.drawable.back_round_black);
        }
    }

    private void getAcceptedEventData() {
        if (Commons.isOnline(mContext)) {
            progressDialog.show();
            String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");
            ApiClient.create().getUserAcceptedEvent(header).enqueue(new Callback<AcceptedEventMainModel>() {
                @Override
                public void onResponse(Call<AcceptedEventMainModel> call, Response<AcceptedEventMainModel> response) {
                    progressDialog.dismiss();
                    if (response.code() == 200 && response.isSuccessful()) {
                        if (response.body() != null) {
                            setAcceptedEvent(response.body().getData());
                            if (!response.body().getStatus().equals("200")) {
                                Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                            }
                        } else {
                            setAcceptedEvent(new ArrayList<>());
                            Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                        }
                    } else {
                        setAcceptedEvent(new ArrayList<>());
                        Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                    }
                }

                @Override
                public void onFailure(Call<AcceptedEventMainModel> call, Throwable t) {
                    progressDialog.dismiss();
                    setAcceptedEvent(new ArrayList<>());
                    Commons.showToast(mContext, getResources().getString(R.string.something_wants_wrong));
                }
            });
        } else {
            Commons.showToast(mContext, mContext.getResources().getString(R.string.no_internet_connection));
        }
    }

    private void setAcceptedEvent(ArrayList<AcceptedEventMainModel.AcceptedData> list) {
        if (list.isEmpty()){
            mBinding.recyclerActivity.setVisibility(View.GONE);
        }else {
            acceptedList = new ArrayList<>();
            acceptedList = list;
            for (AcceptedEventMainModel.AcceptedData data : acceptedList){
                data.setDayName("");
            }
            mBinding.recyclerActivity.setVisibility(View.VISIBLE);
            mBinding.recyclerActivity.setAdapter(new AcceptedRequestAdapter(mContext,acceptedList,(pos, data) -> {
                Intent intent = new Intent(mContext, EventDetailsActivity.class);
                intent.putExtra("eventId", data.getEventId());
                mContext.startActivity(intent);
            }));
        }
    }

    private void getSentRequestListData() {
        if (Commons.isOnline(mContext)) {
            progressDialog.show();
            String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");
            ApiClient.create().getUserSentEventRequest(header).enqueue(new Callback<PendingRequestMainModel>() {
                @Override
                public void onResponse(Call<PendingRequestMainModel> call, Response<PendingRequestMainModel> response) {
                    progressDialog.dismiss();
                    if (response.code() == 200 && response.isSuccessful()) {
                        if (response.body() != null) {
                            setPendingRequest(response.body().getData());
                            if (!response.body().getStatus().equals("200")) {
                                Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                            }
                        } else {
                            setPendingRequest(new ArrayList<>());
                            Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                        }
                    } else {
                        setPendingRequest(new ArrayList<>());
                        Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                    }
                }

                @Override
                public void onFailure(Call<PendingRequestMainModel> call, Throwable t) {
                    progressDialog.dismiss();
                    setPendingRequest(new ArrayList<>());
                    Commons.showToast(mContext, getResources().getString(R.string.something_wants_wrong));
                }
            });
        } else {
            Commons.showToast(mContext, mContext.getResources().getString(R.string.no_internet_connection));
        }
    }

    private void setPendingRequest(ArrayList<PendingRequestMainModel.PendingRequestData> list) {
        if (list.isEmpty()) {
            mBinding.recyclerActivity.setVisibility(View.GONE);
        } else {
            pendingList = new ArrayList<>();
            pendingList = list;
            for (PendingRequestMainModel.PendingRequestData data : pendingList) {
                data.setRequestStatus("1");
            }
            requestAdapter = new ViewSentRequestAdapter(mContext, pendingList, (pos, data, flag) -> {
                if (flag.equals("details")) {
                    Intent intent = new Intent(mContext, EventDetailsActivity.class);
                    intent.putExtra("eventId", data.getEventId());
                    mContext.startActivity(intent);
                } else {
                    if (data.getRequestStatus() != null) {
                        if (data.getRequestStatus().equals("1")) {
                            //TODO : CancelRequest Call
                            cancelRequest(pos, data);
                        } else {
                            //TODO : Request api call
                            requestEvent(pos, data);
                        }
                    }
                }
            });
            mBinding.recyclerActivity.setVisibility(View.VISIBLE);
            mBinding.recyclerActivity.setAdapter(requestAdapter);
        }
    }

    private void requestEvent(int pos, PendingRequestMainModel.PendingRequestData data) {
        if (Commons.isOnline(mContext)) {
            progressDialog.show();
            HashMap<String, String> params = new HashMap<>();
            params.put("event_id", data.getEventId());

            String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");
            ApiClient.create().requestForLiveStream(header, params).enqueue(new Callback<CommonResponseModel>() {
                @Override
                public void onResponse(Call<CommonResponseModel> call, Response<CommonResponseModel> response) {
                    progressDialog.dismiss();
                    if (response.code() == 200 && response.isSuccessful()) {
                        if (response.body() != null) {
                            if (response.body().getStatus().equals("200")) {
                                if (!pendingList.isEmpty()) {
                                    pendingList.get(pos).setRequestStatus("1");
                                }
                                if (requestAdapter != null) {
                                    requestAdapter.notifyDataSetChanged();
                                }
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

    private void cancelRequest(int pos, PendingRequestMainModel.PendingRequestData data) {
        if (Commons.isOnline(mContext)) {
            progressDialog.show();
            HashMap<String, String> params = new HashMap<>();
            params.put("event_id", data.getEventId());
//                    params.put("status","0");

            String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");
            ApiClient.create().approveCancelRequest(header, params).enqueue(new Callback<CommonResponseModel>() {
                @Override
                public void onResponse(Call<CommonResponseModel> call, Response<CommonResponseModel> response) {
                    progressDialog.dismiss();
                    if (response.code() == 200 && response.isSuccessful()) {
                        if (response.body() != null) {
                            if (response.body().getStatus().equals("200")) {
                                if (!pendingList.isEmpty()) {
                                    pendingList.get(pos).setRequestStatus("0");
                                }
                                if (requestAdapter != null) {
                                    requestAdapter.notifyDataSetChanged();
                                }
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
}