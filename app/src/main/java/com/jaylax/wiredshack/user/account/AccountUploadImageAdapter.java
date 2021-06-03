package com.jaylax.wiredshack.user.account;

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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ItemAccountUploadImageBinding;
import com.jaylax.wiredshack.databinding.ItemHomeRecentEventBinding;

import java.util.ArrayList;

public class AccountUploadImageAdapter extends RecyclerView.Adapter<AccountUploadImageAdapter.MyViewHolder> {
    Context context;
    ArrayList<UploadImageModel.UploadImageData> list;
    FollowingEventClick listener;

    public AccountUploadImageAdapter(Context context, ArrayList<UploadImageModel.UploadImageData> list, FollowingEventClick listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_account_upload_image, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(position, list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemAccountUploadImageBinding mBinding;
        public MyViewHolder(@NonNull ItemAccountUploadImageBinding itemView) {
            super(itemView.getRoot());
            mBinding = itemView;
        }

        public void bind(int pos, UploadImageModel.UploadImageData data) {
            RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.place_holder).transform(new CenterCrop()).error(R.drawable.place_holder).priority(Priority.HIGH);
            String imageUrl = data.getPostImg() == null ? "" : data.getPostImg();
            Glide.with(context).load(imageUrl).apply(options).into(mBinding.imgAccountImage);
        }
    }

    public interface FollowingEventClick{
        void onEventClick(FollowingEventMainModel.FollowingEventData data);
    }
}
