package com.jaylax.wiredshack.user.following;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jaylax.wiredshack.eventManager.followed.ManagerFollowedMainModel;

import java.util.ArrayList;

public class UserFollowingMainModel {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private ArrayList<UserFollowingData> data = new ArrayList<>();

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<UserFollowingData> getData() {
        return data;
    }

    public void setData(ArrayList<UserFollowingData> data) {
        this.data = data;
    }

    public class UserFollowingData{
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("manager_name")
        @Expose
        private String managerName;
        @SerializedName("manager_image")
        @Expose
        private String managerImage;
        @SerializedName("managerCoverImage")
        @Expose
        private String managerCoverImage;
        @SerializedName("user_type")
        @Expose
        private String userType;

        private String isFollow;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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

        public String getIsFollow() {
            return isFollow;
        }

        public void setIsFollow(String isFollow) {
            this.isFollow = isFollow;
        }

        public String getUserType() {
            return userType;
        }

        public void setUserType(String userType) {
            this.userType = userType;
        }
    }
}
