package com.jaylax.wiredshack.eventManager.followed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ItemUserFollowingBinding;

import java.util.ArrayList;

public class ManagerFollowedAdapter extends RecyclerView.Adapter<ManagerFollowedAdapter.MyViewHolder> {
    Context context;
    ArrayList<ManagerFollowedMainModel.ManagerFollowedData> list;

    public ManagerFollowedAdapter(Context context, ArrayList<ManagerFollowedMainModel.ManagerFollowedData> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_user_following, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(position, list.get(position));
    }

    public int getItemCount() {
        return list.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemUserFollowingBinding mBinding;

        public MyViewHolder(@NonNull ItemUserFollowingBinding itemView) {
            super(itemView.getRoot());
            this.mBinding = itemView;
        }

        void bind(int pos, ManagerFollowedMainModel.ManagerFollowedData data) {
            RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.place_holder).transform(new CenterCrop()).error(R.drawable.place_holder).priority(Priority.HIGH);
            Glide.with(context).load(data.getUserImage() == null ? "" : data.getUserImage()).apply(options).into(mBinding.imgUserProfile);
            mBinding.tvUserName.setText(data.getUserName() == null ? "N/A" : data.getUserName());
            mBinding.tvFollow.setText(context.getResources().getString(R.string.following));
        }
    }
}
