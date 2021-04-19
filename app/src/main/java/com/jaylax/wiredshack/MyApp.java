package com.jaylax.wiredshack;

import android.app.Application;

public class MyApp extends Application {

    public MyApp instanceApp;
    @Override
    public void onCreate() {
        super.onCreate();
        instanceApp = this;
    }
}
