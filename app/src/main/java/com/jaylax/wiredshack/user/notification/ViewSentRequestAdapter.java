package com.jaylax.wiredshack.user.notification;

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
import com.bumptech.glide.request.RequestOptions;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ItemViewSentRequestBinding;

import java.util.ArrayList;

public class ViewSentRequestAdapter extends RecyclerView.Adapter<ViewSentRequestAdapter.MyViewHolder> {
    Context context;
    ArrayList<PendingRequestMainModel.PendingRequestData> list;
    PendingRequestClick listener;

    public ViewSentRequestAdapter(Context context, ArrayList<PendingRequestMainModel.PendingRequestData> list, PendingRequestClick listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_view_sent_request,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(position,list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ItemViewSentRequestBinding mBinding;
        public MyViewHolder(@NonNull ItemViewSentRequestBinding itemView) {
            super(itemView.getRoot());
            this.mBinding = itemView;
        }

        void bind(int pos, PendingRequestMainModel.PendingRequestData data){
            RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.place_holder).transform(new CenterCrop()).error(R.drawable.place_holder).priority(Priority.HIGH);
            Glide.with(context).load(data.getManagerProfileImage() == null ? "" : data.getManagerProfileImage()).apply(options).into(mBinding.imgRequestProfile);
            /*if (data.getUserType() ==null){
                mBinding.imgRequestProfile.setBackground(ContextCompat.getDrawable(context, R.drawable.back_dash_manager_border));
            }else {
                if (data.getUserType().equals("2")){
                    mBinding.imgRequestProfile.setBackground(ContextCompat.getDrawable(context, R.drawable.back_dash_manager_border));
                }else if (data.getUserType().equals("3")){
                    mBinding.imgRequestProfile.setBackground(ContextCompat.getDrawable(context, R.drawable.back_dash_dj_border));
                }else {
                    mBinding.imgRequestProfile.setBackground(ContextCompat.getDrawable(context, R.drawable.back_dash_manager_border));
                }
            }*/

            mBinding.tvRequestManagerName.setText(data.getManagerName() == null ? "N/A" : data.getManagerName());
            mBinding.tvRequestEventName.setText(data.getEventName() == null ? "N/A" : data.getEventName());
            if (data.getRequestStatus().equals("1")) {
                mBinding.tvViewSentRequest.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.back_border_white));
                mBinding.tvViewSentRequest.setText(context.getResources().getString(R.string.cancel_request));
            } else {
                mBinding.tvViewSentRequest.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.back_follow));
                mBinding.tvViewSentRequest.setText(context.getResources().getString(R.string.request));
            }

            mBinding.tvViewSentRequest.setOnClickListener(view -> {
                listener.onRequestClick(pos,data,"");
            });

            mBinding.linearViewSentRequest.setOnClickListener(view -> {
                listener.onRequestClick(pos,data,"details");
            });
        }
    }

    interface PendingRequestClick{
        void onRequestClick(int pos, PendingRequestMainModel.PendingRequestData data, String flag);
    }
}
