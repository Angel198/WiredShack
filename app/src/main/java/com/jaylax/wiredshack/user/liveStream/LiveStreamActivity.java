package com.jaylax.wiredshack.user.liveStream;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jaylax.wiredshack.ProgressDialog;
import com.jaylax.wiredshack.R;
import com.jaylax.wiredshack.databinding.ActivityLiveStreamBinding;
import com.jaylax.wiredshack.model.CommonResponseModel;
import com.jaylax.wiredshack.model.UserDetailsModel;
import com.jaylax.wiredshack.rest.ApiClient;
import com.jaylax.wiredshack.user.liveVideoPlayer.LiveStreamUserAdapter;
import com.jaylax.wiredshack.user.liveVideoPlayer.LiveStreamUserModel;
import com.jaylax.wiredshack.utils.Commons;
import com.jaylax.wiredshack.utils.SharePref;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import enx_rtc_android.Controller.EnxAdvancedOptionsObserver;
import enx_rtc_android.Controller.EnxPlayerView;
import enx_rtc_android.Controller.EnxRoom;
import enx_rtc_android.Controller.EnxRoomObserver;
import enx_rtc_android.Controller.EnxStream;
import enx_rtc_android.Controller.EnxStreamObserver;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LiveStreamActivity extends AppCompatActivity implements EnxRoomObserver, EnxStreamObserver, EnxAdvancedOptionsObserver {

    private String token;
    private String name;
    ActivityLiveStreamBinding mBinding;
    EnxRoom mEnxRoom;
    private EnxPlayerView enxPlayerView;
    private EnxStream liveStream;
    private Context mContext;
    private ProgressDialog progressDialog;

    private String isRequested = "";
    private String streamId = "";
    private UserDetailsModel userDetailsModel;
    private long timerMilliSec = 120000;

    String[] PERMISSIONS = {
            Manifest.permission.READ_PHONE_STATE
    };
    private int PERMISSION_ALL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_live_stream);
        mContext = this;
        userDetailsModel = Commons.convertStringToObject(this, SharePref.PREF_USER, UserDetailsModel.class);
        progressDialog = new ProgressDialog(mContext);

        getDataFromIntent();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermissions(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            } else {
                initData();
            }
        }

