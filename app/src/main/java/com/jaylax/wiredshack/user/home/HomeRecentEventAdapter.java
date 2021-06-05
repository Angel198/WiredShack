package com.jaylax.wiredshack.user.home;

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
import com.jaylax.wiredshack.databinding.ItemHomeRecentEventBinding;
import com.jaylax.wiredshack.model.RecentEventMainModel;

import java.util.ArrayList;

public class HomeRecentEventAdapter extends RecyclerView.Adapter<HomeRecentEventAdapter.MyViewHolder> {
    Context context;
    ArrayList<RecentEventMainModel.RecentEventData> list;
    RecentEventClick listener;

    public HomeRecentEventAdapter(Context context, ArrayList<RecentEventMainModel.RecentEventData> list, RecentEventClick listener) {
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
            mBinding.tvEventDateDay.setText(HomeFragment.getEventDateDay(data.getDate()));
            mBinding.tvEventDate.setText(HomeFragment.getEventDate(data.getDate()));

            String requestFlag = data.getIsRequest() == null ? "" : data.getIsRequest();
            if (requestFlag.equals("1")) {
                mBinding.tvEventRequest.setText(context.getResources().getString(R.string.cancel_request));
                mBinding.tvEventRequest.setTextColor(ContextCompat.getColor(context, R.color.colorRed));
            } else if (requestFlag.equals("2")) {
                mBinding.tvEventRequest.setText(context.getResources().getString(R.string.accepted));
                mBinding.tvEventRequest.setTextColor(ContextCompat.getColor(context, R.color.colorGoogleGreen));
            } else {
                mBinding.tvEventRequest.setText(context.getResources().getString(R.string.request));
                mBinding.tvEventRequest.setTextColor(ContextCompat.getColor(context, R.color.colorPink88));
            }

            mBinding.tvEventRequest.setOnClickListener(view -> {
                if (!requestFlag.equals("2")){
                    if (requestFlag.equals("1")){
                        listener.onEventRequestCancel(data);
                    }else {
                        listener.onEventRequest(data);
                    }
                }
            });
            mBinding.linearEventMain.setOnClickListener(view -> {
                listener.onEventClick(data);
            });
        }
    }

    public interface RecentEventClick {
        void onEventClick(RecentEventMainModel.RecentEventData data);
        void onEventRequest(RecentEventMainModel.RecentEventData data);
        void onEventRequestCancel(RecentEventMainModel.RecentEventData data);
    }
}
