package com.jaylax.wiredshack.eventManager.editEvent;

import android.content.Context;
import android.os.Build;
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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ItemSelectManagerBinding;

import java.util.ArrayList;

public class SelectManagerAdapter extends RecyclerView.Adapter<SelectManagerAdapter.MyViewHolder> {

    Context context;
    ArrayList<SelectManagerListModel.SelectManagerListData> list;
    SelectManagerListener listener;

    public SelectManagerAdapter(Context context, ArrayList<SelectManagerListModel.SelectManagerListData> list, SelectManagerListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_select_manager, parent, false));
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
        ItemSelectManagerBinding mBinding;

        public MyViewHolder(@NonNull ItemSelectManagerBinding itemView) {
            super(itemView.getRoot());
            mBinding = itemView;
        }

        private void bind(int pos, SelectManagerListModel.SelectManagerListData data) {
            RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.place_holder).transform(new CenterCrop(), new RoundedCorners(10)).error(R.drawable.place_holder).priority(Priority.HIGH);
            String imageURL = data.getManagerImage() == null ? "" : data.getManagerImage();
            Glide.with(context).load(imageURL).apply(options).into(mBinding.imgSelectManagerProfile);

            mBinding.tvSelectManagerName.setText(data.getManagerName() == null ? "" : data.getManagerName());

            if (data.isSelect()) {
                mBinding.imgSelectManageRadio.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_radio_button_checked));
            } else {
                mBinding.imgSelectManageRadio.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_radio_button_unchecked));
            }

            mBinding.linearSelectManager.setOnClickListener(view -> {
                for (SelectManagerListModel.SelectManagerListData model : list){
                    model.setSelect(false);
                }
                data.setSelect(true);
                notifyDataSetChanged();
                listener.onSelectManager(pos,data);
            });
        }
    }

    public interface SelectManagerListener {
        void onSelectManager(int pos, SelectManagerListModel.SelectManagerListData data);
    }
}
