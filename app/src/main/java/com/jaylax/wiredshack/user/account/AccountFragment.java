package com.jaylax.wiredshack.user.account;

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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.gson.Gson;
import com.jaylax.wiredshack.MainActivity;
import com.jaylax.wiredshack.ProgressDialog;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.FragmentAccountBinding;
import com.jaylax.wiredshack.eventManager.editEvent.EventImageModel;
import com.jaylax.wiredshack.eventimage.EventImagesActivity;
import com.jaylax.wiredshack.model.CommonResponseModel;
import com.jaylax.wiredshack.model.UserDetailsModel;
import com.jaylax.wiredshack.rest.ApiClient;
import com.jaylax.wiredshack.user.eventDetails.EventDetailsActivity;
import com.jaylax.wiredshack.user.following.UserFollowingActivity;
import com.jaylax.wiredshack.utils.Commons;
import com.jaylax.wiredshack.utils.SharePref;
import com.jaylax.wiredshack.utils.SpannedGridLayoutManager;

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

public class AccountFragment extends Fragment {

    FragmentAccountBinding mBinding;
    Context mContext;
    ProgressDialog progressDialog;
    String followingCount = "0";
    Boolean isMale, isFemale;

    Dialog dialogEditProfile = null;
    String profileImagePath = "";
    UserDetailsModel userDetailsModel;


    String tempImageUriPath = "";
    Uri temImageUri = null;

