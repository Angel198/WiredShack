package com.jaylax.wiredshack.user.following;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ActivityUserFollowingBinding;

public class UserFollowingActivity extends AppCompatActivity {

    ActivityUserFollowingBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_user_following);

        mBinding.recyclerUserFollowing.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recyclerUserFollowing.setAdapter(new UserFollowingAdapter());
    }
}