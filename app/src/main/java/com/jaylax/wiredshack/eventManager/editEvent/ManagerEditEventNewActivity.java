package com.jaylax.wiredshack.eventManager.editEvent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.birjuvachhani.locus.Locus;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.jaylax.wiredshack.MainActivity;
import com.jaylax.wiredshack.ProgressDialog;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ActivityManagerEditEventNewBinding;
import com.jaylax.wiredshack.eventManager.assignEvent.AssignEventActivity;
import com.jaylax.wiredshack.model.CommonResponseModel;
import com.jaylax.wiredshack.model.UserDetailsModel;
import com.jaylax.wiredshack.rest.ApiClient;
import com.jaylax.wiredshack.user.eventDetails.EventDetailsMainModel;
import com.jaylax.wiredshack.utils.AppMapView;
import com.jaylax.wiredshack.utils.Commons;
import com.jaylax.wiredshack.utils.SharePref;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManagerEditEventNewActivity extends AppCompatActivity implements OnMapReadyCallback {
    ActivityManagerEditEventNewBinding mBinding;
    Context context;
    UserDetailsModel userDetailsModel;
    ProgressDialog progressDialog;
    ArrayList<EventImageModel> imageList = new ArrayList<>();
    EventImagesAdapter imagesAdapter;
    RequestOptions options;
    final Calendar selectedCalendar = Calendar.getInstance();
    Calendar selectedStartTimeCal = null;
    Calendar selectedEndTimeCal = null;
    Place place = null;

    String editEventID = "";
    EventDetailsMainModel.EventDetailsData eventDetailsData = null;
    ArrayList<Integer> deleteImages = new ArrayList<>();
    String latitude = "", longitude = "";
    String tempLatitude = "", tempLongitude = "";
    String selectedManagerID = "";
    String temSelectedManagerID = "";
    String temSelectedManagerImageUrl = "";
    String tempImageUriPath = "";
    Uri temImageUri = null;
    String coverImageURL = "", coverImageID = "";
    String createdByID = "";
    ArrayList<SelectManagerListModel.SelectManagerListData> listData = new ArrayList<>();

    Dialog dialogEventData = null;

    String eventName = "", eventDate = "", eventDescription = "", eventLocation = "", eventTime = "", selectedOrganiserName = "", selectedOrganiserImgUrl = "", selectedOrganiserType = "";
    private GoogleMap mMap = null;

    final static int REQUEST_COVER_IMAGE = 101;
    final static int REQUEST_LIST_IMAGE = 102;
    final static int REQUEST_PLACE_PICKER = 103;
    final static int REQUEST_ASSIGN_MANAGER = 104;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_manager_edit_event_new);
        context = this;
        progressDialog = new ProgressDialog(context);

        if (getIntent().hasExtra("eventId")) {
            editEventID = getIntent().getStringExtra("eventId");
        }

        AppMapView mapFragment = (AppMapView) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.setListener(() -> {
            mBinding.nestedScrollEvent.requestDisallowInterceptTouchEvent(true);
        });

        Places.initialize(getApplicationContext(), getResources().getString(R.string.google_place_key));

        setClickListener();
