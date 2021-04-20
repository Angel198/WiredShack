package com.jaylax.wiredshack;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.jaylax.wiredshack.databinding.ActivityEditProfileBinding;
import com.jaylax.wiredshack.model.UserDetailsModel;
import com.jaylax.wiredshack.utils.Commons;
import com.jaylax.wiredshack.utils.SharePref;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditProfileActivity extends AppCompatActivity {

    ActivityEditProfileBinding mBinding;
    UserDetailsModel userDetailsModel;

    final Calendar selectedCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile);
        mBinding.imgBack.setOnClickListener(view -> onBackPressed());

        userDetailsModel = Commons.convertStringToObject(this, SharePref.PREF_USER, UserDetailsModel.class);
        mBinding.editUserName.setText(userDetailsModel.getName());
        mBinding.editUserEmail.setText(userDetailsModel.getEmail());
        mBinding.editUserBirthDate.setText(userDetailsModel.getBirthDate());
        mBinding.editUserPhone.setText(userDetailsModel.getPhone());

        if (userDetailsModel.getGender() != null) {
            if (userDetailsModel.getGender().equals(getResources().getString(R.string.female))) {
                mBinding.radioGroup.check(R.id.radioButtonFemale);
            } else {
                mBinding.radioGroup.check(R.id.radioButtonMale);
            }
        }else {
            mBinding.radioGroup.check(R.id.radioButtonMale);
        }

        mBinding.imgUserProfile.setOnClickListener(view -> {
            showImagePickerAlert();
        });

        mBinding.editUserBirthDate.setOnClickListener(view -> {
            DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                    selectedCalendar.set(Calendar.YEAR, year);
                    selectedCalendar.set(Calendar.MONTH, monthOfYear);
                    selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    String myFormat = "dd-MM-yyyy"; //In which you need put here
                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                    mBinding.editUserBirthDate.setText(sdf.format(selectedCalendar.getTime()));
                }
            },selectedCalendar.get(Calendar.YEAR), selectedCalendar.get(Calendar.MONTH),
                    selectedCalendar.get(Calendar.DAY_OF_MONTH));
            dialog.getDatePicker().setMaxDate(new Date().getTime());
            dialog.show();
        });
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
        if (requestCode == 101){
            if (resultCode == Activity.RESULT_OK){
                mBinding.imgUserProfile.setImageURI(data.getData());
            }
        }
    }
}