package com.jaylax.wiredshack.eventimage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ActivityEventImagesBinding;
import com.jaylax.wiredshack.user.eventDetails.EventDetailsMainModel;

import java.util.ArrayList;

public class EventImagesActivity extends AppCompatActivity {

    ActivityEventImagesBinding mBinding;
    ArrayList<EventDetailsMainModel.EventDetailsData.EventImage> imageArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_event_images);

        if (getIntent().hasExtra("eventImages")){
//            imageArrayList = (ArrayList<EventDetailsMainModel.EventDetailsData.EventImage>)getIntent().getStringArrayListExtra("eventImages") ;
        }
    }
}