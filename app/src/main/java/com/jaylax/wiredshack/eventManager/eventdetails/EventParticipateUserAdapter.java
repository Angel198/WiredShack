package com.jaylax.wiredshack.eventManager.eventdetails;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ItemEventParticioateUserAddBinding;
import com.jaylax.wiredshack.databinding.ItemEventParticipateUserBinding;


public class EventParticipateUserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Boolean isEdit;

    public EventParticipateUserAdapter(Boolean isEdit) {
        this.isEdit = isEdit;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return (viewType == 0) ? new MyViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_event_participate_user, parent, false)) : new MyAddImageViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_event_particioate_user_add, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == 0) {
            MyViewHolder viewHolder = (MyViewHolder) holder;
            viewHolder.bind();
        } else {
            MyAddImageViewHolder viewHolder = (MyAddImageViewHolder) holder;
            viewHolder.bind();
        }
    }

    @Override
    public int getItemCount() {
        return isEdit ? 7:6;
    }

    @Override
    public int getItemViewType(int position) {

        int viewType = 0;

        if (isEdit) {
            viewType = (position == 6) ? 1 : 0;
        }
        return viewType;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ItemEventParticipateUserBinding mBinding;

        public MyViewHolder(@NonNull ItemEventParticipateUserBinding itemView) {
            super(itemView.getRoot());
            this.mBinding = itemView;
        }

        private void bind() {

            if (isEdit) {
                mBinding.viewEventUser.setVisibility(View.VISIBLE);
                mBinding.imageEventUserTrash.setVisibility(View.VISIBLE);
            } else {
                mBinding.viewEventUser.setVisibility(View.GONE);
                mBinding.imageEventUserTrash.setVisibility(View.GONE);
            }
        }
    }

    public class MyAddImageViewHolder extends RecyclerView.ViewHolder {

        ItemEventParticioateUserAddBinding mBinding;

        public MyAddImageViewHolder(@NonNull ItemEventParticioateUserAddBinding itemView) {
            super(itemView.getRoot());
            this.mBinding = itemView;
        }

        private void bind() {

        }
    }
}
