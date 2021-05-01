package com.jaylax.wiredshack.eventManager.managerActivity;

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
import com.jaylax.wiredshack.databinding.ItemIncomingRequestBinding;

import java.util.ArrayList;

public class ManagerIncomingRequestAdapter extends RecyclerView.Adapter<ManagerIncomingRequestAdapter.MyViewHolder> {
    Context context;
    ArrayList<IncomingRequestMainModel.IncomingRequest> list;
    EventRequestClick listener;

    public ManagerIncomingRequestAdapter(Context context, ArrayList<IncomingRequestMainModel.IncomingRequest> list, EventRequestClick listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_incoming_request, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(position, list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemIncomingRequestBinding mBinding;

        public MyViewHolder(ItemIncomingRequestBinding itemView) {
            super(itemView.getRoot());
            this.mBinding = itemView;
        }

        public void bind(int pos, IncomingRequestMainModel.IncomingRequest data) {
            RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.place_holder).transform(new CenterCrop()).error(R.drawable.place_holder).priority(Priority.HIGH);
            Glide.with(context).load(data.getUserImage() == null ? "" : data.getUserImage()).apply(options).into(mBinding.imgRequestUserImage);
            mBinding.tvRequestUserName.setText(data.getUserName() == null ? "N/A" : data.getUserName());
            mBinding.tvRequestEventName.setText(data.getEventName() == null ? "N/A" : data.getEventName());

            mBinding.tvRequestAccept.setOnClickListener(view -> {
                listener.onAcceptCancel(pos,data,"accept");
            });

            mBinding.tvRequestCancel.setOnClickListener(view -> {
                listener.onAcceptCancel(pos,data,"cancel");
            });
        }
    }

    public interface EventRequestClick {
        void onAcceptCancel(int pos, IncomingRequestMainModel.IncomingRequest data, String flag);
    }
}


