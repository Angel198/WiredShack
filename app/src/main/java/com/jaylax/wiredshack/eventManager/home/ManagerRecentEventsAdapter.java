package com.jaylax.wiredshack.eventManager.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ItemManageRecentEventBinding;
import com.jaylax.wiredshack.model.RecentEventMainModel;

import java.util.ArrayList;

public class ManagerRecentEventsAdapter extends RecyclerView.Adapter<ManagerRecentEventsAdapter.MyViewHolder> {

    Context context;
    ArrayList<RecentEventMainModel.RecentEventData> list;
    ManagerEventClick listener;
    String listType = "";

    public ManagerRecentEventsAdapter(Context context, ArrayList<RecentEventMainModel.RecentEventData> list,ManagerEventClick listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    public ManagerRecentEventsAdapter(Context context, ArrayList<RecentEventMainModel.RecentEventData> list, ManagerEventClick listener, String listType) {
        this.context = context;
        this.list = list;
        this.listener = listener;
        this.listType = listType;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_manage_recent_event, parent, false));
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
//        ItemHomeRecentEventBinding mBinding;
        ItemManageRecentEventBinding mBinding;

        public MyViewHolder(ItemManageRecentEventBinding itemView) {
            super(itemView.getRoot());
            this.mBinding = itemView;
        }

        public void bind(int pos, RecentEventMainModel.RecentEventData data) {
            RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.place_holder).transform(new CenterCrop()).error(R.drawable.place_holder).priority(Priority.HIGH);
            String imageUrl = "";
            if (data.getImages().isEmpty()) {
                imageUrl = data.getManagerImage() == null ? "" : data.getManagerImage();
            } else {
                imageUrl = data.getImages().get(0).getImages() == null ? "" : data.getImages().get(0).getImages();
            }

            Glide.with(context).load(imageUrl).apply(options).into(mBinding.imgEventProfile);
            mBinding.constraintEvent.setOnClickListener(view -> {
                listener.onEventClick(data,listType);
            });
        }
    }

    public interface ManagerEventClick{
        void onEventClick(RecentEventMainModel.RecentEventData data, String lisType);
    }
}

