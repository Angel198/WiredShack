package com.jaylax.wiredshack.user.following;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ItemUserFollowingBinding;

public class UserFollowingAdapter extends RecyclerView.Adapter<UserFollowingAdapter.MyViewHolder> {
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_user_following,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 20;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ItemUserFollowingBinding mBinding;
        public MyViewHolder(@NonNull ItemUserFollowingBinding itemView) {
            super(itemView.getRoot());
            this.mBinding = itemView;
        }
    }
}
