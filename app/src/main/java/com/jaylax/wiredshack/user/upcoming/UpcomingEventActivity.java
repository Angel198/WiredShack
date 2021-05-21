package com.jaylax.wiredshack.user.upcoming;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.jaylax.wiredshack.ProgressDialog;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ActivityUpcomingEventBinding;
import com.jaylax.wiredshack.rest.ApiClient;
import com.jaylax.wiredshack.user.account.AccountFollowingEventAdapter;
import com.jaylax.wiredshack.user.account.FollowingEventMainModel;
import com.jaylax.wiredshack.user.eventDetails.EventDetailsActivity;
import com.jaylax.wiredshack.user.eventDetails.EventDetailsMainModel;
import com.jaylax.wiredshack.user.liveVideoPlayer.LiveVideoPlayerActivity;
import com.jaylax.wiredshack.utils.Commons;
import com.jaylax.wiredshack.utils.SharePref;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpcomingEventActivity extends AppCompatActivity {

    ActivityUpcomingEventBinding mBinding;
    Context mContext;
    ProgressDialog progressDialog;
    String eventId = "";
    String isRequested = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_upcoming_event);
        mContext = this;
        progressDialog = new ProgressDialog(mContext);
        if (getIntent().hasExtra("eventId")) {
            eventId = getIntent().getStringExtra("eventId");
            getEventDetails();
        }

        setClickListener();
    }

    private void setClickListener() {
        mBinding.imgBack.setOnClickListener(view -> onBackPressed());

        mBinding.relativeUpcomingEventLiveView.setOnClickListener(view -> {
            Intent intent = new Intent(this, LiveVideoPlayerActivity.class);
//                intent.putExtra("liveStream",eventData.getId()+"_"+eventData.getEventName());
            intent.putExtra("liveStream", eventId);
            intent.putExtra("isRequested", isRequested);
            startActivity(intent);
        });
    }

    private void getEventDetails() {
        if (Commons.isOnline(mContext)) {
            HashMap<String, String> params = new HashMap<>();
            params.put("event_id", eventId);

            String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");

            Call<EventDetailsMainModel> call = null;
            if (SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "").toString().isEmpty()) {
                call = ApiClient.create().getEventDetailsGuest(params);
            } else {
                call = ApiClient.create().getEventDetails(header, params);
            }

            if (call != null) {
                progressDialog.show();
                call.enqueue(new Callback<EventDetailsMainModel>() {
                    @Override
                    public void onResponse(Call<EventDetailsMainModel> call, Response<EventDetailsMainModel> response) {
                        progressDialog.dismiss();
                        getFollowingEvents();
                        if (response.code() == 200 && response.isSuccessful()) {
                            if (response.body() != null) {
                                if (response.body().getStatus().equals("200")) {
                                    setEventDetails(response.body().getData());
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
                        getFollowingEvents();
                        Commons.showToast(mContext, getResources().getString(R.string.something_wants_wrong));
                    }
                });
            }
        } else {
            Commons.showToast(mContext, getResources().getString(R.string.no_internet_connection));
        }
    }

    private void setEventDetails(EventDetailsMainModel.EventDetailsData eventData) {
        mBinding.tvUpcomingEventTitle.setText(eventData.getEventName());
        RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.place_holder).transform(new CenterCrop()).error(R.drawable.place_holder).priority(Priority.HIGH);
        String coverImage = "";
        if (eventData.getImages().isEmpty()) {
            coverImage = eventData.getCoverImage() == null ? "" : eventData.getCoverImage();
        } else {
            coverImage = eventData.getImages().get(0).getImages() == null ? "" : eventData.getImages().get(0).getImages();
        }
        Glide.with(this).load(coverImage).apply(options).into(mBinding.imgUpcomingEvent);
        /*if (isEventLive(eventData.getDate(), eventData.getStime(), eventData.getEtime())) {
            mBinding.relativeUpcomingEventNotLiveView.setVisibility(View.GONE);
            mBinding.relativeUpcomingEventLiveView.setVisibility(View.VISIBLE);
        } else {
            mBinding.relativeUpcomingEventNotLiveView.setVisibility(View.VISIBLE);
            mBinding.relativeUpcomingEventLiveView.setVisibility(View.GONE);
        }*/
        mBinding.relativeUpcomingEventLiveView.setVisibility(View.VISIBLE);
        String txtUpcoming = getResources().getString(R.string.live_stream) + "\n" + " on " + getEventDate(eventData.getDate()) + " " + getEventTime(eventData.getStime());
        mBinding.tvUpcomingEvent.setText(txtUpcoming);

        if (eventData.getIsRequest() == null) {
            mBinding.imgUpcomingEventVideo.setVisibility(View.GONE);
        } else {
            if (eventData.getIsRequest().equals("2")) {
                mBinding.imgUpcomingEventVideo.setVisibility(View.VISIBLE);
            } else {
                mBinding.imgUpcomingEventVideo.setVisibility(View.GONE);
            }
        }

        isRequested = eventData.getIsRequest() == null ? "": eventData.getIsRequest();
    }

    private void getFollowingEvents() {
        if (Commons.isOnline(mContext)) {
            progressDialog.show();
            String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");
            ApiClient.create().getUserFollowingEvents(header).enqueue(new Callback<FollowingEventMainModel>() {
                @Override
                public void onResponse(Call<FollowingEventMainModel> call, Response<FollowingEventMainModel> response) {
                    progressDialog.dismiss();
                    if (response.code() == 200 && response.isSuccessful()) {
                        if (response.body() != null) {
                            setFollowingEventData(response.body().getData());
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
                public void onFailure(Call<FollowingEventMainModel> call, Throwable t) {
                    progressDialog.dismiss();
                    Commons.showToast(mContext, getResources().getString(R.string.something_wants_wrong));
                }
            });
        } else {
            Commons.showToast(mContext, mContext.getResources().getString(R.string.no_internet_connection));
        }
    }

    private void setFollowingEventData(ArrayList<FollowingEventMainModel.FollowingEventData> list) {
        if (list.isEmpty()) {
            mBinding.linearUpcomingFollowingEvent.setVisibility(View.GONE);
        } else {
            mBinding.linearUpcomingFollowingEvent.setVisibility(View.VISIBLE);
            mBinding.recyclerUpcomingFollowingEvents.setLayoutManager(new GridLayoutManager(this, 3));
            mBinding.recyclerUpcomingFollowingEvents.setAdapter(new AccountFollowingEventAdapter(mContext, list, data -> {
                Intent intent = new Intent(mContext, EventDetailsActivity.class);
                intent.putExtra("eventId", data.getId());
                mContext.startActivity(intent);
            }));
        }
    }

    private String getEventDate(String dateEvent) {
        String eventDate = "N/A";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        if (dateEvent != null) {
            try {
                Date date = format.parse(dateEvent);
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

    private boolean isEventLive(String eventDate, String startTime, String endTime) {
        boolean isLive = false;
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

        if (eventDate != null && startTime != null && endTime != null) {
            String eventSTime = eventDate + " " + startTime;

            String eventETime = eventDate + " " + endTime;
            try {
                Date eventSDate = format.parse(eventSTime);
                Date eventEDate = format.parse(eventETime);
                if (currentDate.after(eventSDate) && currentDate.before(eventEDate)) {
                    isLive = true;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return isLive;
    }
}