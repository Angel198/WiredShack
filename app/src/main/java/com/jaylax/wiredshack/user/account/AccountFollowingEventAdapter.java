package com.jaylax.wiredshack.user.account;

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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ItemHomeRecentEventBinding;
import com.jaylax.wiredshack.model.RecentEventMainModel;
import com.jaylax.wiredshack.user.home.HomeRecentEventAdapter;

import java.util.ArrayList;

public class AccountFollowingEventAdapter extends RecyclerView.Adapter<AccountFollowingEventAdapter.MyViewHolder> {
    Context context;
    ArrayList<FollowingEventMainModel.FollowingEventData> list;
    FollowingEventClick listener;

    public AccountFollowingEventAdapter(Context context, ArrayList<FollowingEventMainModel.FollowingEventData> list, FollowingEventClick listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_home_recent_event, parent, false));
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
        ItemHomeRecentEventBinding mBinding;
        public MyViewHolder(@NonNull ItemHomeRecentEventBinding itemView) {
            super(itemView.getRoot());
            mBinding = itemView;
        }

        public void bind(int pos, FollowingEventMainModel.FollowingEventData data) {
            RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.place_holder).transform(new CenterCrop(), new RoundedCorners(10)).error(R.drawable.place_holder).priority(Priority.HIGH);
            String imageUrl = "";
            if (data.getImages().isEmpty()){
                imageUrl = data.getManagerImage() == null ? "" : data.getManagerImage();
            }else {
                imageUrl = data.getImages().get(0).getImage() == null? "": data.getImages().get(0).getImage();
            }

            Glide.with(context).load(imageUrl).apply(options).into(mBinding.imgEventProfile);

            mBinding.tvEventManagerName.setText(data.getEventName() == null ? "N/A" : data.getEventName());
            mBinding.constraintEvent.setOnClickListener(view -> {
                listener.onEventClick(data);
            });

            if (data.getRequestStatus() == null){
                mBinding.imgEventVideo.setVisibility(View.GONE);
            }else {
                if (data.getRequestStatus().equals("2")) {
                    mBinding.imgEventVideo.setVisibility(View.VISIBLE);
                }else {
                    mBinding.imgEventVideo.setVisibility(View.GONE);
                }
            }
        }
    }

    interface FollowingEventClick{
        void onEventClick(FollowingEventMainModel.FollowingEventData data);
    }
}
