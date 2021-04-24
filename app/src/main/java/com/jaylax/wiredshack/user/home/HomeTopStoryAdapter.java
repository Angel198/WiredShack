package com.jaylax.wiredshack.user.home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ItemHomeTopStoryBinding;
import com.jaylax.wiredshack.user.eventDetails.EventDetailsActivity;

public class HomeTopStoryAdapter extends RecyclerView.Adapter<HomeTopStoryAdapter.MyViewHolder> {
    Context context;
    public HomeTopStoryAdapter(Context context) {
        this.context = context;
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
            mBinding.constraintMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, EventDetailsActivity.class);
                    intent.putExtra("eventId","9");
                    context.startActivity(intent);
                }
            });
        }
    }
}
