package com.jaylax.wiredshack.user.eventDetails;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ItemEventDetailsImageBinding;
import com.jaylax.wiredshack.eventManager.editEvent.EventImageModel;
import com.jaylax.wiredshack.eventimage.EventImagesActivity;

import java.util.ArrayList;


public class EventDetailsImageAdapter extends RecyclerView.Adapter<EventDetailsImageAdapter.MyViewHolder> {
    Context context;
    ArrayList<EventImageModel> list;

    public EventDetailsImageAdapter(Context context, ArrayList<EventImageModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_event_details_image, parent, false));
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
        ItemEventDetailsImageBinding mBinding;

        public MyViewHolder(@NonNull ItemEventDetailsImageBinding itemView) {
            super(itemView.getRoot());
            mBinding = itemView;
        }

        void bind (int pos, EventImageModel data){
            RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.place_holder).transform(new CenterCrop()).error(R.drawable.place_holder).priority(Priority.HIGH);
            String imageUrl = data.getImageURL() == null ? "" : data.getImageURL();
            Glide.with(context).load(imageUrl).apply(options).into(mBinding.imgAccountImage);

            mBinding.constraintImage.setOnClickListener(view -> {
                Intent intent = new Intent(context, EventImagesActivity.class);
                intent.putExtra("eventImages", new Gson().toJson(list));
                context.startActivity(intent);
            });
        }
    }
}
