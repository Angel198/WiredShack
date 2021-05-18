package com.jaylax.wiredshack.user.eventDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class EventDetailsMainModel {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private EventDetailsData data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public EventDetailsData getData() {
        return data;
    }

    public void setData(EventDetailsData data) {
        this.data = data;
    }

    public class EventDetailsData {
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
        @SerializedName("isactived")
        @Expose
        private String isactived;
        @SerializedName("last_reason")
        @Expose
        private String lastReason;
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
        @SerializedName("images")
        @Expose
        private ArrayList<EventImage> images = new ArrayList<>();
        @SerializedName("likes")
        @Expose
        private String likes;
        @SerializedName("likes_count")
        @Expose
        private String likesCount;
        @SerializedName("follows")
        @Expose
        private String follows;
        @SerializedName("commnets")
        @Expose
        private String commnets;
        @SerializedName("latitude")
        @Expose
        private String latitude;
        @SerializedName("longitude")
        @Expose
        private String longitude;
        @SerializedName("is_request")
        @Expose
        private String isRequest;
        @SerializedName("created_by")
        @Expose
        private String createdBy;
        @SerializedName("selected_manager")
        @Expose
        private SelectedManagerData selectedManager;

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

        public String getIsactived() {
            return isactived;
        }

        public void setIsactived(String isactived) {
            this.isactived = isactived;
        }

        public String getLastReason() {
            return lastReason;
        }

        public void setLastReason(String lastReason) {
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

        public String getUserType() {
            return userType;
        }

        public void setUserType(String userType) {
            this.userType = userType;
        }

        public ArrayList<EventImage> getImages() {
            return images;
        }

        public void setImages(ArrayList<EventImage> images) {
            this.images = images;
        }

        public String getLikes() {
            return likes;
        }

        public void setLikes(String likes) {
            this.likes = likes;
        }

        public String getLikesCount() {
            return likesCount;
        }

        public void setLikesCount(String likesCount) {
            this.likesCount = likesCount;
        }

        public String getFollows() {
            return follows;
        }

        public void setFollows(String follows) {
            this.follows = follows;
        }

        public String getCommnets() {
            return commnets;
        }

        public void setCommnets(String commnets) {
            this.commnets = commnets;
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

        public String getIsRequest() {
            return isRequest;
        }

        public void setIsRequest(String isRequest) {
            this.isRequest = isRequest;
        }

        public String getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(String createdBy) {
            this.createdBy = createdBy;
        }

        public SelectedManagerData getSelectedManager() {
            return selectedManager;
        }

        public void setSelectedManager(SelectedManagerData selectedManager) {
            this.selectedManager = selectedManager;
        }

        public class EventImage implements Serializable {
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

        public class SelectedManagerData{
            @SerializedName("name")
            @Expose
            private String name;
            @SerializedName("id")
            @Expose
            private String id;
            @SerializedName("image")
            @Expose
            private String image;
            @SerializedName("cover_image")
            @Expose
            private String coverImage;
            @SerializedName("user_type")
            @Expose
            private String userType;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
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

            public String getUserType() {
                return userType;
            }

            public void setUserType(String userType) {
                this.userType = userType;
            }
        }
    }
}