//        setEditEventUI();

        options = new RequestOptions().centerCrop().placeholder(R.drawable.place_holder).transform(new CenterCrop(), new RoundedCorners(10)).error(R.drawable.place_holder).priority(Priority.HIGH);

        userDetailsModel = Commons.convertStringToObject(this, SharePref.PREF_USER, UserDetailsModel.class);
        mBinding.tvManagerName.setText(userDetailsModel.getName());

        mBinding.recyclerEventImages.setLayoutManager(new GridLayoutManager(this, 4));
        setEditEventUI();

        dialogEventData = new Dialog(context);
        dialogEventData.requestWindowFeature(Window.FEATURE_NO_TITLE);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
    }

    private void setEditEventUI() {
        if (!editEventID.isEmpty()) {
            getEventDetails();
        } else {
            setEventImagesAdapter();
            mBinding.imgDeleteEvent.setVisibility(View.GONE);
        }
    }

    private void getEventDetails() {
        if (Commons.isOnline(context)) {
            HashMap<String, String> params = new HashMap<>();
            params.put("event_id", editEventID);

            String header = "Bearer " + SharePref.getInstance(context).get(SharePref.PREF_TOKEN, "");
            progressDialog.show();
            ApiClient.create().getEventDetails(header, params).enqueue(new Callback<EventDetailsMainModel>() {
                @Override
                public void onResponse(Call<EventDetailsMainModel> call, Response<EventDetailsMainModel> response) {
                    progressDialog.dismiss();
                    if (response.code() == 200 && response.isSuccessful()) {
                        if (response.body() != null) {
                            if (response.body().getStatus().equals("200")) {
                                eventDetailsData = response.body().getData();
                                setEventDataForEdit();
                            } else {
                                Commons.showToast(context, getResources().getString(R.string.please_try_after_some_time));
                            }
                        }
                    } else {
                        Commons.showToast(context, getResources().getString(R.string.please_try_after_some_time));
                    }
                }

                @Override
                public void onFailure(Call<EventDetailsMainModel> call, Throwable t) {
                    progressDialog.dismiss();
                    Commons.showToast(context, getResources().getString(R.string.something_wants_wrong));
                }
            });
        } else {
            Commons.showToast(context, getResources().getString(R.string.no_internet_connection));
        }
    }

    private void setEventDataForEdit() {
        if (eventDetailsData != null) {
            eventName = eventDetailsData.getEventName();
            eventDescription = eventDetailsData.getDescription();
            eventLocation = eventDetailsData.getLocation();
            if (!eventDetailsData.getDate().isEmpty()) {
                SimpleDateFormat parseDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                SimpleDateFormat showDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                try {
                    Date date = parseDateFormat.parse(eventDetailsData.getDate());
                    selectedCalendar.setTime(date);
                    eventDate = showDateFormat.format(selectedCalendar.getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            SimpleDateFormat parseTimeFormat = new SimpleDateFormat("hh:mm:ss", Locale.US);
            SimpleDateFormat showTimeFormat = new SimpleDateFormat("hh:mm a", Locale.US);

            String eventStartTime = "";
            if (!eventDetailsData.getStime().isEmpty()) {
                try {
                    Date date = parseTimeFormat.parse(eventDetailsData.getStime());
                    selectedStartTimeCal = Calendar.getInstance();
                    selectedStartTimeCal.setTime(date);
                    eventStartTime = showTimeFormat.format(selectedStartTimeCal.getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            String eventEndTime = "";
            if (!eventDetailsData.getEtime().isEmpty()) {
                try {
                    Date date = parseTimeFormat.parse(eventDetailsData.getEtime());
                    selectedEndTimeCal = Calendar.getInstance();
                    selectedEndTimeCal.setTime(date);
                    eventEndTime = showTimeFormat.format(selectedEndTimeCal.getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            eventTime = eventStartTime + " - " + eventEndTime;
            deleteImages = new ArrayList<>();
            imageList = new ArrayList<>();
            for (EventDetailsMainModel.EventDetailsData.EventImage data : eventDetailsData.getImages()) {
                imageList.add(new EventImageModel(data.getImages(), data.getId(), "", null));
            }

            coverImageURL = eventDetailsData.getCoverImage() == null ? "" : eventDetailsData.getCoverImage();
            coverImageID = eventDetailsData.getCoverImageId();

            latitude = eventDetailsData.getLatitude();
            longitude = eventDetailsData.getLongitude();
            tempLatitude = eventDetailsData.getLatitude();
            tempLongitude = eventDetailsData.getLongitude();

            if (eventDetailsData.getSelectedManager() != null) {
                if (eventDetailsData.getSelectedManager().getName() != null && eventDetailsData.getSelectedManager().getId() != null) {
                    selectedOrganiserName = eventDetailsData.getSelectedManager().getName();
                    selectedOrganiserImgUrl = eventDetailsData.getSelectedManager().getImage();
                    selectedOrganiserType = eventDetailsData.getSelectedManager().getUserType();
                    selectedManagerID = eventDetailsData.getSelectedManager().getId();
                    temSelectedManagerID = eventDetailsData.getSelectedManager().getId();
                }
            }

            createdByID = eventDetailsData.getCreatedBy() == null ? "" : eventDetailsData.getCreatedBy();

            if (userDetailsModel.getId().equals(createdByID)) {
                if (isEventLive(eventDetailsData.getDate(), eventDetailsData.getStime(), eventDetailsData.getEtime())) {
                    mBinding.imgEventCoverEdit.setVisibility(View.GONE);
                    mBinding.imgEventDataEdit.setVisibility(View.GONE);
                    mBinding.imgEventDateTimeEdit.setVisibility(View.GONE);
                    mBinding.imgShareEvent.setVisibility(View.GONE);
                    mBinding.imgDeleteEvent.setVisibility(View.GONE);
                } else {
                    mBinding.imgEventCoverEdit.setVisibility(View.VISIBLE);
                    mBinding.imgEventDataEdit.setVisibility(View.VISIBLE);
                    mBinding.imgEventDateTimeEdit.setVisibility(View.VISIBLE);
                    mBinding.imgShareEvent.setVisibility(View.VISIBLE);
                    mBinding.imgDeleteEvent.setVisibility(View.VISIBLE);
                }
            } else {
                mBinding.tvManagerName.setText(eventDetailsData.getName());
                mBinding.imgEventCoverEdit.setVisibility(View.GONE);
                mBinding.imgEventDataEdit.setVisibility(View.GONE);
                mBinding.imgEventDateTimeEdit.setVisibility(View.GONE);
                mBinding.imgShareEvent.setVisibility(View.GONE);
                mBinding.imgDeleteEvent.setVisibility(View.GONE);
            }
            setEventDetailsData();
        }
        setEventImagesAdapter();
    }

    private void setEventDetailsData() {
        mBinding.editEventName.setText(eventName);
        mBinding.editEventDescription.setText(eventDescription);
        mBinding.editEventLocation.setText(eventLocation);
        mBinding.editEventDate.setText(eventDate);
        mBinding.editEventTime.setText(eventTime);
        if (coverImageURL.isEmpty()) {
            Glide.with(this).load(temImageUri).apply(options).into(mBinding.imgCoverPhoto);
        } else {
            Glide.with(this).load(coverImageURL).apply(options).into(mBinding.imgCoverPhoto);
        }

        if (selectedManagerID.isEmpty() && selectedOrganiserName.isEmpty()) {
            mBinding.linearSelectManager.setVisibility(View.GONE);
        } else {
            mBinding.linearSelectManager.setVisibility(View.VISIBLE);
            if (selectedOrganiserType.isEmpty()) {
                if (userDetailsModel.getUserType() == null) {
                    mBinding.tvSelectManagerTitle.setText(context.getResources().getString(R.string.club));
                } else {
                    if (userDetailsModel.getUserType().equals("2")) {
                        mBinding.tvSelectManagerTitle.setText(context.getResources().getString(R.string.dj));
                    } else {
                        mBinding.tvSelectManagerTitle.setText(context.getResources().getString(R.string.club));
                    }
                }
            } else {
                if (eventDetailsData.getSelectedManager().getUserType().equals("2")) {
                    mBinding.tvSelectManagerTitle.setText(context.getResources().getString(R.string.club));
                } else {
                    mBinding.tvSelectManagerTitle.setText(context.getResources().getString(R.string.dj));
                }
            }


            Glide.with(context).load(selectedOrganiserImgUrl).apply(options).into(mBinding.imgSelectManagerProfile);
            mBinding.tvSelectManagerName.setText(selectedOrganiserName);
        }
        setEventMarker();
    }

    private void setEventImagesAdapter() {
        if (userDetailsModel.getId().equals(createdByID) || createdByID.isEmpty()) {
            imagesAdapter = new EventImagesAdapter(context, true, imageList, new EventImagesAdapter.EventImageClick() {
                @Override
                public void onImageRemove(int position) {
                    if (!imageList.get(position).getImageId().isEmpty()) {
                        deleteImages.add(Integer.parseInt(imageList.get(position).getImageId()));
                    }
                    imageList.remove(position);
                    imagesAdapter.notifyItemRemoved(position);
                    imagesAdapter.notifyDataSetChanged();
                }

                @Override
                public void addImage() {
                    showImagePickerAlert(REQUEST_LIST_IMAGE);
                }
            });
        } else {
            imagesAdapter = new EventImagesAdapter(context, false, imageList);
        }
        mBinding.recyclerEventImages.setAdapter(imagesAdapter);

    }

    private void setClickListener() {
        mBinding.imgBack.setOnClickListener(view -> onBackPressed());

        mBinding.editEventDate.setOnClickListener(view -> {
            DatePickerDialog dialog = new DatePickerDialog(this, (datePicker, year, monthOfYear, dayOfMonth) -> {
                selectedCalendar.set(Calendar.YEAR, year);
                selectedCalendar.set(Calendar.MONTH, monthOfYear);
                selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "dd-MM-yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                mBinding.editEventDate.setText(sdf.format(selectedCalendar.getTime()));
            }, selectedCalendar.get(Calendar.YEAR), selectedCalendar.get(Calendar.MONTH),
                    selectedCalendar.get(Calendar.DAY_OF_MONTH));

            Calendar currentCal = Calendar.getInstance();
            currentCal.add(Calendar.DAY_OF_MONTH, 1);
            dialog.getDatePicker().setMinDate(currentCal.getTime().getTime());
            dialog.show();
        });

        mBinding.imgShareEvent.setOnClickListener(view -> {
            boolean isValid = false;
            if (tempImageUriPath.isEmpty() && coverImageURL.isEmpty()) {
                Commons.showToast(context, getResources().getString(R.string.upload_cover_photo));
            } else if (imageList.isEmpty()) {
                Commons.showToast(context, getResources().getString(R.string.add_atleast_one_image));
            } else if (eventName.isEmpty()) {
                Commons.showToast(context, getResources().getString(R.string.enter_event_name));
            } else if (eventDescription.isEmpty()) {
                Commons.showToast(context, getResources().getString(R.string.enter_event_description));
            } else if (eventLocation.isEmpty() || latitude.isEmpty() || longitude.isEmpty()) {
                Commons.showToast(context, getResources().getString(R.string.enter_event_location));
            } else if (eventDate.isEmpty()) {
                Commons.showToast(context, getResources().getString(R.string.enter_event_date));
            } else if (selectedManagerID.isEmpty()) {
                if (userDetailsModel.getUserType() == null) {
                    Commons.showToast(context, getResources().getString(R.string.select_organiser_error));
                } else {
                    if (userDetailsModel.getUserType().equals("2")) {
                        Commons.showToast(context, getResources().getString(R.string.select_dj_organiser_error));
                    } else {
                        Commons.showToast(context, getResources().getString(R.string.select_event_organiser_error));
                    }
                }
            } else if (selectedStartTimeCal == null) {
                Commons.showToast(context, getResources().getString(R.string.enter_event_start_time));
            } else if (selectedEndTimeCal == null) {
                Commons.showToast(context, getResources().getString(R.string.enter_event_end_time));
            } else {
                Date sDate = selectedStartTimeCal.getTime();
                Date eDate = selectedEndTimeCal.getTime();

                if (sDate.after(eDate)) {
                    Commons.showToast(context, getResources().getString(R.string.start_end_time_not_valid_msg));
                } else {
                    isValid = true;
                }
            }

            if (isValid) {
                String myFormat = "HH:mm"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                String startTime = sdf.format(selectedStartTimeCal.getTime());
                String endTime = sdf.format(selectedEndTimeCal.getTime());

                addEditEvent(eventName, eventDescription, eventLocation, eventDate, startTime, endTime);
            }
        });

        mBinding.imgEventCoverEdit.setOnClickListener(view -> {
            showDialogUploadCoverImage();
        });

        mBinding.imgEventDataEdit.setOnClickListener(view -> {
            showDialogEventData();
        });

        mBinding.imgEventDateTimeEdit.setOnClickListener(view -> {

            DatePickerDialog dialog = new DatePickerDialog(this, R.style.Dialog_Theme, (datePicker, year, monthOfYear, dayOfMonth) -> {
                selectedCalendar.set(Calendar.YEAR, year);
                selectedCalendar.set(Calendar.MONTH, monthOfYear);
                selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "dd-MM-yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                eventDate = sdf.format(selectedCalendar.getTime());

                showStartTimeDialog();
            }, selectedCalendar.get(Calendar.YEAR), selectedCalendar.get(Calendar.MONTH),
                    selectedCalendar.get(Calendar.DAY_OF_MONTH));

            Calendar currentCal = Calendar.getInstance();
            currentCal.add(Calendar.DAY_OF_MONTH, 1);
            dialog.getDatePicker().setMinDate(currentCal.getTime().getTime());
            dialog.show();

        });

        mBinding.imgDeleteEvent.setOnClickListener(view -> {

            showDeleteEventDialog();
        });
    }

    private void showDeleteEventDialog() {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_log_out);

        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(window.getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);

        AppCompatTextView tvTitle = (AppCompatTextView) dialog.findViewById(R.id.tvLogoutTitle);
        tvTitle.setText(context.getResources().getString(R.string.delete_event_msg));
        AppCompatTextView tvLogoutYes = (AppCompatTextView) dialog.findViewById(R.id.tvLogoutYes);
        AppCompatTextView tvLogoutNo = (AppCompatTextView) dialog.findViewById(R.id.tvLogoutNo);

        tvLogoutYes.setOnClickListener(view -> {
            dialog.dismiss();
            deleteEvent();
        });

        tvLogoutNo.setOnClickListener(view -> {
            dialog.dismiss();
        });
        dialog.show();
    }

    private void deleteEvent() {
        if (Commons.isOnline(context)) {
            progressDialog.show();
            String header = "Bearer " + SharePref.getInstance(context).get(SharePref.PREF_TOKEN, "");

            HashMap<String, String> params = new HashMap<>();
            params.put("event_id", editEventID);

            ApiClient.create().deleteEvent(header, params).enqueue(new Callback<CommonResponseModel>() {
                @Override
                public void onResponse(Call<CommonResponseModel> call, Response<CommonResponseModel> response) {
                    progressDialog.dismiss();
                    if (response.code() == 200 && response.isSuccessful()) {
                        if (response.body() != null) {
                            if (response.body().getStatus().equals("200")) {
                                onBackPressed();
                            } else {
                                String msg = "";
                                if (response.body().getMessage().isEmpty()) {
                                    msg = context.getResources().getString(R.string.please_try_after_some_time);
                                } else {
                                    msg = response.body().getMessage();
                                }
                                Commons.showToast(context, msg);
                            }
                        }
                    } else {
                        Commons.showToast(context, getResources().getString(R.string.please_try_after_some_time));
                    }
                }

                @Override
                public void onFailure(Call<CommonResponseModel> call, Throwable t) {
                    progressDialog.dismiss();
                    Commons.showToast(context, getResources().getString(R.string.something_wants_wrong));
                }
            });
        } else {
            Commons.showToast(context, context.getResources().getString(R.string.no_internet_connection));
        }
    }

    private void showStartTimeDialog() {
        int startHours = selectedStartTimeCal == null ? 12 : selectedStartTimeCal.get(Calendar.HOUR_OF_DAY);
        int startMins = selectedStartTimeCal == null ? 0 : selectedStartTimeCal.get(Calendar.MINUTE);
        TimePickerDialog mStartTimePicker = new TimePickerDialog(context, R.style.Dialog_Theme, (timePicker, selectedHour, selectedMinute) -> {
            selectedStartTimeCal = Calendar.getInstance();
            selectedStartTimeCal.set(Calendar.HOUR_OF_DAY, selectedHour);
            //instead of c.set(Calendar.HOUR, hour);
            selectedStartTimeCal.set(Calendar.MINUTE, selectedMinute);
            String myFormat = "hh:mm a"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

            eventTime = "";
            eventTime = sdf.format(selectedStartTimeCal.getTime());
            showEndTimeDialog();

//                mBinding.editEventStartTime.setText(sdf.format(selectedStartTimeCal.getTime()));
        }, startHours, startMins, false);//Yes 24 hour time
        mStartTimePicker.setTitle("Select Start Time");
        mStartTimePicker.show();
    }

    private void showEndTimeDialog() {
        int endHours = selectedEndTimeCal == null ? 12 : selectedEndTimeCal.get(Calendar.HOUR_OF_DAY);
        int endMins = selectedEndTimeCal == null ? 0 : selectedEndTimeCal.get(Calendar.MINUTE);
        TimePickerDialog mEndTimePicker = new TimePickerDialog(context, R.style.Dialog_Theme, (timePicker, selectedHour, selectedMinute) -> {
            selectedEndTimeCal = Calendar.getInstance();
            selectedEndTimeCal.set(Calendar.HOUR_OF_DAY, selectedHour);
            selectedEndTimeCal.set(Calendar.MINUTE, selectedMinute);
            String myFormat = "hh:mm a"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

            eventTime = eventTime + " - " + sdf.format(selectedEndTimeCal.getTime());

            setEventDetailsData();
//                mBinding.editEventEndTime.setText(sdf.format(selectedEndTimeCal.getTime()));
        }, endHours, endMins, false);//Yes 24 hour time
        mEndTimePicker.setTitle("Select End Time");
        mEndTimePicker.show();
    }

    private void showDialogUploadCoverImage() {
        dialogEventData.setContentView(R.layout.dialog_event_cover_image);

        Window window = dialogEventData.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(window.getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);

        setImageInView();
        dialogEventData.findViewById(R.id.imgCoverDialogClose).setOnClickListener(view -> {
            if (!coverImageURL.isEmpty()) {
                temImageUri = null;
                tempImageUriPath = "";
            }
            dialogEventData.dismiss();
        });

        dialogEventData.findViewById(R.id.imgCoverDialogImage).setOnClickListener(view -> {
            showImagePickerAlert(REQUEST_COVER_IMAGE);
        });

        dialogEventData.findViewById(R.id.tvCoverDialogDone).setOnClickListener(view -> {
            if (temImageUri != null && !tempImageUriPath.isEmpty()) {
                if (!coverImageURL.isEmpty()) {
                    coverImageURL = "";
                }
                setEventDetailsData();
                dialogEventData.dismiss();
            } else {
                if (coverImageURL.isEmpty()) {
                    Commons.showToast(context, getResources().getString(R.string.upload_cover_photo));
                } else {
                    dialogEventData.dismiss();
                }
            }
        });
        dialogEventData.setCanceledOnTouchOutside(false);
        dialogEventData.show();
    }

    private void setImageInView() {
        AppCompatImageView ivEditProfile = dialogEventData.findViewById(R.id.imgCoverDialogImage);
        if (ivEditProfile != null) {
            RequestOptions roundOptions = new RequestOptions().centerCrop().placeholder(0).transform(new CenterCrop(), new RoundedCorners(30)).error(0).priority(Priority.HIGH);
            if (temImageUri == null) {
                if (!coverImageURL.isEmpty()) {
                    Glide.with(context).load(coverImageURL).apply(roundOptions).into(ivEditProfile);
                }
            } else {
                Glide.with(context).load(temImageUri).apply(roundOptions).into(ivEditProfile);
            }
        }
    }

    private void showImagePickerAlert(int requestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.choose_image_from));
        builder.setCancelable(true);
        builder.setPositiveButton(getResources().getString(R.string.camera), (dialogInterface, i) -> {
            new ImagePicker.Builder(this).crop().cameraOnly().compress(1024).start(requestCode);
        });
        builder.setNegativeButton(getResources().getString(R.string.gallery), (dialogInterface, i) -> {
            new ImagePicker.Builder(this).crop().galleryOnly().compress(1024).start(requestCode);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);

    }

    private void showDialogEventData() {
        dialogEventData.setContentView(R.layout.dialog_event_data);

        Window window = dialogEventData.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(window.getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);

        AppCompatEditText etName = (AppCompatEditText) dialogEventData.findViewById(R.id.etEventDataName);
        AppCompatEditText etSelectOrganiser = (AppCompatEditText) dialogEventData.findViewById(R.id.etEventDataSelectOrganiser);
        AppCompatEditText etDescription = (AppCompatEditText) dialogEventData.findViewById(R.id.etEventDataDescription);
        AppCompatEditText etLocation = (AppCompatEditText) dialogEventData.findViewById(R.id.etEventDataLocation);

        if (userDetailsModel.getUserType() == null) {
            etSelectOrganiser.setHint(getResources().getString(R.string.hint_select_club));
        } else {
            if (userDetailsModel.getUserType().equals("2")) {
                etSelectOrganiser.setHint(getResources().getString(R.string.hint_select_dj));
            } else {
                etSelectOrganiser.setHint(getResources().getString(R.string.hint_select_club));
            }
        }
        etName.setText(eventName);
        etSelectOrganiser.setText(selectedOrganiserName);
        etDescription.setText(eventDescription);
        etLocation.setText(eventLocation);

        dialogEventData.findViewById(R.id.imgEventDataClose).setOnClickListener(view -> {
            tempLatitude = "";
            tempLongitude = "";
            temSelectedManagerID = "";
            temSelectedManagerImageUrl = "";
            dialogEventData.dismiss();
        });

        etSelectOrganiser.setOnClickListener(view -> {
            String name = etName.getText().toString().trim();
            if (name.isEmpty()) {
                Commons.showToast(context, getResources().getString(R.string.enter_event_name));
            } else {
                Intent intent = new Intent(context, AssignEventActivity.class);
                intent.putExtra("eventName", name);
                startActivityForResult(intent, REQUEST_ASSIGN_MANAGER);
            }
        });

        etLocation.setOnClickListener(view -> {
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)).build(context);
            startActivityForResult(intent, REQUEST_PLACE_PICKER);
        });

        dialogEventData.findViewById(R.id.tvEventDataDone).setOnClickListener(view -> {
            String name = etName.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String location = etLocation.getText().toString().trim();
            String organiserName = etSelectOrganiser.getText().toString().trim();

            if (name.isEmpty()) {
                Commons.showToast(context, getResources().getString(R.string.enter_event_name));
            } else if (temSelectedManagerID.isEmpty()) {
                if (userDetailsModel.getUserType() == null) {
                    Commons.showToast(context, getResources().getString(R.string.select_organiser_error));
                } else {
                    if (userDetailsModel.getUserType().equals("2")) {
                        Commons.showToast(context, getResources().getString(R.string.select_dj_organiser_error));
                    } else {
                        Commons.showToast(context, getResources().getString(R.string.select_event_organiser_error));
                    }
                }
            } else if (description.isEmpty()) {
                Commons.showToast(context, getResources().getString(R.string.enter_event_description));
            } else if (location.isEmpty() || tempLatitude.isEmpty() || tempLongitude.isEmpty()) {
                Commons.showToast(context, getResources().getString(R.string.enter_event_location));
            } else {
                eventName = name;
                eventDescription = description;
                eventLocation = location;
                latitude = tempLatitude;
                longitude = tempLongitude;
                selectedOrganiserName = organiserName;
                selectedManagerID = temSelectedManagerID;
                selectedOrganiserImgUrl = temSelectedManagerImageUrl;
                setEventDetailsData();
                dialogEventData.dismiss();
            }
        });

        dialogEventData.setCanceledOnTouchOutside(false);
        dialogEventData.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_COVER_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                temImageUri = Uri.parse(data.getData().toString());
                tempImageUriPath = ImagePicker.Companion.getFilePath(data);
                setImageInView();
            }
        } else if (requestCode == REQUEST_LIST_IMAGE) {
            imageList.add(new EventImageModel("", "", ImagePicker.Companion.getFilePath(data), Uri.parse(data.getData().toString())));
            imagesAdapter.notifyDataSetChanged();
        } else if (requestCode == REQUEST_PLACE_PICKER) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                place = Autocomplete.getPlaceFromIntent(data);

                String placeName = place.getName() == null ? "N/A" : place.getName();
                String placeAddress = place.getAddress() == null ? "N/A" : place.getAddress();
                String address = placeName + ", " + placeAddress;
                tempLatitude = String.valueOf(place.getLatLng().latitude);
                tempLongitude = String.valueOf(place.getLatLng().longitude);

                if (dialogEventData != null) {
                    AppCompatEditText editText = (AppCompatEditText) dialogEventData.findViewById(R.id.etEventDataLocation);
                    editText.setText(address);
                }
            }
        } else if (requestCode == REQUEST_ASSIGN_MANAGER) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                temSelectedManagerID = data.getStringExtra("selectedId");
                temSelectedManagerImageUrl = data.getStringExtra("selectedImage");
                if (dialogEventData != null) {
                    AppCompatEditText editText = (AppCompatEditText) dialogEventData.findViewById(R.id.etEventDataSelectOrganiser);
                    editText.setText(data.getStringExtra("selectedName"));
                }
            }
        }
    }

    private void addEditEvent(String eventName, String eventDescription, String eventLocation, String eventDate, String startTime, String endTime) {
        if (Commons.isOnline(context)) {


            HashMap<String, RequestBody> params = new HashMap<>();
            params.put("name", RequestBody.create(MultipartBody.FORM, userDetailsModel.getName()));
            params.put("event_name", RequestBody.create(MultipartBody.FORM, eventName));
            params.put("description", RequestBody.create(MultipartBody.FORM, eventDescription));
            params.put("edate", RequestBody.create(MultipartBody.FORM, eventDate));
            params.put("location", RequestBody.create(MultipartBody.FORM, eventLocation));
            params.put("stime", RequestBody.create(MultipartBody.FORM, startTime));
            params.put("etime", RequestBody.create(MultipartBody.FORM, endTime));
            params.put("event_id", RequestBody.create(MultipartBody.FORM, editEventID));
            params.put("latitude", RequestBody.create(MultipartBody.FORM, latitude));
            params.put("longitude", RequestBody.create(MultipartBody.FORM, longitude));
            params.put("uid", RequestBody.create(MultipartBody.FORM, selectedManagerID));
//            params.put("created_by", RequestBody.create(MultipartBody.FORM, userDetailsModel.getUserType()));
            params.put("created_by", RequestBody.create(MultipartBody.FORM, userDetailsModel.getId()));
            if (temImageUri != null && !tempImageUriPath.isEmpty()) {
                if (coverImageID.isEmpty()) {
                    params.put("cover_id", RequestBody.create(MultipartBody.FORM, ""));
                } else {
                    params.put("cover_id", RequestBody.create(MultipartBody.FORM, coverImageID));
                }
            } else {
                params.put("cover_id", RequestBody.create(MultipartBody.FORM, ""));
            }

            String header = "Bearer " + SharePref.getInstance(context).get(SharePref.PREF_TOKEN, "");

            ArrayList<MultipartBody.Part> imagesMultiPart = new ArrayList<>();
            for (EventImageModel data : imageList) {
                if (data.getImageURL().isEmpty() && !data.getImagePath().isEmpty()) {
                    File imageFile = new File(data.getImagePath());
                    RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
                    imagesMultiPart.add(MultipartBody.Part.createFormData("image[]", imageFile.getName(), requestBody));
                }
            }
            MultipartBody.Part coverImageMultiPart = null;
            if (temImageUri != null && !tempImageUriPath.isEmpty()) {
                File coverFile = new File(tempImageUriPath);
                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), coverFile);
                coverImageMultiPart = MultipartBody.Part.createFormData("cover_image", coverFile.getName(), requestBody);
            }

            progressDialog.show();
            ApiClient.create().addEditEvent(header, params, imagesMultiPart, coverImageMultiPart, deleteImages).enqueue(new Callback<CommonResponseModel>() {
                @Override
                public void onResponse(Call<CommonResponseModel> call, Response<CommonResponseModel> response) {
                    progressDialog.dismiss();
                    if (response.code() == 200 && response.isSuccessful()) {
                        if (response.body() != null) {
                            if (response.body().getStatus().equals("200")) {
                                finish();
                            } else {
                                String msg = "";
                                if (response.body().getMessage().isEmpty()) {
                                    msg = getResources().getString(R.string.please_try_after_some_time);
                                } else {
                                    msg = response.body().getMessage();
                                }
                                Commons.showToast(context, msg);
                            }
                        }
                    } else {
                        Commons.showToast(context, getResources().getString(R.string.please_try_after_some_time));
                    }
                }

                @Override
                public void onFailure(Call<CommonResponseModel> call, Throwable t) {
                    progressDialog.dismiss();
                    Commons.showToast(context, getResources().getString(R.string.something_wants_wrong));
                }
            });

        } else {
            Commons.showToast(context, getResources().getString(R.string.no_internet_connection));
        }
    }

    private void setEventMarker() {
        if (mMap == null) {
            setEventMarker();
        } else {
            if (latitude.isEmpty() || longitude.isEmpty()) {
                Locus.INSTANCE.getCurrentLocation(this, locusResult -> {
                    if (locusResult.getLocation() != null) {
                        LatLng latLng = new LatLng(locusResult.getLocation().getLatitude(), locusResult.getLocation().getLongitude());
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(17f).build();
                        if (mMap != null) {
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }
                    }
                    return null;
                });
            } else {
                LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                        .position(latLng));
                CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(17f).build();
                if (mMap != null) {
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }
        }
    }

    private boolean isEventLive(String eventDate, String startTime, String endTime) {
        boolean isLive = false;
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

        if (eventDate != null && startTime != null && endTime != null) {
            String eventSTime = eventDate + " " + startTime;

            String eventETime = eventDate + " " + endTime;
            try {
                Date eventSDate = format.parse(eventSTime);
                Date eventEDate = format.parse(eventETime);
                if (currentDate.after(eventSDate) && currentDate.before(eventEDate)) {
                    isLive = true;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return isLive;
    }
}