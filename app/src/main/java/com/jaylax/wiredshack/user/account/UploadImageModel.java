package com.jaylax.wiredshack.user.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class UploadImageModel {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private ArrayList<UploadImageData> data = new ArrayList<>();

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<UploadImageData> getData() {
        return data;
    }

    public void setData(ArrayList<UploadImageData> data) {
        this.data = data;
    }

    public class UploadImageData{
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("uid")
        @Expose
        private String uid;
        @SerializedName("post_img")
        @Expose
        private String postImg;
        @SerializedName("status")
        @Expose
        private String status;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getPostImg() {
            return postImg;
        }

        public void setPostImg(String postImg) {
            this.postImg = postImg;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

    }
}
