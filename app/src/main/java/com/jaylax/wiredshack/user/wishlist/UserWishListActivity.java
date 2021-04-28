package com.jaylax.wiredshack.user.wishlist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ActivityUserWishListBinding;
import com.jaylax.wiredshack.user.eventDetails.EventDetailsActivity;
import com.jaylax.wiredshack.user.home.HomeRecentEventAdapter;

public class UserWishListActivity extends AppCompatActivity {

    ActivityUserWishListBinding mBinding;
    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_user_wish_list);
        mContext = this;

        mBinding.imgBack.setOnClickListener(view -> onBackPressed());
        mBinding.recyclerUserWishList.setLayoutManager(new GridLayoutManager(mContext,3));
        mBinding.recyclerUserWishList.setAdapter(new UserWishListAdapter(mContext));
    }
}