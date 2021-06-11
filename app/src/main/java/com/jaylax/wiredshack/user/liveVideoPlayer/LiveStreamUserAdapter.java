package com.jaylax.wiredshack.user.liveVideoPlayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jaylax.wiredshack.databinding.ItemLiveStreamUserBinding;

public class LiveStreamUserAdapter extends RecyclerView.Adapter<LiveStreamUserAdapter.MyViewHolder> {
    Context context;

    public LiveStreamUserAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(ItemLiveStreamUserBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemLiveStreamUserBinding binding;

        public MyViewHolder(@NonNull ItemLiveStreamUserBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