//        initData();


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        }
    }

    private void getDataFromIntent() {
        if (getIntent() != null) {
            token = getIntent().getStringExtra("token");
            name = getIntent().getStringExtra("name");
            streamId = getIntent().getStringExtra("streamId");
            if (getIntent().hasExtra("isRequested")) {
                isRequested = getIntent().getStringExtra("isRequested");
            }
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


    private void initData() {

        mEnxRoom = new EnxRoom(this, this, this);
        mEnxRoom.init(LiveStreamActivity.this);
        mEnxRoom.connect(token, getRoomConnectInfo(), new JSONArray());

        mBinding.recyclerLiveUser.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        if (isRequested.equals("2")) {
            callEnterEventAPI();
            mBinding.tvLiveStreamCountDown.setVisibility(View.GONE);
        } else {
            streamCheck("1");
            mBinding.tvLiveStreamCountDown.setVisibility(View.VISIBLE);
            startTimer();
        }
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
        mBinding.tvStreamLive.setVisibility(View.VISIBLE);
    }

    @Override
    public void onUserDisConnected(JSONObject jsonObject) {
        //while Stream closed
        Log.e("onUserDisConnected", jsonObject.toString());
        /*mBinding.tvStreamLive.setVisibility(View.VISIBLE);
        mEnxRoom.disconnect();*/
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

        this.liveStream = enxStream;
        runOnUiThread(() -> {
            enxPlayerView = new EnxPlayerView(LiveStreamActivity.this, EnxPlayerView.ScalingType.SCALE_ASPECT_FILL, true);
            liveStream.attachRenderer(enxPlayerView);
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
        if (isRequested.equals("2")) {
            callExitStream();
        } else {
            streamCheck("0");
        }

        /*if (mEnxRoom.isConnected()) {
            if (liveStream != null) {
                liveStream.detachRenderer();
            }
            mEnxRoom.disconnect();
        } else {
            finish();
        }*/
    }

    private void callEnterEventAPI() {

        //TODO : Call enterLiveStream api
        if (Commons.isOnline(mContext)) {
            HashMap<String, String> params = new HashMap<>();
            params.put("event_id", streamId);
            params.put("uid", userDetailsModel.getId());

            String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");
            ApiClient.create().enterLiveStream(header, params).enqueue(new Callback<CommonResponseModel>() {
                @Override
                public void onResponse(Call<CommonResponseModel> call, Response<CommonResponseModel> response) {
                    /*if (response.code() == 200 && response.isSuccessful()) {
                        if (response.body() != null) {
                            if (!response.body().getStatus().equals("200")) {
                                Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                            }
                        } else {
                            Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                        }
                    } else {
                        Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                    }*/
                    callLiveStreamUserApi();
                }

                @Override
                public void onFailure(Call<CommonResponseModel> call, Throwable t) {
                    Commons.showToast(mContext, getResources().getString(R.string.something_wants_wrong));
                    callLiveStreamUserApi();
                }
            });
        } else {
            Commons.showToast(mContext, mContext.getResources().getString(R.string.no_internet_connection));
        }
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
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
                                    mBinding.recyclerLiveUser.setVisibility(View.GONE);
                                    mBinding.tvLiveStreamUserCount.setText("0");
                                } else {
                                    mBinding.recyclerLiveUser.setVisibility(View.VISIBLE);
                                    mBinding.tvLiveStreamUserCount.setText(String.valueOf(response.body().getData().size()));

                                    mBinding.recyclerLiveUser.setAdapter(new LiveStreamUserAdapter(mContext, response.body().getData()));
                                    mBinding.recyclerLiveUser.smoothScrollToPosition(response.body().getData().size() - 1);
                                }
                                /*if (!response.body().getStatus().equals("200")) {
                                    Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                                }*/
                            } else {
//                                Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                            }
                        } else {
//                            Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                        }
                        handler.postDelayed(runnable, 5000);
                    }

                    @Override
                    public void onFailure(Call<LiveStreamUserModel> call, Throwable t) {
                        Commons.showToast(mContext, getResources().getString(R.string.something_wants_wrong));
                        handler.postDelayed(runnable, 5000);
                    }
                });
            } else {
                Commons.showToast(mContext, mContext.getResources().getString(R.string.no_internet_connection));
            }
        }
    };

    private void callLiveStreamUserApi() {
        handler.postDelayed(runnable, 5000);
    }

    private void callExitStream() {
        //TODO : Call exitLiveStream api
        if (Commons.isOnline(mContext)) {
            HashMap<String, String> params = new HashMap<>();
            params.put("event_id", streamId);
            params.put("uid", userDetailsModel.getId());

            String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");
            ApiClient.create().exitLiveStream(header, params).enqueue(new Callback<CommonResponseModel>() {
                @Override
                public void onResponse(Call<CommonResponseModel> call, Response<CommonResponseModel> response) {
                    /*if (response.code() == 200 && response.isSuccessful()) {
                        if (response.body() != null) {
                            if (!response.body().getStatus().equals("200")) {
                                Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                            }
                        } else {
                            Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                        }
                    } else {
                        Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                    }*/
                    if (mEnxRoom.isConnected()) {
                        if (liveStream != null) {
                            liveStream.detachRenderer();
                        }
                        mEnxRoom.disconnect();
                    }
                    new Handler().postDelayed(() -> {
                            finish();
                    }, 1500);
                }

                @Override
                public void onFailure(Call<CommonResponseModel> call, Throwable t) {
                    Commons.showToast(mContext, getResources().getString(R.string.something_wants_wrong));
                    if (mEnxRoom.isConnected()) {
                        if (liveStream != null) {
                            liveStream.detachRenderer();
                        }
                        mEnxRoom.disconnect();
                    }
                    new Handler().postDelayed(() -> {
                        finish();
                    }, 1500);
                }
            });
        } else {
            Commons.showToast(mContext, mContext.getResources().getString(R.string.no_internet_connection));
        }
    }

    private void startTimer() {
        CountDownTimer streamCountDown = new CountDownTimer(timerMilliSec, 1000) {
            public void onTick(long millisUntilFinished) {
                timerMilliSec = millisUntilFinished;
                //Convert milliseconds into hour,minute and seconds

                String hms = String.format("%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(timerMilliSec),
                        TimeUnit.MILLISECONDS.toMinutes(timerMilliSec) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timerMilliSec)), TimeUnit.MILLISECONDS.toSeconds(timerMilliSec) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timerMilliSec)));

                mBinding.tvLiveStreamCountDown.setText(hms);//set text
            }

            public void onFinish() {
                mBinding.tvLiveStreamCountDown.setText("00:00:00"); //On finish change timer text
                if (!isRequested.equals("2")) {
                    if (!(isFinishing())) {
                        showRequestDialog();
                    }
                }

            }
        }.start();
    }


    private void showRequestDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String msg = "";
        String positiveTxt = "";
        String negativeTxt = "";
        if (isRequested.equals("1")) {
            msg = getResources().getString(R.string.already_requested_msg);
            positiveTxt = getResources().getString(R.string.txt_ok);
            negativeTxt = "";
        } else {
            msg = getResources().getString(R.string.request_event_msg);
            positiveTxt = getResources().getString(R.string.txt_cancel);
            negativeTxt = getResources().getString(R.string.request);
        }

        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton(positiveTxt, (dialogInterface, i) -> {
            //TODO : Call stream_check Api
            streamCheck("0");
        });

        if (!isRequested.equals("1")) {
            builder.setNegativeButton(negativeTxt, (dialogInterface, i) -> {
                //TODO : RequestLiveStream and close thi activity
                requestStream();
            });
        }
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
    }

    private void requestStream() {
        if (Commons.isOnline(mContext)) {
            progressDialog.show();
            HashMap<String, String> params = new HashMap<>();
            params.put("event_id", streamId);

            String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");
            ApiClient.create().requestForLiveStream(header, params).enqueue(new Callback<CommonResponseModel>() {
                @Override
                public void onResponse(Call<CommonResponseModel> call, Response<CommonResponseModel> response) {
                    progressDialog.dismiss();
                   /*if (response.code() == 200 && response.isSuccessful()) {
                        if (response.body() != null) {
                            if (response.body().getStatus().equals("200")) {

                            } else {
                                String msg = "";
                                if (response.body().getMessage().isEmpty()) {
                                    msg = getResources().getString(R.string.please_try_after_some_time);
                                } else {
                                    msg = response.body().getMessage();
                                }
                                Commons.showToast(mContext, msg);
                            }
                        }
                    } else {
                        Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                    }*/
                    streamCheck("0");
                }

                @Override
                public void onFailure(Call<CommonResponseModel> call, Throwable t) {
                    progressDialog.dismiss();
                    Commons.showToast(mContext, getResources().getString(R.string.something_wants_wrong));
                    streamCheck("0");
                }
            });
        } else {
            Commons.showToast(mContext, getResources().getString(R.string.no_internet_connection));
        }
    }


    private void streamCheck(String isEnable) {

        if (Commons.isOnline(mContext)) {
            progressDialog.show();
            HashMap<String, String> params = new HashMap<>();
            params.put("uid", userDetailsModel.getId());
            params.put("event_id", streamId);
            params.put("is_enabale", isEnable);

            String header = "Bearer " + SharePref.getInstance(mContext).get(SharePref.PREF_TOKEN, "");
            ApiClient.create().streamCheck(header, params).enqueue(new Callback<CommonResponseModel>() {
                @Override
                public void onResponse(Call<CommonResponseModel> call, Response<CommonResponseModel> response) {
                    progressDialog.dismiss();
                    /*if (response.code() == 200 && response.isSuccessful()) {
                        if (response.body() != null) {
                            if (!response.body().getStatus().equals("200")) {
                                Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                            }
                        } else {
                            Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                        }
                    } else {
                        Commons.showToast(mContext, getResources().getString(R.string.please_try_after_some_time));
                    }*/
                    if (isEnable.equals("0")) {
                        callExitStream();
                    } else {
                        callEnterEventAPI();
                    }


                }

                @Override
                public void onFailure(Call<CommonResponseModel> call, Throwable t) {
                    progressDialog.dismiss();
                    Commons.showToast(mContext, getResources().getString(R.string.something_wants_wrong));
                    if (isEnable.equals("0")) {
                        callExitStream();
                    } else {
                        callEnterEventAPI();
                    }
                }
            });
        } else {
            Commons.showToast(mContext, mContext.getResources().getString(R.string.no_internet_connection));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initData();
                } else {
                    Toast.makeText(this, "Please enable permissions to further proceed.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}