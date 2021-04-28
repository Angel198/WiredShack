package com.jaylax.wiredshack.eventManager.followed;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.os.Bundle;

import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ActivityManagerFollowedBinding;
import com.jaylax.wiredshack.user.following.UserFollowingAdapter;

public class ManagerFollowedActivity extends AppCompatActivity {

    ActivityManagerFollowedBinding mBinding;
    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_manager_followed);
        mContext = this;

        mBinding.imgBack.setOnClickListener(view -> onBackPressed());
        mBinding.recyclerManagerFollowed.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recyclerManagerFollowed.setAdapter(new ManagerFollowedAdapter());
    }
}