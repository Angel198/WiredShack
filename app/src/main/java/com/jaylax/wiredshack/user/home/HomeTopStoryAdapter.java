package com.jaylax.wiredshack.user.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ItemHomeTopStoryBinding;

public class HomeTopStoryAdapter extends RecyclerView.Adapter<HomeTopStoryAdapter.MyViewHolder> {
    public HomeTopStoryAdapter() {
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_home_top_story, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind();
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemHomeTopStoryBinding mBinding;

        public MyViewHolder(ItemHomeTopStoryBinding itemView) {
            super(itemView.getRoot());
            this.mBinding = itemView;
        }

        public void bind() {

        }
    }
}
