package com.jaylax.wiredshack.user.eventDetails;

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
import com.bumptech.glide.request.RequestOptions;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ItemEventCommentBinding;

import java.util.ArrayList;

public class EventCommentAdapter extends RecyclerView.Adapter<EventCommentAdapter.MyViewHolder> {

    Context context;
    ArrayList<EventCommentMainModel.EventCommentData> list;

    public EventCommentAdapter(Context context, ArrayList<EventCommentMainModel.EventCommentData> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_event_comment,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (list.isEmpty()){
            holder.bindWithoutData();
        }else {
            holder.bind(position,list.get(position));
        }
    }

    @Override
    public int getItemCount() {

        int size = 0;
        if (list.isEmpty()){
            size = 5;
        }else {
            size = list.size();
        }
        return size;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ItemEventCommentBinding mBinding;
        public MyViewHolder(@NonNull ItemEventCommentBinding itemView) {
            super(itemView.getRoot());
            this.mBinding = itemView;
        }

        private void bind(int pos, EventCommentMainModel.EventCommentData data){
            RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.place_holder).transform(new CenterCrop()).error(R.drawable.place_holder).priority(Priority.HIGH);
            String imageUrl = "";
            if (data.getUserImage() == null || data.getUserImage().isEmpty()){
                imageUrl = data.getManagerImage() == null? "": data.getManagerImage();
            }else {
                imageUrl = data.getUserImage();
            }
            Glide.with(context).load(imageUrl).apply(options).into(mBinding.imgCommentProfile);

            String name = "";
            if (data.getUserName() == null || data.getUserName().isEmpty()){
                name = data.getManagerName() == null? "N/A": data.getManagerName();
            }else {
                name = data.getUserName();
            }

            mBinding.tvCommentUserName.setText(name);
            mBinding.tvComment.setText(data.getComment() == null ?"N/A" : data.getComment());
            mBinding.tvCommentTime.setText(data.getCreatedAt());
        }

        private void bindWithoutData(){}
    }
}
