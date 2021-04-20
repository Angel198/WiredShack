package com.jaylax.wiredshack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jaylax.wiredshack.databinding.ActivityRegistrationBinding;
import com.jaylax.wiredshack.eventManager.dashboard.DashboardEventManagerActivity;
import com.jaylax.wiredshack.model.CommonResponseModel;
import com.jaylax.wiredshack.rest.ApiClient;
import com.jaylax.wiredshack.user.dashboard.DashboardActivity;
import com.jaylax.wiredshack.utils.Commons;
import com.jaylax.wiredshack.utils.SharePref;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity extends AppCompatActivity {


    ActivityRegistrationBinding mBinding;
    ProgressDialog progressDialog;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_registration);
        mContext = this;
        progressDialog = new ProgressDialog(mContext);


        mBinding.textSignup.setOnClickListener(view -> {
            String email = mBinding.userEmail.getText().toString().trim();
            String name = mBinding.username.getText().toString().trim();
            String password = mBinding.password.getText().toString().trim();
            String confirmPass = mBinding.confirmPassword.getText().toString().trim();

            RadioButton radioButton = (RadioButton) findViewById(mBinding.radioGroup.getCheckedRadioButtonId());
            String userType = "0";
            if (radioButton.getText().toString().equals("User")) {
                userType = "1";
            } else {
                userType = "2";
            }

            if (name.isEmpty()) {
                Commons.showToast(mContext, getResources().getString(R.string.enter_name));
            } else if (email.isEmpty()) {
                Commons.showToast(mContext, getResources().getString(R.string.enter_email));
            } else if (!Commons.isValidEmail(email)) {
                Commons.showToast(mContext, getResources().getString(R.string.enter_valid_email));
            } else if (password.isEmpty()) {
                Commons.showToast(mContext, getResources().getString(R.string.enter_password));
            } else if (confirmPass.isEmpty()) {
                Commons.showToast(mContext, getResources().getString(R.string.enter_confirm_password));
            } else if (!password.equals(confirmPass)) {
                Commons.showToast(mContext, getResources().getString(R.string.password_must_be_same));
            } else {
                if (Commons.isOnline(mContext)) {
                    progressDialog.show();
                    HashMap<String, String> params = new HashMap<>();
                    params.put("email", mBinding.userEmail.getText().toString().trim());
                    params.put("password", mBinding.confirmPassword.getText().toString().trim());
                    params.put("name", mBinding.username.getText().toString().trim());
                    params.put("user_type", userType);

                    ApiClient.create().register(params).enqueue(new Callback<CommonResponseModel>() {
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
                                        Commons.showToast(mContext, msg);
                                    }
                                }
                            } else {
                                Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                            }
                        }

                        @Override
                        public void onFailure(Call<CommonResponseModel> call, Throwable t) {
                            progressDialog.dismiss();
                            Commons.showToast(mContext, getResources().getString(R.string.something_wants_wrong));
                        }
                    });
                } else {
                    Commons.showToast(mContext, getResources().getString(R.string.no_internet_connection));
                }
            }
        });
    }


}