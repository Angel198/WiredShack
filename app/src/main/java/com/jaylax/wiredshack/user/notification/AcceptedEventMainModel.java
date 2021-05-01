package com.jaylax.wiredshack.user.notification;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AcceptedEventMainModel {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private ArrayList<AcceptedData> data = new ArrayList<>();

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<AcceptedData> getData() {
        return data;
    }

    public void setData(ArrayList<AcceptedData> data) {
        this.data = data;
    }

    public class AcceptedData {
        @SerializedName("event_id")
        @Expose
        private String eventId;
        @SerializedName("event_name")
        @Expose
        private String eventName;
        @SerializedName("manager_id")
        @Expose
        private String managerId;
        @SerializedName("manager_name")
        @Expose
        private String managerName;
        @SerializedName("manager_profile_image")
        @Expose
        private String managerProfileImage;
        @SerializedName("manager_cover_image")
        @Expose
        private String managerCoverImage;

        private String dayName;

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

        public String getManagerId() {
            return managerId;
        }

        public void setManagerId(String managerId) {
            this.managerId = managerId;
        }

        public String getManagerName() {
            return managerName;
        }

        public void setManagerName(String managerName) {
            this.managerName = managerName;
        }

        public String getManagerProfileImage() {
            return managerProfileImage;
        }

        public void setManagerProfileImage(String managerProfileImage) {
            this.managerProfileImage = managerProfileImage;
        }

        public String getManagerCoverImage() {
            return managerCoverImage;
        }

        public void setManagerCoverImage(String managerCoverImage) {
            this.managerCoverImage = managerCoverImage;
        }

        public String getDayName() {
            return dayName;
        }

        public void setDayName(String dayName) {
            this.dayName = dayName;
        }
    }
}
