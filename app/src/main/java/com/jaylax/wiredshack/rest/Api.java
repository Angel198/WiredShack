package com.jaylax.wiredshack.rest;

import com.jaylax.wiredshack.model.CommonResponseModel;
import com.jaylax.wiredshack.model.LoginResponseModel;
import com.jaylax.wiredshack.model.UserDetailsModel;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface Api {

    @POST("api/auth/register")
    Call<CommonResponseModel> register(@Body HashMap<String,String> params);

    @POST("api/auth/login")
    Call<LoginResponseModel> login(@Body HashMap<String,String> params);

    @POST("api/auth/user")
    Call<UserDetailsModel> userDetails(@Header("Authorization") String authHeader);
}
