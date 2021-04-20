 package com.jaylax.wiredshack;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.jaylax.wiredshack.eventManager.dashboard.DashboardEventManagerActivity;
import com.jaylax.wiredshack.model.UserDetailsModel;
import com.jaylax.wiredshack.user.dashboard.DashboardActivity;
import com.jaylax.wiredshack.utils.Commons;
import com.jaylax.wiredshack.utils.SharePref;

 public class MainActivity extends AppCompatActivity {

     Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        new Handler().postDelayed(() -> {
            Intent intent;
            if (SharePref.getInstance(mContext).get(SharePref.PREF_USER,"").toString().isEmpty()){
                intent = new Intent(this, DashboardActivity.class);
            }else {
                UserDetailsModel userDetailsModel = Commons.convertStringToObject(mContext,SharePref.PREF_USER,UserDetailsModel.class);
                if (userDetailsModel.getUserType().equals("1")){
                    intent = new Intent(mContext, DashboardActivity.class);
                }else {
                    intent = new Intent(mContext, DashboardEventManagerActivity.class);
                }
            }
            startActivity(intent);
            finishAffinity();
        },3000);
    }
}