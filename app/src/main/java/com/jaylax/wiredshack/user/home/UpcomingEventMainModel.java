package com.jaylax.wiredshack.user.home;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jaylax.wiredshack.user.eventDetails.EventDetailsMainModel;

import java.util.ArrayList;

public class UpcomingEventMainModel {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("following_event_count")
    @Expose
    private String followingEventCount;
    @SerializedName("data")
    @Expose
    private UpcomingEventData data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UpcomingEventData getData() {
        return data;
    }

    public void setData(UpcomingEventData data) {
        this.data = data;
    }

    public String getFollowingEventCount() {
        return followingEventCount;
    }

    public void setFollowingEventCount(String followingEventCount) {
        this.followingEventCount = followingEventCount;
    }

    public class UpcomingEventData{
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("manager_id")
        @Expose
        private String managerId;
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
        @SerializedName("latitude")
        @Expose
        private String latitude;
        @SerializedName("longitude")
        @Expose
        private String longitude;
        @SerializedName("isactived")
        @Expose
        private String isactived;
        @SerializedName("last_reason")
        @Expose
        private Object lastReason;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("image")
        @Expose
        private String image;
        @SerializedName("cover_image")
        @Expose
        private String coverImage;
        @SerializedName("user_type")
        @Expose
        private String userType;
        @SerializedName("is_active")
        @Expose
        private String isActive;
        @SerializedName("images")
        @Expose
        private ArrayList<EventDetailsMainModel.EventDetailsData.EventImage> images = new ArrayList<>();
        @SerializedName("likes_count")
        @Expose
        private String likesCount;
        @SerializedName("follows_count")
        @Expose
        private String followsCount;
        @SerializedName("commnets")
        @Expose
        private String commnets;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getManagerId() {
            return managerId;
        }

        public void setManagerId(String managerId) {
            this.managerId = managerId;
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

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getUserType() {
            return userType;
        }

        public void setUserType(String userType) {
            this.userType = userType;
        }

        public String getIsactived() {
            return isactived;
        }

        public void setIsactived(String isactived) {
            this.isactived = isactived;
        }

        public Object getLastReason() {
            return lastReason;
        }

        public void setLastReason(Object lastReason) {
            this.lastReason = lastReason;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getCoverImage() {
            return coverImage;
        }

        public void setCoverImage(String coverImage) {
            this.coverImage = coverImage;
        }

        public String getIsActive() {
            return isActive;
        }

        public void setIsActive(String isActive) {
            this.isActive = isActive;
        }

        public ArrayList<EventDetailsMainModel.EventDetailsData.EventImage> getImages() {
            return images;
        }

        public void setImages(ArrayList<EventDetailsMainModel.EventDetailsData.EventImage> images) {
            this.images = images;
        }

        public String getLikesCount() {
            return likesCount;
        }

        public void setLikesCount(String likesCount) {
            this.likesCount = likesCount;
        }

        public String getFollowsCount() {
            return followsCount;
        }

        public void setFollowsCount(String followsCount) {
            this.followsCount = followsCount;
        }

        public String getCommnets() {
            return commnets;
        }

        public void setCommnets(String commnets) {
            this.commnets = commnets;
        }
    }
}
