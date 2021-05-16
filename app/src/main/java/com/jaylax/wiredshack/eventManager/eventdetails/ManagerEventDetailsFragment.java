package com.jaylax.wiredshack.eventManager.eventdetails;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.jaylax.wiredshack.ProgressDialog;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.FragmentManagerEventDetailsBinding;
import com.jaylax.wiredshack.eventManager.editEvent.EventImageModel;
import com.jaylax.wiredshack.eventManager.editEvent.ManagerEditEventActivity;
import com.jaylax.wiredshack.eventManager.liveVideoBroadcaster.LiveVideoBroadcasterActivity;
import com.jaylax.wiredshack.model.RecentEventMainModel;
import com.jaylax.wiredshack.model.UserDetailsModel;
import com.jaylax.wiredshack.rest.ApiClient;
import com.jaylax.wiredshack.user.eventDetails.EventCommentAdapter;
import com.jaylax.wiredshack.user.eventDetails.EventCommentMainModel;
import com.jaylax.wiredshack.user.eventDetails.EventDetailsMainModel;
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

public class ManagerEventDetailsFragment extends Fragment {

    FragmentManagerEventDetailsBinding mBinding;
    Context mContext;
    UserDetailsModel userDetailsModel;
    ProgressDialog progressDialog;
    String editEventID = "";
    EventDetailsMainModel.EventDetailsData eventDetailsData = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.fragment_manager_event_details, container, false);
        mContext = getActivity();
        progressDialog = new ProgressDialog(mContext);
        userDetailsModel = Commons.convertStringToObject(mContext, SharePref.PREF_USER, UserDetailsModel.class);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            editEventID = bundle.getString("eventId");
            Log.e("EditEventId", editEventID);
        }

        setCLickListener();

        return mBinding.getRoot();
    }

    private void setCLickListener() {
        mBinding.tvEventEdit.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), ManagerEditEventActivity.class);
            intent.putExtra("eventId", eventDetailsData.getId());
            getActivity().startActivity(intent);
        });

        mBinding.tvEventLiveNow.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), LiveVideoBroadcasterActivity.class);
