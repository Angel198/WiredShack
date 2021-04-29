package com.jaylax.wiredshack.user.dashboard;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
        checkLocationPermission();
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
                    if (SharePref.getInstance(context).get(SharePref.PREF_USER, "").toString().isEmpty()) {
                        Intent intent = new Intent(context, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        replaceFragment(new NotificationFragment(), "Notification");
                    }
                } else if (tab.getPosition() == 3) {
                    if (SharePref.getInstance(context).get(SharePref.PREF_USER, "").toString().isEmpty()) {
                        Intent intent = new Intent(context, LoginActivity.class);
                        startActivity(intent);
                    } else {
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

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.txt_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(DashboardActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    //Request location updates:
//                        locationManager.requestLocationUpdates(provider, 400, 1, this);
                }

            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

        }
    }
}