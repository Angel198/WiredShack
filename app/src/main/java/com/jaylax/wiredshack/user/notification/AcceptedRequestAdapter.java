package com.jaylax.wiredshack.user.notification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ItemAcceptedRequestBinding;

import java.util.ArrayList;

public class AcceptedRequestAdapter extends RecyclerView.Adapter<AcceptedRequestAdapter.MyViewHolder> {
    Context context;
    ArrayList<AcceptedEventMainModel.AcceptedData> dummyList;
    AcceptedEventClick listener;

    public AcceptedRequestAdapter(Context context, ArrayList<AcceptedEventMainModel.AcceptedData> dummyList, AcceptedEventClick listener) {
        this.context = context;
        this.dummyList = dummyList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_accepted_request, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(position,dummyList.get(position));
    }

    @Override
    public int getItemCount() {
        return dummyList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemAcceptedRequestBinding mBinding;

        public MyViewHolder(ItemAcceptedRequestBinding itemView) {
            super(itemView.getRoot());
            this.mBinding = itemView;
        }

        private void bind(int pos,AcceptedEventMainModel.AcceptedData data){
            if (data.getDayName().isEmpty()){
                mBinding.tvAcceptedRequestHeader.setVisibility(View.GONE);
            }else {
                mBinding.tvAcceptedRequestHeader.setVisibility(View.VISIBLE);
            }
            RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.place_holder).transform(new CenterCrop()).error(R.drawable.place_holder).priority(Priority.HIGH);
            Glide.with(context).load(data.getManagerProfileImage() == null ? "" : data.getManagerProfileImage()).apply(options).into(mBinding.imgAcceptedProfile);
            if (data.getUserType() ==null){
                mBinding.imgAcceptedProfile.setBackground(ContextCompat.getDrawable(context, R.drawable.back_dash_manager_border));
            }else {
                if (data.getUserType().equals("2")){
                    mBinding.imgAcceptedProfile.setBackground(ContextCompat.getDrawable(context, R.drawable.back_dash_manager_border));
                }else if (data.getUserType().equals("3")){
                    mBinding.imgAcceptedProfile.setBackground(ContextCompat.getDrawable(context, R.drawable.back_dash_dj_border));
                }else {
                    mBinding.imgAcceptedProfile.setBackground(ContextCompat.getDrawable(context, R.drawable.back_dash_manager_border));
                }
            }

            mBinding.tvManagerName.setText(data.getManagerName() == null ? "N/A" : data.getManagerName());
            mBinding.tvEventName.setText(data.getEventName() == null ? "N/A" : data.getEventName());

            if (pos+1 < dummyList.size()){
                if (dummyList.get(pos+1).getDayName().isEmpty()){
                    mBinding.viewAcceptedRequest.setVisibility(View.GONE);
                }else {
                    mBinding.viewAcceptedRequest.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    interface AcceptedEventClick{
        void onEventClick(int pos, AcceptedEventMainModel.AcceptedData data);
    }
}
