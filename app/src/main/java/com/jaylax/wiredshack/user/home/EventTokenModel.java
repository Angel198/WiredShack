package com.jaylax.wiredshack.user.home;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EventTokenModel {
    @SerializedName("result")
    @Expose
    private String result;
    @SerializedName("token")
    @Expose
    private String token;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
