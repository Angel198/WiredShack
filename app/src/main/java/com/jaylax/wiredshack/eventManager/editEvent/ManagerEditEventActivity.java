package com.jaylax.wiredshack.eventManager.editEvent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Bundle;

import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ActivityManagerEditEventBinding;
import com.jaylax.wiredshack.eventManager.eventdetails.EventParticipateUserAdapter;

public class ManagerEditEventActivity extends AppCompatActivity {

    ActivityManagerEditEventBinding mBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_manager_edit_event);

        mBinding.recyclerEventUserEdit.setLayoutManager(new GridLayoutManager(this,4));
        mBinding.recyclerEventUserEdit.setAdapter(new EventParticipateUserAdapter(true));
    }
}