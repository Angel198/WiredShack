package com.jaylax.wiredshack.eventManager.managerActivity;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ItemHomeRecentEventBinding;
import com.jaylax.wiredshack.databinding.ItemIncomingRequestBinding;
import com.jaylax.wiredshack.eventManager.home.ManagerRecentEventsAdapter;

public class ManagerIncomingRequestAdapter extends RecyclerView.Adapter<ManagerIncomingRequestAdapter.MyViewHolder> {
    public ManagerIncomingRequestAdapter() {
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_incoming_request, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind();
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemIncomingRequestBinding mBinding;

        public MyViewHolder(ItemIncomingRequestBinding itemView) {
            super(itemView.getRoot());
            this.mBinding = itemView;
        }

        public void bind() {

        }
    }
}


