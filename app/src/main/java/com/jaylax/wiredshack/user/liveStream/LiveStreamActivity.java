package com.jaylax.wiredshack.user.liveStream;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ActivityLiveStreamBinding;
import com.jaylax.wiredshack.eventManager.liveStream.LiveStreamPublisherActivity;
import com.jaylax.wiredshack.webcommunication.WebCall;
import com.jaylax.wiredshack.webcommunication.WebConstants;
import com.jaylax.wiredshack.webcommunication.WebResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import enx_rtc_android.Controller.EnxAdvancedOptionsObserver;
import enx_rtc_android.Controller.EnxPlayerView;
import enx_rtc_android.Controller.EnxReconnectObserver;
import enx_rtc_android.Controller.EnxRoom;
import enx_rtc_android.Controller.EnxRoomObserver;
import enx_rtc_android.Controller.EnxRtc;
import enx_rtc_android.Controller.EnxScreenShotObserver;
import enx_rtc_android.Controller.EnxStream;
import enx_rtc_android.Controller.EnxStreamObserver;


public class LiveStreamActivity extends AppCompatActivity implements EnxRoomObserver, EnxStreamObserver, EnxAdvancedOptionsObserver {

    private String token;
    private String name;
    ActivityLiveStreamBinding mBinding;
    EnxRoom mEnxRoom;
    private EnxPlayerView enxPlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_live_stream);

        getDataFromIntent();
        initData();
    }

    private void getDataFromIntent() {
        if (getIntent() != null) {
            token = getIntent().getStringExtra("token");
            name = getIntent().getStringExtra("name");
        }
    }

    private void initData() {
        mEnxRoom = new EnxRoom(this, this, this);
        mEnxRoom.init(this);
        mEnxRoom.connect(token, getRoomConnectInfo(), new JSONArray());
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

    @Override
    public void onRoomConnected(EnxRoom enxRoom, JSONObject jsonObject) {
        Log.e("onRoomConnected", jsonObject.toString());
    }

    @Override
    public void onRoomError(JSONObject jsonObject) {
        Log.e("Token", token.toString());
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
        mEnxRoom.disconnect();
    }

    @Override
    public void onPublishedStream(EnxStream enxStream) {
        Log.e("onPublishedStream", "jsonObject.toString()");
    }

    @Override
    public void onUnPublishedStream(EnxStream enxStream) {
        Log.e("onUnPublishedStream", "jsonObject.toString()");
    }

    @Override
    public void onStreamAdded(EnxStream enxStream) {
        Log.e("onStreamAdded", "jsonObject.toString()");
        mEnxRoom.subscribe(enxStream);
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

        runOnUiThread(() -> {
            enxPlayerView = new EnxPlayerView(LiveStreamActivity.this, EnxPlayerView.ScalingType.SCALE_ASPECT_BALANCED, true);
            enxStream.attachRenderer(enxPlayerView);
            mBinding.selfFL.setVisibility(View.VISIBLE);
            mBinding.selfFL.addView(enxPlayerView);
            Log.e("runOnUiThread", "jsonObject.toString()");
        });
    }

    @Override
    public void onUnSubscribedStream(EnxStream enxStream) {
        Log.e("onUnSubscribedStream", "jsonObject.toString()");
    }

    @Override
    public void onRoomDisConnected(JSONObject jsonObject) {
        Log.e("onRoomDisConnected", jsonObject.toString());
        onBackPressed();
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
}