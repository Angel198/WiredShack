package com.jaylax.wiredshack.user.upcoming;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.TimeUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.jaylax.wiredshack.ProgressDialog;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ActivityUpcomingEventBinding;
import com.jaylax.wiredshack.databinding.ActivityUpcomingEventNewBinding;
import com.jaylax.wiredshack.eventManager.editEvent.EventImageModel;
import com.jaylax.wiredshack.model.CommonResponseModel;
import com.jaylax.wiredshack.model.UserDetailsModel;
import com.jaylax.wiredshack.rest.ApiClient;
import com.jaylax.wiredshack.user.account.AccountUploadImageAdapter;
import com.jaylax.wiredshack.user.account.FollowingEventMainModel;
import com.jaylax.wiredshack.user.eventDetails.EventDetailsActivity;
import com.jaylax.wiredshack.user.eventDetails.EventDetailsImageAdapter;
import com.jaylax.wiredshack.user.eventDetails.EventDetailsMainModel;
import com.jaylax.wiredshack.user.home.EventRoomModel;
import com.jaylax.wiredshack.user.home.EventTokenModel;
import com.jaylax.wiredshack.user.home.UpcomingEventMainModel;
import com.jaylax.wiredshack.user.liveStream.LiveStreamActivity;
import com.jaylax.wiredshack.user.liveVideoPlayer.LiveVideoPlayerActivity;
import com.jaylax.wiredshack.utils.Commons;
import com.jaylax.wiredshack.utils.SharePref;
import com.jaylax.wiredshack.utils.SpannedGridLayoutManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpcomingEventActivity extends AppCompatActivity {

    //    ActivityUpcomingEventBinding mBinding;
    ActivityUpcomingEventNewBinding mBinding;
    Context mContext;
    ProgressDialog progressDialog;
    String eventId = "";
    String isRequested = "";
    String isFreeStream = "";
    UserDetailsModel userDetailsModel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_upcoming_event_new);
        mContext = this;
        progressDialog = new ProgressDialog(mContext);
        userDetailsModel = Commons.convertStringToObject(mContext, SharePref.PREF_USER, UserDetailsModel.class);
        if (userDetailsModel == null) {
            mBinding.imgAccountProfile.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.toplogo));
        } else {
            RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.place_holder).transform(new CenterCrop()).error(R.drawable.place_holder).priority(Priority.HIGH);
            Glide.with(mContext).load(userDetailsModel.getImage() == null ? "" : userDetailsModel.getImage()).apply(options).into(mBinding.imgAccountProfile);
        }
        if (getIntent().hasExtra("eventId")) {
            eventId = getIntent().getStringExtra("eventId");
            getEventDetails(true);
        }

        setClickListener();
    }

    private void setClickListener() {
        mBinding.imgBack.setOnClickListener(view -> onBackPressed());

        mBinding.relativeUpcomingEventLiveView.setOnClickListener(view -> {
            if (isRequested.equals("2")) {
                getEnableXRoomId();
            } else {
                if (isFreeStream.equals("0")) {
                    showRequestDialog();
                } else {
                    getEnableXRoomId();
                }
            }
        });
    }

    private void getEventDetails(boolean isFirst) {
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
                        Commons.showToast(mContext, getResources().getString(R.string.something_wants_wrong));
                    }
                });
            }
        } else {
            Commons.showToast(mContext, getResources().getString(R.string.no_internet_connection));
        }
    }

    private void setEventDetails(EventDetailsMainModel.EventDetailsData eventData) {
        mBinding.tvEventDateTitle.setText(getEventDateTitle(eventData.getDate()));
        mBinding.tvUpcomingEventTitle.setText(eventData.getEventName());
        RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.place_holder).transform(new CenterCrop()).error(R.drawable.place_holder).priority(Priority.HIGH);
        String coverImage = "";
        if (eventData.getImages().isEmpty()) {
            coverImage = eventData.getCoverImage() == null ? "" : eventData.getCoverImage();
        } else {
            coverImage = eventData.getImages().get(0).getImages() == null ? "" : eventData.getImages().get(0).getImages();
        }
        Glide.with(this).load(coverImage).apply(options).into(mBinding.imgUpcomingEvent);
        if (isEventLive(eventData.getDate(), eventData.getStime(), eventData.getEtime())) {
            mBinding.tvUpcomingEventTime.setVisibility(View.GONE);
            mBinding.linearEventDateTime.setVisibility(View.GONE);
            mBinding.relativeUpcomingEventLiveView.setVisibility(View.VISIBLE);
        } else {
            showEventLiveTime(eventData);
            mBinding.relativeUpcomingEventLiveView.setVisibility(View.GONE);
        }

        mBinding.tvEventDate.setText(mContext.getResources().getString(R.string.event_date, eventData.getDate() == null ? "N/A" : getEventDate(eventData.getDate())));
        mBinding.tvEventTime.setText(mContext.getResources().getString(R.string.event_time, getEventTime(eventData.getStime()), getEventTime(eventData.getEtime())));

        Glide.with(this).load(eventData.getImage() == null ? "" : eventData.getImage()).apply(options).into(mBinding.imgManagerProfile);
        mBinding.tvManagerName.setText(eventData.getName() == null ? "N/A" : eventData.getName());
        mBinding.tvEventDescription.setText(eventData.getDescription() == null ? "N/A" : eventData.getDescription());

        isRequested = eventData.getIsRequest() == null ? "" : eventData.getIsRequest();
        isFreeStream = eventData.getFreestream() == null ? "" : eventData.getFreestream();

        if (eventData.getImages().isEmpty()) {
            mBinding.recyclerEventImages.setVisibility(View.GONE);
        } else {
            ArrayList<EventImageModel> imageList = new ArrayList<>();

            for (EventDetailsMainModel.EventDetailsData.EventImage image : eventData.getImages()) {
                imageList.add(new EventImageModel(image.getImages() == null ? "" : image.getImages(), image.getId() == null ? "" : image.getId(), "", null));
            }
            RecyclerView.LayoutManager manager;
            if (imageList.size() > 1){
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
            mBinding.recyclerEventImages.setHasFixedSize(true);
            mBinding.recyclerEventImages.setLayoutManager(manager);
            mBinding.recyclerEventImages.setAdapter(new EventDetailsImageAdapter(mContext, imageList));
        }
    }

    private void showEventLiveTime(EventDetailsMainModel.EventDetailsData eventData) {

        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

        if (eventData.getDate() != null && eventData.getStime() != null) {
            String eventSTime = eventData.getDate() + " " + eventData.getStime();
            try {
                Date eventSDate = format.parse(eventSTime);
                final long[] different = {eventSDate.getTime() - currentDate.getTime()};
                long hoursInMilli = TimeUnit.MILLISECONDS.toHours(different[0]);
                long minutesInMilli = TimeUnit.MILLISECONDS.toHours(different[0]);

                Log.e("remaining Hours", String.valueOf(TimeUnit.MILLISECONDS.toHours(different[0])));
                Log.e("remaining Minutes", String.valueOf(TimeUnit.MILLISECONDS.toMinutes(different[0])));

                if (hoursInMilli >= 1) {
                    mBinding.tvUpcomingEventTime.setVisibility(View.GONE);
                    mBinding.linearEventDateTime.setVisibility(View.VISIBLE);
                } else {
                    mBinding.tvUpcomingEventTime.setVisibility(View.VISIBLE);
                    mBinding.linearEventDateTime.setVisibility(View.GONE);
                    new CountDownTimer(different[0], 1000) {
                        public void onTick(long millisUntilFinished) {
                            different[0] = millisUntilFinished;
                            String hms = String.format("%02d:%02d:%02d",
                                    TimeUnit.MILLISECONDS.toHours(different[0]),
                                    TimeUnit.MILLISECONDS.toMinutes(different[0]) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(different[0])), TimeUnit.MILLISECONDS.toSeconds(different[0]) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(different[0])));

                            mBinding.tvUpcomingEventTime.setText(hms);//set text
                        }

                        public void onFinish() {
                            mBinding.linearEventDateTime.setVisibility(View.GONE);
                            mBinding.tvUpcomingEventTime.setVisibility(View.GONE);
                            mBinding.relativeUpcomingEventLiveView.setVisibility(View.VISIBLE);
                        }
                    }.start();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void showRequestDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String msg = "";
        String positiveTxt = "";
        String negativeTxt = "";
        if (isRequested.equals("1")) {
            msg = getResources().getString(R.string.already_requested_msg);
            positiveTxt = getResources().getString(R.string.txt_ok);
            negativeTxt = "";
        } else {
            msg = getResources().getString(R.string.request_event_msg);
            positiveTxt = getResources().getString(R.string.txt_cancel);
            negativeTxt = getResources().getString(R.string.request);
        }

        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton(positiveTxt, (dialogInterface, i) -> {
            //TODO : Call stream_check Api
        });

        if (!isRequested.equals("1")) {
            builder.setNegativeButton(negativeTxt, (dialogInterface, i) -> {
                //TODO : RequestLiveStream and close thi activity
                requestStream();
            });
        }
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
    }

    private void requestStream() {
        if (Commons.isOnline(mContext)) {
            progressDialog.show();
            HashMap<String, String> params = new HashMap<>();
            params.put("event_id", eventId);

            String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");
            ApiClient.create().requestForLiveStream(header, params).enqueue(new Callback<CommonResponseModel>() {
                @Override
                public void onResponse(Call<CommonResponseModel> call, Response<CommonResponseModel> response) {
                    progressDialog.dismiss();
                    if (response.code() == 200 && response.isSuccessful()) {
                        if (response.body() != null) {
                            if (response.body().getStatus().equals("200")) {
                                getEventDetails(false);
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

    private String getEventDateTitle(String stEventDate) {
        String stLabel = "N/A";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        if (stEventDate != null) {
            try {
                Date date = format.parse(stEventDate);
                format = new SimpleDateFormat("dd MMMM yyyy", Locale.US);
                if (DateUtils.isToday(date.getTime())) {
                    stLabel = "Toady";
                } else if (DateUtils.isToday(date.getTime() - DateUtils.DAY_IN_MILLIS)) {
                    stLabel = "Tomorrow";
                } else {
                    stLabel = format.format(date);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return stLabel;
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

    private void getEnableXRoomId() {

        if (Commons.isOnline(mContext)) {
            progressDialog.show();
            HashMap<String, String> params = new HashMap<>();
            params.put("event_id", eventId);

            String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");
            ApiClient.create().getEnableXRoomId(header, params).enqueue(new Callback<EventRoomModel>() {
                @Override
                public void onResponse(Call<EventRoomModel> call, Response<EventRoomModel> response) {
                    progressDialog.dismiss();
                    if (response.code() == 200 && response.isSuccessful()) {
                        if (response.body() != null) {
                            if (response.body().getStatus().equals("200")) {
                                callGetTokenApi(response.body());
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
                public void onFailure(Call<EventRoomModel> call, Throwable t) {
                    progressDialog.dismiss();
                    Commons.showToast(mContext, getResources().getString(R.string.something_wants_wrong));
                }
            });
        } else {
            Commons.showToast(mContext, getResources().getString(R.string.no_internet_connection));
        }
    }

    private void callGetTokenApi(EventRoomModel body) {
        if (Commons.isOnline(mContext)) {
            progressDialog.show();
            HashMap<String, String> params = new HashMap<>();
            params.put("name", "name");
            params.put("role", "participant");
            params.put("roomId", body.getData());
            params.put("user_ref", "xdada");

            ApiClient.create().getEnableXToken(params).enqueue(new Callback<EventTokenModel>() {
                @Override
                public void onResponse(Call<EventTokenModel> call, Response<EventTokenModel> response) {
                    progressDialog.dismiss();
                    if (response.code() == 200 && response.isSuccessful()) {
                        if (response.body() != null) {
                            if (response.body().getToken() != null) {
                                Intent intent = new Intent(UpcomingEventActivity.this, LiveStreamActivity.class);
                                intent.putExtra("token", response.body().getToken());
                                intent.putExtra("name", "name");
                                intent.putExtra("streamId", eventId);
                                intent.putExtra("isRequested", isRequested);
                                startActivityForResult(intent,101);
                            }
                        }
                    } else {
                        Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                    }
                }

                @Override
                public void onFailure(Call<EventTokenModel> call, Throwable t) {
                    progressDialog.dismiss();
                    Commons.showToast(mContext, getResources().getString(R.string.something_wants_wrong));
                }
            });
        } else {
            Commons.showToast(mContext, getResources().getString(R.string.no_internet_connection));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (!isRequested.equals("2")) {
                getEventDetails(false);
            }
        }
    }
}