package com.jaylax.wiredshack.eventManager.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.jaylax.wiredshack.MainActivity;
import com.jaylax.wiredshack.ProgressDialog;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.FragmentManagerHomeBinding;
import com.jaylax.wiredshack.eventManager.dashboard.DashboardEventManagerActivity;
import com.jaylax.wiredshack.eventManager.editEvent.ManagerEditEventActivity;
import com.jaylax.wiredshack.eventManager.editEvent.ManagerEditEventNewActivity;
import com.jaylax.wiredshack.eventManager.followed.ManagerFollowedActivity;
import com.jaylax.wiredshack.eventManager.liveVideoBroadcaster.LiveVideoBroadcasterActivity;
import com.jaylax.wiredshack.model.CommonResponseModel;
import com.jaylax.wiredshack.model.RecentEventMainModel;
import com.jaylax.wiredshack.model.UserDetailsModel;
import com.jaylax.wiredshack.rest.ApiClient;
import com.jaylax.wiredshack.user.dashboard.DashboardActivity;
import com.jaylax.wiredshack.user.eventDetails.EventDetailsMainModel;
import com.jaylax.wiredshack.user.following.UserFollowingActivity;
import com.jaylax.wiredshack.utils.Commons;
import com.jaylax.wiredshack.utils.SharePref;
import com.jaylax.wiredshack.utils.SpannedGridLayoutManager;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManagerHomeFragment extends Fragment {

    FragmentManagerHomeBinding mBinding;
    Context mContext;
    ProgressDialog progressDialog;
    UserDetailsModel userDetailsModel;
    String followedCount = "0";

    Dialog dialogEditProfile;
    String profileImagePath = "";
    Boolean isMale, isFemale;

    final static int REQUEST_IMAGE_PICKER = 101;

    RecentEventMainModel.RecentEventData nearestEvent = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.fragment_manager_home, container, false);
        mContext = getActivity();
        progressDialog = new ProgressDialog(Objects.requireNonNull(mContext));
        userDetailsModel = Commons.convertStringToObject(mContext, SharePref.PREF_USER, UserDetailsModel.class);

        setUserDetails();
        setClickListener();

        return mBinding.getRoot();
    }

    private void setUserDetails() {
        RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.place_holder).transform(new CenterCrop()).error(R.drawable.place_holder).priority(Priority.HIGH);
        Glide.with(this).load(userDetailsModel.getImage() == null ? "" : userDetailsModel.getImage()).apply(options).into(mBinding.imgAccountProfile);
