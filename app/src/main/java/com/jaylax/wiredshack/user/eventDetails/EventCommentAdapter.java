package com.jaylax.wiredshack.user.eventDetails;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ItemEventCommentBinding;

public class EventCommentAdapter extends RecyclerView.Adapter<EventCommentAdapter.MyViewHolder> {
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_event_comment,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ItemEventCommentBinding mBinding;
        public MyViewHolder(@NonNull ItemEventCommentBinding itemView) {
            super(itemView.getRoot());
            this.mBinding = itemView;
        }
    }
}
