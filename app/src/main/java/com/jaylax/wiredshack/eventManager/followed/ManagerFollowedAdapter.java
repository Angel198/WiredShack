package com.jaylax.wiredshack.eventManager.followed;

import android.content.Context;
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
import com.jaylax.wiredshack.databinding.ItemUserFollowingBinding;
import com.jaylax.wiredshack.user.following.UserFollowingMainModel;

import java.util.ArrayList;

public class ManagerFollowedAdapter extends RecyclerView.Adapter<ManagerFollowedAdapter.MyViewHolder> implements Filterable {
    Context context;
    ArrayList<ManagerFollowedMainModel.ManagerFollowedData> list;
    ArrayList<ManagerFollowedMainModel.ManagerFollowedData> filteredFollowedList;

    public ManagerFollowedAdapter(Context context, ArrayList<ManagerFollowedMainModel.ManagerFollowedData> list) {
        this.context = context;
        this.list = list;
        this.filteredFollowedList = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_user_following, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(position, filteredFollowedList.get(position));
    }

    public int getItemCount() {
        return filteredFollowedList.size();
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

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredFollowedList = list;
                } else {
                    ArrayList<ManagerFollowedMainModel.ManagerFollowedData> filteredList = new ArrayList<>();
                    for (ManagerFollowedMainModel.ManagerFollowedData movie : list) {
                        if (movie.getUserName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(movie);
                        }
                    }
                    filteredFollowedList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredFollowedList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredFollowedList = (ArrayList<ManagerFollowedMainModel.ManagerFollowedData>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
