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

import com.jaylax.wiredshack.R;
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


public class LiveStreamActivity extends AppCompatActivity implements EnxRoomObserver, EnxStreamObserver, EnxScreenShotObserver, WebResponse, EnxReconnectObserver {
    private String token;
    private String name;
    private FrameLayout selfFL;
    private EnxRtc enxRtc;
    private EnxRoom mEnxRoom;
    private EnxStream localStream;
    private int PERMISSION_ALL = 1;
    private EnxPlayerView enxPlayerView;
    private int count = 0;
    String roomId;
    String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_PHONE_STATE
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_stream);
        getPreviousIntent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermissions(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            } else {
                initialize();
            }
        }
    }

    private void initialize() {
        Log.e("initialize", "initialize");

        setView();
        enxRtc = new EnxRtc(this, this, this);
        localStream = enxRtc.joinRoom(token, getPublisherInfo(), getReconnectInfo(), new JSONArray());

        /*mEnxRoom = new EnxRoom(this, this, new EnxAdvancedOptionsObserver() {
            @Override
            public void onAdvancedOptionsUpdate(JSONObject jsonObject) {

            }

            @Override
            public void onGetAdvancedOptions(JSONObject jsonObject) {

            }
        });
        mEnxRoom.connect(token, getReconnectInfo(), new JSONArray());*/
    }

    @Override
    public void onRoomConnected(EnxRoom enxRoom, JSONObject jsonObject) {
        //received when user connected with Enablex room
        Log.e("onRoomConnected", jsonObject.toString());
//        this.mEnxRoom = enxRoom;
        roomId = jsonObject.optJSONObject("room").optString("_id");
        Log.e("roomId", roomId);
//        mEnxRoom.publish(localStream);
//        mEnxRoom.setReconnectObserver(this);
    }

    @Override
    public void onRoomError(JSONObject jsonObject) {
        //received when any error occurred while connecting to the Enablex room
        Log.e("onRoomError", jsonObject.toString());
        Toast.makeText(LiveStreamActivity.this, "Room Error", Toast.LENGTH_SHORT).show();
        this.finish();
    }

    @Override
    public void onUserConnected(JSONObject jsonObject) {
        // received when a new remote participant joins the call
        Log.e("onUserConnected", jsonObject.toString());
    }

    @Override
    public void onUserDisConnected(JSONObject jsonObject) {
        // received when a remote participant left the call
        Log.e("onUserDisConnected", jsonObject.toString());
    }

    @Override
    public void onPublishedStream(EnxStream enxStream) {
        //received when audio video published successfully to the other remote users
        Log.e("onPublishedStream", enxStream.toString());
        /*runOnUiThread(new Runnable() {
            @Override
            public void run() {
                enxPlayerView = new EnxPlayerView(LiveStreamActivity.this, EnxPlayerView.ScalingType.SCALE_ASPECT_BALANCED, true);
                enxPlayerView.setZOrderMediaOverlay(true);
                enxStream.attachRenderer(enxPlayerView);
                selfFL.setVisibility(View.VISIBLE);
                selfFL.addView(enxPlayerView);
            }
        });*/
    }

    @Override
    public void onUnPublishedStream(EnxStream enxStream) {
        //received when audio video unpublished successfully to the other remote users
        Log.e("onUnPublishedStream", enxStream.toString());
    }


    @Override
    public void onStreamAdded(EnxStream enxStream) {
        //received when a new stream added
        Log.e("onStreamAdded", enxStream.toString());
        mEnxRoom.subscribe(enxStream);
    }

    @Override
    public void onSubscribedStream(EnxStream enxStream) {
        //received when a remote stream subscribed successfully
        Log.e("onSubscribedStream", enxStream.toString());

        runOnUiThread(() -> {
            enxPlayerView = new EnxPlayerView(LiveStreamActivity.this, EnxPlayerView.ScalingType.SCALE_ASPECT_BALANCED, true);
            enxPlayerView.setZOrderMediaOverlay(true);
            enxStream.attachRenderer(enxPlayerView);
            selfFL.setVisibility(View.VISIBLE);
            selfFL.addView(enxPlayerView);
        });
    }

    @Override
    public void onUnSubscribedStream(EnxStream enxStream) {
        //received when a remote stream unsubscribed successfully
        Log.e("onUnSubscribedStream", enxStream.toString());
    }

    @Override
    public void onRoomDisConnected(JSONObject jsonObject) {
        //received when Enablex room successfully disconnected
        Log.e("onRoomDisConnected", jsonObject.toString());
        /*Intent intent = new Intent(LiveStreamActivity.this, EndKYCActivity.class);
        startActivity(intent);
        finish();*/
    }

    @Override
    public void onEventError(JSONObject jsonObject) {
        //received when any error occurred for any room event
        Log.e("onEventError", jsonObject.toString());
    }

    @Override
    public void onEventInfo(JSONObject jsonObject) {
        // received for different events update
        Log.e("onEventInfo", jsonObject.toString());
    }

    @Override
    public void onNotifyDeviceUpdate(String s) {
        // received when when new media device changed
        Log.e("onNotifyDeviceUpdate", s);
    }


    @Override
    public void onAcknowledgedSendData(JSONObject jsonObject) {
// received your chat data successfully sent to the other end
        Log.e("onAcknowledgedSendData", jsonObject.toString());
    }

    @Override
    public void onMessageReceived(JSONObject jsonObject) {
// received when chat data received at room
        Log.e("onMessageReceived", jsonObject.toString());
    }

    @Override
    public void onUserDataReceived(JSONObject jsonObject) {

        Log.e("onUserDataReceived", jsonObject.toString());
    }

    @Override
    public void onSwitchedUserRole(JSONObject jsonObject) {
        // received when user switch their role (from moderator  to participant)
        Log.e("onSwitchedUserRole", jsonObject.toString());
    }

    @Override
    public void onUserRoleChanged(JSONObject jsonObject) {
// received when user role changed successfully
        Log.e("onUserRoleChanged", jsonObject.toString());
    }

    @Override
    public void onConferencessExtended(JSONObject jsonObject) {
        Log.e("onConferencessExtended", jsonObject.toString());

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
        //received when audio mute/unmute happens
        Log.e("onAudioEvent", jsonObject.toString());
    }

    @Override
    public void onVideoEvent(JSONObject jsonObject) {
        //received when video mute/unmute happens
        Log.e("onVideoEvent", jsonObject.toString());
    }

    @Override
    public void onReceivedData(JSONObject jsonObject) {
        //received when chat data received at room level
        Log.e("onReceivedData", jsonObject.toString());
    }

    @Override
    public void onRemoteStreamAudioMute(JSONObject jsonObject) {
        //received when any remote stream mute audio
        Log.e("", "onRemoteStreamAudioMute : " + jsonObject.toString());
    }

    @Override
    public void onRemoteStreamAudioUnMute(JSONObject jsonObject) {
        //received when any remote stream unmute audio
        Log.e("", "onRemoteStreamAudioUnMute : " + jsonObject.toString());
    }

    @Override
    public void onRemoteStreamVideoMute(JSONObject jsonObject) {
        //received when any remote stream mute video
        Log.e("", "onRemoteStreamVideoMute" + jsonObject.toString());
    }

    @Override
    public void onRemoteStreamVideoUnMute(JSONObject jsonObject) {
        //received when any remote stream unmute video
        Log.e("", "onRemoteStreamVideoUnMute : " + jsonObject.toString());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED
                        && grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                    initialize();
                } else {
                    Toast.makeText(this, "Please enable permissions to further proceed.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Log.e("onBackPressed", "onBackPressed");
        super.onBackPressed();

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
            jsonObject.put("audio", false);
            jsonObject.put("video", false);
            jsonObject.put("data", true);
            jsonObject.put("audioMuted", true);
            jsonObject.put("videoMuted", true);
            jsonObject.put("name", "Remit Online");
            jsonObject.put("maxVideoLayers", 3);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("getPublisherInfo", jsonObject.toString());

        return jsonObject;
    }

    private void setView() {
        selfFL = (FrameLayout) findViewById(R.id.selfFL);
        Log.e("setView", "setView");
    }

    private void getPreviousIntent() {
        if (getIntent() != null) {
            token = getIntent().getStringExtra("token");
            name = getIntent().getStringExtra("name");
        }
    }

    public JSONObject getReconnectInfo() {
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
        Log.e("getReconnectInfo", jsonObject.toString());
        return jsonObject;
    }

    @Override
    public void OnCapturedView(Bitmap bitmap) {
        //received when any screenshot capture of any stream
        Log.e("OnCapturedView", bitmap.toString());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        JSONObject jsonObject = new JSONObject();
        Log.e("Filename", roomId + "_snapshot.png");
        try {
            jsonObject.put("data", Base64.encodeToString(byteArray, Base64.DEFAULT));
            jsonObject.put("filename", roomId + "_snapshot.png");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        new WebCall(this, this, jsonObject, WebConstants.uploadImageURL, WebConstants.uploadImageCode, false).execute();
    }

    @Override
    public void onWebResponse(String response, int callCode) {
        Log.e("response", response);
        switch (callCode) {
            case WebConstants.uploadImageCode:
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.optString("success").equalsIgnoreCase("true")) {
                        Log.e("Snapshot", "done");
                    } else {
                        Log.e("Snapshot", "Error occurred");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onWebResponseError(String error, int callCode) {
        Log.e("onWebResponseError", error);

    }

    @Override
    public void onReconnect(String s) {
// received when room tries to reconnect due to low bandwidth or any connection interruption

        Log.e("onReconnect", s);
    }

    @Override
    public void onUserReconnectSuccess(EnxRoom enxRoom, JSONObject jsonObject) {
// received when reconnect successfully completed
        Log.e("onUserReconnectSuccess", jsonObject.toString());
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        Log.e("onPointerCaptureChanged", String.valueOf(hasCapture));

    }
}