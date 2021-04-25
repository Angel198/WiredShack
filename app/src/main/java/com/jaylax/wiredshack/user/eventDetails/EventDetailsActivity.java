package com.jaylax.wiredshack.user.eventDetails;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.jaylax.wiredshack.ProgressDialog;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ActivityEventDetailsBinding;
import com.jaylax.wiredshack.model.CommonResponseModel;
import com.jaylax.wiredshack.model.UserDetailsModel;
import com.jaylax.wiredshack.rest.ApiClient;
import com.jaylax.wiredshack.utils.Commons;
import com.jaylax.wiredshack.utils.SharePref;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("All")
public class EventDetailsActivity extends AppCompatActivity {

    ActivityEventDetailsBinding mBinding;
    Context mContext;
    ProgressDialog progressDialog;
    String eventId = "";
    EventDetailsMainModel.EventDetailsData eventDetailsData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_event_details);

        mContext = this;
        progressDialog = new ProgressDialog(mContext);

        if (getIntent().hasExtra("eventId")) {
            eventId = getIntent().getStringExtra("eventId");
            getEventDetails();
        }

        setClickListener();

        mBinding.recyclerEventComment.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recyclerEventComment.setAdapter(new EventCommentAdapter());
        mBinding.imgBack.setOnClickListener(view -> onBackPressed());
    }

    private void getEventDetails() {
        if (Commons.isOnline(mContext)) {
            progressDialog.show();
            HashMap<String, String> params = new HashMap<>();
            params.put("event_id", eventId);

            String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");
            ApiClient.create().getEventDetails(header, params).enqueue(new Callback<EventDetailsMainModel>() {
                @Override
                public void onResponse(Call<EventDetailsMainModel> call, Response<EventDetailsMainModel> response) {
                    progressDialog.dismiss();
                    if (response.code() == 200 && response.isSuccessful()) {
                        if (response.body() != null) {
                            if (response.body().getStatus().equals("200")) {
                                eventDetailsData = response.body().getData();
                                setEventDetails();
                            } else {
                                Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                            }
                        }
                    } else {
                        Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                    }
                }

                @Override
                public void onFailure(Call<EventDetailsMainModel> call, Throwable t) {
                    progressDialog.dismiss();
                    Commons.showToast(mContext, getResources().getString(R.string.something_wants_wrong));
                }
            });
        } else {
            Commons.showToast(mContext, getResources().getString(R.string.no_internet_connection));
        }
    }

    private void setEventDetails() {
        if (eventDetailsData != null) {
            RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.place_holder).transform(new CenterCrop()).error(R.drawable.place_holder).priority(Priority.HIGH);
            Glide.with(this).load(eventDetailsData.getImage() == null ? "" : eventDetailsData.getImage()).apply(options).into(mBinding.imgEventProfile);
            Glide.with(this).load(eventDetailsData.getCoverImage() == null ? "" : eventDetailsData.getCoverImage()).apply(options).into(mBinding.imgEventCover);

            mBinding.tvEventManagerName.setText(eventDetailsData.getName());
            mBinding.tvEventLike.setText(eventDetailsData.getLikes());
            mBinding.tvEventComment.setText(eventDetailsData.getCommnets());
            String eventName = eventDetailsData.getDate() == null ? "N/A" : getEventDate() + " " + eventDetailsData.getEventName();
            mBinding.tvEventDateName.setText(eventName);
            mBinding.tvEventDescription.setText(eventDetailsData.getDescription() == null ? "N/A" : eventDetailsData.getDescription());
            mBinding.tvEventTime.setText(mContext.getResources().getString(R.string.event_time, getEventTime(eventDetailsData.getStime()), getEventTime(eventDetailsData.getEtime())));
            mBinding.tvEventLocation.setText(mContext.getResources().getString(R.string.event_location, eventDetailsData.getLocation() == null ? "N/A" : eventDetailsData.getLocation()));
        }
    }

    private void setClickListener() {
        mBinding.imgEventCommentSend.setOnClickListener(view -> {
            if (mBinding.editEventComment.getText().toString().trim().isEmpty()) {
                Commons.showToast(mContext, getResources().getString(R.string.enter_comment));
            } else {
                if (Commons.isOnline(mContext)) {
                    if (!SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "").toString().isEmpty()) {
                        progressDialog.show();
                        HashMap<String, String> params = new HashMap<>();
                        params.put("event_id", eventId);
                        params.put("comment", mBinding.editEventComment.getText().toString().trim());

                        String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");
                        ApiClient.create().sendComment(header, params).enqueue(new Callback<CommonResponseModel>() {
                            @Override
                            public void onResponse(Call<CommonResponseModel> call, Response<CommonResponseModel> response) {
                                progressDialog.dismiss();
                                if (response.code() == 200 && response.isSuccessful()) {
                                    if (response.body() != null) {
                                        if (response.body().getStatus().equals("200")) {
                                            mBinding.editEventComment.setText("");
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
                    }
                } else {
                    Commons.showToast(mContext, getResources().getString(R.string.no_internet_connection));
                }
            }
        });

        mBinding.tvEventLike.setOnClickListener(view -> {
            if (Commons.isOnline(mContext)) {
                if (!SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "").toString().isEmpty()) {
                    progressDialog.show();
                    HashMap<String, String> params = new HashMap<>();
                    params.put("event_id", eventId);

                    String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");
                    ApiClient.create().likeEvent(header, params).enqueue(new Callback<CommonResponseModel>() {
                        @Override
                        public void onResponse(Call<CommonResponseModel> call, Response<CommonResponseModel> response) {
                            progressDialog.dismiss();
                            if (response.code() == 200 && response.isSuccessful()) {
                                if (response.body() != null) {
                                    if (!response.body().getStatus().equals("200")) {
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
                }
            } else {
                Commons.showToast(mContext, getResources().getString(R.string.no_internet_connection));
            }
        });

        mBinding.tvFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Commons.isOnline(mContext)) {
                    if (!SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "").toString().isEmpty()) {
                        progressDialog.show();
                        HashMap<String, String> params = new HashMap<>();
                        params.put("event_id", eventId);

                        String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");
                        ApiClient.create().followEventManager(header, params).enqueue(new Callback<CommonResponseModel>() {
                            @Override
                            public void onResponse(Call<CommonResponseModel> call, Response<CommonResponseModel> response) {
                                progressDialog.dismiss();
                                if (response.code() == 200 && response.isSuccessful()) {
                                    if (response.body() != null) {
                                        if (!response.body().getStatus().equals("200")) {
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
                    }
                } else {
                    Commons.showToast(mContext, getResources().getString(R.string.no_internet_connection));
                }
            }
        });
    }

    private String getEventDate() {
        String eventDate = "N/A";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        if (eventDetailsData.getDate() != null) {
            try {
                Date date = format.parse(eventDetailsData.getDate());
                format = new SimpleDateFormat("dd MMMM yyyy", Locale.US);
                eventDate = format.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return eventDate;
    }

    private String getEventTime(String time) {
        String eventTime = "N/A";
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.US);
        if (time != null) {
            try {
                Date date = format.parse(time);
                format = new SimpleDateFormat("hh:mm a", Locale.US);
                eventTime = format.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return eventTime;
    }
}