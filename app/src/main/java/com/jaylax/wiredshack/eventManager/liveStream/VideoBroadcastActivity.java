package com.jaylax.wiredshack.eventManager.liveStream;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.jaylax.wiredshack.ProgressDialog;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ActivityLiveStreamBinding;
import com.jaylax.wiredshack.eventManager.home.RoomIDCreateModel;
import com.jaylax.wiredshack.model.CommonResponseModel;
import com.jaylax.wiredshack.model.UserDetailsModel;
import com.jaylax.wiredshack.rest.ApiClient;
import com.jaylax.wiredshack.user.liveStream.LiveStreamUserModel;
import com.jaylax.wiredshack.utils.Commons;
import com.jaylax.wiredshack.utils.SharePref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

import enx_rtc_android.Controller.EnxAdvancedOptionsObserver;
import enx_rtc_android.Controller.EnxPlayerView;
import enx_rtc_android.Controller.EnxRoom;
import enx_rtc_android.Controller.EnxRoomObserver;
import enx_rtc_android.Controller.EnxRtc;
import enx_rtc_android.Controller.EnxStream;
import enx_rtc_android.Controller.EnxStreamObserver;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoBroadcastActivity extends AppCompatActivity implements EnxRoomObserver, EnxStreamObserver, EnxAdvancedOptionsObserver {

    private String token;
    private String name;
    ActivityLiveStreamBinding mBinding;
    EnxRtc mEnxRtc;
    EnxRoom mEnxRoom;
    private EnxPlayerView enxPlayerView;
    private EnxStream mEnxStream;
    private Context mContext;
    private ProgressDialog progressDialog;

    private String streamId = "";
    private UserDetailsModel userDetailsModel;
    private long timerMilliSec = 120000;

    String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_PHONE_STATE
    };
    private final int PERMISSION_ALL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_live_stream);
        mContext = this;
        userDetailsModel = Commons.convertStringToObject(this, SharePref.PREF_USER, UserDetailsModel.class);
        progressDialog = new ProgressDialog(mContext);
        mBinding.llLiveStreamRight.setVisibility(View.GONE);
        Objects.requireNonNull(mBinding.imgSwitchCamera).setVisibility(View.VISIBLE);
        getDataFromIntent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermissions(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            } else {
                initData();
            }
        }

    }

    private void getDataFromIntent() {
        if (getIntent() != null) {
            token = getIntent().getStringExtra("token");
            name = getIntent().getStringExtra("name");
            if (getIntent().hasExtra("streamId")) {
                streamId = getIntent().getStringExtra("streamId");
            }
        }
    }

    private void initData() {
        mEnxRtc = new EnxRtc(this, this, this);
        mEnxStream = mEnxRtc.joinRoom(token, getPublisherInfo(), getRoomConnectInfo(), getAdvancedOption());
        if (streamId.isEmpty()) {
            callManagerLiveApi("1");
        } else {
            callLiveStreamUserApi();
        }
        setClick();
    }

    private void setClick() {
        mBinding.imgSwitchCamera.setOnClickListener(view -> {
            if (mEnxStream != null) {
                mEnxStream.switchCamera();
            }
        });
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            callApiEveryInterval();
            handler.postDelayed(runnable, 5000);
        }
    };

    private void callManagerLiveApi(String isActive) {
        if (Commons.isOnline(mContext)) {
            HashMap<String, String> params = new HashMap<>();
            params.put("uid", userDetailsModel.getId());
            params.put("is_active", isActive);

            ApiClient.create().setMangerLive(params).enqueue(new Callback<CommonResponseModel>() {
                @Override
                public void onResponse(Call<CommonResponseModel> call, Response<CommonResponseModel> response) {
                    if (response.code() != 200 && !response.isSuccessful()) {
                        Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                    }

                    if (isActive.equals("0")) {
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<CommonResponseModel> call, Throwable t) {
                    Commons.showToast(mContext, getResources().getString(R.string.something_wants_wrong));
                    if (isActive.equals("0")) {
                        finish();
                    }
                }
            });
        } else {
            Commons.showToast(mContext, getResources().getString(R.string.no_internet_connection));
        }
    }

    private void callLiveStreamUserApi() {
        handler.postDelayed(runnable, 5000);
    }

    private void callApiEveryInterval() {
        if (Commons.isOnline(mContext)) {
            HashMap<String, String> params = new HashMap<>();
            params.put("event_id", streamId);

            String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");
            ApiClient.create().liveStreamUser(header, params).enqueue(new Callback<LiveStreamUserModel>() {
                @Override
                public void onResponse(Call<LiveStreamUserModel> call, Response<LiveStreamUserModel> response) {
                    if (response.code() == 200 && response.isSuccessful()) {
                        if (response.body() != null) {
                            if (response.body().getData().isEmpty()) {
                                mBinding.tvLiveStreamUserCount.setText("0");
                            } else {
                                mBinding.tvLiveStreamUserCount.setText(String.valueOf(response.body().getData().size()));
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<LiveStreamUserModel> call, Throwable t) {
                    Commons.showToast(mContext, getResources().getString(R.string.something_wants_wrong));
                }
            });
        } else {
            Commons.showToast(mContext, mContext.getResources().getString(R.string.no_internet_connection));
        }
    }

    private boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private JSONObject getPublisherInfo() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("audio", true);
            jsonObject.put("video", true);
            jsonObject.put("data", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("getPublisherInfo", jsonObject.toString());

        return jsonObject;
    }

    public JSONObject getRoomConnectInfo() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("allow_reconnect", true);
            jsonObject.put("number_of_attempts", 3);
            jsonObject.put("timeout_interval", 15);
            jsonObject.put("activeviews", "view");
            jsonObject.put("forceTurn", false);
            jsonObject.put("chat_only", false);
            JSONObject playerConfigJson = new JSONObject();
            playerConfigJson.put("audiomute", true);
            playerConfigJson.put("videomute", true);
            playerConfigJson.put("bandwidth", true);
            playerConfigJson.put("screenshot", true);
            playerConfigJson.put("avatar", true);
            playerConfigJson.put("iconColor", "#FFFFFF");
            playerConfigJson.put("iconHeight", 30);
            playerConfigJson.put("iconWidth", 30);
            playerConfigJson.put("avatarHeight", 200);
            playerConfigJson.put("avatarWidth", 200);
            jsonObject.put("playerConfiguration", playerConfigJson);

        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("getRoomConnectInfo", jsonObject.toString());
        return jsonObject;
    }

    public JSONArray getAdvancedOption() {
        JSONArray jsonArray = new JSONArray();

        try {
            JSONObject json1 = new JSONObject();
            json1.put("id", "notify-video-resolution-change");
            json1.put("enable", true);
            JSONObject json2 = new JSONObject();
            json2.put("id", "battery_updates");
            json2.put("enable", true);

            jsonArray.put(json1);
            jsonArray.put(json2);

        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("getAdvancedOption", jsonArray.toString());
        return jsonArray;
    }

    @Override
    public void onRoomConnected(EnxRoom enxRoom, JSONObject jsonObject) {
        Log.e("onRoomConnected", jsonObject.toString());
        this.mEnxRoom = enxRoom;
    }

    @Override
    public void onRoomError(JSONObject jsonObject) {
        Log.e("onRoomError", jsonObject.toString());
    }

    @Override
    public void onUserConnected(JSONObject jsonObject) {
        Log.e("onUserConnected", jsonObject.toString());
    }

    @Override
    public void onUserDisConnected(JSONObject jsonObject) {
        //while Stream closed
        Log.e("onUserDisConnected", jsonObject.toString());
    }

    @Override
    public void onPublishedStream(EnxStream enxStream) {
        Log.e("onPublishedStream", "jsonObject.toString()");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                enxPlayerView = new EnxPlayerView(VideoBroadcastActivity.this, EnxPlayerView.ScalingType.SCALE_ASPECT_BALANCED, true);
                enxPlayerView.setZOrderMediaOverlay(true);
                mEnxStream.attachRenderer(enxPlayerView);
                mBinding.selfFL.setVisibility(View.VISIBLE);
                mBinding.selfFL.addView(enxPlayerView);
            }
        });
    }

    @Override
    public void onUnPublishedStream(EnxStream enxStream) {
        Log.e("onUnPublishedStream", "jsonObject.toString()");
    }

    @Override
    public void onStreamAdded(EnxStream enxStream) {
        Log.e("onStreamAdded", "jsonObject.toString()");
        if (mEnxRoom != null) {
            mEnxRoom.publish(mEnxStream);
        }
    }

    @Override
    public void onSubscribedStream(EnxStream enxStream) {
        Log.e("onSubscribedStream", "jsonObject.toString()");
        Log.e("onSubscribedStream", "hasVideo : " + enxStream.hasVideo());
        Log.e("onSubscribedStream", "hasAudio : " + enxStream.hasAudio());
        Log.e("onSubscribedStream", "isVideoActive : " + enxStream.isVideoActive());
        Log.e("onSubscribedStream", "isAudioActive : " + enxStream.isAudioActive());
        Log.e("onSubscribedStream", "isLocal : " + enxStream.isLocal());
        Log.e("onSubscribedStream", "State : " + enxStream.getState());
    }

    @Override
    public void onUnSubscribedStream(EnxStream enxStream) {
        Log.e("onUnSubscribedStream", "jsonObject.toString()");
    }

    @Override
    public void onRoomDisConnected(JSONObject jsonObject) {
        Log.e("onRoomDisConnected", jsonObject.toString());
        callManagerLiveApi("0");
//        finish();
    }

    @Override
    public void onEventError(JSONObject jsonObject) {
        Log.e("onEventError", jsonObject.toString());
    }

    @Override
    public void onEventInfo(JSONObject jsonObject) {
        Log.e("onEventInfo", jsonObject.toString());
    }

    @Override
    public void onNotifyDeviceUpdate(String s) {
        Log.e("onNotifyDeviceUpdate", s);
    }

    @Override
    public void onAcknowledgedSendData(JSONObject jsonObject) {
        Log.e("onAcknowledgedSendData", jsonObject.toString());
    }

    @Override
    public void onMessageReceived(JSONObject jsonObject) {
        Log.e("onMessageReceived", jsonObject.toString());
    }

    @Override
    public void onUserDataReceived(JSONObject jsonObject) {
        Log.e("onUserDataReceived", jsonObject.toString());
    }

    @Override
    public void onSwitchedUserRole(JSONObject jsonObject) {
        Log.e("onSwitchedUserRole", jsonObject.toString());
    }

    @Override
    public void onUserRoleChanged(JSONObject jsonObject) {
        Log.e("onUserRoleChanged", jsonObject.toString());
    }

    @Override
    public void onConferencessExtended(JSONObject jsonObject) {
        Log.e("onConferencesExtended", jsonObject.toString());
    }

    @Override
    public void onConferenceRemainingDuration(JSONObject jsonObject) {
        Log.e("", "onConferenceRemainingDuration : " + jsonObject.toString());
    }

    @Override
    public void onAckDropUser(JSONObject jsonObject) {
        Log.e("onAckDropUser", jsonObject.toString());
    }

    @Override
    public void onAckDestroy(JSONObject jsonObject) {
        Log.e("onAckDestroy", jsonObject.toString());
    }

    @Override
    public void onAckPinUsers(JSONObject jsonObject) {
        Log.e("onAckPinUsers", jsonObject.toString());
    }

    @Override
    public void onAckUnpinUsers(JSONObject jsonObject) {
        Log.e("onAckUnpinUsers", jsonObject.toString());
    }

    @Override
    public void onPinnedUsers(JSONObject jsonObject) {
        Log.e("onPinnedUsers", jsonObject.toString());
    }

    @Override
    public void onRoomAwaited(EnxRoom enxRoom, JSONObject jsonObject) {
        Log.e("onRoomAwaited", jsonObject.toString());
    }

    @Override
    public void onUserAwaited(JSONObject jsonObject) {
        Log.e("onUserAwaited", jsonObject.toString());
    }

    @Override
    public void onAckForApproveAwaitedUser(JSONObject jsonObject) {
        Log.e("", "onAckForApproveAwaitedUser : " + jsonObject.toString());
    }

    @Override
    public void onAckForDenyAwaitedUser(JSONObject jsonObject) {
        Log.e("onAckForDenyAwaitedUser", jsonObject.toString());
    }

    @Override
    public void onAudioEvent(JSONObject jsonObject) {
        Log.e("onAudioEvent", jsonObject.toString());
    }

    @Override
    public void onVideoEvent(JSONObject jsonObject) {
        Log.e("onVideoEvent", jsonObject.toString());
    }

    @Override
    public void onReceivedData(JSONObject jsonObject) {
        Log.e("onReceivedData", jsonObject.toString());
    }

    @Override
    public void onRemoteStreamAudioMute(JSONObject jsonObject) {
        Log.e("onRemoteStreamAudioMute", jsonObject.toString());
    }

    @Override
    public void onRemoteStreamAudioUnMute(JSONObject jsonObject) {
        Log.e("", "onRemoteStreamAudioUnMute : " + jsonObject.toString());
    }

    @Override
    public void onRemoteStreamVideoMute(JSONObject jsonObject) {
        Log.e("onRemoteStreamVideoMute", jsonObject.toString());
    }

    @Override
    public void onRemoteStreamVideoUnMute(JSONObject jsonObject) {
        Log.e("", "onRemoteStreamVideoUnMute : " + jsonObject.toString());
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        Log.e("onPointerCaptureChanged", String.valueOf(hasCapture));
    }

    @Override
    public void onAdvancedOptionsUpdate(JSONObject jsonObject) {
        Log.e("onAdvancedOptionsUpdate", jsonObject.toString());
    }

    @Override
    public void onGetAdvancedOptions(JSONObject jsonObject) {
        Log.e("onGetAdvancedOptions", jsonObject.toString());
    }

    @Override
    public void onBackPressed() {
        handler.removeCallbacks(runnable);

        if (mEnxRoom == null) {
            callManagerLiveApi("0");
        } else {
            mEnxRoom.disconnect();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_ALL:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED
                        && grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                    initData();
                } else {
                    Toast.makeText(this, "Please enable permissions to further proceed.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}