package com.jaylax.wiredshack.user.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jaylax.wiredshack.model.RecentEventMainModel;

import java.util.ArrayList;

public class FollowingEventMainModel {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private ArrayList<FollowingEventData> data = new ArrayList<>();

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<FollowingEventData> getData() {
        return data;
    }

    public void setData(ArrayList<FollowingEventData> data) {
        this.data = data;
    }

    public class FollowingEventData{
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("event_name")
        @Expose
        private String eventName;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("location")
        @Expose
        private String location;
        @SerializedName("date")
        @Expose
        private String date;
        @SerializedName("stime")
        @Expose
        private String stime;
        @SerializedName("etime")
        @Expose
        private String etime;
        @SerializedName("user_type")
        @Expose
        private String userType;
        @SerializedName("manager_name")
        @Expose
        private String managerName;
        @SerializedName("manager_image")
        @Expose
        private String managerImage;
        @SerializedName("manager_cover_image")
        @Expose
        private String managerCoverImage;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("request_status")
        @Expose
        private String requestStatus;
        @SerializedName("images")
        @Expose
        private ArrayList<FollowingEventImage> images = new ArrayList<>();

        public ArrayList<FollowingEventImage> getImages() {
            return images;
        }

        public void setImages(ArrayList<FollowingEventImage> images) {
            this.images = images;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getEventName() {
            return eventName;
        }

        public void setEventName(String eventName) {
            this.eventName = eventName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getStime() {
            return stime;
        }

        public void setStime(String stime) {
            this.stime = stime;
        }

        public String getEtime() {
            return etime;
        }

        public void setEtime(String etime) {
            this.etime = etime;
        }

        public String getUserType() {
            return userType;
        }

        public void setUserType(String userType) {
            this.userType = userType;
        }

        public String getManagerName() {
            return managerName;
        }

        public void setManagerName(String managerName) {
            this.managerName = managerName;
        }

        public String getManagerImage() {
            return managerImage;
        }

        public void setManagerImage(String managerImage) {
            this.managerImage = managerImage;
        }

        public String getManagerCoverImage() {
            return managerCoverImage;
        }

        public void setManagerCoverImage(String managerCoverImage) {
            this.managerCoverImage = managerCoverImage;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getRequestStatus() {
            return requestStatus;
        }

        public void setRequestStatus(String requestStatus) {
            this.requestStatus = requestStatus;
        }

        public class FollowingEventImage {
            @SerializedName("image")
            @Expose
            private String image;
            @SerializedName("id")
            @Expose
            private String id;

            public String getImage() {
                return image;
            }

            public void setImage(String images) {
                this.image = images;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }
        }
    }

}
