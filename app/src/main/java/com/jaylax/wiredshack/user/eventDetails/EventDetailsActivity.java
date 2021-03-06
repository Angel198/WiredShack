package com.jaylax.wiredshack.user.eventDetails;

import android.app.Activity;
import android.content.Context;
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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jaylax.wiredshack.ProgressDialog;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ActivityEventDetailsBinding;
import com.jaylax.wiredshack.eventManager.editEvent.EventImageModel;
import com.jaylax.wiredshack.eventManager.editEvent.EventImagesAdapter;
import com.jaylax.wiredshack.model.CommonResponseModel;
import com.jaylax.wiredshack.model.UserDetailsModel;
import com.jaylax.wiredshack.rest.ApiClient;
import com.jaylax.wiredshack.utils.AppMapView;
import com.jaylax.wiredshack.utils.Commons;
import com.jaylax.wiredshack.utils.SharePref;
import com.jaylax.wiredshack.utils.SpannedGridLayoutManager;

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
public class EventDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    ActivityEventDetailsBinding mBinding;
    Context mContext;
    ProgressDialog progressDialog;
    String eventId = "";
    EventDetailsMainModel.EventDetailsData eventDetailsData = null;
    int likeCount = 0, commentCount = 0;
    boolean isLike = false;
    boolean isFollow = false;
    String requestFlag = "";

    ArrayList<EventCommentMainModel.EventCommentData> commentList = new ArrayList<>();
    EventCommentAdapter commentAdapter = null;

    UserDetailsModel userDetailsModel = null;
    private GoogleMap mMap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_event_details);

        mContext = this;
        progressDialog = new ProgressDialog(mContext);
        if (!SharePref.getInstance(mContext).get(SharePref.PREF_USER, "").toString().isEmpty()) {
            userDetailsModel = Commons.convertStringToObject(mContext, SharePref.PREF_USER, UserDetailsModel.class);
        }

        if (userDetailsModel == null) {
            mBinding.imgAccountProfile.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.wiredshack));
        } else {
            RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.place_holder).transform(new CenterCrop()).error(R.drawable.place_holder).priority(Priority.HIGH);
            Glide.with(this).load(userDetailsModel.getImage() == null ? "" : userDetailsModel.getImage()).apply(options).into(mBinding.imgAccountProfile);
        }

        AppMapView mapFragment = (AppMapView) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.setListener(() -> {
            mBinding.nestedScrollEvent.requestDisallowInterceptTouchEvent(true);
        });

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

            String coverImage = eventDetailsData.getCoverImage() == null ? "" : eventDetailsData.getCoverImage();
            Glide.with(this).load(coverImage).apply(options).into(mBinding.imgCoverPhoto);

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

            requestFlag = eventDetailsData.getIsRequest() == null ? "" : eventDetailsData.getIsRequest();
            setRequestUI();

            mBinding.tvEventName.setText(eventDetailsData.getEventName());
            if (eventDetailsData.getImages().isEmpty()) {
                mBinding.recyclerEventImages.setVisibility(View.GONE);
            } else {
                ArrayList<EventImageModel> imageList = new ArrayList<>();

                for (EventDetailsMainModel.EventDetailsData.EventImage image : eventDetailsData.getImages()) {
                    imageList.add(new EventImageModel(image.getImages() == null ? "" : image.getImages(), image.getId() == null ? "" : image.getId(), "", null));
                }
                RecyclerView.LayoutManager manager;
                if (imageList.size() > 1) {
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
                } else {
                    manager = new GridLayoutManager(mContext, 2);
                }
                mBinding.recyclerEventImages.setHasFixedSize(true);
                mBinding.recyclerEventImages.setLayoutManager(manager);
                mBinding.recyclerEventImages.setAdapter(new EventDetailsImageAdapter(mContext, imageList));
            }
            if (eventDetailsData.getSelectedManager() == null) {
                mBinding.linearSelectManager.setVisibility(View.GONE);
            } else {
                mBinding.linearSelectManager.setVisibility(View.VISIBLE);
                if (eventDetailsData.getSelectedManager().getUserType() == null) {
                    mBinding.tvSelectManagerTitle.setText(mContext.getResources().getString(R.string.club));
                } else {
                    if (eventDetailsData.getSelectedManager().getUserType().equals("2")) {
                        mBinding.tvSelectManagerTitle.setText(mContext.getResources().getString(R.string.club));
                    } else {
                        mBinding.tvSelectManagerTitle.setText(mContext.getResources().getString(R.string.dj));
                    }
                }
                String imageURL = eventDetailsData.getSelectedManager().getImage() == null ? "" : eventDetailsData.getSelectedManager().getImage();
                Glide.with(mContext).load(imageURL).apply(options).into(mBinding.imgSelectManagerProfile);

                mBinding.tvSelectManagerName.setText(eventDetailsData.getSelectedManager().getName() == null ? "" : eventDetailsData.getSelectedManager().getName());
            }


            mBinding.tvEventDescription.setText(eventDetailsData.getDescription() == null ? "N/A" : eventDetailsData.getDescription());

            mBinding.tvEventDate.setText(mContext.getResources().getString(R.string.event_date, eventDetailsData.getDate() == null ? "N/A" : getEventDate()));
            mBinding.tvEventTime.setText(mContext.getResources().getString(R.string.event_time, getEventTime(eventDetailsData.getStime()), getEventTime(eventDetailsData.getEtime())));