//            intent.putExtra("eventStream", eventDetailsData.getId() + "_"+eventDetailsData.getEventName());
            intent.putExtra("eventStream", eventDetailsData.getId());
            getActivity().startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (editEventID.isEmpty()) {
            getRecentEvents();
        } else {
            getEventDetails(editEventID);
        }
    }

    private void getRecentEvents() {
        if (Commons.isOnline(mContext)) {
            progressDialog.show();
            String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");
            ApiClient.create().getRecentEventsManager(header).enqueue(new Callback<RecentEventMainModel>() {
                @Override
                public void onResponse(Call<RecentEventMainModel> call, Response<RecentEventMainModel> response) {
                    progressDialog.dismiss();
                    if (response.code() == 200 && response.isSuccessful()) {
                        if (response.body() != null) {
                            setRecentEventData(response.body().getData());
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
                public void onFailure(Call<RecentEventMainModel> call, Throwable t) {
                    progressDialog.dismiss();
                    Commons.showToast(mContext, getResources().getString(R.string.something_wants_wrong));
                }
            });
        } else {
            Commons.showToast(mContext, mContext.getResources().getString(R.string.no_internet_connection));
        }
    }

    private void setRecentEventData(ArrayList<RecentEventMainModel.RecentEventData> list) {
        if (list.isEmpty()) {
            mBinding.linearEventMain.setVisibility(View.GONE);
            mBinding.tvNoEventFound.setVisibility(View.VISIBLE);
        } else {
            getEventDetails(list.get(0).getId());
        }
    }

    private void getEventDetails(String eventID) {
        if (Commons.isOnline(mContext)) {
            HashMap<String, String> params = new HashMap<>();
            params.put("event_id", eventID);

            String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");
            progressDialog.show();
            ApiClient.create().getEventDetails(header, params).enqueue(new Callback<EventDetailsMainModel>() {
                @Override
                public void onResponse(Call<EventDetailsMainModel> call, Response<EventDetailsMainModel> response) {
                    progressDialog.dismiss();
                    if (response.code() == 200 && response.isSuccessful()) {
                        if (response.body() != null) {
                            if (response.body().getStatus().equals("200")) {
                                eventDetailsData = response.body().getData();
                            } else {
                                Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                            }
                        }
                    } else {
                        Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                    }
                    setEventDetails();
                }

                @Override
                public void onFailure(Call<EventDetailsMainModel> call, Throwable t) {
                    progressDialog.dismiss();
                    Commons.showToast(mContext, getResources().getString(R.string.something_wants_wrong));
                    setEventDetails();
                }
            });
        } else {
            Commons.showToast(mContext, getResources().getString(R.string.no_internet_connection));
        }
    }

    private void setEventDetails() {
        if (eventDetailsData == null) {
            mBinding.linearEventMain.setVisibility(View.GONE);
            mBinding.tvNoEventFound.setVisibility(View.VISIBLE);
        } else {
            getCommentList();
            mBinding.linearEventMain.setVisibility(View.VISIBLE);
            mBinding.tvNoEventFound.setVisibility(View.GONE);

            RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.place_holder).transform(new CenterCrop()).error(R.drawable.place_holder).priority(Priority.HIGH);
            Glide.with(this).load(eventDetailsData.getImage() == null ? "" : eventDetailsData.getImage()).apply(options).into(mBinding.imgEventProfile);

            String coverImage = "";
            if (eventDetailsData.getImages().isEmpty()) {
                coverImage = eventDetailsData.getCoverImage() == null ? "" : eventDetailsData.getCoverImage();
            } else {
                coverImage = eventDetailsData.getImages().get(0).getImages() == null ? "" : eventDetailsData.getImages().get(0).getImages();
            }

            Glide.with(this).load(coverImage).apply(options).into(mBinding.imgEventCover);

            mBinding.tvEventManagerName.setText(eventDetailsData.getName());
            mBinding.tvEventLike.setText(eventDetailsData.getLikesCount() == null ? "0" : eventDetailsData.getLikesCount());
            mBinding.tvEventComment.setText(eventDetailsData.getCommnets() == null ? "0" : eventDetailsData.getCommnets());


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
                mBinding.recyclerEventImages.setLayoutManager(new GridLayoutManager(mContext, 4));
                mBinding.recyclerEventImages.setAdapter(new EventImagesAdapter(mContext, false, imageList));
            }
            mBinding.tvEventDescription.setText(eventDetailsData.getDescription() == null ? "N/A" : eventDetailsData.getDescription());
            mBinding.tvEventDate.setText(mContext.getResources().getString(R.string.event_date, eventDetailsData.getDate() == null ? "N/A" : getEventDate()));

            if (eventDetailsData.getSelectedManager() == null){
                mBinding.linearSelectManager.setVisibility(View.GONE);
            }else {
                mBinding.linearSelectManager.setVisibility(View.VISIBLE);

                if (eventDetailsData.getSelectedManager().getUserType() == null){
                    mBinding.tvSelectManagerTitle.setText(mContext.getResources().getString(R.string.organiser));
                }else {
                    if (eventDetailsData.getSelectedManager().getUserType().equals("2")){
                        mBinding.tvSelectManagerTitle.setText(mContext.getResources().getString(R.string.event_organiser));
                    }else {
                        mBinding.tvSelectManagerTitle.setText(mContext.getResources().getString(R.string.dj_organiser));
                    }
                }

                String imageURL = eventDetailsData.getSelectedManager().getImage() == null ? "" : eventDetailsData.getSelectedManager().getImage() ;
                Glide.with(mContext).load(imageURL).apply(options).into(mBinding.imgSelectManagerProfile);
                mBinding.tvSelectManagerName.setText(eventDetailsData.getSelectedManager().getName() == null ? "" : eventDetailsData.getSelectedManager().getName());
            }

            mBinding.tvEventTime.setText(mContext.getResources().getString(R.string.event_time, getEventTime(eventDetailsData.getStime()), getEventTime(eventDetailsData.getEtime())));
            mBinding.tvEventLocation.setText(mContext.getResources().getString(R.string.event_location, eventDetailsData.getLocation() == null ? "N/A" : eventDetailsData.getLocation()));

            if (isEventLive()) {
                mBinding.tvEventLiveNow.setVisibility(View.VISIBLE);
                mBinding.tvEventEdit.setVisibility(View.GONE);
            } else if (isEventClose()){
                mBinding.tvEventLiveNow.setVisibility(View.GONE);
                mBinding.tvEventEdit.setVisibility(View.GONE);
            }else {
                mBinding.tvEventLiveNow.setVisibility(View.GONE);
                mBinding.tvEventEdit.setVisibility(View.VISIBLE);
            }
//            mBinding.tvEventLiveNow.setVisibility(View.VISIBLE);
//            mBinding.tvEventEdit.setVisibility(View.GONE);
        }
    }

    private void getCommentList() {
        if (Commons.isOnline(mContext)) {
            progressDialog.show();
            HashMap<String, String> params = new HashMap<>();
            params.put("event_id", eventDetailsData.getId());

            String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");

            ApiClient.create().getEventComments(header, params).enqueue(new Callback<EventCommentMainModel>() {
                @Override
                public void onResponse(Call<EventCommentMainModel> call, Response<EventCommentMainModel> response) {
                    progressDialog.dismiss();
                    if (response.code() == 200 && response.isSuccessful()) {
                        if (response.body() != null) {
                            setEventComment(response.body().getData());
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
                    progressDialog.dismiss();
                    Commons.showToast(mContext, getResources().getString(R.string.something_wants_wrong));
                }
            });
        } else {
            Commons.showToast(mContext, getResources().getString(R.string.no_internet_connection));
        }
    }

    private void setEventComment(ArrayList<EventCommentMainModel.EventCommentData> list) {
        if (list.isEmpty()) {
            mBinding.recyclerEventComment.setVisibility(View.GONE);
        } else {
            mBinding.recyclerEventComment.setVisibility(View.VISIBLE);
            mBinding.recyclerEventComment.setLayoutManager(new LinearLayoutManager(mContext));
            mBinding.recyclerEventComment.setAdapter(new EventCommentAdapter(mContext, list));
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

    private boolean isEventLive() {
        boolean isLive = false;
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

        if (eventDetailsData.getDate() != null && eventDetailsData.getStime() != null && eventDetailsData.getEtime() != null) {
            String eventSTime = eventDetailsData.getDate() + " " + eventDetailsData.getStime();

            String eventETime = eventDetailsData.getDate() + " " + eventDetailsData.getEtime();
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

    private boolean isEventClose() {
        boolean isClose = false;
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

        if (eventDetailsData.getDate() != null && eventDetailsData.getStime() != null) {

            String eventETime = eventDetailsData.getDate() + " " + eventDetailsData.getEtime();
            try {
                Date eventEDate = format.parse(eventETime);
                if (currentDate.after(eventEDate)) {
                    isClose = true;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return isClose;
    }
}