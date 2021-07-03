package com.jaylax.wiredshack.user.home;

import android.content.Context;
import android.content.Intent;
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
import com.jaylax.wiredshack.databinding.ItemHomeUpcomingEventBinding;
import com.jaylax.wiredshack.model.RecentEventMainModel;
import com.jaylax.wiredshack.user.eventDetails.EventDetailsActivity;
import com.jaylax.wiredshack.user.upcoming.UpcomingEventActivity;
import com.jaylax.wiredshack.utils.Commons;
import com.jaylax.wiredshack.utils.SharePref;

import org.json.JSONException;

import java.util.ArrayList;

public class HomeUpcomingEventAdapter extends RecyclerView.Adapter<HomeUpcomingEventAdapter.MyViewHolder> {
    Context context;
    ArrayList<UpcomingEventMainModel.UpcomingEventData> list;
    UpcomingEventClick listener;

    public HomeUpcomingEventAdapter(Context context, ArrayList<UpcomingEventMainModel.UpcomingEventData> list, UpcomingEventClick listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_home_upcoming_event, parent, false));
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
        ItemHomeUpcomingEventBinding mBinding;

        public MyViewHolder(@NonNull ItemHomeUpcomingEventBinding itemView) {
            super(itemView.getRoot());
            mBinding = itemView;
        }

        void bind(int pos, UpcomingEventMainModel.UpcomingEventData eventData) {
            RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.place_holder).transform(new CenterCrop(), new RoundedCorners(50)).error(R.drawable.place_holder).priority(Priority.HIGH);
            String coverImage = eventData.getEventCoverImage() == null ? "" : eventData.getEventCoverImage();
            Glide.with(context).load(coverImage).apply(options).into(mBinding.imgUpcomingEvent);
            mBinding.tvUpcomingEventDateDay.setText(Commons.getEventDateDay(eventData.getDate()));
            mBinding.tvUpcomingEventDate.setText(Commons.getEventDate(eventData.getDate()));

            if (eventData.getIsActive() == null) {
                mBinding.imgEventLive.setVisibility(View.GONE);
                mBinding.imgEventPlay.setVisibility(View.GONE);
            } else {
                if (eventData.getIsActive().equals("1")) {
                    mBinding.imgEventLive.setVisibility(View.VISIBLE);
                    mBinding.imgEventPlay.setVisibility(View.VISIBLE);
                } else {
                    mBinding.imgEventLive.setVisibility(View.GONE);
                    mBinding.imgEventPlay.setVisibility(View.GONE);
                }
            }

            mBinding.relativeUpcomingEvent.setOnClickListener(view -> {
                try {
                    listener.onEventClick(eventData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public interface UpcomingEventClick {
        void onEventClick(UpcomingEventMainModel.UpcomingEventData data) throws JSONException;
    }
}
