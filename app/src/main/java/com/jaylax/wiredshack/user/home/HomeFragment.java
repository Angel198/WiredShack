package com.jaylax.wiredshack.user.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
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
import com.jaylax.wiredshack.databinding.FragmentHomeBinding;
import com.jaylax.wiredshack.model.RecentEventMainModel;
import com.jaylax.wiredshack.model.UserDetailsModel;
import com.jaylax.wiredshack.rest.ApiClient;
import com.jaylax.wiredshack.user.eventDetails.EventDetailsActivity;
import com.jaylax.wiredshack.user.managerDetails.ManagerDetailsActivity;
import com.jaylax.wiredshack.user.upcoming.UpcomingEventActivity;
import com.jaylax.wiredshack.utils.Commons;
import com.jaylax.wiredshack.utils.SharePref;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    FragmentHomeBinding mBinding;
    Context context;
    UserDetailsModel userDetailsModel;
    ProgressDialog progressDialog;
    Boolean isManager = true;
    Boolean isDJ = false;


    ArrayList<ManagerListMainModel.ManagerListData> managerList = new ArrayList<>();
    ArrayList<ManagerListMainModel.ManagerListData> djList = new ArrayList<>();

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
        getEventManagerList();
        setClickListener();

        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
//        getEventManagerList();
    }

    private void getEventManagerList() {
        if (Commons.isOnline(context)) {
            String header = "Bearer " + SharePref.getInstance(context).get(SharePref.PREF_TOKEN, "");

            Call<ManagerListMainModel> call;
            if (SharePref.getInstance(context).get(SharePref.PREF_TOKEN, "").toString().isEmpty()) {
                call = ApiClient.create().getGuestEventsManager();
            } else {
                call = ApiClient.create().getEventsManager(header);
            }
            if (call != null) {
                progressDialog.show();
                call.enqueue(new Callback<ManagerListMainModel>() {
                    @Override
                    public void onResponse(Call<ManagerListMainModel> call, Response<ManagerListMainModel> response) {
                        progressDialog.dismiss();
                        getUpcomingEvent();
                        if (response.code() == 200 && response.isSuccessful()) {
                            if (response.body() != null) {
                                setEventManagerData(response.body().getData());
                            }
                        } else {
                            Commons.showToast(context, getResources().getString(R.string.please_try_after_some_time));
                        }
                    }

                    @Override
                    public void onFailure(Call<ManagerListMainModel> call, Throwable t) {
                        progressDialog.dismiss();
                        getUpcomingEvent();
                        Commons.showToast(context, getResources().getString(R.string.something_wants_wrong));
                    }
                });
            }
        } else {
            Commons.showToast(context, context.getResources().getString(R.string.no_internet_connection));
        }
    }

    private void setEventManagerData(ArrayList<ManagerListMainModel.ManagerListData> list) {
        if (list.isEmpty()) {
            mBinding.linearHomeManager.setVisibility(View.GONE);
        } else {
            mBinding.linearHomeManager.setVisibility(View.VISIBLE);
            for (ManagerListMainModel.ManagerListData data : list) {
                if (data.getUserType() == null) {
                    managerList.add(data);
                } else {
                    if (data.getUserType().equals("2")) {
                        managerList.add(data);
                    } else if (data.getUserType().equals("3")) {
                        djList.add(data);
                    } else {
                        managerList.add(data);
                    }
                }
            }
            mBinding.recyclerHomeManager.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            setTabLayout();
        }
    }


    private void setClickListener() {
        mBinding.tvEventOrganiser.setOnClickListener(view -> {
            if (!isManager) {
                mBinding.recyclerHomeManager.setVisibility(View.GONE);
            }
            isManager = true;
            isDJ = false;
            setTabLayout();
        });

        mBinding.tvDJOrganiser.setOnClickListener(view -> {
            if (!isDJ) {
                mBinding.recyclerHomeManager.setVisibility(View.GONE);
            }
            isManager = false;
            isDJ = true;
            setTabLayout();
        });
    }

    private void setTabLayout() {
        ArrayList<ManagerListMainModel.ManagerListData> list = new ArrayList<>();
        if (isManager) {
            mBinding.tvEventOrganiser.setBackgroundResource(R.drawable.back_round_white);
            mBinding.tvEventOrganiser.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorBlackText));
            list = managerList;
        } else {
            mBinding.tvEventOrganiser.setBackgroundResource(R.drawable.back_border_white);
            mBinding.tvEventOrganiser.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        }

        if (isDJ) {
            mBinding.tvDJOrganiser.setBackgroundResource(R.drawable.back_round_white);
            mBinding.tvDJOrganiser.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorBlackText));
            list = djList;
        } else {
            mBinding.tvDJOrganiser.setBackgroundResource(R.drawable.back_border_white);
            mBinding.tvDJOrganiser.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        }

        if (list.isEmpty()){
            mBinding.recyclerHomeManager.setVisibility(View.GONE);
        }else {
            mBinding.recyclerHomeManager.setVisibility(View.VISIBLE);
            mBinding.recyclerHomeManager.setAdapter(new HomeTopStoryAdapter(context, list, data -> {
                Intent intent = new Intent(context, ManagerDetailsActivity.class);
                intent.putExtra("managerId", data.getId());
                context.startActivity(intent);
            }));
        }
    }


    private void getUpcomingEvent() {
        if (Commons.isOnline(context)) {
            String header = "Bearer " + SharePref.getInstance(context).get(SharePref.PREF_TOKEN, "");

            Call<UpcomingEventMainModel> call;
            if (SharePref.getInstance(context).get(SharePref.PREF_TOKEN, "").toString().isEmpty()) {
                call = ApiClient.create().getGuestUpcomingEvent();
            } else {
                call = ApiClient.create().getUpcomingEvent(header);
            }
            if (call != null) {
                progressDialog.show();
                call.enqueue(new Callback<UpcomingEventMainModel>() {
                    @Override
                    public void onResponse(Call<UpcomingEventMainModel> call, Response<UpcomingEventMainModel> response) {
                        progressDialog.dismiss();
                        getEventList();
                        if (response.code() == 200 && response.isSuccessful()) {
                            if (response.body() != null) {
                                if (response.body().getStatus().equals("200") && response.body().getData() != null) {
                                    setUpcomingEventDat(response.body().getData(), response.body().getFollowingEventCount() == null ? "" : response.body().getFollowingEventCount());
                                } else {
                                    hideUpcomingEventData();
                                }
                            } else {
                                hideUpcomingEventData();
                            }
                        } else {
                            hideUpcomingEventData();
                        }
                    }

                    @Override
                    public void onFailure(Call<UpcomingEventMainModel> call, Throwable t) {
                        progressDialog.dismiss();
                        hideUpcomingEventData();
                        getEventList();
                        Commons.showToast(context, getResources().getString(R.string.something_wants_wrong));
                    }
                });
            }
        } else {
            Commons.showToast(context, context.getResources().getString(R.string.no_internet_connection));
        }
    }

    private void setUpcomingEventDat(UpcomingEventMainModel.UpcomingEventData eventData, String followCount) {
        mBinding.relativeUpcomingEvent.setVisibility(View.VISIBLE);
        RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.place_holder).transform(new CenterCrop()).error(R.drawable.place_holder).priority(Priority.HIGH);
        String coverImage = "";
        if (eventData.getImages().isEmpty()) {
            coverImage = eventData.getCoverImage() == null ? "" : eventData.getCoverImage();
        } else {
            coverImage = eventData.getImages().get(0).getImages() == null ? "" : eventData.getImages().get(0).getImages();
        }
        Glide.with(getContext()).load(coverImage).apply(options).into(mBinding.imgUpcomingEvent);
        String txtUpcoming = "";
        if (eventData.getIsActive() == null) {
            mBinding.imgUpcomingEventVideo.setVisibility(View.GONE);
            mBinding.tvUpcomingEventTime.setVisibility(View.VISIBLE);
        } else {
            if (eventData.getIsActive().equals("1")) {
                mBinding.imgUpcomingEventVideo.setVisibility(View.VISIBLE);
                mBinding.tvUpcomingEventTime.setVisibility(View.GONE);
            } else {
                mBinding.imgUpcomingEventVideo.setVisibility(View.GONE);
                mBinding.tvUpcomingEventTime.setVisibility(View.VISIBLE);
            }
        }

        mBinding.tvUpcomingEventName.setText(eventData.getEventName());
        mBinding.tvUpcomingEventDate.setText(getEventDate(eventData.getDate()));
        mBinding.tvUpcomingEventTime.setText(getEventTime(eventData.getStime()));

        mBinding.relativeUpcomingEvent.setOnClickListener(view -> {
            if (SharePref.getInstance(context).get(SharePref.PREF_TOKEN, "").toString().isEmpty()) {
                Intent intent = new Intent(context, EventDetailsActivity.class);
                intent.putExtra("eventId", eventData.getId());
                context.startActivity(intent);
            } else {
                //TODO : OpenUpcomingEventScreen
                Intent intent = new Intent(context, UpcomingEventActivity.class);
                intent.putExtra("eventId", eventData.getId());
                context.startActivity(intent);

               /* if (Integer.parseInt(followCount) > 0) {
                    Intent intent = new Intent(context, UpcomingEventActivity.class);
                    intent.putExtra("eventId", eventData.getId());
                    context.startActivity(intent);
                }else {
                    Intent intent = new Intent(getActivity(), LiveVideoPlayerActivity.class);
//                intent.putExtra("liveStream",eventData.getId()+"_"+eventData.getEventName());
                    intent.putExtra("liveStream", eventData.getId());
                    getActivity().startActivity(intent);

                    *//*if (eventData.getIsActive() == null) {
                        Intent intent = new Intent(context, EventDetailsActivity.class);
                        intent.putExtra("eventId", eventData.getId());
                        context.startActivity(intent);
                    } else {
                        if (eventData.getIsActive().equals("1")) {
                            Intent intent = new Intent(getActivity(), LiveVideoPlayerActivity.class);
//                intent.putExtra("liveStream",eventData.getId()+"_"+eventData.getEventName());
                            intent.putExtra("liveStream",eventData.getId());
                            getActivity().startActivity(intent);
                        } else {
                            Intent intent = new Intent(context, EventDetailsActivity.class);
                            intent.putExtra("eventId", eventData.getId());
                            context.startActivity(intent);
                        }
                    }*//*


                }*/
            }

           /* if (SharePref.getInstance(context).get(SharePref.PREF_TOKEN, "").toString().isEmpty()) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                getActivity().startActivity(intent);
            } else {
                Intent intent = new Intent(getActivity(), LiveVideoPlayerActivity.class);
//                intent.putExtra("liveStream",eventData.getId()+"_"+eventData.getEventName());
                intent.putExtra("liveStream",eventData.getId());
                getActivity().startActivity(intent);
            }*/
        });

        /*if (isEventLive(eventData.getDate(), eventData.getStime(), eventData.getEtime())) {
            mBinding.imgUpcomingEventVideo.setVisibility(View.VISIBLE);
        } else {
            mBinding.imgUpcomingEventVideo.setVisibility(View.GONE);
        }*/
    }

    private void hideUpcomingEventData() {
        mBinding.relativeUpcomingEvent.setVisibility(View.GONE);
    }

    private void getEventList() {
        if (Commons.isOnline(context)) {
            progressDialog.show();
            String header = "Bearer " + SharePref.getInstance(context).get(SharePref.PREF_TOKEN, "");
            ApiClient.create().getRecentEventsUser().enqueue(new Callback<RecentEventMainModel>() {
                @Override
                public void onResponse(Call<RecentEventMainModel> call, Response<RecentEventMainModel> response) {
                    progressDialog.dismiss();
                    if (response.code() == 200 && response.isSuccessful()) {
                        if (response.body() != null) {
                            setRecentEventData(response.body().getData());
                        }
                    }
                }

                @Override
                public void onFailure(Call<RecentEventMainModel> call, Throwable t) {
                    progressDialog.dismiss();
                    Commons.showToast(context, getResources().getString(R.string.something_wants_wrong));
                }
            });
        } else {
            Commons.showToast(context, context.getResources().getString(R.string.no_internet_connection));
        }
    }

    private void setRecentEventData(ArrayList<RecentEventMainModel.RecentEventData> list) {
        if (list.isEmpty()) {
            mBinding.linearHomeRecentEvent.setVisibility(View.GONE);
        } else {
            mBinding.linearHomeRecentEvent.setVisibility(View.VISIBLE);
            mBinding.recyclerHomeRecentEvent.setLayoutManager(new GridLayoutManager(getActivity(), 3));
            mBinding.recyclerHomeRecentEvent.setAdapter(new HomeRecentEventAdapter(context, list, data -> {
                Intent intent = new Intent(context, EventDetailsActivity.class);
                intent.putExtra("eventId", data.getId());
                context.startActivity(intent);
            }, true));
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