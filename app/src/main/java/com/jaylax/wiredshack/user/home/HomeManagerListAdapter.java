package com.jaylax.wiredshack.user.home;

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
import com.jaylax.wiredshack.databinding.ItemHomeManagerListBinding;

import java.util.ArrayList;

public class HomeManagerListAdapter extends RecyclerView.Adapter<HomeManagerListAdapter.MyViewHolder> {
    Context context;
    ArrayList<ManagerListMainModel.ManagerListData> list;
    ManagerClick listener;

    public HomeManagerListAdapter(Context context, ArrayList<ManagerListMainModel.ManagerListData> list, ManagerClick listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_home_manager_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(position,list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemHomeManagerListBinding mBinding;

        public MyViewHolder(ItemHomeManagerListBinding itemView) {
            super(itemView.getRoot());
            this.mBinding = itemView;
        }

        public void bind(int position, ManagerListMainModel.ManagerListData data) {
            RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.place_holder).transform(new CenterCrop()).error(R.drawable.place_holder).priority(Priority.HIGH);
            Glide.with(context).load(data.getManagerImage() == null ? "" : data.getManagerImage()).apply(options).into(mBinding.imgHomeManagerProfile);

            mBinding.tvHomeManagerName.setText(data.getManagerName() == null ? "N/A" : data.getManagerName());

            /*if (data.getUserType() ==null){
                mBinding.imgHomeManagerProfile.setBackground(ContextCompat.getDrawable(context, R.drawable.back_dash_manager_border));
            }else {
                if (data.getUserType().equals("2")){
                    mBinding.imgHomeManagerProfile.setBackground(ContextCompat.getDrawable(context, R.drawable.back_dash_manager_border));
                }else if (data.getUserType().equals("3")){
                    mBinding.imgHomeManagerProfile.setBackground(ContextCompat.getDrawable(context, R.drawable.back_dash_dj_border));
                }else {
                    mBinding.imgHomeManagerProfile.setBackground(ContextCompat.getDrawable(context, R.drawable.back_dash_manager_border));
                }
            }*/
            mBinding.constraintMain.setOnClickListener(view -> {
                listener.onManagerClick(data);
            });
        }
    }

    interface ManagerClick{
        void onManagerClick(ManagerListMainModel.ManagerListData data);
    }
}