//            mBinding.tvEventLocation.setText(mContext.getResources().getString(R.string.event_location, eventDetailsData.getLocation() == null ? "N/A" : eventDetailsData.getLocation()));
            mBinding.tvEventLocation.setText(eventDetailsData.getLocation() == null ? "N/A" : eventDetailsData.getLocation());

            if (mMap != null) {
                if (eventDetailsData.getLatitude() != null && eventDetailsData.getLongitude() != null) {
                    LatLng latLng = new LatLng(Double.parseDouble(eventDetailsData.getLatitude()), Double.parseDouble(eventDetailsData.getLongitude()));
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                            .position(latLng));
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(17f).build();
                    if (mMap != null) {
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                }
            }
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
            commentList = new ArrayList<>();
            commentList = list;
            if (userDetailsModel != null) {
                for (EventCommentMainModel.EventCommentData commentData : commentList) {
                    if (userDetailsModel.getId().equals(commentData.getUserId())) {
                        commentData.setLoginUser(true);
                    } else {
                        commentData.setLoginUser(false);
                    }
                }
            }

            commentAdapter = new EventCommentAdapter(mContext, commentList, (pos, data) -> {
                deleteComment(pos, data);
            });
            mBinding.recyclerEventComment.setVisibility(View.VISIBLE);
            mBinding.recyclerEventComment.setLayoutManager(new LinearLayoutManager(this));
            mBinding.recyclerEventComment.setAdapter(commentAdapter);

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

    private void deleteComment(int pos, EventCommentMainModel.EventCommentData data) {
        if (Commons.isOnline(mContext)) {
            progressDialog.show();
            HashMap<String, String> params = new HashMap<>();
            params.put("id", data.getId());

            String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");
            ApiClient.create().deleteComment(header, params).enqueue(new Callback<CommonResponseModel>() {
                @Override
                public void onResponse(Call<CommonResponseModel> call, Response<CommonResponseModel> response) {
                    progressDialog.dismiss();
                    if (response.code() == 200 && response.isSuccessful()) {
                        if (response.body() != null) {
                            if (response.body().getStatus().equals("200")) {
                                if (!commentList.isEmpty()) {
                                    commentList.remove(pos);
                                }
                                if (commentAdapter != null) {
                                    commentAdapter.notifyItemRemoved(pos);
                                    commentAdapter.notifyDataSetChanged();
                                }
                                commentCount = commentCount - 1;
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
                    progressDialog.dismiss();
                    Commons.showToast(mContext, getResources().getString(R.string.something_wants_wrong));
                }
            });
        } else {
            Commons.showToast(mContext, getResources().getString(R.string.no_internet_connection));
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

                        params.put("manager_id", eventDetailsData.getCreatedBy());

                        String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");
                        ApiClient.create().followManager(header, params).enqueue(new Callback<CommonResponseModel>() {
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

        mBinding.tvEventRequest.setOnClickListener(view -> {
            if (SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "").toString().isEmpty()) {
                openLogin();
            } else {
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
                                        requestFlag = "1";
                                        setRequestUI();
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
        });

        mBinding.tvEventCancelRequest.setOnClickListener(view -> {
            if (SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "").toString().isEmpty()) {
                openLogin();
            } else {
                if (Commons.isOnline(mContext)) {
                    progressDialog.show();
                    HashMap<String, String> params = new HashMap<>();
                    params.put("event_id", eventId);
//                    params.put("status","0");

                    String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");
                    ApiClient.create().approveCancelRequest(header, params).enqueue(new Callback<CommonResponseModel>() {
                        @Override
                        public void onResponse(Call<CommonResponseModel> call, Response<CommonResponseModel> response) {
                            progressDialog.dismiss();
                            if (response.code() == 200 && response.isSuccessful()) {
                                if (response.body() != null) {
                                    if (response.body().getStatus().equals("200")) {
                                        requestFlag = "0";
                                        setRequestUI();
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
        });
    }

    private void setFollowUI() {
        if (isFollow) {
            mBinding.tvFollow.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.back_border_white));
            mBinding.tvFollow.setText(mContext.getResources().getString(R.string.following));
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

    private void setRequestUI() {
        if (requestFlag.equals("1")) {
            mBinding.tvEventRequest.setVisibility(View.GONE);
            mBinding.tvEventCancelRequest.setVisibility(View.VISIBLE);
            mBinding.tvEventRequestAccepted.setVisibility(View.GONE);
        } else if (requestFlag.equals("2")) {
            mBinding.tvEventRequest.setVisibility(View.GONE);
            mBinding.tvEventCancelRequest.setVisibility(View.GONE);
            mBinding.tvEventRequestAccepted.setVisibility(View.VISIBLE);
        } else {
            mBinding.tvEventRequest.setVisibility(View.VISIBLE);
            mBinding.tvEventCancelRequest.setVisibility(View.GONE);
            mBinding.tvEventRequestAccepted.setVisibility(View.GONE);
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
        Commons.openLoginScree(mContext);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
    }
}