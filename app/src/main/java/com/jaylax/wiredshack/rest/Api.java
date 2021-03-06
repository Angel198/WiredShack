package com.jaylax.wiredshack.rest;

import com.jaylax.wiredshack.eventManager.editEvent.SelectManagerListModel;
import com.jaylax.wiredshack.eventManager.followed.ManagerFollowedMainModel;
import com.jaylax.wiredshack.eventManager.home.RoomIDCreateModel;
import com.jaylax.wiredshack.eventManager.managerActivity.IncomingRequestMainModel;
import com.jaylax.wiredshack.eventManager.managerActivity.ManagerActivityMainModel;
import com.jaylax.wiredshack.model.CommonResponseModel;
import com.jaylax.wiredshack.model.LoginResponseModel;
import com.jaylax.wiredshack.model.UserDetailsModel;
import com.jaylax.wiredshack.user.account.FollowingEventMainModel;
import com.jaylax.wiredshack.user.account.UploadImageModel;
import com.jaylax.wiredshack.user.eventDetails.EventCommentMainModel;
import com.jaylax.wiredshack.user.eventDetails.EventDetailsMainModel;
import com.jaylax.wiredshack.user.following.UserFollowingMainModel;
import com.jaylax.wiredshack.user.home.EventRoomModel;
import com.jaylax.wiredshack.user.home.EventTokenModel;
import com.jaylax.wiredshack.user.home.ManagerListMainModel;
import com.jaylax.wiredshack.model.RecentEventMainModel;
import com.jaylax.wiredshack.user.home.UpcomingEventMainModel;
import com.jaylax.wiredshack.user.liveStream.LiveStreamUserModel;
import com.jaylax.wiredshack.user.managerDetails.ManagerDetailsMainModel;
import com.jaylax.wiredshack.user.notification.AcceptedEventMainModel;
import com.jaylax.wiredshack.user.notification.PendingRequestMainModel;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
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
    Call<CommonResponseModel> addEditEvent(@Header("Authorization") String authHeader, @PartMap HashMap<String, RequestBody> params, @Part ArrayList<MultipartBody.Part> images, @Part MultipartBody.Part coverImages, @Part("delete_images[]") ArrayList<Integer> deleteImage);

    @POST("api/auth/comment")
    Call<CommonResponseModel> sendComment(@Header("Authorization") String authHeader, @Body HashMap<String, String> params);

    @POST("api/auth/like")
    Call<CommonResponseModel> likeEvent(@Header("Authorization") String authHeader, @Body HashMap<String, String> params);

    @POST("api/auth/follow")
    Call<CommonResponseModel> followEventManager(@Header("Authorization") String authHeader, @Body HashMap<String, String> params);

    @POST("api/auth/managerfollow")
    Call<CommonResponseModel> followManager(@Header("Authorization") String authHeader, @Body HashMap<String, String> params);

    @GET("api/auth/recenteventlist")
    Call<RecentEventMainModel> getRecentEventsUser();

    @POST("api/auth/recenteventlist_token")
    Call<RecentEventMainModel> getRecentEventsUser(@Header("Authorization") String authHeader);

    @POST("api/auth/recentevent")
    Call<RecentEventMainModel> getRecentEventsManager(@Header("Authorization") String authHeader);

    @POST("api/auth/neareventlist")
    Call<RecentEventMainModel> getNearByEvent(@Body HashMap<String, String> params);

    @POST("api/auth/pastevent")
    Call<RecentEventMainModel> getPastEventsManager(@Header("Authorization") String authHeader);

    @POST("api/auth/managerlist")
    Call<ManagerListMainModel> getEventsManager(@Header("Authorization") String authHeader);

    @POST("api/auth/guestmanagerlist")
    Call<ManagerListMainModel> getGuestEventsManager();

    @POST("api/auth/getupcomingevent")
    Call<UpcomingEventMainModel> getUpcomingEvent(@Header("Authorization") String authHeader);

    @POST("api/auth/getupcomingevent_guest")
    Call<UpcomingEventMainModel> getGuestUpcomingEvent();

    @POST("api/auth/managerlist")
    Call<ManagerListMainModel> getEventsManagerFilter(@Header("Authorization") String authHeader, @Body HashMap<String, String> params);

    @POST("api/auth/guestmanagerlist")
    Call<ManagerListMainModel> getGuestEventsManagerFilter(@Body HashMap<String, String> params);

    @POST("api/auth/geteventdetails")
    Call<EventDetailsMainModel> getEventDetails(@Header("Authorization") String authHeader, @Body HashMap<String, String> params);

    @POST("api/auth/geteventdetailslist")
    Call<EventDetailsMainModel> getEventDetailsGuest(@Body HashMap<String, String> params);

    @POST("api/auth/getcomment")
    Call<EventCommentMainModel> getEventComments(@Header("Authorization") String authHeader, @Body HashMap<String, String> params);

    @POST("api/auth/managerdetails")
    Call<ManagerDetailsMainModel> getEventMangerDetails(@Body HashMap<String, String> params);

    @POST("api/auth/managerdetails_token")
    Call<ManagerDetailsMainModel> getEventMangerDetailsToken(@Header("Authorization") String authHeader, @Body HashMap<String, String> params);

    @POST("api/auth/followeduser")
    Call<ManagerFollowedMainModel> getFollowedData(@Header("Authorization") String authHeader);

    @POST("api/auth/followingmanager")
    Call<UserFollowingMainModel> getFollowingData(@Header("Authorization") String authHeader);

    @POST("api/auth/user-event-request")
    Call<CommonResponseModel> requestForLiveStream(@Header("Authorization") String authHeader, @Body HashMap<String, String> params);

    @POST("api/auth/incoming-request")
    Call<IncomingRequestMainModel> incomingRequest(@Header("Authorization") String authHeader);

    @POST("api/auth/approve_cancel-request")
    Call<CommonResponseModel> approveCancelRequest(@Header("Authorization") String authHeader, @Body HashMap<String, String> params);

    @POST("api/auth/event_following_user")
    Call<FollowingEventMainModel> getUserFollowingEvents(@Header("Authorization") String authHeader);

    @POST("api/auth/event_activity")
    Call<ManagerActivityMainModel> getAccountActivity(@Header("Authorization") String authHeader);

    @POST("api/auth/user-event-requestpending")
    Call<PendingRequestMainModel> getUserSentEventRequest(@Header("Authorization") String authHeader);

    @POST("api/auth/user-event-requestaccepet")
    Call<AcceptedEventMainModel> getUserAcceptedEvent(@Header("Authorization") String authHeader);

    @POST("api/auth/deletecomment")
    Call<CommonResponseModel> deleteComment(@Header("Authorization") String authHeader, @Body HashMap<String, String> params);

    @POST("api/auth/djlist")
    Call<SelectManagerListModel> managerListForSelect(@Header("Authorization") String authHeader);

    @POST("api/auth/send-mail")
    Call<CommonResponseModel> sendEmail(@Header("Authorization") String authHeader, @Body HashMap<String, String> params);

    @POST("api/auth/stream_check")
    Call<CommonResponseModel> streamCheck(@Header("Authorization") String authHeader, @Body HashMap<String, String> params);

    @POST("api/auth/forgot_pass")
    Call<CommonResponseModel> forgotPassword(@Body HashMap<String, String> params);

    @POST("api/auth/get_post_image")
    Call<UploadImageModel> getUserPostImages(@Header("Authorization") String authHeader);

    @POST("api/auth/del_img")
    Call<CommonResponseModel> deleteUserImage(@Header("Authorization") String authHeader, @Body HashMap<String, String> params);

    @POST("api/auth/delete_event")
    Call<CommonResponseModel> deleteEvent(@Header("Authorization") String authHeader, @Body HashMap<String, String> params);

    @Multipart
    @POST("api/auth/post_image")
    Call<CommonResponseModel> uploadUserImage(@Header("Authorization") String authHeader, @Part MultipartBody.Part profile);

    @POST("api/auth/enter_live_stream")
    Call<CommonResponseModel> enterLiveStream(@Header("Authorization") String authHeader, @Body HashMap<String, String> params);

    @POST("api/auth/exit_live_stream")
    Call<CommonResponseModel> exitLiveStream(@Header("Authorization") String authHeader, @Body HashMap<String, String> params);

    @POST("api/auth/live_stream_user")
    Call<LiveStreamUserModel> liveStreamUser(@Header("Authorization") String authHeader, @Body HashMap<String, String> params);

    @POST("api/auth/get_room_id")
    Call<EventRoomModel> getEnableXRoomId(@Header("Authorization") String authHeader, @Body HashMap<String, String> params);

    @POST("api/auth/get_manager_room")
    Call<EventRoomModel> getManagerEnableXRoomId(@Header("Authorization") String authHeader, @Body HashMap<String, String> params);

    @POST("api/createToken")
    Call<EventTokenModel> getEnableXToken(@Body HashMap<String, String> params);

    @POST("api/createRoom")
    Call<RoomIDCreateModel> createEnableXRoomId(@Body HashMap<String, String> params);

    @POST("api/manager_live_room")
    Call<RoomIDCreateModel> createEnableXRoomIdForManager(@Body HashMap<String, String> params);

    @POST("api/manager_live")
    Call<CommonResponseModel> setMangerLive(@Body HashMap<String, String> params);


}
