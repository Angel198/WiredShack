package com.jaylax.wiredshack.user.eventDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class EventCommentMainModel {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private ArrayList<EventCommentData> data = new ArrayList<>();

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<EventCommentData> getData() {
        return data;
    }

    public void setData(ArrayList<EventCommentData> data) {
        this.data = data;
    }

    public class EventCommentData{
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("comment")
        @Expose
        private String comment;
        @SerializedName("manager_name")
        @Expose
        private String managerName;
        @SerializedName("manager_image")
        @Expose
        private String managerImage;
        @SerializedName("manager_cover_image")
        @Expose
        private String managerCoverImage;
        @SerializedName("user_name")
        @Expose
        private String userName;
        @SerializedName("user_image")
        @Expose
        private String userImage;
        @SerializedName("user_cover_image")
        @Expose
        private String userCoverImage;
        @SerializedName("created_at")
        @Expose
        private String createdAt;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
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

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }
    }
}
