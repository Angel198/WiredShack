package com.jaylax.wiredshack.eventManager.dashboard;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ActivityDashboardBinding;
import com.jaylax.wiredshack.eventManager.home.ManagerHomeFragment;
import com.jaylax.wiredshack.eventManager.managerActivity.ManagerActivityFragment;

import java.util.ArrayList;

public class DashboardEventManagerActivity extends AppCompatActivity {
    static ActivityDashboardBinding mBinding;

    ArrayList<Integer> arySelect = new ArrayList<>();

    static String eventId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard);

        mBinding.imgDashboardAdd.setVisibility(View.VISIBLE);
        arySelect.add(R.drawable.account_white);
        arySelect.add(R.drawable.notification_white);

        mBinding.tabDashboard.addTab(mBinding.tabDashboard.newTab().setTag(0).setIcon(R.drawable.account_white));
        mBinding.tabDashboard.addTab(mBinding.tabDashboard.newTab().setTag(1).setIcon(R.drawable.notification_white));

        replaceFragment(new ManagerHomeFragment(), "Home");
        initListener();
    }

    private void replaceFragment(Fragment fragment, String tagFragment) {
        if (!eventId.isEmpty()) {
            Bundle args = new Bundle();
            args.putString("eventId", eventId);
            fragment.setArguments(args);
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.viewDashboard, fragment, tagFragment);
        fragmentTransaction.commit();

        if (!eventId.isEmpty()) {
            eventId = "";
        }
    }

    private void initListener() {
        mBinding.tabDashboard.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.setIcon(arySelect.get((Integer) tab.getTag()));
                if (tab.getPosition() == 0) {
                    replaceFragment(new ManagerHomeFragment(), "Home");
                } else if (tab.getPosition() == 1) {
                    replaceFragment(new ManagerActivityFragment(), "EventDetails");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public static void redirectToEditEvent(String eventId, FragmentActivity activity) {
        DashboardEventManagerActivity.eventId = eventId;
        DashboardEventManagerActivity.mBinding.tabDashboard.getTabAt(1).select();
        /*Fragment fragment = new ManagerEventDetailsFragment();
        Bundle args = new Bundle();
        args.putString("eventId", eventId);
        fragment.setArguments(args);
        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.viewDashboard, fragment, "EventDetails");
        fragmentTransaction.commit();*/
    }
}