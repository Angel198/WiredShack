package com.jaylax.wiredshack.eventimage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jaylax.wiredshack.MainActivity;
import com.jaylax.wiredshack.ProgressDialog;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ActivityEventImagesBinding;
import com.jaylax.wiredshack.eventManager.editEvent.EventImageModel;
import com.jaylax.wiredshack.model.CommonResponseModel;
import com.jaylax.wiredshack.rest.ApiClient;
import com.jaylax.wiredshack.user.account.UploadImageModel;
import com.jaylax.wiredshack.utils.Commons;
import com.jaylax.wiredshack.utils.SharePref;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventImagesActivity extends AppCompatActivity {

    Context mContext;
    ActivityEventImagesBinding mBinding;
    ArrayList<EventImageModel> imageArrayList = new ArrayList<>();
    boolean isFromProfile = false;
    EventImagePagerAdapter adapter = null;
    ProgressDialog progressDialog;

    int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_event_images);

        mContext = this;
        progressDialog = new ProgressDialog(mContext);

        if (getIntent().hasExtra("eventImages")) {
            String jsonString = getIntent().getStringExtra("eventImages");
            if (!jsonString.isEmpty()) {
                imageArrayList = new Gson().fromJson(jsonString, new TypeToken<ArrayList<EventImageModel>>() {
                }.getType());
            }
        }

        if (getIntent().hasExtra("pos")) {
            position = getIntent().getIntExtra("pos", 0);
        }

        if (getIntent().hasExtra("fromProfile")) {
            isFromProfile = getIntent().getBooleanExtra("fromProfile", true);
        }
        mBinding.imgBack.setOnClickListener(view -> {
            onBackPressed();
        });

        if (isFromProfile) {
            mBinding.imgDelete.setVisibility(View.VISIBLE);
        } else {
            mBinding.imgDelete.setVisibility(View.GONE);
        }

        mBinding.imgDelete.setOnClickListener(view -> {
            //TODO :Delete Image
            int pos = mBinding.viewPagerImages.getCurrentItem();
            showImageDeleteDialog(mBinding.viewPagerImages.getCurrentItem());
        });
        if (imageArrayList.isEmpty()) {
            onBackPressed();
        } else {
            adapter = new EventImagePagerAdapter(imageArrayList, this);
            mBinding.viewPagerImages.setAdapter(adapter);
            mBinding.viewPagerImages.setCurrentItem(position);
        }
    }

    private void showImageDeleteDialog(int pos) {
        Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_log_out);

        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(window.getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);

        AppCompatTextView tvTitle = (AppCompatTextView) dialog.findViewById(R.id.tvLogoutTitle);
        tvTitle.setText(mContext.getResources().getString(R.string.delete_image_msg));
        AppCompatTextView tvYes = (AppCompatTextView) dialog.findViewById(R.id.tvLogoutYes);
        AppCompatTextView tvNo = (AppCompatTextView) dialog.findViewById(R.id.tvLogoutNo);

        tvYes.setOnClickListener(view -> {
            dialog.dismiss();
            deleteImage(pos);
        });

        tvNo.setOnClickListener(view -> {
            dialog.dismiss();
        });
        dialog.show();
    }

    private void deleteImage(int pos) {
        if (!imageArrayList.isEmpty()) {
            if (Commons.isOnline(mContext)) {
                progressDialog.show();
                String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");

                HashMap<String, String> params = new HashMap<>();
                params.put("img_id", imageArrayList.get(pos).getImageId());

                ApiClient.create().deleteUserImage(header, params).enqueue(new Callback<CommonResponseModel>() {
                    @Override
                    public void onResponse(Call<CommonResponseModel> call, Response<CommonResponseModel> response) {
                        progressDialog.dismiss();
                        if (response.code() == 200 && response.isSuccessful()) {
                            if (response.body() != null) {
                                if (response.body().getStatus().equals("200")) {
                                    onBackPressed();
                                } else {
                                    String msg = "";
                                    if (response.body().getMessage().isEmpty()) {
                                        msg = mContext.getResources().getString(R.string.please_try_after_some_time);
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
                Commons.showToast(mContext, mContext.getResources().getString(R.string.no_internet_connection));
            }
        }
    }
}