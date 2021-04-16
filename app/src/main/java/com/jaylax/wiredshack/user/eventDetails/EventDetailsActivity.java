package com.jaylax.wiredshack.user.eventDetails;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ActivityEventDetailsBinding;

public class EventDetailsActivity extends AppCompatActivity {

    ActivityEventDetailsBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_event_details);
        mBinding.recyclerEventComment.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recyclerEventComment.setAdapter(new EventCommentAdapter());
        mBinding.imgBack.setOnClickListener(view -> onBackPressed());
    }
}