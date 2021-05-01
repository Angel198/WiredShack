package com.jaylax.wiredshack.user.search;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.jaylax.wiredshack.LoginActivity;
import com.jaylax.wiredshack.ProgressDialog;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.FragmentSearchBinding;
import com.jaylax.wiredshack.model.CommonResponseModel;
import com.jaylax.wiredshack.model.RecentEventMainModel;
import com.jaylax.wiredshack.model.UserDetailsModel;
import com.jaylax.wiredshack.rest.ApiClient;
import com.jaylax.wiredshack.user.eventDetails.EventDetailsActivity;
import com.jaylax.wiredshack.user.home.ManagerListMainModel;
import com.jaylax.wiredshack.user.managerDetails.ManagerDetailsActivity;
import com.jaylax.wiredshack.utils.AppMapView;
import com.jaylax.wiredshack.utils.Commons;
import com.jaylax.wiredshack.utils.SharePref;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment implements OnMapReadyCallback {
    FragmentSearchBinding mBinding;
    Context mContext;
    UserDetailsModel userDetailsModel;
    ProgressDialog progressDialog;
    private GoogleMap mMap = null;
    Place place = null;
    ArrayList<ManagerListMainModel.ManagerListData> managerList = new ArrayList<>();
    SearchSuggestionAdapter searchAdapter = null;
    String previousMarkerTag = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false);
        mContext = getActivity();
        progressDialog = new ProgressDialog(mContext);

        userDetailsModel = Commons.convertStringToObject(mContext, SharePref.PREF_USER, UserDetailsModel.class);

        AppMapView mapFragment = (AppMapView) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.setListener(() -> {
            mBinding.nestedScrollSearch.requestDisallowInterceptTouchEvent(true);
        });
        Places.initialize(getActivity().getApplicationContext(), getResources().getString(R.string.google_place_key));

