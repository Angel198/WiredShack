package com.jaylax.wiredshack.eventManager.managerActivity;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ItemEventActivitiesBinding;
import com.jaylax.wiredshack.databinding.ItemIncomingRequestBinding;

public class ManagerEventActivitiesAdapter extends RecyclerView.Adapter<ManagerEventActivitiesAdapter.MyViewHolder> {
    public ManagerEventActivitiesAdapter() {
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_event_activities, parent, false));
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
        ItemEventActivitiesBinding mBinding;

        public MyViewHolder(ItemEventActivitiesBinding itemView) {
            super(itemView.getRoot());
            this.mBinding = itemView;
        }

        public void bind() {

        }
    }
}
