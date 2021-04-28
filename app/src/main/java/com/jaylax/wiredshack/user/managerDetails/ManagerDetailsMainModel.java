package com.jaylax.wiredshack.user.managerDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jaylax.wiredshack.model.RecentEventMainModel;

import java.util.ArrayList;

public class ManagerDetailsMainModel {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private ManagerDetailsData data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ManagerDetailsData getData() {
        return data;
    }

    public void setData(ManagerDetailsData data) {
        this.data = data;
    }

    public class ManagerDetailsData{
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
        @SerializedName("followed_count")
        @Expose
        private String followedCount;
        @SerializedName("follow")
        @Expose
        private String follow;
        @SerializedName("recent_event")
        @Expose
        private ArrayList<RecentEventMainModel.RecentEventData> recentEvent = new ArrayList<>();

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

        public String getFollowedCount() {
            return followedCount;
        }

        public void setFollowedCount(String followedCount) {
            this.followedCount = followedCount;
        }

        public String getFollow() {
            return follow;
        }

        public void setFollow(String follow) {
            this.follow = follow;
        }

        public ArrayList<RecentEventMainModel.RecentEventData> getRecentEvent() {
            return recentEvent;
        }

        public void setRecentEvent(ArrayList<RecentEventMainModel.RecentEventData> recentEvent) {
            this.recentEvent = recentEvent;
        }
    }
}
