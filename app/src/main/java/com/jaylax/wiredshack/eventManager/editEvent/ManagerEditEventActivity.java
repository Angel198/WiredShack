package com.jaylax.wiredshack.eventManager.editEvent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.jaylax.wiredshack.ProgressDialog;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ActivityManagerEditEventBinding;
import com.jaylax.wiredshack.eventManager.eventdetails.EventImagesAdapter;
import com.jaylax.wiredshack.model.CommonResponseModel;
import com.jaylax.wiredshack.model.UserDetailsModel;
import com.jaylax.wiredshack.rest.ApiClient;
import com.jaylax.wiredshack.utils.Commons;
import com.jaylax.wiredshack.utils.SharePref;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManagerEditEventActivity extends AppCompatActivity {

    ActivityManagerEditEventBinding mBinding;
    Context context;
    UserDetailsModel userDetailsModel;
    ProgressDialog progressDialog;
    ArrayList<EventImageModel> imageList;
    RequestOptions options;
    EventImagesAdapter imagesAdapter;
    final Calendar selectedCalendar = Calendar.getInstance();
    Calendar selectedStartTimeCal = null;
    Calendar selectedEndTimeCal = null;

    Place place = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_manager_edit_event);
        context = this;
        progressDialog = new ProgressDialog(context);

        Places.initialize(getApplicationContext(), getResources().getString(R.string.google_place_key));

        setClickListener();

        imageList = new ArrayList<>();
        options = new RequestOptions().centerCrop().placeholder(R.drawable.place_holder).transform(new CenterCrop(), new RoundedCorners(10)).error(R.drawable.place_holder).priority(Priority.HIGH);

        userDetailsModel = Commons.convertStringToObject(this, SharePref.PREF_USER, UserDetailsModel.class);
        Glide.with(this).load(userDetailsModel.getImage() == null ? "" : userDetailsModel.getImage()).apply(options).into(mBinding.imgProfilePhoto);
        Glide.with(this).load(userDetailsModel.getCoverImage() == null ? "" : userDetailsModel.getCoverImage()).apply(options).into(mBinding.imgCoverPhoto);
        mBinding.tvManagerName.setText(userDetailsModel.getName());

        mBinding.recyclerEventImages.setLayoutManager(new GridLayoutManager(this, 4));
        imagesAdapter = new EventImagesAdapter(context, true, imageList, new EventImagesAdapter.EventImageClick() {
            @Override
            public void onImageRemove(int position) {
                imageList.remove(position);
                imagesAdapter.notifyItemRemoved(position);
                imagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void addImage() {
                showImagePickerAlert();
            }
        });
        mBinding.recyclerEventImages.setAdapter(imagesAdapter);
    }

    private void setClickListener() {
        mBinding.imgBack.setOnClickListener(view -> onBackPressed());

        mBinding.editEventDate.setOnClickListener(view -> {
            DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                    selectedCalendar.set(Calendar.YEAR, year);
                    selectedCalendar.set(Calendar.MONTH, monthOfYear);
                    selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    String myFormat = "dd-MM-yyyy"; //In which you need put here
                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                    mBinding.editEventDate.setText(sdf.format(selectedCalendar.getTime()));
                }
            }, selectedCalendar.get(Calendar.YEAR), selectedCalendar.get(Calendar.MONTH),
                    selectedCalendar.get(Calendar.DAY_OF_MONTH));

            Calendar currentCal = Calendar.getInstance();
            currentCal.add(Calendar.DAY_OF_MONTH, 1);
            dialog.getDatePicker().setMinDate(currentCal.getTime().getTime());
            dialog.show();
        });

        mBinding.editEventStartTime.setOnClickListener(view -> {
            int hours = selectedStartTimeCal == null ? 12 : selectedStartTimeCal.get(Calendar.HOUR_OF_DAY);
            int mins = selectedStartTimeCal == null ? 0 : selectedStartTimeCal.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(context, (timePicker, selectedHour, selectedMinute) -> {
                selectedStartTimeCal = Calendar.getInstance();
                selectedStartTimeCal.set(Calendar.HOUR_OF_DAY, selectedHour);
                //instead of c.set(Calendar.HOUR, hour);
                selectedStartTimeCal.set(Calendar.MINUTE, selectedMinute);
                String myFormat = "hh:mm a"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                mBinding.editEventStartTime.setText(sdf.format(selectedStartTimeCal.getTime()));
            }, hours, mins, false);//Yes 24 hour time
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();

        });

        mBinding.editEventEndTime.setOnClickListener(view -> {
            int hours = selectedEndTimeCal == null ? 12 : selectedEndTimeCal.get(Calendar.HOUR_OF_DAY);
            int mins = selectedEndTimeCal == null ? 0 : selectedEndTimeCal.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(context, (timePicker, selectedHour, selectedMinute) -> {
                selectedEndTimeCal = Calendar.getInstance();
                selectedEndTimeCal.set(Calendar.HOUR_OF_DAY, selectedHour);
                selectedEndTimeCal.set(Calendar.MINUTE, selectedMinute);
                String myFormat = "hh:mm a"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                mBinding.editEventEndTime.setText(sdf.format(selectedEndTimeCal.getTime()));
            }, hours, mins, false);//Yes 24 hour time
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        });

        mBinding.tvEventSave.setOnClickListener(view -> {
            String eventName = mBinding.editEventName.getText().toString().trim();
            String eventDescription = mBinding.editEventDescription.getText().toString().trim();
            String eventLocation = mBinding.editEventLocation.getText().toString().trim();
            String eventDate = mBinding.editEventDate.getText().toString().trim();

            boolean isValid = false;

            if (eventName.isEmpty()) {
                Commons.showToast(context, getResources().getString(R.string.enter_event_name));
            } else if (eventDescription.isEmpty()) {
                Commons.showToast(context, getResources().getString(R.string.enter_event_description));
            } else if (eventLocation.isEmpty()) {
                Commons.showToast(context, getResources().getString(R.string.enter_event_location));
            } else if (eventDate.isEmpty()) {
                Commons.showToast(context, getResources().getString(R.string.enter_event_date));
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

        /*mBinding.editEventLocation.setOnClickListener(view -> {
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)).build(context);
            startActivityForResult(intent, 102);
        });*/
    }

    private void showImagePickerAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.choose_image_from));
        builder.setCancelable(false);
        builder.setPositiveButton(getResources().getString(R.string.camera), (dialogInterface, i) -> {
            new ImagePicker.Builder(this).crop().cameraOnly().compress(1024).start(101);
        });
        builder.setNegativeButton(getResources().getString(R.string.gallery), (dialogInterface, i) -> {
            new ImagePicker.Builder(this).crop().galleryOnly().compress(1024).start(101);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (resultCode == Activity.RESULT_OK) {
                imageList.add(new EventImageModel("", "", ImagePicker.Companion.getFilePath(data), Uri.parse(data.getData().toString())));
                imagesAdapter.notifyDataSetChanged();
            }
        } else if (requestCode == 102) {
            if (resultCode == Activity.RESULT_OK && data != null){
                place = Autocomplete.getPlaceFromIntent(data);
                mBinding.editEventLocation.setText(place.getAddress() == null? "": place.getAddress());
            }
        }
    }

    private void addEditEvent(String eventName, String eventDescription, String eventLocation, String eventDate, String startTime, String endTime) {
        if (Commons.isOnline(context)) {
            ArrayList<MultipartBody.Part> imagesMultiPart = new ArrayList<>();
            for (EventImageModel data : imageList) {
                if (data.getImageURL().isEmpty() && !data.getImagePath().isEmpty()) {
                    File imageFile = new File(data.getImagePath());
                    RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
                    imagesMultiPart.add(MultipartBody.Part.createFormData("image", imageFile.getName(), requestBody));
                }
            }

            HashMap<String, RequestBody> params = new HashMap<>();
            params.put("name", RequestBody.create(MultipartBody.FORM, userDetailsModel.getName()));
            params.put("event_name", RequestBody.create(MultipartBody.FORM, eventName));
            params.put("description", RequestBody.create(MultipartBody.FORM, eventDescription));
            params.put("edate", RequestBody.create(MultipartBody.FORM, eventDate));
            params.put("location", RequestBody.create(MultipartBody.FORM, eventLocation));
            params.put("stime", RequestBody.create(MultipartBody.FORM, startTime));
            params.put("etime", RequestBody.create(MultipartBody.FORM, endTime));
            params.put("event_id", RequestBody.create(MultipartBody.FORM, ""));

            String header = "Bearer " + SharePref.getInstance(context).get(SharePref.PREF_TOKEN, "");

            progressDialog.show();
            ApiClient.create().addEditEvent(header, params, imagesMultiPart).enqueue(new Callback<CommonResponseModel>() {
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
}