package com.jaylax.wiredshack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jaylax.wiredshack.databinding.ActivityForgetPasswordBinding;
import com.jaylax.wiredshack.model.CommonResponseModel;
import com.jaylax.wiredshack.model.LoginResponseModel;
import com.jaylax.wiredshack.rest.ApiClient;
import com.jaylax.wiredshack.utils.Commons;
import com.jaylax.wiredshack.utils.SharePref;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgetPasswordActivity extends AppCompatActivity {

    ActivityForgetPasswordBinding mBinding;
    Context mContext;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_forget_password);
        mContext = this;
        progressDialog = new ProgressDialog(mContext);

        mBinding.tvSendCode.setOnClickListener(v -> {
            String email = mBinding.editForgotEmail.getText().toString().trim();
            if (email.isEmpty()) {
                Commons.showToast(mContext, getResources().getString(R.string.enter_email));
            } else if (!Commons.isValidEmail(email)) {
                Commons.showToast(mContext, getResources().getString(R.string.enter_valid_email));
            }else {
                if (Commons.isOnline(mContext)) {
                    progressDialog.show();
                    HashMap<String, String> params = new HashMap<>();
                    params.put("email", email);

                    ApiClient.create().forgotPassword(params).enqueue(new Callback<CommonResponseModel>() {
                        @Override
                        public void onResponse(Call<CommonResponseModel> call, Response<CommonResponseModel> response) {
                            progressDialog.dismiss();
                            if (response.code() == 200 && response.isSuccessful()) {
                                if (response.body() != null) {
                                    String msg = "";
                                    if (response.body().getStatus().equals("200")) {
                                        if (response.body().getMessage().isEmpty()) {
                                            msg = getResources().getString(R.string.email_send_success_fully);
                                        } else {
                                            msg = response.body().getMessage();
                                        }
                                        Commons.showToast(mContext, msg);
                                        new Handler().postDelayed(() -> {
                                            finish();
                                        },1500);
                                    } else {
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

        mBinding.imgClose.setOnClickListener(view -> {
            onBackPressed();
        });

        mBinding.imgLogin.setOnClickListener(view -> {
            onBackPressed();
        });
    }
}