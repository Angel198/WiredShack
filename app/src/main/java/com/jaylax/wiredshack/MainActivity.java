package com.jaylax.wiredshack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.jaylax.wiredshack.databinding.ActivityMainBinding;
import com.jaylax.wiredshack.eventManager.dashboard.DashboardEventManagerActivity;
import com.jaylax.wiredshack.model.UserDetailsModel;
import com.jaylax.wiredshack.user.dashboard.DashboardActivity;
import com.jaylax.wiredshack.utils.Commons;
import com.jaylax.wiredshack.utils.SharePref;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    Context mContext;
    ActivityMainBinding mainBinding;
    boolean isLogout = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mContext = this;

        printHashKey();
        if (getIntent().hasExtra("isLogout")) {
            isLogout = getIntent().getBooleanExtra("isLogout", false);
        }

        if (isLogout) {
            redirectToFlow();
        } else {
            new Handler().postDelayed(this::redirectToFlow, 2000);
        }
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

    private void setListener() {
        mainBinding.imgClose.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, LoginSignupActivity.class);
            intent.putExtra("fromSplash", true);
            startActivity(intent);
            finishAffinity();
        });

        mainBinding.imgHome.setOnClickListener(view -> {
            Intent intent = new Intent(this, DashboardActivity.class);
            startActivity(intent);
            finishAffinity();
        });
    }

    public void printHashKey() {
        try {
            @SuppressLint("PackageManagerGetSignatures") PackageInfo info = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.e("TAG", "printHashKey() Hash Key: " + hashKey);
            }
        } catch (Exception e) {
            Log.e("TAG", "printHashKey()", e);
        }
    }
}