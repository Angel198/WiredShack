package com.jaylax.wiredshack;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.iid.FirebaseInstanceId;
import com.jaylax.wiredshack.databinding.ActivityLoginSignupBinding;
import com.jaylax.wiredshack.eventManager.dashboard.DashboardEventManagerActivity;
import com.jaylax.wiredshack.model.CommonResponseModel;
import com.jaylax.wiredshack.model.LoginResponseModel;
import com.jaylax.wiredshack.model.UserDetailsModel;
import com.jaylax.wiredshack.rest.ApiClient;
import com.jaylax.wiredshack.user.dashboard.DashboardActivity;
import com.jaylax.wiredshack.utils.Commons;
import com.jaylax.wiredshack.utils.SharePref;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginSignupActivity extends AppCompatActivity {

    ActivityLoginSignupBinding mBinding;
    Context context;
    boolean isUser = true, isClub = false, isDJ = false;
    //    {1 : User, 2 : Club/EventManager, 3 : DJ}
    String userType = "1";
    ProgressDialog progressDialog;
    boolean isFromSplash = false;

    Calendar selectedStartTimeCal = null;
    Calendar selectedEndTimeCal = null;
    Place place = null;
    String addressLat = "", addressLong = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login_signup);
        context = this;
        progressDialog = new ProgressDialog(context);

        if (getIntent().hasExtra("fromSplash")) {
            isFromSplash = getIntent().getBooleanExtra("fromSplash", false);
        }

        Places.initialize(getApplicationContext(), getResources().getString(R.string.google_place_key));

        /*mBinding.imgLogin.setOnClickListener(view -> {
            mBinding.linearSignupChild.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.slide_down));
            mBinding.linearSignupChild.animate().alpha(0.0f);

            mBinding.linearLoginChild.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.slide_up));
            mBinding.linearLoginChild.animate().alpha(1.0f);
        });*/

        setSelectionUI();
        setListener();
    }

    private void setListener() {
        mBinding.tvSelectionUser.setOnClickListener(view -> {
            if (!isUser) {
                userType = "1";
                isUser = true;
                isClub = false;
                isDJ = false;
                setSelectionUI();
            }
        });
        mBinding.tvSelectionClub.setOnClickListener(view -> {
            if (!isClub) {
                userType = "2";
                isUser = false;
                isClub = true;
                isDJ = false;
                setSelectionUI();
            }
        });

        mBinding.tvSelectionDJ.setOnClickListener(view -> {
            if (!isDJ) {
                userType = "3";
                isUser = false;
                isClub = false;
                isDJ = true;
                setSelectionUI();
            }
        });

        mBinding.imgLogin.setOnClickListener(view -> {
            if (mBinding.linearLoginChild.getVisibility() == View.GONE) {
                mBinding.linearLoginChild.setVisibility(View.VISIBLE);
                mBinding.nestedSignupChild.setVisibility(View.GONE);
                mBinding.imgSignupGoogle.setVisibility(View.GONE);
                mBinding.imgSignupFacebook.setVisibility(View.GONE);
                mBinding.linearLoginChild.animate().alpha(0.0f);
                mBinding.nestedSignupChild.animate().alpha(1.0f);
                mBinding.linearLoginChild.animate().alpha(1.0f).translationY(0);
                mBinding.nestedSignupChild.animate().alpha(0.0f).translationY(0);
                setSelectionUI();
            } else {
                callLogin(true);
            }
        });

        mBinding.tvSignup.setOnClickListener(view -> {
            if (mBinding.nestedSignupChild.getVisibility() == View.GONE) {
                mBinding.linearLoginChild.setVisibility(View.GONE);
                mBinding.nestedSignupChild.setVisibility(View.VISIBLE);
                mBinding.imgSignupGoogle.setVisibility(View.VISIBLE);
                mBinding.imgSignupFacebook.setVisibility(View.VISIBLE);
                mBinding.linearLoginChild.animate().alpha(1.0f);
                mBinding.nestedSignupChild.animate().alpha(0.0f);
                mBinding.linearLoginChild.animate().alpha(0.0f).translationY(0);
                mBinding.nestedSignupChild.animate().alpha(1.0f).translationY(0);
                setSelectionUI();
            } else {
                callRegistration();
            }
        });

        mBinding.imgClose.setOnClickListener(view -> {
            if (isFromSplash) {
                Intent intent = new Intent(this, DashboardActivity.class);
                startActivity(intent);
                finishAffinity();
            } else {
                onBackPressed();
            }
        });

        mBinding.tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(context, ForgetPasswordActivity.class);
            startActivity(intent);
        });

        mBinding.editSignupClubType.setOnClickListener(view -> {
            openTypeSelectPopup();
        });

        mBinding.editSignupClubStartTime.setOnClickListener(view -> {
            int hours = selectedStartTimeCal == null ? 12 : selectedStartTimeCal.get(Calendar.HOUR_OF_DAY);
            int mins = selectedStartTimeCal == null ? 0 : selectedStartTimeCal.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(context, (timePicker, selectedHour, selectedMinute) -> {
                selectedStartTimeCal = Calendar.getInstance();
                selectedStartTimeCal.set(Calendar.HOUR_OF_DAY, selectedHour);
                selectedStartTimeCal.set(Calendar.MINUTE, selectedMinute);
                String myFormat = "hh:mm a"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                mBinding.editSignupClubStartTime.setText(sdf.format(selectedStartTimeCal.getTime()));
            }, hours, mins, false);//Yes 24 hour time
            mTimePicker.setTitle("Select Opening Time");
            mTimePicker.show();
        });

        mBinding.editSignupClubEndTime.setOnClickListener(view -> {
            int hours = selectedEndTimeCal == null ? 12 : selectedEndTimeCal.get(Calendar.HOUR_OF_DAY);
            int mins = selectedEndTimeCal == null ? 0 : selectedEndTimeCal.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(context, (timePicker, selectedHour, selectedMinute) -> {
                selectedEndTimeCal = Calendar.getInstance();
                selectedEndTimeCal.set(Calendar.HOUR_OF_DAY, selectedHour);
                selectedEndTimeCal.set(Calendar.MINUTE, selectedMinute);
                String myFormat = "hh:mm a"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                mBinding.editSignupClubEndTime.setText(sdf.format(selectedEndTimeCal.getTime()));
            }, hours, mins, false);//Yes 24 hour time
            mTimePicker.setTitle("Select Closing Time");
            mTimePicker.show();
        });

        mBinding.editSignupClubAddress.setOnClickListener(view -> {
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)).build(context);
            startActivityForResult(intent, 102);
        });
    }

    private void openTypeSelectPopup() {
        PopupMenu popup = new PopupMenu(LoginSignupActivity.this, mBinding.editSignupClubType);
        popup.getMenu().add("Club");
        popup.getMenu().add("Bar");
        popup.getMenu().add("Lounge");
        popup.getMenu().add("Pub");
        popup.getMenu().add("Tavern");
        popup.getMenu().add("Restaurant");
        popup.getMenu().add("Sports Bar");
        popup.getMenu().add("Disco");
        popup.getMenu().add("Other");

        popup.setOnMenuItemClickListener(item -> {
            mBinding.editSignupClubType.setText(item.getTitle());
            return false;
        });
        popup.show();
    }

    private void setSelectionUI() {
        if (isUser) {
            mBinding.tvSelectionUser.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.back_login_selection_child));
        } else {
            mBinding.tvSelectionUser.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.back_login_selection_main));
        }
        if (isClub) {
            mBinding.tvSelectionClub.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.back_login_selection_child));
            if (mBinding.nestedSignupChild.getVisibility() == View.VISIBLE) {
                mBinding.llClubOtherField.setVisibility(View.VISIBLE);
                mBinding.imgSignupGoogle.setVisibility(View.GONE);
                mBinding.imgSignupFacebook.setVisibility(View.GONE);
            }
        } else {
            mBinding.tvSelectionClub.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.back_login_selection_main));
            if (mBinding.nestedSignupChild.getVisibility() == View.VISIBLE) {
                mBinding.llClubOtherField.setVisibility(View.GONE);
                mBinding.imgSignupGoogle.setVisibility(View.VISIBLE);
                mBinding.imgSignupFacebook.setVisibility(View.VISIBLE);
            }
        }

        if (isDJ) {
            mBinding.tvSelectionDJ.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.back_login_selection_child));
        } else {
            mBinding.tvSelectionDJ.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.back_login_selection_main));
        }
    }

    private void callRegistration() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, instanceIdResult -> {

            String token = instanceIdResult.getToken();
            Log.e("DeviceToken", token);

            String email = Objects.requireNonNull(mBinding.editSignupEmail.getText()).toString().trim();
            String name = Objects.requireNonNull(mBinding.editSignupName.getText()).toString().trim();
            String password = Objects.requireNonNull(mBinding.editSignupPassword.getText()).toString().trim();
            String clubName = Objects.requireNonNull(mBinding.editSignupClubName.getText()).toString().trim();
            String clubAddress = Objects.requireNonNull(mBinding.editSignupClubAddress.getText()).toString().trim();
            String clubOpenTime = Objects.requireNonNull(mBinding.editSignupClubStartTime.getText()).toString().trim();
            String clubCloseTime = Objects.requireNonNull(mBinding.editSignupClubEndTime.getText()).toString().trim();
            String clubType = Objects.requireNonNull(mBinding.editSignupClubType.getText()).toString().trim();

            boolean isValid = false;
            if (selectedStartTimeCal != null && selectedEndTimeCal != null) {
                Date sDate = selectedStartTimeCal.getTime();
                Date eDate = selectedEndTimeCal.getTime();
                if (!sDate.after(eDate)) {
                    isValid = true;
                }
            }
            if (name.isEmpty()) {
                Commons.showToast(context, getResources().getString(R.string.enter_name));
            } else if (email.isEmpty()) {
                Commons.showToast(context, getResources().getString(R.string.enter_email));
            } else if (!Commons.isValidEmail(email)) {
                Commons.showToast(context, getResources().getString(R.string.enter_valid_email));
            } else if (password.isEmpty()) {
                Commons.showToast(context, getResources().getString(R.string.enter_password));
            } else if (userType.equalsIgnoreCase("2") && clubName.isEmpty()) {
                Commons.showToast(context, getResources().getString(R.string.enter_club_name));
            } else if (userType.equalsIgnoreCase("2") && clubAddress.isEmpty()) {
                Commons.showToast(context, getResources().getString(R.string.enter_physical_address));
            } else if (userType.equalsIgnoreCase("2") && selectedStartTimeCal == null) {
                Commons.showToast(context, getResources().getString(R.string.select_opening_time));
            } else if (userType.equalsIgnoreCase("2") && selectedEndTimeCal == null) {
                Commons.showToast(context, getResources().getString(R.string.select_closing_time));
            } else if (userType.equalsIgnoreCase("2") && clubType.isEmpty()) {
                Commons.showToast(context, getResources().getString(R.string.select_club_type));
            } else if (userType.equalsIgnoreCase("2") && !isValid) {
                Commons.showToast(context, getResources().getString(R.string.start_end_time_not_valid_msg));
            } else {

                if (isValid) {
                    /*if (Commons.isOnline(context)) {
                        progressDialog.show();
                        HashMap<String, String> params = new HashMap<>();
                        params.put("email", email);
                        params.put("password", password);
                        params.put("name", name);
                        params.put("user_type", userType);
                        params.put("device_token", token);

                        if (userType.equalsIgnoreCase("2")) {
                            params.put("clubName", clubName);
                            params.put("clubAddress", clubAddress);
                            params.put("clubOpenTime", clubOpenTime);
                            params.put("clubCloseTime", clubCloseTime);
                            params.put("clubType", clubType);
                            params.put("latitude", addressLat);
                            params.put("longitude", addressLong);
                        } else {
                            params.put("clubName", "");
                            params.put("clubAddress", "");
                            params.put("clubOpenTime", "");
                            params.put("clubCloseTime", "");
                            params.put("clubType", "");
                            params.put("latitude", "");
                            params.put("longitude", "");
                        }

                        ApiClient.create().register(params).enqueue(new Callback<CommonResponseModel>() {
                            @Override
                            public void onResponse(Call<CommonResponseModel> call, Response<CommonResponseModel> response) {
                                progressDialog.dismiss();
                                if (response.code() == 200 && response.isSuccessful()) {
                                    if (response.body() != null) {
                                        if (response.body().getStatus().equals("200")) {
                                            callLogin(false);
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
                    } else {
                        Commons.showToast(context, getResources().getString(R.string.no_internet_connection));
                    }*/
                }
            }
        });
    }

    private void callLogin(boolean isLogin) {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, instanceIdResult -> {
            String token = instanceIdResult.getToken();

            String email;
            String password;
            if (isLogin) {
                email = mBinding.editLoginEmail.getText().toString().trim();
                password = mBinding.editLoginPassword.getText().toString().trim();
            } else {
                email = mBinding.editSignupEmail.getText().toString().trim();
                password = mBinding.editSignupPassword.getText().toString().trim();
            }

            if (email.isEmpty()) {
                Commons.showToast(context, getResources().getString(R.string.enter_email));
            } else if (!Commons.isValidEmail(email)) {
                Commons.showToast(context, getResources().getString(R.string.enter_valid_email));
            } else if (password.isEmpty()) {
                Commons.showToast(context, getResources().getString(R.string.enter_password));
            } else {
                if (Commons.isOnline(context)) {
                    progressDialog.show();
                    HashMap<String, String> params = new HashMap<>();
                    params.put("email", email);
                    params.put("password", password);
                    params.put("user_type", userType);
                    params.put("device_token", token);

                    ApiClient.create().login(params).enqueue(new Callback<LoginResponseModel>() {
                        @Override
                        public void onResponse(Call<LoginResponseModel> call, Response<LoginResponseModel> response) {
                            progressDialog.dismiss();
                            if (response.code() == 200 && response.isSuccessful()) {
                                if (response.body() != null) {
                                    if (response.body().getStatus().equals("200")) {
                                        SharePref.getInstance(context).save(SharePref.PREF_TOKEN, response.body().getAccessToken());
                                        getUserDetails();
                                    } else {
                                        String msg = "";
                                        if (response.body().getMessage().isEmpty()) {
                                            msg = getResources().getString(R.string.please_try_after_some_time);
                                        } else {
                                            msg = response.body().getMessage();
                                        }
                                        Commons.showToast(context, msg);
                                    }
                                } else {
                                    Commons.showToast(context, getResources().getString(R.string.please_try_after_some_time));
                                }
                            } else {
                                Commons.showToast(context, getResources().getString(R.string.please_try_after_some_time));
                            }
                        }

                        @Override
                        public void onFailure(Call<LoginResponseModel> call, Throwable t) {
                            progressDialog.dismiss();
                            Commons.showToast(context, getResources().getString(R.string.something_wants_wrong));
                        }
                    });
                } else {
                    Commons.showToast(context, getResources().getString(R.string.no_internet_connection));
                }
            }
        });
    }

    private void getUserDetails() {
        if (Commons.isOnline(context)) {
            progressDialog.show();
            String header = "Bearer " + SharePref.getInstance(context).get(SharePref.PREF_TOKEN, "");
            ApiClient.create().userDetails(header).enqueue(new Callback<UserDetailsModel>() {
                @Override
                public void onResponse(Call<UserDetailsModel> call, Response<UserDetailsModel> response) {
                    progressDialog.dismiss();
                    if (response.code() == 200 && response.isSuccessful()) {
                        if (response.body() != null) {
                            SharePref.getInstance(context).save(SharePref.PREF_USER, Commons.convertObjectToString(response.body()));
                            Intent intent;
                            if (response.body().getUserType().equals("1")) {
                                intent = new Intent(context, DashboardActivity.class);
                            } else {
                                intent = new Intent(context, DashboardEventManagerActivity.class);
                            }
                            context.startActivity(intent);
                            finishAffinity();
                        }
                    } else {
                        Commons.showToast(context, getResources().getString(R.string.please_try_after_some_time));
                    }
                }

                @Override
                public void onFailure(Call<UserDetailsModel> call, Throwable t) {
                    progressDialog.dismiss();
                    Commons.showToast(context, getResources().getString(R.string.something_wants_wrong));
                }
            });
        } else {
            Commons.showToast(context, getResources().getString(R.string.no_internet_connection));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 102) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                place = Autocomplete.getPlaceFromIntent(data);

                String placeName = place.getName() == null ? "N/A" : place.getName();
                String placeAddress = place.getAddress() == null ? "N/A" : place.getAddress();
                String address = placeName + ", " + placeAddress;
                addressLat = String.valueOf(Objects.requireNonNull(place.getLatLng()).latitude);
                addressLong = String.valueOf(place.getLatLng().longitude);
                mBinding.editSignupClubAddress.setText(address);
            }
        }
    }
}