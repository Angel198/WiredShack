package com.jaylax.wiredshack.user.notification;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ItemAcceptedRequestBinding;

import java.util.ArrayList;

public class AcceptedRequestAdapter extends RecyclerView.Adapter<AcceptedRequestAdapter.MyViewHolder> {
    ArrayList<DummyModel> dummyList;

    public AcceptedRequestAdapter(ArrayList<DummyModel> dummyList) {
        this.dummyList = dummyList;
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

        private void bind(int pos,DummyModel data){
            if (data.dayName.isEmpty()){
                mBinding.tvAcceptedRequestHeader.setVisibility(View.GONE);
            }else {
                mBinding.tvAcceptedRequestHeader.setVisibility(View.VISIBLE);
            }

            if (pos+1 < dummyList.size()){
                if (dummyList.get(pos+1).dayName.isEmpty()){
                    mBinding.viewAcceptedRequest.setVisibility(View.GONE);
                }else {
                    mBinding.viewAcceptedRequest.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
