package com.jaylax.wiredshack.eventManager.assignEvent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ItemAssignManagerBinding;
import com.jaylax.wiredshack.eventManager.editEvent.SelectManagerListModel;

import java.util.ArrayList;

public class AssignManagerAdapter extends RecyclerView.Adapter<AssignManagerAdapter.MyViewHolder> implements Filterable {

    Context context;
    Activity activity;
    ArrayList<SelectManagerListModel.SelectManagerListData> list;
    ArrayList<SelectManagerListModel.SelectManagerListData> filteredManagerList;

    public AssignManagerAdapter(Context context, Activity activity,ArrayList<SelectManagerListModel.SelectManagerListData> list) {
        this.context = context;
        this.activity = activity;
        this.list = list;
        this.filteredManagerList= list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_assign_manager, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(position, filteredManagerList.get(position));
    }

    @Override
    public int getItemCount() {
        return filteredManagerList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemAssignManagerBinding mBinding;

        public MyViewHolder(@NonNull ItemAssignManagerBinding itemView) {
            super(itemView.getRoot());
            this.mBinding = itemView;
        }

        void bind(int pos, SelectManagerListModel.SelectManagerListData data) {
            RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.place_holder).transform(new CenterCrop()).error(R.drawable.place_holder).priority(Priority.HIGH);
            Glide.with(context).load(data.getManagerImage() == null ? "" : data.getManagerImage()).apply(options).into(mBinding.imgUserProfile);
            mBinding.tvUserName.setText(data.getManagerName() == null ? "N/A" : data.getManagerName());

            mBinding.tvAssign.setOnClickListener(view -> {
                Intent intent = new Intent();
                intent.putExtra("selectedId",data.getId());
                intent.putExtra("selectedName",data.getManagerName());
                intent.putExtra("selectedImage",data.getManagerImage());
                activity.setResult(Activity.RESULT_OK,intent);
                activity.finish();
            });
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredManagerList = list;
                } else {
                    ArrayList<SelectManagerListModel.SelectManagerListData> filteredList = new ArrayList<>();
                    for (SelectManagerListModel.SelectManagerListData movie : list) {
                        if (movie.getManagerName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(movie);
                        }
                    }
                    filteredManagerList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredManagerList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredManagerList = (ArrayList<SelectManagerListModel.SelectManagerListData>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
