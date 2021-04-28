package com.jaylax.wiredshack.user.eventDetails;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.jaylax.wiredshack.LoginActivity;
import com.jaylax.wiredshack.ProgressDialog;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ActivityEventDetailsBinding;
import com.jaylax.wiredshack.eventManager.editEvent.EventImageModel;
import com.jaylax.wiredshack.eventManager.eventdetails.EventImagesAdapter;
import com.jaylax.wiredshack.model.CommonResponseModel;
import com.jaylax.wiredshack.rest.ApiClient;
import com.jaylax.wiredshack.utils.Commons;
import com.jaylax.wiredshack.utils.SharePref;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    int likeCount = 0, commentCount = 0;
    boolean isLike = false;
    boolean isFollow = false;

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

        mBinding.imgBack.setOnClickListener(view -> onBackPressed());
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
            }
        } else {
            Commons.showToast(mContext, getResources().getString(R.string.no_internet_connection));
        }
    }

    private void setEventDetails() {
        getCommentList(false);
        if (eventDetailsData != null) {
            RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.place_holder).transform(new CenterCrop()).error(R.drawable.place_holder).priority(Priority.HIGH);
            Glide.with(this).load(eventDetailsData.getImage() == null ? "" : eventDetailsData.getImage()).apply(options).into(mBinding.imgEventProfile);

            String coverImage = "";
            if (eventDetailsData.getImages().isEmpty()){
                coverImage = eventDetailsData.getCoverImage() == null ? "" : eventDetailsData.getCoverImage();
            }else {
                coverImage = eventDetailsData.getImages().get(0).getImages() == null? "": eventDetailsData.getImages().get(0).getImages();
            }

            Glide.with(this).load(coverImage).apply(options).into(mBinding.imgEventCover);

            mBinding.tvEventManagerName.setText(eventDetailsData.getName());

            if (eventDetailsData.getFollows() == null) {
                isFollow = false;
            } else {
                if (eventDetailsData.getFollows().equals("1")) {
                    isFollow = true;
                } else {
                    isFollow = false;
                }
            }
            setFollowUI();

            likeCount = Integer.parseInt(eventDetailsData.getLikesCount() == null ? "0" : eventDetailsData.getLikesCount());
            commentCount = Integer.parseInt(eventDetailsData.getCommnets() == null ? "0" : eventDetailsData.getCommnets());
            if (eventDetailsData.getLikes() == null) {
                isLike = false;
            } else {
                if (eventDetailsData.getLikes().equals("1")) {
                    isLike = true;
                } else {
                    isLike = false;
                }
            }
            setLikeCommentCount();

//            String eventName = eventDetailsData.getDate() == null ? "N/A" : getEventDate() + " " + eventDetailsData.getEventName();
            mBinding.tvEventName.setText(eventDetailsData.getEventName());
            if (eventDetailsData.getImages().isEmpty()) {
                mBinding.recyclerEventImages.setVisibility(View.GONE);
            } else {
                ArrayList<EventImageModel> imageList = new ArrayList<>();

                for (EventDetailsMainModel.EventDetailsData.EventImage image : eventDetailsData.getImages()) {
                    imageList.add(new EventImageModel(image.getImages() == null ? "" : image.getImages(), image.getId() == null ? "" : image.getId(), "", null));
                }
                mBinding.recyclerEventImages.setVisibility(View.VISIBLE);
                mBinding.recyclerEventImages.setLayoutManager(new GridLayoutManager(this, 4));
                mBinding.recyclerEventImages.setAdapter(new EventImagesAdapter(mContext, false, imageList));
            }
            mBinding.tvEventDescription.setText(eventDetailsData.getDescription() == null ? "N/A" : eventDetailsData.getDescription());
            mBinding.tvEventDate.setText(mContext.getResources().getString(R.string.event_date, eventDetailsData.getDate() == null ? "N/A" : getEventDate()));
            mBinding.tvEventTime.setText(mContext.getResources().getString(R.string.event_time, getEventTime(eventDetailsData.getStime()), getEventTime(eventDetailsData.getEtime())));
            mBinding.tvEventLocation.setText(mContext.getResources().getString(R.string.event_location, eventDetailsData.getLocation() == null ? "N/A" : eventDetailsData.getLocation()));

        }
    }

    private void getCommentList(boolean isRefresh) {
        if (Commons.isOnline(mContext)) {
            if (!isRefresh) {
                progressDialog.show();
            }
            HashMap<String, String> params = new HashMap<>();
            params.put("event_id", eventId);

            String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");

            ApiClient.create().getEventComments(header, params).enqueue(new Callback<EventCommentMainModel>() {
                @Override
                public void onResponse(Call<EventCommentMainModel> call, Response<EventCommentMainModel> response) {
                    if (!isRefresh) {
                        progressDialog.dismiss();
                    }
                    if (response.code() == 200 && response.isSuccessful()) {
                        if (response.body() != null) {
                            setEventComment(response.body().getData(), isRefresh);
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
                public void onFailure(Call<EventCommentMainModel> call, Throwable t) {
                    if (!isRefresh) {
                        progressDialog.dismiss();
                    }
                    Commons.showToast(mContext, getResources().getString(R.string.something_wants_wrong));
                }
            });
        } else {
            Commons.showToast(mContext, getResources().getString(R.string.no_internet_connection));
        }
    }

    private void setEventComment(ArrayList<EventCommentMainModel.EventCommentData> list, boolean isRefresh) {
        if (list.isEmpty()) {
            mBinding.recyclerEventComment.setVisibility(View.GONE);
        } else {
            mBinding.recyclerEventComment.setVisibility(View.VISIBLE);
            mBinding.recyclerEventComment.setLayoutManager(new LinearLayoutManager(this));
            mBinding.recyclerEventComment.setAdapter(new EventCommentAdapter(mContext, list));

            if (isRefresh) {
//                mBinding.nestedScrollEvent.scrollTo(0, mBinding.nestedScrollEvent.getBottom());
                mBinding.nestedScrollEvent.post(new Runnable() {
                    @Override
                    public void run() {
                        mBinding.nestedScrollEvent.fullScroll(mBinding.nestedScrollEvent.FOCUS_DOWN);
                    }
                });
            }
        }

    }

    private void setClickListener() {
        mBinding.imgEventCommentSend.setOnClickListener(view -> {
            if (mBinding.editEventComment.getText().toString().trim().isEmpty()) {
                Commons.showToast(mContext, getResources().getString(R.string.enter_comment));
            } else {
                if (SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "").toString().isEmpty()) {
                    openLogin();
                } else {
                    if (Commons.isOnline(mContext)) {
//                        progressDialog.show();
                        HashMap<String, String> params = new HashMap<>();
                        params.put("event_id", eventId);
                        params.put("comment", mBinding.editEventComment.getText().toString().trim());

                        String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");
                        ApiClient.create().sendComment(header, params).enqueue(new Callback<CommonResponseModel>() {
                            @Override
                            public void onResponse(Call<CommonResponseModel> call, Response<CommonResponseModel> response) {
//                                progressDialog.dismiss();
                                if (response.code() == 200 && response.isSuccessful()) {
                                    if (response.body() != null) {
                                        if (response.body().getStatus().equals("200")) {
                                            mBinding.editEventComment.setText("");
                                            commentCount = commentCount + 1;
                                            hideSoftKeyboard();
                                            setLikeCommentCount();
                                            getCommentList(true);
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
//                                progressDialog.dismiss();
                                Commons.showToast(mContext, getResources().getString(R.string.something_wants_wrong));
                            }
                        });
                    } else {
                        Commons.showToast(mContext, getResources().getString(R.string.no_internet_connection));
                    }
                }
            }
        });

        mBinding.tvEventLike.setOnClickListener(view -> {
            if (SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "").toString().isEmpty()) {
                openLogin();
            } else {
                if (Commons.isOnline(mContext)) {
//                    progressDialog.show();
                    HashMap<String, String> params = new HashMap<>();
                    params.put("event_id", eventId);

                    String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");
                    ApiClient.create().likeEvent(header, params).enqueue(new Callback<CommonResponseModel>() {
                        @Override
                        public void onResponse(Call<CommonResponseModel> call, Response<CommonResponseModel> response) {
//                            progressDialog.dismiss();
                            if (response.code() == 200 && response.isSuccessful()) {
                                if (response.body() != null) {
                                    if (response.body().getStatus().equals("200")) {
                                        if (isLike) {
                                            isLike = false;
                                            likeCount = likeCount - 1;
                                        } else {
                                            isLike = true;
                                            likeCount = likeCount + 1;
                                        }
                                        setLikeCommentCount();
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
        });

        mBinding.tvFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "").toString().isEmpty()) {
                    openLogin();
                } else {
                    if (Commons.isOnline(mContext)) {
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
                                        if (response.body().getStatus().equals("200")) {
                                            if (isFollow) {
                                                isFollow = false;
                                            } else {
                                                isFollow = true;
                                            }
                                            setFollowUI();
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
        });
    }

    private void setFollowUI() {
        if (isFollow) {
            mBinding.tvFollow.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.back_border_white));
            mBinding.tvFollow.setText(mContext.getResources().getString(R.string.unfollow));
        } else {
            mBinding.tvFollow.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.back_follow));
            mBinding.tvFollow.setText(mContext.getResources().getString(R.string.follow));
        }
    }

    private void setLikeCommentCount() {
        mBinding.tvEventLike.setText(String.valueOf(likeCount));
        mBinding.tvEventComment.setText(String.valueOf(commentCount));

        if (isLike) {
            for (Drawable drawable : mBinding.tvEventLike.getCompoundDrawables()) {
                if (drawable != null) {
                    drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(mBinding.tvEventLike.getContext(), R.color.colorOptionText), PorterDuff.Mode.SRC_IN));
                }
            }
        } else {
            for (Drawable drawable : mBinding.tvEventLike.getCompoundDrawables()) {
                if (drawable != null) {
                    drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(mBinding.tvEventLike.getContext(), R.color.white), PorterDuff.Mode.SRC_IN));
                }
            }
        }
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

    private void openLogin() {
        Intent intent = new Intent(mContext, LoginActivity.class);
        startActivity(intent);
    }

    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) this.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                this.getCurrentFocus().getWindowToken(),
                0
        );
    }
}