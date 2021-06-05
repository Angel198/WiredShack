package com.jaylax.wiredshack.eventManager.editEvent;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.google.gson.Gson;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ItemEventImageAddBinding;
import com.jaylax.wiredshack.databinding.ItemEventImageBinding;
import com.jaylax.wiredshack.eventManager.editEvent.EventImageModel;
import com.jaylax.wiredshack.eventimage.EventImagesActivity;

import java.util.ArrayList;


public class EventImagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Boolean isEdit;
    ArrayList<EventImageModel> list;
    EventImageClick listener;
    Context context;

    public EventImagesAdapter(Context context, Boolean isEdit, ArrayList<EventImageModel> list) {
        this.isEdit = isEdit;
        this.list = list;
        this.context = context;
    }

    public EventImagesAdapter(Context context, Boolean isEdit, ArrayList<EventImageModel> list, EventImageClick listener) {
        this.isEdit = isEdit;
        this.list = list;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return (viewType == 0) ? new MyViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_event_image, parent, false)) : new MyAddImageViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_event_image_add, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == 0) {
            MyViewHolder viewHolder = (MyViewHolder) holder;
            viewHolder.bind(position, list.get(position));
        } else {
            MyAddImageViewHolder viewHolder = (MyAddImageViewHolder) holder;
            viewHolder.bind();
        }
    }

    @Override
    public int getItemCount() {
        return isEdit ? list.size() + 1 : list.size();
    }

    @Override
    public int getItemViewType(int position) {

        int viewType = 0;

        if (isEdit) {
            viewType = (position == list.size()) ? 1 : 0;
        }
        return viewType;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ItemEventImageBinding mBinding;

        public MyViewHolder(@NonNull ItemEventImageBinding itemView) {
            super(itemView.getRoot());
            this.mBinding = itemView;
        }

        private void bind(int position, EventImageModel data) {

            if (isEdit) {
                mBinding.viewEventUser.setVisibility(View.VISIBLE);
                mBinding.imageEventUserTrash.setVisibility(View.VISIBLE);
            } else {
                mBinding.viewEventUser.setVisibility(View.GONE);
                mBinding.imageEventUserTrash.setVisibility(View.GONE);
            }
            mBinding.viewEventUser.setOnClickListener(view -> {
                if (isEdit) {
                    listener.onImageRemove(position);
                }
            });

            mBinding.constraintMain.setOnClickListener(view -> {
                if (!isEdit) {
                    Intent intent = new Intent(context, EventImagesActivity.class);
                    intent.putExtra("eventImages", new Gson().toJson(list));
                    context.startActivity(intent);
                }
            });
            RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.place_holder).transform(new CenterCrop(), new RoundedCorners(10)).error(R.drawable.place_holder).priority(Priority.HIGH);

            if (data.getImageURL().isEmpty()) {
                Glide.with(context).load(data.getUri()).apply(options).into(mBinding.imageEventImage);
            } else {
                Glide.with(context).load(data.getImageURL()).apply(options).into(mBinding.imageEventImage);

            }
        }
    }

    public class MyAddImageViewHolder extends RecyclerView.ViewHolder {

        ItemEventImageAddBinding mBinding;

        public MyAddImageViewHolder(@NonNull ItemEventImageAddBinding itemView) {
            super(itemView.getRoot());
            this.mBinding = itemView;
        }

        private void bind() {
            mBinding.constraintMain.setOnClickListener(view -> listener.addImage());
        }
    }

    public interface EventImageClick {
        void onImageRemove(int position);

        void addImage();
    }
}
