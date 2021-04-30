package com.jaylax.wiredshack.user.wishlist;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ItemHomeRecentEventBinding;
import com.jaylax.wiredshack.user.eventDetails.EventDetailsActivity;

import java.util.ArrayList;

public class UserWishListAdapter extends RecyclerView.Adapter<UserWishListAdapter.MyViewHolder> {
    Context mContext;
    ArrayList<UserWishListMainModel.UserWishList> list;

    public UserWishListAdapter(Context mContext, ArrayList<UserWishListMainModel.UserWishList> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_home_recent_event, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(position,list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemHomeRecentEventBinding mBinding;
        public MyViewHolder(@NonNull ItemHomeRecentEventBinding itemView) {
            super(itemView.getRoot());
            this.mBinding = itemView;
        }

        void bind(int position, UserWishListMainModel.UserWishList data){
            if (data.getLike().equals("1")){
                mBinding.imgEventVideo.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.heart_like));
            }else {
                mBinding.imgEventVideo.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.heart));
            }

            RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.place_holder).transform(new CenterCrop(), new RoundedCorners(10)).error(R.drawable.place_holder).priority(Priority.HIGH);
            String imageUrl = "";
            if (data.getImages().isEmpty()){
                imageUrl = data.getManagerImage() == null ? "" : data.getManagerImage();
            }else {
                imageUrl = data.getImages().get(0).getImages() == null? "": data.getImages().get(0).getImages();
            }

            Glide.with(mContext).load(imageUrl).apply(options).into(mBinding.imgEventProfile);

            mBinding.tvEventManagerName.setText(data.getEventName() == null ? "N/A" : data.getEventName());
            mBinding.constraintEvent.setOnClickListener(view -> {
                Intent intent = new Intent(mContext, EventDetailsActivity.class);
                intent.putExtra("eventId",data.getId());
                mContext.startActivity(intent);
            });
        }
    }
}