    final static int REQUEST_IMAGE_PICKER = 101;
    final static int REQUEST_IMAGE_POST_PICKER = 102;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_account, container, false);
        mContext = getActivity();
        progressDialog = new ProgressDialog(Objects.requireNonNull(mContext));

        /*mBinding.tvAccountWishList.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), UserWishListActivity.class);
            Objects.requireNonNull(getActivity()).startActivity(intent);
        });*/

        mBinding.linearProfileName.setOnClickListener(view -> {
            if (Integer.parseInt(followingCount) > 0) {
                Intent intent = new Intent(getActivity(), UserFollowingActivity.class);
                Objects.requireNonNull(getActivity()).startActivity(intent);
            }
        });

        mBinding.imgProfileLogout.setOnClickListener(view -> {
            showLogoutDialog();
        });

        mBinding.imgProfileEdit.setOnClickListener(view -> {
            showEditDialog();
        });

        mBinding.imgUploadImage.setOnClickListener(view -> {
            showDialogUploadCoverImage();
        });

        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dialogEditProfile == null) {
            getUserDetails(false);
        } else {
            if (!dialogEditProfile.isShowing()) {
                getUserDetails(false);
            }
        }
    }

    private void getUserDetails(boolean isRefresh) {
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
                            setUserData(isRefresh);
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

    private void setUserData(boolean isRefresh) {
        userDetailsModel = Commons.convertStringToObject(mContext, SharePref.PREF_USER, UserDetailsModel.class);
        RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.place_holder).transform(new CenterCrop()).error(R.drawable.place_holder).priority(Priority.HIGH);
        Glide.with(this).load(userDetailsModel.getImage() == null ? "" : userDetailsModel.getImage()).apply(options).into(mBinding.imgAccountProfile);
//        Glide.with(this).load(userDetailsModel.getCoverImage() == null ? "" : userDetailsModel.getCoverImage()).apply(options).into(mBinding.imgAccountCover);

        mBinding.tvAccountProfileName.setText(userDetailsModel.getName());

        if (userDetailsModel.getFollowing() != null) {
            if (!userDetailsModel.getFollowing().isEmpty()) {
                followingCount = userDetailsModel.getFollowing();
            }
        }
        mBinding.tvFollowCount.setText(followingCount);

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

        if (userDetailsModel.getAboutMe() == null) {
            mBinding.linearProfileAboutMe.setVisibility(View.GONE);
        } else {
            if (userDetailsModel.getAboutMe().isEmpty()) {
                mBinding.linearProfileAboutMe.setVisibility(View.GONE);
            } else {
                mBinding.linearProfileAboutMe.setVisibility(View.VISIBLE);
                mBinding.tvProfileAboutMe.setText(userDetailsModel.getAboutMe());
            }
        }

        if (!isRefresh) {
            getImageList();
        }
    }

    private void getImageList() {
        if (Commons.isOnline(mContext)) {
            progressDialog.show();
            String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");
            ApiClient.create().getUserPostImages(header).enqueue(new Callback<UploadImageModel>() {
                @Override
                public void onResponse(Call<UploadImageModel> call, Response<UploadImageModel> response) {
                    progressDialog.dismiss();
                    if (response.code() == 200 && response.isSuccessful()) {
                        if (response.body() != null) {
                            setUploadImageData(response.body().getData());
                        } else {
                            Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                        }
                    } else {
                        Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                    }
                }

                @Override
                public void onFailure(Call<UploadImageModel> call, Throwable t) {
                    progressDialog.dismiss();
                    Commons.showToast(mContext, getResources().getString(R.string.something_wants_wrong));
                }
            });
        } else {
            Commons.showToast(mContext, mContext.getResources().getString(R.string.no_internet_connection));
        }
    }

    private void setUploadImageData(ArrayList<UploadImageModel.UploadImageData> list) {
        if (list.isEmpty()) {
            mBinding.recyclerAccountImages.setVisibility(View.GONE);
        } else {
            mBinding.recyclerAccountImages.setVisibility(View.VISIBLE);
            RecyclerView.LayoutManager manager ;
            if (list.size() > 1){
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
            mBinding.recyclerAccountImages.setHasFixedSize(true);
            mBinding.recyclerAccountImages.setLayoutManager(manager);
            mBinding.recyclerAccountImages.setAdapter(new AccountUploadImageAdapter(mContext, list, data -> {
                ArrayList<EventImageModel> imageList = new ArrayList<>();
                imageList.add(new EventImageModel(data.getPostImg(),data.getId(),"",null));
                Intent intent = new Intent(mContext, EventImagesActivity.class);
                intent.putExtra("eventImages", new Gson().toJson(imageList));
                intent.putExtra("fromProfile", true);
                startActivity(intent);
            }));
        }
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
        etEditProfileAboutMe.setText(userDetailsModel.getAboutMe() == null? "" : userDetailsModel.getAboutMe());

        setMaleFemaleUI();

        dialogEditProfile.findViewById(R.id.imgEditClose).setOnClickListener(view -> {
            dialogEditProfile.dismiss();
        });

        ivEditProfile.setOnClickListener(view -> {
            showImagePickerAlert(REQUEST_IMAGE_PICKER);
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

    private void showDialogUploadCoverImage() {
        dialogEditProfile = new Dialog(mContext);
        dialogEditProfile.setContentView(R.layout.dialog_event_cover_image);

        Window window = dialogEditProfile.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(window.getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);

        dialogEditProfile.findViewById(R.id.imgCoverDialogClose).setOnClickListener(view -> {
            temImageUri = null;
            tempImageUriPath = "";
            dialogEditProfile.dismiss();
        });

        dialogEditProfile.findViewById(R.id.imgCoverDialogImage).setOnClickListener(view -> {
            showImagePickerAlert(REQUEST_IMAGE_POST_PICKER);
        });

        dialogEditProfile.findViewById(R.id.tvCoverDialogDone).setOnClickListener(view -> {
            if (!tempImageUriPath.isEmpty()) {
                uploadImage();
                dialogEditProfile.dismiss();
            } else {
                Commons.showToast(mContext, getResources().getString(R.string.upload_cover_photo));
            }
        });
        dialogEditProfile.setCanceledOnTouchOutside(false);
        dialogEditProfile.show();
    }


    private void showImagePickerAlert(int requestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(getResources().getString(R.string.choose_image_from));
        builder.setCancelable(false);
        builder.setPositiveButton(getResources().getString(R.string.camera), (dialogInterface, i) -> {
            new ImagePicker.Builder(this).crop().cameraOnly().compress(1024).start(requestCode);
        });
        builder.setNegativeButton(getResources().getString(R.string.gallery), (dialogInterface, i) -> {
            new ImagePicker.Builder(this).crop().galleryOnly().compress(1024).start(requestCode);
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
                maleTV.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
            } else {
                maleTV.setBackgroundResource(0);
                maleTV.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorBlack66));
            }

            if (isFemale) {
                femaleTV.setBackgroundResource(R.drawable.back_grey);
                femaleTV.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
            } else {
                femaleTV.setBackgroundResource(0);
                femaleTV.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorBlack66));
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
                                        getUserDetails(true);
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


    private void uploadImage() {
        if (Commons.isOnline(mContext)) {
            File imageFile = new File(tempImageUriPath);
            MultipartBody.Part imageBody = null;
            if (!tempImageUriPath.isEmpty()) {
                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
                imageBody = MultipartBody.Part.createFormData("image", imageFile.getName(), requestBody);
            }

            String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");
            Call<CommonResponseModel> retrofitCall = ApiClient.create().uploadUserImage(header, imageBody);

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
                                        getImageList();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICKER) {
            if (resultCode == Activity.RESULT_OK) {
                if (dialogEditProfile != null) {
                    AppCompatImageView ivEditProfile = (AppCompatImageView) dialogEditProfile.findViewById(R.id.imgEditProfile);
                    RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.ic_upload_photo).transform(new CenterCrop(), new RoundedCorners(30)).error(R.drawable.ic_upload_photo).priority(Priority.HIGH);
                    Glide.with(this).load(Uri.parse(data.getData().toString())).apply(options).into(ivEditProfile);
                    profileImagePath = ImagePicker.Companion.getFilePath(data);
                }
            }
        } else if (requestCode == REQUEST_IMAGE_POST_PICKER) {
            if (resultCode == Activity.RESULT_OK) {
                if (dialogEditProfile != null) {
                    AppCompatImageView ivEditProfile = (AppCompatImageView) dialogEditProfile.findViewById(R.id.imgCoverDialogImage);
                    RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.ic_upload_photo).transform(new CenterCrop(), new RoundedCorners(30)).error(R.drawable.ic_upload_photo).priority(Priority.HIGH);
                    Glide.with(this).load(Uri.parse(data.getData().toString())).apply(options).into(ivEditProfile);
                    tempImageUriPath = ImagePicker.Companion.getFilePath(data);
                }
            }
        }
    }
}