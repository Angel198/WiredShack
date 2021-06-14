package com.jaylax.wiredshack.user.liveVideoPlayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ItemLiveStreamUserBinding;

import java.util.ArrayList;

public class LiveStreamUserAdapter extends RecyclerView.Adapter<LiveStreamUserAdapter.MyViewHolder> {
    Context context;
    ArrayList<LiveStreamUserModel.LiveStreamUserData> list = new ArrayList<>();

    public LiveStreamUserAdapter(Context context, ArrayList<LiveStreamUserModel.LiveStreamUserData> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(ItemLiveStreamUserBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemLiveStreamUserBinding binding;

        public MyViewHolder(@NonNull ItemLiveStreamUserBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        private void bind(LiveStreamUserModel.LiveStreamUserData data) {
            RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.place_holder).transform(new CenterCrop()).error(R.drawable.place_holder).priority(Priority.HIGH);
            Glide.with(context).load(data.getImage() == null ? "" : data.getImage()).apply(options).into(binding.imgAccountProfile);
        }
    }
}
