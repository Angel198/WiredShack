package com.jaylax.wiredshack.user.home;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ManagerListMainModel {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private ArrayList<ManagerListData> data = new ArrayList<>();

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<ManagerListData> getData() {
        return data;
    }

    public void setData(ArrayList<ManagerListData> data) {
        this.data = data;
    }

    public class ManagerListData{
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("manager_name")
        @Expose
        private String managerName;
        @SerializedName("manager_image")
        @Expose
        private String managerImage;
        @SerializedName("manager_cover_image")
        @Expose
        private String managerCoverImage;
        @SerializedName("following")
        @Expose
        private String following;
        @SerializedName("user_type")
        @Expose
        private String userType;
        @SerializedName("is_active")
        @Expose
        private String isActive;

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

        public String getFollowing() {
            return following;
        }

        public void setFollowing(String following) {
            this.following = following;
        }

        public String getUserType() {
            return userType;
        }

        public void setUserType(String userType) {
            this.userType = userType;
        }

        public String getIsActive() {
            return isActive;
        }

        public void setIsActive(String isActive) {
            this.isActive = isActive;
        }
    }
}
