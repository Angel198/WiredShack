package com.jaylax.wiredshack.eventManager.assignEvent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.jaylax.wiredshack.ProgressDialog;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ActivityAssignEventBinding;
import com.jaylax.wiredshack.eventManager.editEvent.SelectManagerBottomSheet;
import com.jaylax.wiredshack.eventManager.editEvent.SelectManagerListModel;
import com.jaylax.wiredshack.eventManager.followed.ManagerFollowedAdapter;
import com.jaylax.wiredshack.model.CommonResponseModel;
import com.jaylax.wiredshack.model.UserDetailsModel;
import com.jaylax.wiredshack.rest.ApiClient;
import com.jaylax.wiredshack.utils.Commons;
import com.jaylax.wiredshack.utils.SharePref;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssignEventActivity extends AppCompatActivity {

    ActivityAssignEventBinding mBinding;
    Context mContext;
    ProgressDialog progressDialog;
    UserDetailsModel userDetailsModel;

    ArrayList<SelectManagerListModel.SelectManagerListData> managerList = new ArrayList<>();
    AssignManagerAdapter adapter = null;
    String eventName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_assign_event);

        if (getIntent().hasExtra("eventName")) {
            eventName = getIntent().getStringExtra("eventName");
        }

        mContext = this;
        progressDialog = new ProgressDialog(Objects.requireNonNull(mContext));
        userDetailsModel = Commons.convertStringToObject(mContext, SharePref.PREF_USER, UserDetailsModel.class);
        RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.place_holder).transform(new CenterCrop()).error(R.drawable.place_holder).priority(Priority.HIGH);
        Glide.with(this).load(userDetailsModel.getImage() == null ? "" : userDetailsModel.getImage()).apply(options).into(mBinding.imgAccountProfile);
        if (userDetailsModel.getUserType() == null) {
            mBinding.tvTitleAssignManager.setText(getResources().getString(R.string.list_of_club));
            mBinding.tvSendMail.setVisibility(View.GONE);
        } else {
            if (userDetailsModel.getUserType().equals("2")) {
                mBinding.tvTitleAssignManager.setText(getResources().getString(R.string.list_of_dj));
                mBinding.tvSendMail.setVisibility(View.VISIBLE);
            } else {
                mBinding.tvTitleAssignManager.setText(getResources().getString(R.string.list_of_club));
                mBinding.tvSendMail.setVisibility(View.GONE);
            }
        }

        getManagerListForSelection();
        mBinding.imgBack.setOnClickListener(view -> onBackPressed());

        mBinding.edtSearchSearchOrganiser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (adapter != null) {
                    adapter.getFilter().filter(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mBinding.imgUserFollowingSort.setOnClickListener(view -> {
            if (!managerList.isEmpty() && adapter != null) {
                Collections.sort(managerList, (managerData, t1) -> managerData.getManagerName().compareToIgnoreCase(t1.getManagerName()));

                adapter.notifyDataSetChanged();
            }
        });

        mBinding.tvSendMail.setOnClickListener(view -> {
            SelectManagerBottomSheet bottomSheet = new SelectManagerBottomSheet(mContext, eventName, new SelectManagerBottomSheet.BottomSheetListener() {
                @Override
                public void onEmailSend(String stEmail) {
                    senEmail(stEmail);
                }
            });
            bottomSheet.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
            bottomSheet.show(getSupportFragmentManager(), "select");
        });
    }

    private void getManagerListForSelection() {
        if (Commons.isOnline(mContext)) {
            String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");
            progressDialog.show();
            ApiClient.create().managerListForSelect(header).enqueue(new Callback<SelectManagerListModel>() {
                @Override
                public void onResponse(Call<SelectManagerListModel> call, Response<SelectManagerListModel> response) {
                    progressDialog.dismiss();
                    if (response.code() == 200 && response.isSuccessful()) {
                        if (response.body() != null) {
                            setSelectManagerUI(response.body().getData());
                            if (!response.body().getStatus().equals("200")) {
                                Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                            }
                        }
                    } else {
                        Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                    }
                }

                @Override
                public void onFailure(Call<SelectManagerListModel> call, Throwable t) {
                    progressDialog.dismiss();
                    Commons.showToast(mContext, getResources().getString(R.string.something_wants_wrong));
                }
            });
        } else {
            Commons.showToast(mContext, getResources().getString(R.string.no_internet_connection));
        }
    }

    private void setSelectManagerUI(ArrayList<SelectManagerListModel.SelectManagerListData> list) {
        if (list.isEmpty()) {
            mBinding.recyclerOrganiser.setVisibility(View.GONE);
        } else {
            managerList = list;
            mBinding.recyclerOrganiser.setVisibility(View.VISIBLE);
            adapter = new AssignManagerAdapter(mContext, this, managerList);
            mBinding.recyclerOrganiser.setLayoutManager(new LinearLayoutManager(mContext));
            mBinding.recyclerOrganiser.setAdapter(adapter);
        }
    }

    private void senEmail(String stEmail) {
        if (Commons.isOnline(mContext)) {
            String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");

            HashMap<String, String> params = new HashMap<>();
            params.put("event_name", eventName);
            params.put("email", stEmail);
            params.put("manager_name", userDetailsModel.getName() == null ? "Event Organiser" : userDetailsModel.getName());

            progressDialog.show();
            ApiClient.create().sendEmail(header, params).enqueue(new Callback<CommonResponseModel>() {
                @Override
                public void onResponse(Call<CommonResponseModel> call, Response<CommonResponseModel> response) {
                    progressDialog.dismiss();
                    if (response.code() == 200 && response.isSuccessful()) {
                        if (response.body() != null) {
                            Commons.showToast(mContext, response.body().getMessage() == null ? "" : response.body().getMessage());
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
}