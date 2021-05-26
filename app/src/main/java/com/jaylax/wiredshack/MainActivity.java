package com.jaylax.wiredshack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.jaylax.wiredshack.databinding.ActivityMainBinding;
import com.jaylax.wiredshack.eventManager.dashboard.DashboardEventManagerActivity;
import com.jaylax.wiredshack.model.UserDetailsModel;
import com.jaylax.wiredshack.user.dashboard.DashboardActivity;
import com.jaylax.wiredshack.utils.Commons;
import com.jaylax.wiredshack.utils.SharePref;

public class MainActivity extends AppCompatActivity {

    Context mContext;
    ActivityMainBinding mainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mContext = this;

        new Handler().postDelayed(this::redirectToFlow, 3000);
        setListener();
    }

    private void redirectToFlow() {
        if (SharePref.getInstance(mContext).get(SharePref.PREF_USER, "").toString().isEmpty()) {
            mainBinding.viewSplash.setVisibility(View.VISIBLE);
            mainBinding.imgClose.setVisibility(View.VISIBLE);
            mainBinding.imgHome.setVisibility(View.VISIBLE);
        } else {
            Intent intent;
            UserDetailsModel userDetailsModel = Commons.convertStringToObject(mContext, SharePref.PREF_USER, UserDetailsModel.class);
            if (userDetailsModel.getUserType().equals("1")) {
                intent = new Intent(mContext, DashboardActivity.class);
            } else {
                intent = new Intent(mContext, DashboardEventManagerActivity.class);
            }
            startActivity(intent);
            finishAffinity();
        }
    }
    private void setListener(){
        mainBinding.imgClose.setOnClickListener(view -> {
            Intent intent = new Intent(mContext,LoginSignupActivity.class);
            intent.putExtra("fromSplash",true);
            startActivity(intent);
            finishAffinity();
        });

        mainBinding.imgHome.setOnClickListener(view -> {
            Intent intent = new Intent(this, DashboardActivity.class);
            startActivity(intent);
            finishAffinity();
        });
    }
}