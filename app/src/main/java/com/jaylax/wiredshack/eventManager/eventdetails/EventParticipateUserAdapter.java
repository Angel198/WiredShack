package com.jaylax.wiredshack.eventManager.eventdetails;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ItemEventParticipateUserBinding;


public class EventParticipateUserAdapter extends RecyclerView.Adapter<EventParticipateUserAdapter.MyViewHolder> {

   Boolean isEdit;

    public EventParticipateUserAdapter(Boolean isEdit) {
        this.isEdit = isEdit;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_event_participate_user,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind();
    }

    @Override
    public int getItemCount() {
        return 6;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ItemEventParticipateUserBinding mBinding;
        public MyViewHolder(@NonNull ItemEventParticipateUserBinding itemView) {
            super(itemView.getRoot());
            this.mBinding = itemView;
        }
        private void bind(){

            if (isEdit){
                mBinding.viewEventUser.setVisibility(View.VISIBLE);
                mBinding.imageEventUserTrash.setVisibility(View.VISIBLE);
            }else {
                mBinding.viewEventUser.setVisibility(View.GONE);
                mBinding.imageEventUserTrash.setVisibility(View.GONE);
            }
        }
    }
}
