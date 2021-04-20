package com.jaylax.wiredshack;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.jaylax.wiredshack.databinding.ActivityLoginBinding;
import com.jaylax.wiredshack.eventManager.dashboard.DashboardEventManagerActivity;
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

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding mBinding;
    Context mContext;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        mContext = this;

        progressDialog = new ProgressDialog(mContext);

        mBinding.textLogin.setOnClickListener(view -> {
            String email = mBinding.username.getText().toString().trim();
            String password = mBinding.password.getText().toString().trim();

            RadioButton radioButton = (RadioButton) findViewById(mBinding.radioGroup.getCheckedRadioButtonId());

            String userType = "0";
            if (radioButton.getText().toString().equals("User")) {
                userType = "1";
            } else {
                userType = "2";
            }

            if (email.isEmpty()) {
                Commons.showToast(mContext, getResources().getString(R.string.enter_email));
            } else if (!Commons.isValidEmail(email)) {
                Commons.showToast(mContext, getResources().getString(R.string.enter_valid_email));
            } else if (password.isEmpty()) {
                Commons.showToast(mContext, getResources().getString(R.string.enter_password));
            } else {
                if (Commons.isOnline(mContext)) {
                    progressDialog.show();
                    HashMap<String, String> params = new HashMap<>();
                    params.put("email", email);
                    params.put("password", password);
                    params.put("user_type", userType);

                    ApiClient.create().login(params).enqueue(new Callback<LoginResponseModel>() {
                        @Override
                        public void onResponse(Call<LoginResponseModel> call, Response<LoginResponseModel> response) {
                            progressDialog.dismiss();
                            if (response.code() == 200 && response.isSuccessful()) {
                                if (response.body() != null) {
                                    if (response.body().getStatus().equals("200")) {
                                        SharePref.getInstance(mContext).save(SharePref.PREF_TOKEN, response.body().getAccessToken());
                                        getUserDetails();
                                    }else {
                                        String msg = "";
                                        if (response.body().getMessage().isEmpty()) {
                                            msg = getResources().getString(R.string.please_try_after_some_time);
                                        } else {
                                            msg = response.body().getMessage();
                                        }
                                        Commons.showToast(mContext, msg);
                                    }
                                } else {
                                    Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                                }
                            } else {
                                Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                            }
                        }

                        @Override
                        public void onFailure(Call<LoginResponseModel> call, Throwable t) {
                            progressDialog.dismiss();
                            Commons.showToast(mContext, getResources().getString(R.string.something_wants_wrong));
                        }
                    });
                } else {
                    Commons.showToast(mContext, getResources().getString(R.string.no_internet_connection));
                }
            }
        });

        mBinding.forgetPass.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
            startActivity(intent);
        });

        mBinding.textRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });

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
                            Intent intent;
                            if (response.body().getUserType().equals("1")) {
                                intent = new Intent(mContext, DashboardActivity.class);
                            } else {
                                intent = new Intent(mContext, DashboardEventManagerActivity.class);
                            }
                            mContext.startActivity(intent);
                            finishAffinity();
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
}