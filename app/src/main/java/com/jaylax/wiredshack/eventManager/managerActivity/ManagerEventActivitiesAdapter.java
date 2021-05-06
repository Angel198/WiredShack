package com.jaylax.wiredshack.eventManager.managerActivity;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
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
import com.jaylax.wiredshack.databinding.ItemEventActivitiesBinding;

import java.util.ArrayList;

public class ManagerEventActivitiesAdapter extends RecyclerView.Adapter<ManagerEventActivitiesAdapter.MyViewHolder> {

    Context mContext;
    ArrayList<ManagerActivityMainModel.ManagerActivityData> list;

    public ManagerEventActivitiesAdapter(Context mContext, ArrayList<ManagerActivityMainModel.ManagerActivityData> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_event_activities, parent, false));
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
        ItemEventActivitiesBinding mBinding;

        public MyViewHolder(ItemEventActivitiesBinding itemView) {
            super(itemView.getRoot());
            this.mBinding = itemView;
        }

        public void bind(int position, ManagerActivityMainModel.ManagerActivityData data) {
            RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.place_holder).transform(new CenterCrop()).error(R.drawable.place_holder).priority(Priority.HIGH);
            Glide.with(mContext).load(data.getProfileImage() == null ? "" : data.getProfileImage()).apply(options).into(mBinding.imgActivity);
//            mBinding.tvActivityUserName.setText(data.getName() == null ? "N/A" : data.getName());
//            mBinding.tvActivityDescription.setText(data.getMessage() == null ? " N/A" : " " + data.getMessage());
//            mBinding.tvActivityTime.setText(data.getTime() == null ? "" : " " +data.getTime());

            SpannableString name = new SpannableString(data.getName() == null ? "N/A" : data.getName());
            name.setSpan(new RelativeSizeSpan(1f), 0, name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            name.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.white)),0,name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mBinding.tvActivityUserName.setText(name);

            SpannableString activityTxt = new SpannableString(data.getMessage() == null ? " N/A" : " " + data.getMessage());
            activityTxt.setSpan(new RelativeSizeSpan(1f), 0, activityTxt.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            activityTxt.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.white)),0,activityTxt.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mBinding.tvActivityUserName.append(activityTxt);

            SpannableString timeTxt = new SpannableString(data.getTime() == null ? "" : "\n" +data.getTime());
            timeTxt.setSpan(new RelativeSizeSpan(0.8f), 0, timeTxt.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            timeTxt.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.colorHintTex)),0,timeTxt.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mBinding.tvActivityUserName.append(timeTxt);
        }
    }
}
