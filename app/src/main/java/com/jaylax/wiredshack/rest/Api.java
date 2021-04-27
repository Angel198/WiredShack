package com.jaylax.wiredshack.rest;

import com.jaylax.wiredshack.model.CommonResponseModel;
import com.jaylax.wiredshack.model.LoginResponseModel;
import com.jaylax.wiredshack.model.UserDetailsModel;
import com.jaylax.wiredshack.user.eventDetails.EventCommentMainModel;
import com.jaylax.wiredshack.user.eventDetails.EventDetailsMainModel;
import com.jaylax.wiredshack.user.home.ManagerListMainModel;
import com.jaylax.wiredshack.user.home.RecentEventMainModel;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface Api {

    @POST("api/auth/register")
    Call<CommonResponseModel> register(@Body HashMap<String, String> params);

    @POST("api/auth/login")
    Call<LoginResponseModel> login(@Body HashMap<String, String> params);

    @GET("api/auth/user")
    Call<UserDetailsModel> userDetails(@Header("Authorization") String authHeader);

    @Multipart
    @POST("api/auth/userprofile")
    Call<CommonResponseModel> updateProfile(@Header("Authorization") String authHeader, @PartMap HashMap<String, RequestBody> params);

    @Multipart
    @POST("api/auth/userprofile")
    Call<CommonResponseModel> updateProfile(@Header("Authorization") String authHeader, @PartMap HashMap<String, RequestBody> params, @Part MultipartBody.Part profile);

    @Multipart
    @POST("api/auth/userprofile")
    Call<CommonResponseModel> updateProfile(@Header("Authorization") String authHeader, @Part MultipartBody.Part coverPhoto, @PartMap HashMap<String, RequestBody> params);

    @Multipart
    @POST("api/auth/userprofile")
    Call<CommonResponseModel> updateProfile(@Header("Authorization") String authHeader, @PartMap HashMap<String, RequestBody> params, @Part MultipartBody.Part profile, @Part MultipartBody.Part coverPhoto);

    @Multipart
    @POST("api/auth/addUpdateEvent")
    Call<CommonResponseModel> addEditEvent(@Header("Authorization") String authHeader, @PartMap HashMap<String, RequestBody> params, @Part ArrayList<MultipartBody.Part> images);

    @POST("api/auth/comment")
    Call<CommonResponseModel> sendComment(@Header("Authorization") String authHeader, @Body HashMap<String, String> params);

    @POST("api/auth/like")
    Call<CommonResponseModel> likeEvent(@Header("Authorization") String authHeader, @Body HashMap<String, String> params);

    @POST("api/auth/follow")
    Call<CommonResponseModel> followEventManager(@Header("Authorization") String authHeader, @Body HashMap<String, String> params);

    @GET("api/auth/recenteventlist")
    Call<RecentEventMainModel> getRecentEventsUser(@Header("Authorization") String authHeader);


    @POST("api/auth/managerlist")
    Call<ManagerListMainModel> getEventsManager(@Header("Authorization") String authHeader);

    @POST("api/auth/geteventdetails")
    Call<EventDetailsMainModel> getEventDetails(@Header("Authorization") String authHeader, @Body HashMap<String, String> params);

    @POST("api/auth/geteventdetailslist")
    Call<EventDetailsMainModel> getEventDetailsGuest(@Body HashMap<String, String> params);

    @POST("api/auth/getcomment")
    Call<EventCommentMainModel> getEventComments(@Header("Authorization") String authHeader, @Body HashMap<String, String> params);

    @POST("api/auth/managerdetails")
    Call<CommonResponseModel> getEventMangerDetails(@Body HashMap<String, String> params);

}
