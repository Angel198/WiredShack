package com.jaylax.wiredshack;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.RadioButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.jaylax.wiredshack.databinding.ActivityEditProfileBinding;
import com.jaylax.wiredshack.model.CommonResponseModel;
import com.jaylax.wiredshack.model.UserDetailsModel;
import com.jaylax.wiredshack.rest.Api;
import com.jaylax.wiredshack.rest.ApiClient;
import com.jaylax.wiredshack.utils.Commons;
import com.jaylax.wiredshack.utils.SharePref;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    ActivityEditProfileBinding mBinding;
    UserDetailsModel userDetailsModel;

    final Calendar selectedCalendar = Calendar.getInstance();
    String profileImagePath = "", coverPhotoImagePath = "";
    RequestOptions options;
    Context context;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile);
        context = this;
        progressDialog = new ProgressDialog(context);
        setClickListener();


        userDetailsModel = Commons.convertStringToObject(this, SharePref.PREF_USER, UserDetailsModel.class);

        options = new RequestOptions().centerCrop().placeholder(R.drawable.place_holder).transform(new CenterCrop()).error(R.drawable.place_holder).priority(Priority.HIGH);
        Glide.with(this).load(userDetailsModel.getImage() == null ? "" : userDetailsModel.getImage()).apply(options).into(mBinding.imgUserProfile);
        Glide.with(this).load(userDetailsModel.getCoverImage() == null ? "" : userDetailsModel.getCoverImage()).apply(options).into(mBinding.imgUserCoverPhoto);

        mBinding.editUserName.setText(userDetailsModel.getName());
        mBinding.editUserEmail.setText(userDetailsModel.getEmail());
        mBinding.editUserBirthDate.setText(userDetailsModel.getBirthDate());
        mBinding.editUserPhone.setText(userDetailsModel.getPhone());

        if (userDetailsModel.getGender() != null) {
            if (userDetailsModel.getGender().equals(getResources().getString(R.string.female))) {
                mBinding.radioGroup.check(R.id.radioButtonFemale);
            } else {
                mBinding.radioGroup.check(R.id.radioButtonMale);
            }
        } else {
            mBinding.radioGroup.check(R.id.radioButtonMale);
        }


    }

    private void setClickListener() {

        mBinding.imgBack.setOnClickListener(view -> onBackPressed());

        mBinding.imgUserProfile.setOnClickListener(view -> {
            showImagePickerAlert(101);
        });

        mBinding.imgUserCoverPhoto.setOnClickListener(view -> {
            showImagePickerAlert(102);
        });

        mBinding.tvUserProfile.setOnClickListener(view -> mBinding.imgUserProfile.performClick());
        mBinding.tvUserUploadCoverPhoto.setOnClickListener(view -> mBinding.imgUserCoverPhoto.performClick());

        mBinding.editUserBirthDate.setOnClickListener(view -> {
            DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                    selectedCalendar.set(Calendar.YEAR, year);
                    selectedCalendar.set(Calendar.MONTH, monthOfYear);
                    selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    String myFormat = "dd-MM-yyyy"; //In which you need put here
                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                    mBinding.editUserBirthDate.setText(sdf.format(selectedCalendar.getTime()));
                }
            }, selectedCalendar.get(Calendar.YEAR), selectedCalendar.get(Calendar.MONTH),
                    selectedCalendar.get(Calendar.DAY_OF_MONTH));
            dialog.getDatePicker().setMaxDate(new Date().getTime());
            dialog.show();
        });

        mBinding.tvUserProfileDone.setOnClickListener(view -> {
            String userName = Objects.requireNonNull(mBinding.editUserName.getText()).toString().trim();
            String phoneNumber = Objects.requireNonNull(mBinding.editUserPhone.getText()).toString().trim();
            RadioButton radioButton = (RadioButton) findViewById(mBinding.radioGroup.getCheckedRadioButtonId());
            String gender = radioButton.getText().toString();
            String dob = Objects.requireNonNull(mBinding.editUserBirthDate.getText()).toString().trim();
            if (userName.isEmpty()) {
                Commons.showToast(context, getResources().getString(R.string.enter_name));
            } else if (phoneNumber.isEmpty()) {
                Commons.showToast(context, getResources().getString(R.string.enter_phone_number));
            } else if (phoneNumber.length() < 10) {
                Commons.showToast(context, getResources().getString(R.string.enter_valid_phone_number));
            } else if (dob.isEmpty()) {
                Commons.showToast(context, getResources().getString(R.string.enter_dob));
            } else {
                updateProfile(userName, phoneNumber, gender, dob);
            }
        });
    }

    private void showImagePickerAlert(Integer requestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);

    }

    private void updateProfile(String userName, String phoneNumber, String gender, String dob) {
        if (Commons.isOnline(context)) {
            File profileFile = new File(profileImagePath);
            MultipartBody.Part profileBody = null;
            if (!profileImagePath.isEmpty()) {
                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), profileFile);
                profileBody = MultipartBody.Part.createFormData("image", profileFile.getName(), requestBody);
            }

            File coverFile = new File(coverPhotoImagePath);
            MultipartBody.Part coverBody = null;
            if (!coverPhotoImagePath.isEmpty()) {
                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), coverFile);
                coverBody = MultipartBody.Part.createFormData("cover_image", coverFile.getName(), requestBody);
            }

            HashMap<String, RequestBody> params = new HashMap<>();
            params.put("name", RequestBody.create(MultipartBody.FORM, userName));
            params.put("phone", RequestBody.create(MultipartBody.FORM, phoneNumber));
            params.put("gender", RequestBody.create(MultipartBody.FORM, gender));
            params.put("birth_date", RequestBody.create(MultipartBody.FORM, dob));

            String header = "Bearer " + SharePref.getInstance(context).get(SharePref.PREF_TOKEN, "");

            Call<CommonResponseModel> retrofitCall = null;

            if (profileBody == null && coverBody == null) {
                retrofitCall = ApiClient.create().updateProfile(header,params);
            } else if (profileBody != null && coverBody != null) {
                retrofitCall = ApiClient.create().updateProfile(header,params, profileBody, coverBody);
            } else {
                if (profileBody != null) {
                    retrofitCall = ApiClient.create().updateProfile(header,params, profileBody);
                }
                if (coverBody != null) {
                    retrofitCall = ApiClient.create().updateProfile(header,coverBody, params);
                }
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
                                    finish();
                                } else {
                                    String msg = "";
                                    if (response.body().getMessage().isEmpty()) {
                                        msg = getResources().getString(R.string.please_try_after_some_time);
                                    } else {
                                        msg = response.body().getMessage();
                                    }
                                    Commons.showToast(context, msg);
                                }
                            }
                        } else {
                            Commons.showToast(context, getResources().getString(R.string.please_try_after_some_time));
                        }
                    }

                    @Override
                    public void onFailure(Call<CommonResponseModel> call, Throwable t) {
                        progressDialog.dismiss();
                        Commons.showToast(context, getResources().getString(R.string.something_wants_wrong));
                    }
                });
            }
        } else {
            Commons.showToast(context, getResources().getString(R.string.no_internet_connection));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (resultCode == Activity.RESULT_OK) {
//                mBinding.imgUserProfile.setImageURI(data.getData());
                Glide.with(this).load(Uri.parse(data.getData().toString())).apply(options).into(mBinding.imgUserProfile);
                profileImagePath = ImagePicker.Companion.getFilePath(data);
            }
        } else if (requestCode == 102) {
            if (resultCode == Activity.RESULT_OK) {
                Glide.with(this).load(Uri.parse(data.getData().toString())).apply(options).into(mBinding.imgUserCoverPhoto);
                coverPhotoImagePath = ImagePicker.Companion.getFilePath(data);
            }
        }
    }
}