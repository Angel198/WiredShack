package com.jaylax.wiredshack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jaylax.wiredshack.databinding.ActivityForgetPasswordBinding;

public class ForgetPasswordActivity extends AppCompatActivity {

    ActivityForgetPasswordBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_forget_password);

        mBinding.tvSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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