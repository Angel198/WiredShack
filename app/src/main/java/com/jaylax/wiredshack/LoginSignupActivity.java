package com.jaylax.wiredshack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;

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

import java.util.HashMap;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login_signup);
        context = this;
        progressDialog = new ProgressDialog(context);

        if (getIntent().hasExtra("fromSplash")) {
            isFromSplash = getIntent().getBooleanExtra("fromSplash", false);
        }
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
                mBinding.linearSignupChild.setVisibility(View.GONE);
                mBinding.linearLoginChild.animate().alpha(0.0f);
                mBinding.linearSignupChild.animate().alpha(1.0f);
                mBinding.linearLoginChild.animate().alpha(1.0f).translationY(0);
                mBinding.linearSignupChild.animate().alpha(0.0f).translationY(0);
            } else {
                callLogin(true);
            }
        });

        mBinding.tvSignup.setOnClickListener(view -> {
            if (mBinding.linearSignupChild.getVisibility() == View.GONE) {
                mBinding.linearLoginChild.setVisibility(View.GONE);
                mBinding.linearSignupChild.setVisibility(View.VISIBLE);
                mBinding.linearLoginChild.animate().alpha(1.0f);
                mBinding.linearSignupChild.animate().alpha(0.0f);
                mBinding.linearLoginChild.animate().alpha(0.0f).translationY(0);
                mBinding.linearSignupChild.animate().alpha(1.0f).translationY(0);
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
    }

    private void setSelectionUI() {
        if (isUser) {
            mBinding.tvSelectionUser.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.back_login_selection_child));
        } else {
            mBinding.tvSelectionUser.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.back_login_selection_main));
        }
        if (isClub) {
            mBinding.tvSelectionClub.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.back_login_selection_child));
        } else {
            mBinding.tvSelectionClub.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.back_login_selection_main));
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

            String email = mBinding.editSignupEmail.getText().toString().trim();
            String name = mBinding.editSignupName.getText().toString().trim();
            String password = mBinding.editSignupPassword.getText().toString().trim();

            if (name.isEmpty()) {
                Commons.showToast(context, getResources().getString(R.string.enter_name));
            } else if (email.isEmpty()) {
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
                    params.put("name", name);
                    params.put("user_type", userType);
                    params.put("device_token", token);

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
}