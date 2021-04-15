package com.jaylax.wiredshack.user.notification;

public class DummyModel {
    String dayName,userName;

    public DummyModel(String dayName, String userName) {
        this.dayName = dayName;
        this.userName = userName;
    }

    public String getDayName() {
        return dayName;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
