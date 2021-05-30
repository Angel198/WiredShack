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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ItemHomeRecentEventNewBinding;
import com.jaylax.wiredshack.model.RecentEventMainModel;

import java.util.ArrayList;

public class HomeRecentEventNewAdapter extends RecyclerView.Adapter<HomeRecentEventNewAdapter.MyViewHolder> {
    Context context;
    ArrayList<RecentEventMainModel.RecentEventData> list;
    HomeRecentEventAdapter.RecentEventClick listener;

    public HomeRecentEventNewAdapter(Context context, ArrayList<RecentEventMainModel.RecentEventData> list, HomeRecentEventAdapter.RecentEventClick listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_home_recent_event_new, parent, false));
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
        ItemHomeRecentEventNewBinding mBinding;

        public MyViewHolder(@NonNull ItemHomeRecentEventNewBinding itemView) {
            super(itemView.getRoot());
            mBinding = itemView;
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
            Glide.with(context).load(data.getManagerImage() == null ? "" : data.getManagerImage()).apply(options).into(mBinding.imgAccountProfile);

            mBinding.tvEventManagerName.setText(data.getEventName() == null ? "N/A" : data.getEventName());
            mBinding.tvEventDateDay.setText(HomeNewFragment.getEventDateDay(data.getDate()));
            mBinding.tvEventDate.setText(HomeNewFragment.getEventDate(data.getDate()));

            mBinding.linearEventMain.setOnClickListener(view -> {
                listener.onEventClick(data);
            });
        }
    }

    public interface RecentEventClick {
        void onEventClick(RecentEventMainModel.RecentEventData data);
    }
}
