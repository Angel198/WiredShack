package com.jaylax.wiredshack.user.home;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.jaylax.wiredshack.MainActivity;
import com.jaylax.wiredshack.ProgressDialog;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.FragmentHomeNewBinding;
import com.jaylax.wiredshack.model.RecentEventMainModel;
import com.jaylax.wiredshack.model.UserDetailsModel;
import com.jaylax.wiredshack.rest.ApiClient;
import com.jaylax.wiredshack.user.dashboard.DashboardActivity;
import com.jaylax.wiredshack.user.eventDetails.EventDetailsActivity;
import com.jaylax.wiredshack.user.managerDetails.ManagerDetailsActivity;
import com.jaylax.wiredshack.user.upcoming.UpcomingEventActivity;
import com.jaylax.wiredshack.utils.Commons;
import com.jaylax.wiredshack.utils.DotsIndicatorDecoration;
import com.jaylax.wiredshack.utils.SharePref;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import me.relex.circleindicator.CircleIndicator2;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeNewFragment extends Fragment {
    //    FragmentHomeBinding mBinding;
    FragmentHomeNewBinding mBinding;
    Context context;
    UserDetailsModel userDetailsModel = null;
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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_new, container, false);

        context = getActivity();
        progressDialog = new ProgressDialog(context);
        userDetailsModel = Commons.convertStringToObject(context, SharePref.PREF_USER, UserDetailsModel.class);
        if (userDetailsModel == null) {
            mBinding.imgProfileLogout.setVisibility(View.INVISIBLE);
            mBinding.imgAccountProfile.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.toplogo));
        } else {
            mBinding.imgProfileLogout.setVisibility(View.VISIBLE);
            RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.place_holder).transform(new CenterCrop()).error(R.drawable.place_holder).priority(Priority.HIGH);
            Glide.with(this).load(userDetailsModel.getImage() == null ? "" : userDetailsModel.getImage()).apply(options).into(mBinding.imgAccountProfile);
        }

        mBinding.imgProfileLogout.setOnClickListener(view -> showLogoutDialog());

//        getEventList();
        getUpcomingEvent();

        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
//        getEventManagerList();
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

    private void setUpcomingEventDat(ArrayList<UpcomingEventMainModel.UpcomingEventData> eventData, String followCount) {
        if (eventData.isEmpty()){
            hideUpcomingEventData();
        }else {
            mBinding.tvHomeUpcomingEventTitle.setVisibility(View.VISIBLE);
            mBinding.recyclerHomeUpcomingEvent.setVisibility(View.VISIBLE);
            mBinding.recyclerHomeUpcomingEvent.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
            mBinding.recyclerHomeUpcomingEvent.setAdapter(new HomeUpcomingEventAdapter(context,eventData,data -> {
                if (SharePref.getInstance(context).get(SharePref.PREF_TOKEN, "").toString().isEmpty()) {
                    Intent intent = new Intent(context, EventDetailsActivity.class);
                    intent.putExtra("eventId", data.getId());
                    context.startActivity(intent);
                } else {
                    //TODO : OpenUpcomingEventScreen
                    Intent intent = new Intent(context, UpcomingEventActivity.class);
                    intent.putExtra("eventId", data.getId());
                    context.startActivity(intent);
                }
            }));
        }
    }

    private void hideUpcomingEventData() {
        mBinding.tvHomeUpcomingEventTitle.setVisibility(View.GONE);
        mBinding.recyclerHomeUpcomingEvent.setVisibility(View.GONE);
    }


    private void getEventList() {
        if (Commons.isOnline(context)) {
            progressDialog.show();
            String header = "Bearer " + SharePref.getInstance(context).get(SharePref.PREF_TOKEN, "");
            ApiClient.create().getRecentEventsUser().enqueue(new Callback<RecentEventMainModel>() {
                @Override
                public void onResponse(Call<RecentEventMainModel> call, Response<RecentEventMainModel> response) {
                    progressDialog.dismiss();
                    getEventManagerList();
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
                    getEventManagerList();
                }
            });
        } else {
            Commons.showToast(context, context.getResources().getString(R.string.no_internet_connection));
        }
    }

    private void setRecentEventData(ArrayList<RecentEventMainModel.RecentEventData> list) {
        if (list.isEmpty()) {
            mBinding.tvHomeRecentEventTitle.setVisibility(View.GONE);
            mBinding.recyclerHomeRecentEvent.setVisibility(View.GONE);
        } else {
            mBinding.tvHomeRecentEventTitle.setVisibility(View.VISIBLE);
            mBinding.recyclerHomeRecentEvent.setVisibility(View.VISIBLE);
            mBinding.recyclerHomeRecentEvent.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            HomeRecentEventNewAdapter adapter = new HomeRecentEventNewAdapter(context, list, data -> {
                Intent intent = new Intent(context, EventDetailsActivity.class);
                intent.putExtra("eventId", data.getId());
                context.startActivity(intent);
            });
            mBinding.recyclerHomeRecentEvent.setAdapter(adapter);
            PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
            pagerSnapHelper.attachToRecyclerView(mBinding.recyclerHomeRecentEvent);

            mBinding.indicator.attachToRecyclerView(mBinding.recyclerHomeRecentEvent, pagerSnapHelper);

// optional
            adapter.registerAdapterDataObserver(mBinding.indicator.getAdapterDataObserver());
        }
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
            mBinding.tvHomeFollowingSubTitle.setVisibility(View.GONE);
            mBinding.tvHomeFollowingTitle.setVisibility(View.GONE);
            mBinding.recyclerHomeManager.setVisibility(View.GONE);
        } else {
            mBinding.tvHomeFollowingSubTitle.setVisibility(View.VISIBLE);
            mBinding.tvHomeFollowingTitle.setVisibility(View.VISIBLE);
            mBinding.recyclerHomeManager.setVisibility(View.VISIBLE);

            mBinding.recyclerHomeManager.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            mBinding.recyclerHomeManager.setAdapter(new HomeManagerListAdapter(context, list, data -> {
                Intent intent = new Intent(context, ManagerDetailsActivity.class);
                intent.putExtra("managerId", data.getId());
                context.startActivity(intent);
            }));
        }
    }

    public static String getEventDate(String dateEvent) {
        String eventDate = "N/A";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        if (dateEvent != null) {
            try {
                Date date = format.parse(dateEvent);
                format = new SimpleDateFormat("dd", Locale.US);
                eventDate = format.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return eventDate;
    }

    public static String getEventDateDay(String dateEvent) {
        String eventDate = "N/A";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        if (dateEvent != null) {
            try {
                Date date = format.parse(dateEvent);
                format = new SimpleDateFormat("MMM", Locale.US);
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

    private void showLogoutDialog() {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_log_out);

        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(window.getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);

        AppCompatTextView tvLogoutYes = (AppCompatTextView) dialog.findViewById(R.id.tvLogoutYes);
        AppCompatTextView tvLogoutNo = (AppCompatTextView) dialog.findViewById(R.id.tvLogoutNo);

        tvLogoutYes.setOnClickListener(view -> {
            dialog.dismiss();
            SharePref.getInstance(getActivity()).clearAll();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra("isLogout", true);
            Objects.requireNonNull(getActivity()).startActivity(intent);
            Objects.requireNonNull(getActivity()).finishAffinity();
        });

        tvLogoutNo.setOnClickListener(view -> {
            dialog.dismiss();
        });
        dialog.show();
    }
}