//        checkLocationPermission();
        getRecentEvent();
        setViewClickListener();
        return mBinding.getRoot();
    }

    private void setViewClickListener() {
        mBinding.linearSearchByLocation.setOnClickListener(view -> {
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)).build(mContext);
            startActivityForResult(intent, 102);
        });

        mBinding.edtSearchByLocation.setOnClickListener(view -> {
            mBinding.linearSearchByLocation.performClick();
        });

        mBinding.edtSearchManager.addTextChangedListener(new TextWatcher() {
            Timer timer = new Timer();
            final Long delay = 1000L;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                timer.cancel();
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        getActivity().runOnUiThread(SearchFragment.this::searchManager);
                    }
                }, delay);
            }
        });
    }

    private void getRecentEvent() {
        if (Commons.isOnline(mContext)) {
            progressDialog.show();
            String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");
            ApiClient.create().getRecentEventsUser().enqueue(new Callback<RecentEventMainModel>() {
                @Override
                public void onResponse(Call<RecentEventMainModel> call, Response<RecentEventMainModel> response) {
                    progressDialog.dismiss();
                    if (response.code() == 200 && response.isSuccessful()) {
                        if (response.body() != null) {
                            setRecentEventMarker(response.body().getData());
                        } else {
                            Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                        }
                    } else {
                        Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                    }
                }

                @Override
                public void onFailure(Call<RecentEventMainModel> call, Throwable t) {
                    progressDialog.dismiss();
                    Commons.showToast(mContext, getResources().getString(R.string.something_wants_wrong));
                }
            });
        } else {
            Commons.showToast(mContext, mContext.getResources().getString(R.string.no_internet_connection));
        }
    }

    private void setRecentEventMarker(ArrayList<RecentEventMainModel.RecentEventData> list) {
        if (mMap == null) {
            setRecentEventMarker(list);
        } else {
            if (!list.isEmpty()) {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                LatLngBounds bounds = null;
                for (RecentEventMainModel.RecentEventData data : list) {
                    if (data.getLatitude() != null && data.getLongitude() != null) {
                        LatLng latLng = new LatLng(Double.parseDouble(data.getLatitude()), Double.parseDouble(data.getLongitude()));
                        Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title(data.getEventName()));
                        marker.setTag(data.getId());
                        builder.include(latLng);
                    }
                }

                bounds = builder.build();
                if (bounds != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));
                }
                mMap.setOnMarkerClickListener(marker -> {
                    String eventId = (String) marker.getTag();
                    if (eventId != null) {
                        if (!eventId.isEmpty()) {
                            if (previousMarkerTag.equals(eventId)) {
                                previousMarkerTag = eventId;

                                Intent intent = new Intent(mContext, EventDetailsActivity.class);
                                intent.putExtra("eventId", eventId);
                                mContext.startActivity(intent);
                            } else {
                                previousMarkerTag = eventId;
                            }
                        }
                    }
                    return false;
                });
            }
        }
    }

    private void searchManager() {
        if (Commons.isOnline(mContext)) {
            String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");

            HashMap<String, String> params = new HashMap<>();
            params.put("search", mBinding.edtSearchManager.getText().toString().trim());
            Call<ManagerListMainModel> call;
            if (SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "").toString().isEmpty()) {
                call = ApiClient.create().getGuestEventsManagerFilter(params);
            } else {
                call = ApiClient.create().getEventsManagerFilter(header, params);
            }
            if (call != null) {
                progressDialog.show();
                call.enqueue(new Callback<ManagerListMainModel>() {
                    @Override
                    public void onResponse(Call<ManagerListMainModel> call, Response<ManagerListMainModel> response) {
                        progressDialog.dismiss();
                        if (response.code() == 200 && response.isSuccessful()) {
                            if (response.body() != null) {
                                setEventManagerData(response.body().getData());
                                if (!response.body().getStatus().equals("200")) {
                                    Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                                }
                            } else {
                                Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                            }
                        } else {
                            Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                        }
                    }

                    @Override
                    public void onFailure(Call<ManagerListMainModel> call, Throwable t) {
                        progressDialog.dismiss();
                        Commons.showToast(mContext, getResources().getString(R.string.something_wants_wrong));
                    }
                });
            }
        } else {
            Commons.showToast(mContext, mContext.getResources().getString(R.string.no_internet_connection));
        }
    }

    private void setEventManagerData(ArrayList<ManagerListMainModel.ManagerListData> list) {
        if (list.isEmpty()) {
            mBinding.recyclerSearchSuggestion.setVisibility(View.GONE);
        } else {
            mBinding.recyclerSearchSuggestion.setVisibility(View.VISIBLE);
            managerList = new ArrayList<>();
            managerList = list;
            searchAdapter = new SearchSuggestionAdapter(mContext, managerList, (int position, ManagerListMainModel.ManagerListData data, String tag) -> {
                if (tag.equals("follow")) {
                    onFollowEventManager(position, data);
                } else {
                    Intent intent = new Intent(mContext, ManagerDetailsActivity.class);
                    intent.putExtra("managerId", data.getId());
                    intent.putExtra("listPos", String.valueOf(position));
                    startActivityForResult(intent, 101);
                }
            });
            mBinding.recyclerSearchSuggestion.setLayoutManager(new GridLayoutManager(getActivity(), 3));
            mBinding.recyclerSearchSuggestion.setAdapter(searchAdapter);
        }
    }

    private void onFollowEventManager(int position, ManagerListMainModel.ManagerListData data) {
        if (SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "").toString().isEmpty()) {
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
        } else {
            if (Commons.isOnline(mContext)) {
                progressDialog.show();
                HashMap<String, String> params = new HashMap<>();
                params.put("manager_id", data.getId());

                String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");
                ApiClient.create().followManager(header, params).enqueue(new Callback<CommonResponseModel>() {
                    @Override
                    public void onResponse(Call<CommonResponseModel> call, Response<CommonResponseModel> response) {
                        progressDialog.dismiss();
                        if (response.code() == 200 && response.isSuccessful()) {
                            if (response.body() != null) {
                                if (response.body().getStatus().equals("200")) {
                                    if (managerList.get(position).getFollowing().equals("1")) {
                                        managerList.get(position).setFollowing("0");
                                    } else {
                                        managerList.get(position).setFollowing("1");
                                    }
                                    if (searchAdapter != null) {
                                        searchAdapter.notifyDataSetChanged();
                                    }
                                } else {
                                    String msg = "";
                                    if (response.body().getMessage().isEmpty()) {
                                        msg = getResources().getString(R.string.please_try_after_some_time);
                                    } else {
                                        msg = response.body().getMessage();
                                    }
                                    Commons.showToast(mContext, msg);
                                }
                            }
                        } else {
                            Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                        }
                    }

                    @Override
                    public void onFailure(Call<CommonResponseModel> call, Throwable t) {
                        progressDialog.dismiss();
                        Commons.showToast(mContext, getResources().getString(R.string.something_wants_wrong));
                    }
                });
            } else {
                Commons.showToast(mContext, getResources().getString(R.string.no_internet_connection));
            }
        }
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.txt_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                int pos = Integer.parseInt(data.getStringExtra("listPos"));
                String followFlg = data.getStringExtra("followFlag");

                if (!managerList.isEmpty()) {
                    managerList.get(pos).setFollowing(followFlg);

                    if (searchAdapter != null) {
                        searchAdapter.notifyDataSetChanged();
                    }
                }
            }
        } else if (requestCode == 102) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                place = Autocomplete.getPlaceFromIntent(data);
                getMapCameraPos(place.getLatLng());
            }
        }
    }

    private void getMapCameraPos(LatLng latLng) {
        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latLng.latitude, latLng.longitude)).zoom(17f).build();
        if (mMap != null) {
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }
}