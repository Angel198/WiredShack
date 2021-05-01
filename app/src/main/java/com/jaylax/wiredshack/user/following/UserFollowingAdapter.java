package com.jaylax.wiredshack.user.following;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ItemUserFollowingBinding;
import com.jaylax.wiredshack.eventManager.followed.ManagerFollowedMainModel;

import java.util.ArrayList;

public class UserFollowingAdapter extends RecyclerView.Adapter<UserFollowingAdapter.MyViewHolder> {
    Context context;
    ArrayList<UserFollowingMainModel.UserFollowingData> list;
    FollowingManageClick listener;

    public UserFollowingAdapter(Context context, ArrayList<UserFollowingMainModel.UserFollowingData> list, FollowingManageClick listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_user_following, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(position, list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemUserFollowingBinding mBinding;

        public MyViewHolder(@NonNull ItemUserFollowingBinding itemView) {
            super(itemView.getRoot());
            this.mBinding = itemView;
        }

        void bind(int pos, UserFollowingMainModel.UserFollowingData data) {
            RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.place_holder).transform(new CenterCrop()).error(R.drawable.place_holder).priority(Priority.HIGH);
            Glide.with(context).load(data.getManagerImage() == null ? "" : data.getManagerImage()).apply(options).into(mBinding.imgUserProfile);
            mBinding.tvUserName.setText(data.getManagerName() == null ? "N/A" : data.getManagerName());

            if (data.getIsFollow().equals("1")) {
                mBinding.tvFollow.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.back_border_white));
                mBinding.tvFollow.setText(context.getResources().getString(R.string.following));
            } else {
                mBinding.tvFollow.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.back_follow));
                mBinding.tvFollow.setText(context.getResources().getString(R.string.follow));
            }

            mBinding.tvFollow.setOnClickListener(view -> listener.onFollowClick(pos, data));
        }
    }

    public interface FollowingManageClick {
        void onFollowClick(int pos, UserFollowingMainModel.UserFollowingData data);
    }
}
