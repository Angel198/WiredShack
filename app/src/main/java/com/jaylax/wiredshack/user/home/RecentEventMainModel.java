package com.jaylax.wiredshack.user.home;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jaylax.wiredshack.user.eventDetails.EventDetailsMainModel;

import java.util.ArrayList;
import java.util.List;

public class RecentEventMainModel {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private ArrayList<RecentEventData> data = new ArrayList<>();

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<RecentEventData> getData() {
        return data;
    }

    public void setData(ArrayList<RecentEventData> data) {
        this.data = data;
    }


    public static class RecentEventData {
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
        @SerializedName("images")
        @Expose
        private ArrayList<EventImage> images = new ArrayList<>();

        public ArrayList<EventImage> getImages() {
            return images;
        }

        public void setImages(ArrayList<EventImage> images) {
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

        class EventImage {
            @SerializedName("images")
            @Expose
            private String images;
            @SerializedName("id")
            @Expose
            private String id;

            public String getImages() {
                return images;
            }

            public void setImages(String images) {
                this.images = images;
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
