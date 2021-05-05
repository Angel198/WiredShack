package com.jaylax.wiredshack.user.eventDetails;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

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
    CommentClick listener = null;

    public EventCommentAdapter(Context context, ArrayList<EventCommentMainModel.EventCommentData> list) {
        this.context = context;
        this.list = list;
    }

    public EventCommentAdapter(Context context, ArrayList<EventCommentMainModel.EventCommentData> list, CommentClick listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_event_comment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (list.isEmpty()) {
            holder.bindWithoutData();
        } else {
            holder.bind(position, list.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemEventCommentBinding mBinding;

        public MyViewHolder(@NonNull ItemEventCommentBinding itemView) {
            super(itemView.getRoot());
            this.mBinding = itemView;
        }

        @SuppressLint("RtlHardcoded")
        private void bind(int pos, EventCommentMainModel.EventCommentData data) {
            RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.place_holder).transform(new CenterCrop()).error(R.drawable.place_holder).priority(Priority.HIGH);
            String imageUrl = "";
            if (data.getUserImage() == null || data.getUserImage().isEmpty()) {
                imageUrl = data.getManagerImage() == null ? "" : data.getManagerImage();
            } else {
                imageUrl = data.getUserImage();
            }
            Glide.with(context).load(imageUrl).apply(options).into(mBinding.imgCommentProfile);

            String name = "";
            if (data.getUserName() == null || data.getUserName().isEmpty()) {
                name = data.getManagerName() == null ? "N/A" : data.getManagerName();
            } else {
                name = data.getUserName();
            }

            mBinding.tvCommentUserName.setText(name);
            mBinding.tvComment.setText(data.getComment() == null ? "N/A" : data.getComment());
            mBinding.tvCommentTime.setText(data.getCreatedAt());

            if (data.isLoginUser()){
                mBinding.imgCommentOption.setVisibility(View.VISIBLE);
            }else {
                mBinding.imgCommentOption.setVisibility(View.GONE);
            }
            mBinding.imgCommentOption.setOnClickListener(view -> {

                PopupMenu popupMenu = new PopupMenu(mBinding.imgCommentOption.getContext(), mBinding.imgCommentOption);
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    if (menuItem.getItemId() == R.id.comment_delete) {
                        if (listener != null) {
                            listener.onCommentOptionClick(pos, data);
                        }
                        return true;
                    }
                    return false;
                });

                popupMenu.inflate(R.menu.comment_menu);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    popupMenu.setGravity(Gravity.RIGHT);
                }
                popupMenu.show();
            });
        }

        private void bindWithoutData() {
        }
    }

    public interface CommentClick {
        void onCommentOptionClick(int pos, EventCommentMainModel.EventCommentData data);
    }
}
