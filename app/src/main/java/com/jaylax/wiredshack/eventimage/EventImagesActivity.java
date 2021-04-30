package com.jaylax.wiredshack.eventimage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ActivityEventImagesBinding;
import com.jaylax.wiredshack.eventManager.editEvent.EventImageModel;

import java.util.ArrayList;

public class EventImagesActivity extends AppCompatActivity {

    ActivityEventImagesBinding mBinding;
    ArrayList<EventImageModel> imageArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_event_images);

        if (getIntent().hasExtra("eventImages")){
            String jsonString = getIntent().getStringExtra("eventImages");
            if (!jsonString.isEmpty()){
                imageArrayList = new Gson().fromJson(jsonString,new TypeToken< ArrayList<EventImageModel> >(){}.getType());
            }
        }

        mBinding.imgBack.setOnClickListener(view -> {
            onBackPressed();
        });
        if (imageArrayList.isEmpty()){
            onBackPressed();
        }else {
            EventImagePagerAdapter adapter = new EventImagePagerAdapter(imageArrayList,this);
            mBinding.viewPagerImages.setAdapter(adapter);
        }
    }
}