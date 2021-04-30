package com.jaylax.wiredshack.eventManager.managerActivity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class IncomingRequestMainModel {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private ArrayList<IncomingRequest> data = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<IncomingRequest> getData() {
        return data;
    }

    public void setData(ArrayList<IncomingRequest> data) {
        this.data = data;
    }

    public class IncomingRequest{
        @SerializedName("event_id")
        @Expose
        private String eventId;
        @SerializedName("event_name")
        @Expose
        private String eventName;
        @SerializedName("user_id")
        @Expose
        private String userId;
        @SerializedName("user_name")
        @Expose
        private String userName;
        @SerializedName("user_image")
        @Expose
        private String userImage;
        @SerializedName("user_cover_image")
        @Expose
        private String userCoverImage;

        public String getEventId() {
            return eventId;
        }

        public void setEventId(String eventId) {
            this.eventId = eventId;
        }

        public String getEventName() {
            return eventName;
        }

        public void setEventName(String eventName) {
            this.eventName = eventName;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserImage() {
            return userImage;
        }

        public void setUserImage(String userImage) {
            this.userImage = userImage;
        }

        public String getUserCoverImage() {
            return userCoverImage;
        }

        public void setUserCoverImage(String userCoverImage) {
            this.userCoverImage = userCoverImage;
        }
    }
}
