package com.jaylax.wiredshack.user.notification;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ItemViewSentRequestBinding;

public class ViewSentRequestAdapter extends RecyclerView.Adapter<ViewSentRequestAdapter.MyViewHolder> {
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_view_sent_request,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ItemViewSentRequestBinding mBinding;
        public MyViewHolder(@NonNull ItemViewSentRequestBinding itemView) {
            super(itemView.getRoot());
            this.mBinding = itemView;
        }
    }
}
