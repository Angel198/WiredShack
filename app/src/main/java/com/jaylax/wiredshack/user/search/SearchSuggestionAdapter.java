package com.jaylax.wiredshack.user.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ItemSearchSuggestionBinding;
import com.jaylax.wiredshack.user.home.ManagerListMainModel;

import java.util.ArrayList;

public class SearchSuggestionAdapter extends RecyclerView.Adapter<SearchSuggestionAdapter.MyViewHolder> {

    Context context;
    ArrayList<ManagerListMainModel.ManagerListData> list;
    EventMangerClick listener;

    public SearchSuggestionAdapter(Context context, ArrayList<ManagerListMainModel.ManagerListData> list, EventMangerClick listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_search_suggestion, parent, false));
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
        ItemSearchSuggestionBinding mBinding;

        public MyViewHolder(ItemSearchSuggestionBinding itemView) {
            super(itemView.getRoot());
            this.mBinding = itemView;
        }

        public void bind(int position, ManagerListMainModel.ManagerListData data) {

            RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.place_holder).transform(new CenterCrop(),new RoundedCorners(10)).error(R.drawable.place_holder).priority(Priority.HIGH);
            Glide.with(context).load(data.getManagerImage() == null ? "" : data.getManagerImage()).apply(options).into(mBinding.imageManager);

            mBinding.tvManagerName.setText(data.getManagerName() == null ? "N/A" : data.getManagerName());

            if (data.getFollowing() == null){
                mBinding.tvFollow.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.back_follow));
                mBinding.tvFollow.setText(context.getResources().getString(R.string.follow));
            }else {
                if (data.getFollowing().equals("1")) {
                    mBinding.tvFollow.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.back_border_white));
                    mBinding.tvFollow.setText(context.getResources().getString(R.string.following));
                } else {
                    mBinding.tvFollow.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.back_follow));
                    mBinding.tvFollow.setText(context.getResources().getString(R.string.follow));
                }
            }

            if (data.getUserType() ==null){
                mBinding.viewManager.setBackground(ContextCompat.getDrawable(context, R.drawable.back_manager_data));
//                mBinding.tvManagerType.setText(context.getResources().getString(R.string.event_organiser));
//                mBinding.tvManagerType.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_light));
            }else {
                if (data.getUserType().equals("2")){
                    mBinding.viewManager.setBackground(ContextCompat.getDrawable(context, R.drawable.back_manager_data));
//                    mBinding.tvManagerType.setText(context.getResources().getString(R.string.event_organiser));
//                    mBinding.tvManagerType.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_light));
                }else if (data.getUserType().equals("3")){
                    mBinding.viewManager.setBackground(ContextCompat.getDrawable(context, R.drawable.back_dj_data));
//                    mBinding.tvManagerType.setText(context.getResources().getString(R.string.dj_organiser));
//                    mBinding.tvManagerType.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_light));
                }else {
                    mBinding.viewManager.setBackground(ContextCompat.getDrawable(context, R.drawable.back_manager_data));
//                    mBinding.tvManagerType.setText(context.getResources().getString(R.string.event_organiser));
//                    mBinding.tvManagerType.setTextColor(ContextCompat.getColor(context,android.R.color.holo_red_light));
                }
            }

            mBinding.tvFollow.setOnClickListener(view -> {
                listener.onManagerClick(position,data,"follow");
            });

            mBinding.constraintManager.setOnClickListener(view -> {
                listener.onManagerClick(position,data,"");
            });
        }
    }

    public interface EventMangerClick{
        void onManagerClick(int position, ManagerListMainModel.ManagerListData data, String tag);
    }
}
