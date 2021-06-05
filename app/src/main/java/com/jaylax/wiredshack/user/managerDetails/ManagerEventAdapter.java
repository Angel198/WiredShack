package com.jaylax.wiredshack.user.managerDetails;

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
import com.jaylax.wiredshack.databinding.ItemManagerDetailsEventBinding;
import com.jaylax.wiredshack.model.RecentEventMainModel;

import java.util.ArrayList;

public class ManagerEventAdapter extends RecyclerView.Adapter<ManagerEventAdapter.MyViewHolder> {
    Context context;
    ArrayList<RecentEventMainModel.RecentEventData> list;
    ManagerEventClick listener;
    boolean isUserHome = false;

    public ManagerEventAdapter(Context context, ArrayList<RecentEventMainModel.RecentEventData> list, ManagerEventClick listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_manager_details_event, parent, false));
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
        ItemManagerDetailsEventBinding mBinding;

        public MyViewHolder(ItemManagerDetailsEventBinding itemView) {
            super(itemView.getRoot());
            this.mBinding = itemView;
        }

        public void bind(int pos, RecentEventMainModel.RecentEventData data) {
            RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.place_holder).transform(new CenterCrop(), new RoundedCorners(10)).error(R.drawable.place_holder).priority(Priority.HIGH);
            String imageUrl = "";
            if (data.getImages().isEmpty()){
                imageUrl = data.getManagerImage() == null ? "" : data.getManagerImage();
            }else {
                imageUrl = data.getImages().get(0).getImages() == null? "": data.getImages().get(0).getImages();
            }

            Glide.with(context).load(imageUrl).apply(options).into(mBinding.imgEventImage);

            mBinding.constraintImage.setOnClickListener(view -> {
                listener.onEventClick(data);
            });
        }
    }

    public interface ManagerEventClick {
        void onEventClick(RecentEventMainModel.RecentEventData data);
    }
}
