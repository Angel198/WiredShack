package com.jaylax.wiredshack.user.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ItemHomeManagerListBinding;
import com.jaylax.wiredshack.databinding.ItemHomeManagerListRoundBinding;

import java.util.ArrayList;

public class HomeManagerListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    ArrayList<ManagerListMainModel.ManagerListData> list;
    HomeManagerListAdapter.ManagerClick listener;
    boolean isShowLiveTag;
    String type;

    public HomeManagerListAdapter(Context context, ArrayList<ManagerListMainModel.ManagerListData> list, HomeManagerListAdapter.ManagerClick listener, boolean isShowLiveTag, String type) {
        this.context = context;
        this.list = list;
        this.listener = listener;
        this.isShowLiveTag = isShowLiveTag;
        this.type = type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (type.equalsIgnoreCase("following")){
            return new FollowingViewHolder(DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.item_home_manager_list_round,parent,false));
        }else {
            return new MyViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_home_manager_list, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FollowingViewHolder){
            ((FollowingViewHolder) holder).bind(position, list.get(position));
        }else {
            ((MyViewHolder) holder).bind(position, list.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class FollowingViewHolder extends RecyclerView.ViewHolder {
        ItemHomeManagerListRoundBinding mBinding;

        public FollowingViewHolder(ItemHomeManagerListRoundBinding itemView) {
            super(itemView.getRoot());
            this.mBinding = itemView;
        }

        public void bind(int position, ManagerListMainModel.ManagerListData data) {
            RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.place_holder).transform(new CenterCrop()).error(R.drawable.place_holder).priority(Priority.HIGH);
            Glide.with(context).load(data.getManagerImage() == null ? "" : data.getManagerImage()).apply(options).into(mBinding.imgHomeManagerProfile);

            mBinding.tvHomeManagerName.setText(data.getManagerName() == null ? "N/A" : data.getManagerName());

            if (isShowLiveTag) {
                if (data.getIsActive() == null) {
                    mBinding.tvManagerLiveTag.setVisibility(View.GONE);
                } else {
                    if (data.getIsActive().equals("1")) {
                        mBinding.tvManagerLiveTag.setVisibility(View.VISIBLE);
                    } else {
                        mBinding.tvManagerLiveTag.setVisibility(View.GONE);
                    }
                }
            } else {
                mBinding.tvManagerLiveTag.setVisibility(View.GONE);
            }

            mBinding.constraintMain.setOnClickListener(view -> {
                listener.onManagerClick(data);
            });
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemHomeManagerListBinding mBinding;

        public MyViewHolder(ItemHomeManagerListBinding itemView) {
            super(itemView.getRoot());
            this.mBinding = itemView;
        }

        public void bind(int position, ManagerListMainModel.ManagerListData data) {
            RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.place_holder).transform(new CenterCrop()).error(R.drawable.place_holder).priority(Priority.HIGH);
            Glide.with(context).load(data.getManagerImage() == null ? "" : data.getManagerImage()).apply(options).into(mBinding.imgHomeManagerProfile);

            mBinding.tvHomeManagerName.setText(data.getManagerName() == null ? "N/A" : data.getManagerName());

            if (isShowLiveTag) {
                if (data.getIsActive() == null) {
                    mBinding.tvManagerLiveTag.setVisibility(View.GONE);
                } else {
                    if (data.getIsActive().equals("1")) {
                        mBinding.tvManagerLiveTag.setVisibility(View.VISIBLE);
                    } else {
                        mBinding.tvManagerLiveTag.setVisibility(View.GONE);
                    }
                }
            } else {
                mBinding.tvManagerLiveTag.setVisibility(View.GONE);
            }

            mBinding.constraintMain.setOnClickListener(view -> {
                listener.onManagerClick(data);
            });
        }
    }

    interface ManagerClick {
        void onManagerClick(ManagerListMainModel.ManagerListData data);
    }
}