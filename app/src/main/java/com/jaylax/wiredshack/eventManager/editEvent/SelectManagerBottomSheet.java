package com.jaylax.wiredshack.eventManager.editEvent;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.BottomSelectManagerLayoutBinding;
import com.jaylax.wiredshack.model.UserDetailsModel;
import com.jaylax.wiredshack.utils.Commons;
import com.jaylax.wiredshack.utils.SharePref;

import java.util.ArrayList;

public class SelectManagerBottomSheet extends DialogFragment {

    Context mContext;
    ArrayList<SelectManagerListModel.SelectManagerListData> list;
    BottomSheetListener listener;
    UserDetailsModel userDetailsModel = null;

    BottomSelectManagerLayoutBinding mBinding;

    boolean isFromList = true;

    public SelectManagerBottomSheet(Context mContext, ArrayList<SelectManagerListModel.SelectManagerListData> list, BottomSheetListener listener) {
        this.mContext = mContext;
        this.list = list;
        this.listener =listener;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(window.getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.BOTTOM;
        window.setAttributes(layoutParams);

        getDialog().setCancelable(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.bottom_select_manager_layout, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
        userDetailsModel = Commons.convertStringToObject(mContext, SharePref.PREF_USER, UserDetailsModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI();
    }

    private void initUI() {
        if (userDetailsModel == null){
            mBinding.tvSelectManagerTitle.setText(getResources().getString(R.string.select_organiser_line));
            mBinding.tvSelectManagerBottom.setVisibility(View.GONE);
        }else {
            if (userDetailsModel.getUserType() == null) {
                mBinding.tvSelectManagerTitle.setText(getResources().getString(R.string.select_organiser_line));
                mBinding.tvSelectManagerBottom.setVisibility(View.GONE);
            } else {
                if (userDetailsModel.getUserType().equals("2")) {
                    mBinding.tvSelectManagerTitle.setText(getResources().getString(R.string.select_dj_organiser_line));
                    mBinding.tvSelectManagerBottom.setVisibility(View.VISIBLE);
                } else {
                    mBinding.tvSelectManagerTitle.setText(getResources().getString(R.string.select_event_organiser_line));
                    mBinding.tvSelectManagerBottom.setVisibility(View.GONE);
                }
            }
        }

        setSelectUI();

        mBinding.recycleSelectManager.setLayoutManager(new LinearLayoutManager(mContext));
        mBinding.recycleSelectManager.setAdapter(new SelectManagerAdapter(mContext,list,(pos, data) -> {
            listener.onManagerSelect(pos,data);
            dismiss();
        }));

        mBinding.viewSelectManager.setOnClickListener(view -> {
            dismiss();
        });

        mBinding.imgSelectManagerClose.setOnClickListener(view -> {
            dismiss();
        });

        mBinding.tvSelectManagerBottom.setOnClickListener(view -> {
            isFromList = !isFromList;
            setSelectUI();
        });

        mBinding.tvSelectManagerEmailSend.setOnClickListener(view -> {
            dismiss();
        });
    }

    private void setSelectUI(){
        if (isFromList){
            mBinding.recycleSelectManager.setVisibility(View.VISIBLE);
            mBinding.linearSelectManagerEmail.setVisibility(View.GONE);
            mBinding.tvSelectManagerBottom.setText(mContext.getResources().getString(R.string.send_email));
        }else {
            mBinding.recycleSelectManager.setVisibility(View.GONE);
            mBinding.linearSelectManagerEmail.setVisibility(View.VISIBLE);
            mBinding.tvSelectManagerBottom.setText(mContext.getResources().getString(R.string.select));
        }
    }
    public interface BottomSheetListener{
        void onManagerSelect(int pos, SelectManagerListModel.SelectManagerListData model);
    }
}
