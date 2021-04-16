package com.jaylax.wiredshack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;

import com.jaylax.wiredshack.databinding.ActivityEditProfileBinding;

public class EditProfileActivity extends AppCompatActivity {

    ActivityEditProfileBinding mBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_edit_profile);
        mBinding.imgBack.setOnClickListener(view -> onBackPressed());
    }
}