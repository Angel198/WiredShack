package com.jaylax.wiredshack.eventManager.followed;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ManagerFollowedMainModel {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private ArrayList<ManagerFollowedData> data = new ArrayList<>();

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<ManagerFollowedData> getData() {
        return data;
    }

    public void setData(ArrayList<ManagerFollowedData> data) {
        this.data = data;
    }

    public class ManagerFollowedData{
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("user_name")
        @Expose
        private String userName;
        @SerializedName("user_image")
        @Expose
        private String userImage;
        @SerializedName("user_cover_image")
        @Expose
        private String userCoverImage;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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
