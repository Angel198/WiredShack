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
import android.os.Handler;
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
import com.jaylax.wiredshack.model.CommonResponseModel;
import com.jaylax.wiredshack.model.RecentEventMainModel;
import com.jaylax.wiredshack.model.UserDetailsModel;
import com.jaylax.wiredshack.rest.ApiClient;
import com.jaylax.wiredshack.user.dashboard.DashboardActivity;
import com.jaylax.wiredshack.user.following.UserFollowingActivity;
import com.jaylax.wiredshack.utils.Commons;
import com.jaylax.wiredshack.utils.SharePref;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

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

        if (userDetailsModel.getAboutMe() == null){
            mBinding.linearProfileAboutMe.setVisibility(View.GONE);
        }else {
            if (userDetailsModel.getAboutMe().isEmpty()){
                mBinding.linearProfileAboutMe.setVisibility(View.GONE);
            }else {
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
        getRecentEvents();
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
            mBinding.recyclerHomeRecentEventEvents.setLayoutManager(new GridLayoutManager(getActivity(), 3));
            mBinding.recyclerHomeRecentEventEvents.setAdapter(new ManagerRecentEventsAdapter(mContext, list, (data, lisType) -> {
                /*if (lisType.isEmpty()) {
                    DashboardEventManagerActivity.redirectToEditEvent(data.getId(), getActivity());
                }*/
                Intent intent = new Intent(mContext, ManagerEditEventNewActivity.class);
                intent.putExtra("eventId", data.getId());
                startActivity(intent);
            }));
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
}