//        Glide.with(this).load(userDetailsModel.getCoverImage() == null ? "" : userDetailsModel.getCoverImage()).apply(options).into(mBinding.imgAccountCover);

        mBinding.tvAccountProfileName.setText(userDetailsModel.getName());
        if (userDetailsModel.getFollowed() != null) {
            if (!userDetailsModel.getFollowed().isEmpty()) {
                followedCount = userDetailsModel.getFollowed();
            }
        }
        if (userDetailsModel.getGender() != null) {
            if (userDetailsModel.getGender().equals(getResources().getString(R.string.female))) {
                isMale = false;
                isFemale = true;
            } else {
                isMale = true;
                isFemale = false;
            }
        } else {
            isMale = true;
            isFemale = false;
        }
        mBinding.tvFollowedCount.setText(followedCount);

        if (userDetailsModel.getAboutMe() == null) {
            mBinding.linearProfileAboutMe.setVisibility(View.GONE);
        } else {
            if (userDetailsModel.getAboutMe().isEmpty()) {
                mBinding.linearProfileAboutMe.setVisibility(View.GONE);
            } else {
                mBinding.linearProfileAboutMe.setVisibility(View.VISIBLE);
                mBinding.tvManagerAboutMe.setText(userDetailsModel.getAboutMe());
            }
        }

    }

    private void setClickListener() {
        /*mBinding.tvAccountAddEvent.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), ManagerEditEventActivity.class);
            mContext.startActivity(intent);
        });*/

        mBinding.linearProfileName.setOnClickListener(view -> {
            if (Integer.parseInt(followedCount) > 0) {
                Intent intent = new Intent(getActivity(), ManagerFollowedActivity.class);
                mContext.startActivity(intent);
            }
        });

        mBinding.imgProfileLogout.setOnClickListener(view -> {
            showLogoutDialog();
        });

        mBinding.imgProfileEdit.setOnClickListener(view -> {
            showEditDialog();
        });

        mBinding.imgGoLive.setOnClickListener(view -> {
            if (nearestEvent != null) {
                if (isEventLive(nearestEvent.getDate(), nearestEvent.getStime(), nearestEvent.getEtime())) {
                    Intent intent = new Intent(getActivity(), LiveVideoBroadcasterActivity.class);
                    intent.putExtra("eventStream", nearestEvent.getId());
                    startActivity(intent);
                }else {
                    Commons.showToast(mContext,mContext.getResources().getString(R.string.event_not_started));
                }
            }
        });
    }

    private void showLogoutDialog() {
        Dialog dialog = new Dialog(mContext);
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

    @Override
    public void onResume() {
        super.onResume();
        if (dialogEditProfile == null) {
            getRecentEvents();
        } else {
            if (!dialogEditProfile.isShowing()) {
                getRecentEvents();
            }
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
            mBinding.recyclerHomeRecentEventEvents.setVisibility(View.GONE);
        } else {
            mBinding.recyclerHomeRecentEventEvents.setVisibility(View.VISIBLE);
            RecyclerView.LayoutManager manager;
            if (list.size() > 1) {
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
            mBinding.recyclerHomeRecentEventEvents.setHasFixedSize(true);
            mBinding.recyclerHomeRecentEventEvents.setLayoutManager(manager);
            mBinding.recyclerHomeRecentEventEvents.setAdapter(new ManagerRecentEventsAdapter(mContext, list, (data, lisType) -> {
                /*if (lisType.isEmpty()) {
                    DashboardEventManagerActivity.redirectToEditEvent(data.getId(), getActivity());
                }*/
                Intent intent = new Intent(mContext, ManagerEditEventNewActivity.class);
                intent.putExtra("eventId", data.getId());
                startActivity(intent);
            }));

            getNearestLiveEvent(list);
        }
    }

    private void getNearestLiveEvent(ArrayList<RecentEventMainModel.RecentEventData> list) {
        ArrayList<Date> date = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        try {
            for (RecentEventMainModel.RecentEventData data : list) {
                if (data.getCreatedBy().equals(userDetailsModel.getId())) {
                    if (data.getDate() != null && data.getStime() != null) {
                        date.add(format.parse(data.getDate() + " " + data.getStime()));
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date.isEmpty()) {
            if (list.get(0).getCreatedBy().equals(userDetailsModel.getId())) {
                nearestEvent = list.get(0);
            }
        } else {
            final long now = System.currentTimeMillis();
            Date closet = Collections.min(date, (d1, d2) -> {
                long diff1 = Math.abs(d1.getTime() - now);
                long diff2 = Math.abs(d2.getTime() - now);
                return Long.compare(diff1, diff2);
            });
            /*SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

            String stClosetDate = dateFormat.format(closet);*/
            Log.e("ClosetDate", closet.toString());

            for (RecentEventMainModel.RecentEventData event : list) {
                try {
                    Date eventDate = format.parse(event.getDate() + " " + event.getStime());
                    if (eventDate != null && eventDate.equals(closet)) {
                        nearestEvent = event;
                        break;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        if (nearestEvent == null) {
            mBinding.linearGoLiveEvent.setVisibility(View.GONE);
        } else {
//            mBinding.linearGoLiveEvent.setVisibility(View.VISIBLE);
//            mBinding.tvHomeEventTitle.setText(nearestEvent.getEventName());
            showEventLiveTime();
        }
    }

    private void showEventLiveTime() {

        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

        if (nearestEvent.getDate() != null && nearestEvent.getStime() != null) {
            String eventSTime = nearestEvent.getDate() + " " + nearestEvent.getStime();
            try {
                Date eventSDate = format.parse(eventSTime);
                final long[] different = {eventSDate.getTime() - currentDate.getTime()};
                long hoursInMilli = TimeUnit.MILLISECONDS.toHours(different[0]);
                long minutesInMilli = TimeUnit.MILLISECONDS.toHours(different[0]);

                Log.e("remaining Hours", String.valueOf(TimeUnit.MILLISECONDS.toHours(different[0])));
                Log.e("remaining Minutes", String.valueOf(TimeUnit.MILLISECONDS.toMinutes(different[0])));

                if (hoursInMilli >= 1) {
                    mBinding.linearGoLiveEvent.setVisibility(View.GONE);
                } else {
                    mBinding.linearGoLiveEvent.setVisibility(View.VISIBLE);
                    mBinding.tvHomeEventTitle.setText(nearestEvent.getEventName());
                    new CountDownTimer(different[0], 1000) {
                        public void onTick(long millisUntilFinished) {
                            different[0] = millisUntilFinished;
                            String hms = String.format("%02d:%02d:%02d",
                                    TimeUnit.MILLISECONDS.toHours(different[0]),
                                    TimeUnit.MILLISECONDS.toMinutes(different[0]) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(different[0])), TimeUnit.MILLISECONDS.toSeconds(different[0]) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(different[0])));

                            mBinding.tvHomeEventTime.setText(hms);//set text
                        }

                        public void onFinish() {
                        }
                    }.start();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }


    private void showEditDialog() {
        dialogEditProfile = new Dialog(mContext);
        dialogEditProfile.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogEditProfile.setCancelable(true);
        dialogEditProfile.setContentView(R.layout.dialog_user_edit_profile);

        Window window = dialogEditProfile.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(window.getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);

        AppCompatImageView ivEditProfile = (AppCompatImageView) dialogEditProfile.findViewById(R.id.imgEditProfile);
        AppCompatEditText etEditProfileName = (AppCompatEditText) dialogEditProfile.findViewById(R.id.etEditProfileName);
        AppCompatEditText etEditProfileEmail = (AppCompatEditText) dialogEditProfile.findViewById(R.id.etEditProfileEmail);
        AppCompatEditText etEditProfilePhone = (AppCompatEditText) dialogEditProfile.findViewById(R.id.etEditProfilePhone);
        AppCompatEditText etEditProfileAboutMe = (AppCompatEditText) dialogEditProfile.findViewById(R.id.etEditProfileAboutMe);

        RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.ic_upload_photo).transform(new CenterCrop(), new RoundedCorners(30)).error(R.drawable.ic_upload_photo).priority(Priority.HIGH);
        Glide.with(this).load(userDetailsModel.getImage() == null ? "" : userDetailsModel.getImage()).apply(options).into(ivEditProfile);

        etEditProfileName.setText(userDetailsModel.getName());
        etEditProfileEmail.setText(userDetailsModel.getEmail());
        etEditProfilePhone.setText(userDetailsModel.getPhone());
        etEditProfileAboutMe.setText(userDetailsModel.getAboutMe() == null ? "" : userDetailsModel.getAboutMe());

        setMaleFemaleUI();

        dialogEditProfile.findViewById(R.id.imgEditClose).setOnClickListener(view -> {
            dialogEditProfile.dismiss();
        });

        ivEditProfile.setOnClickListener(view -> {
            showImagePickerAlert();
        });

        dialogEditProfile.findViewById(R.id.tvEditProfileMale).setOnClickListener(view -> {
            if (!isMale) {
                isMale = true;
                isFemale = false;
                setMaleFemaleUI();
            }
        });

        dialogEditProfile.findViewById(R.id.tvEditProfileFemale).setOnClickListener(view -> {
            if (!isFemale) {
                isMale = false;
                isFemale = true;
                setMaleFemaleUI();
            }
        });

        dialogEditProfile.findViewById(R.id.tvEditProfileDone).setOnClickListener(view -> {
            String userName = Objects.requireNonNull(etEditProfileName.getText()).toString().trim();
            String phoneNumber = Objects.requireNonNull(etEditProfilePhone.getText()).toString().trim();
            String aboutMe = Objects.requireNonNull(etEditProfileAboutMe.getText()).toString().trim();
            String gender;
            if (isMale) {
                gender = mContext.getResources().getString(R.string.male);
            } else {
                gender = mContext.getResources().getString(R.string.female);
            }
            String dob = "01-01-1990";
            if (userName.isEmpty()) {
                Commons.showToast(mContext, mContext.getResources().getString(R.string.enter_name));
            } else if (phoneNumber.isEmpty()) {
                Commons.showToast(mContext, mContext.getResources().getString(R.string.enter_phone_number));
            } else if (phoneNumber.length() < 10) {
                Commons.showToast(mContext, mContext.getResources().getString(R.string.enter_valid_phone_number));
            } /*else if (dob.isEmpty()) {
                Commons.showToast(context, getResources().getString(R.string.enter_dob));
            } */ else {
                updateProfile(userName, phoneNumber, gender, dob, aboutMe);
            }
        });

        dialogEditProfile.setCancelable(true);
        dialogEditProfile.show();
    }

    private void showImagePickerAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(getResources().getString(R.string.choose_image_from));
        builder.setCancelable(false);
        builder.setPositiveButton(getResources().getString(R.string.camera), (dialogInterface, i) -> {
            new ImagePicker.Builder(this).crop().cameraOnly().compress(1024).start(REQUEST_IMAGE_PICKER);
        });
        builder.setNegativeButton(getResources().getString(R.string.gallery), (dialogInterface, i) -> {
            new ImagePicker.Builder(this).crop().galleryOnly().compress(1024).start(REQUEST_IMAGE_PICKER);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(true);
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);

    }

    private void setMaleFemaleUI() {
        if (dialogEditProfile != null) {
            AppCompatTextView maleTV = (AppCompatTextView) dialogEditProfile.findViewById(R.id.tvEditProfileMale);
            AppCompatTextView femaleTV = (AppCompatTextView) dialogEditProfile.findViewById(R.id.tvEditProfileFemale);
            if (isMale) {
                maleTV.setBackgroundResource(R.drawable.back_grey);
                maleTV.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            } else {
                maleTV.setBackgroundResource(0);
                maleTV.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlack66));
            }

            if (isFemale) {
                femaleTV.setBackgroundResource(R.drawable.back_grey);
                femaleTV.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            } else {
                femaleTV.setBackgroundResource(0);
                femaleTV.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlack66));
            }
        }
    }

    private void updateProfile(String userName, String phoneNumber, String gender, String dob, String aboutMe) {
        if (Commons.isOnline(mContext)) {
            File profileFile = new File(profileImagePath);
            MultipartBody.Part profileBody = null;
            if (!profileImagePath.isEmpty()) {
                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), profileFile);
                profileBody = MultipartBody.Part.createFormData("image", profileFile.getName(), requestBody);
            }

            MultipartBody.Part coverBody = null;
           /* File coverFile = new File(coverPhotoImagePath);
            MultipartBody.Part coverBody = null;
            if (!coverPhotoImagePath.isEmpty()) {
                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), coverFile);
                coverBody = MultipartBody.Part.createFormData("cover_image", coverFile.getName(), requestBody);
            }*/

            HashMap<String, RequestBody> params = new HashMap<>();
            params.put("name", RequestBody.create(MultipartBody.FORM, userName));
            params.put("phone", RequestBody.create(MultipartBody.FORM, phoneNumber));
            params.put("gender", RequestBody.create(MultipartBody.FORM, gender));
            params.put("birth_date", RequestBody.create(MultipartBody.FORM, dob));
            params.put("about_me", RequestBody.create(MultipartBody.FORM, aboutMe));

            String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");

            Call<CommonResponseModel> retrofitCall = null;

            if (profileBody == null) {
                retrofitCall = ApiClient.create().updateProfile(header, params);
            } else {
                retrofitCall = ApiClient.create().updateProfile(header, params, profileBody);
            }

            if (retrofitCall != null) {
                progressDialog.show();
                retrofitCall.enqueue(new Callback<CommonResponseModel>() {
                    @Override
                    public void onResponse(Call<CommonResponseModel> call, Response<CommonResponseModel> response) {
                        progressDialog.dismiss();
                        if (response.code() == 200 && response.isSuccessful()) {
                            if (response.body() != null) {
                                if (response.body().getStatus().equals("200")) {
                                    if (dialogEditProfile != null) {
                                        dialogEditProfile.dismiss();
                                    }
                                    new Handler().postDelayed(() -> {
                                        getUserDetails();
                                    }, 500);
                                } else {
                                    String msg = "";
                                    if (response.body().getMessage().isEmpty()) {
                                        msg = mContext.getResources().getString(R.string.please_try_after_some_time);
                                    } else {
                                        msg = response.body().getMessage();
                                    }
                                    Commons.showToast(mContext, msg);
                                }
                            }
                        } else {
                            Commons.showToast(mContext, mContext.getResources().getString(R.string.please_try_after_some_time));
                        }
                    }

                    @Override
                    public void onFailure(Call<CommonResponseModel> call, Throwable t) {
                        progressDialog.dismiss();
                        Commons.showToast(mContext, mContext.getResources().getString(R.string.something_wants_wrong));
                    }
                });
            }
        } else {
            Commons.showToast(mContext, mContext.getResources().getString(R.string.no_internet_connection));
        }
    }

    private void getUserDetails() {
        if (Commons.isOnline(mContext)) {
            progressDialog.show();
            String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");
            ApiClient.create().userDetails(header).enqueue(new Callback<UserDetailsModel>() {
                @Override
                public void onResponse(Call<UserDetailsModel> call, Response<UserDetailsModel> response) {
                    progressDialog.dismiss();
                    if (response.code() == 200 && response.isSuccessful()) {
                        if (response.body() != null) {
                            SharePref.getInstance(mContext).save(SharePref.PREF_USER, Commons.convertObjectToString(response.body()));
                            userDetailsModel = response.body();
                            setUserDetails();
                        }
                    } else {
                        Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                    }
                }

                @Override
                public void onFailure(Call<UserDetailsModel> call, Throwable t) {
                    progressDialog.dismiss();
                    Commons.showToast(mContext, getResources().getString(R.string.something_wants_wrong));
                }
            });
        } else {
            Commons.showToast(mContext, getResources().getString(R.string.no_internet_connection));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (resultCode == Activity.RESULT_OK) {
                if (dialogEditProfile != null) {
                    AppCompatImageView ivEditProfile = (AppCompatImageView) dialogEditProfile.findViewById(R.id.imgEditProfile);
                    RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.ic_upload_photo).transform(new CenterCrop(), new RoundedCorners(30)).error(R.drawable.ic_upload_photo).priority(Priority.HIGH);
                    Glide.with(this).load(Uri.parse(data.getData().toString())).apply(options).into(ivEditProfile);
                    profileImagePath = ImagePicker.Companion.getFilePath(data);
                }
            }
        }
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