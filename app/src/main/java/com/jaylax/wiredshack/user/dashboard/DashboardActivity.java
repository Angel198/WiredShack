package com.jaylax.wiredshack.user.dashboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.jaylax.wiredshack.LoginActivity;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.user.account.AccountFragment;
import com.jaylax.wiredshack.databinding.ActivityDashboardBinding;
import com.jaylax.wiredshack.user.home.HomeFragment;
import com.jaylax.wiredshack.user.notification.NotificationFragment;
import com.jaylax.wiredshack.user.search.SearchFragment;
import com.jaylax.wiredshack.utils.SharePref;

import java.util.ArrayList;
import java.util.Objects;

public class DashboardActivity extends AppCompatActivity {

    ActivityDashboardBinding mBinding;
    Context context;

    ArrayList<Integer> arySelect = new ArrayList<>();
    ArrayList<Integer> aryUnSelect = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard);

        context = this;

        arySelect.add(R.drawable.home_white);
        arySelect.add(R.drawable.search_white);
        arySelect.add(R.drawable.notification_white);
        arySelect.add(R.drawable.account_white);

        aryUnSelect.add(R.drawable.home);
        aryUnSelect.add(R.drawable.search);
        aryUnSelect.add(R.drawable.notification);
        aryUnSelect.add(R.drawable.account);

        mBinding.tabDashboard.addTab(mBinding.tabDashboard.newTab().setTag(0).setIcon(R.drawable.home_white));
        mBinding.tabDashboard.addTab(mBinding.tabDashboard.newTab().setTag(1).setIcon(R.drawable.search));
        mBinding.tabDashboard.addTab(mBinding.tabDashboard.newTab().setTag(2).setIcon(R.drawable.notification));
        mBinding.tabDashboard.addTab(mBinding.tabDashboard.newTab().setTag(3).setIcon(R.drawable.account));

        replaceFragment(new HomeFragment(), "Home");
        initListener();
    }

    private void replaceFragment(Fragment fragment, String tagFragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.viewDashboard, fragment, tagFragment);
        fragmentTransaction.commit();
    }

    private void initListener() {
        mBinding.tabDashboard.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.setIcon(arySelect.get((Integer) tab.getTag()));
                if (tab.getPosition() == 0) {
                    replaceFragment(new HomeFragment(), "Home");
                } else if (tab.getPosition() == 1) {
                    replaceFragment(new SearchFragment(), "Search");
                } else if (tab.getPosition() == 2) {
                    replaceFragment(new NotificationFragment(), "Notification");
                } else if (tab.getPosition() == 3) {
                    if (SharePref.getInstance(context).get(SharePref.PREF_USER,"").toString().isEmpty()){
                        Intent intent = new Intent(context, LoginActivity.class);
                        startActivity(intent);
                    }else {
                        replaceFragment(new AccountFragment(), "Account");
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.setIcon(aryUnSelect.get((Integer) tab.getTag()));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}