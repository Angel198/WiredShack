package com.jaylax.wiredshack.eventManager.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ItemHomeRecentEventBinding;
import com.jaylax.wiredshack.user.home.HomeRecentEventAdapter;

public class ManagerRecentEventsAdapter extends RecyclerView.Adapter<ManagerRecentEventsAdapter.MyViewHolder> {
    public ManagerRecentEventsAdapter() {
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_home_recent_event, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind();
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemHomeRecentEventBinding mBinding;

        public MyViewHolder(ItemHomeRecentEventBinding itemView) {
            super(itemView.getRoot());
            this.mBinding = itemView;
        }

        public void bind() {

        }
    }
}

