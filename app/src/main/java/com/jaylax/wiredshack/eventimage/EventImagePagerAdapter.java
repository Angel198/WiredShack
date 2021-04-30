package com.jaylax.wiredshack.eventimage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.eventManager.editEvent.EventImageModel;

import java.util.ArrayList;

public class EventImagePagerAdapter extends PagerAdapter {

    ArrayList<EventImageModel> imageList ;
    Context context;

    public EventImagePagerAdapter(ArrayList<EventImageModel> imageList, Context context) {
        this.imageList = imageList;
        this.context = context;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event_image_gallery,container,false);
        PhotoView imageView = (PhotoView) view.findViewById(R.id.imageEvent);
        RequestOptions options = new RequestOptions().placeholder(R.drawable.place_holder).error(R.drawable.place_holder).priority(Priority.HIGH);
        Glide.with(context).load(imageList.get(position).getImageURL()== null?"":imageList.get(position).getImageURL()).apply(options).into(imageView);
        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        View view = (View)object;
        container.removeView(view);
    }
}
