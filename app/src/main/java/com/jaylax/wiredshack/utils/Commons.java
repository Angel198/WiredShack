package com.jaylax.wiredshack.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Patterns;
import android.widget.Toast;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.google.gson.Gson;
import com.jaylax.wiredshack.LoginSignupActivity;
import com.jaylax.wiredshack.R;

public class Commons {
    public static String RTMP_URL = "rtmp://estandardcodex.in/LiveApp/";

    public static boolean isValidEmail(CharSequence target) {
        return Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static void showToast(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
    }

    public static boolean isOnline(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        }
        else {
            return false;
        }
    }

    public static String convertObjectToString (Object obj){
        return new Gson().toJson(obj);
    }

    public static <T> T convertStringToObject (Context context, String key , Class<T> className){
        String sharePreValue = SharePref.getInstance(context).get(key,"").toString();
        return new Gson().fromJson(sharePreValue,className);
    }

    public static void openLoginScree (Context context){
        Intent intent = new Intent(context, LoginSignupActivity.class);
        context.startActivity(intent);
    }

    public static RoundedBitmapDrawable generateRoundCornerPlaceholder(Context context){
        Bitmap placeholder = BitmapFactory.decodeResource(context.getResources(), R.drawable.place_holder);
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(),placeholder);
        roundedBitmapDrawable.setCornerRadius(50f);
        return roundedBitmapDrawable;
    }
}
