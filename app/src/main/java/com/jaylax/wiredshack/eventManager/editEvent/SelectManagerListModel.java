package com.jaylax.wiredshack.eventManager.editEvent;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SelectManagerListModel {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private ArrayList<SelectManagerListData> data = new ArrayList<>();

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<SelectManagerListData> getData() {
        return data;
    }

    public void setData(ArrayList<SelectManagerListData> data) {
        this.data = data;
    }

    public class SelectManagerListData{
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("manager_name")
        @Expose
        private String managerName;
        @SerializedName("user_type")
        @Expose
        private String userType;
        @SerializedName("manager_image")
        @Expose
        private String managerImage;
        @SerializedName("manager_cover_image")
        @Expose
        private String managerCoverImage;

        private boolean isSelect = false;

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

        public String getUserType() {
            return userType;
        }

        public void setUserType(String userType) {
            this.userType = userType;
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

        public boolean isSelect() {
            return isSelect;
        }

        public void setSelect(boolean select) {
            isSelect = select;
        }
    }
}
