package com.jaylax.wiredshack.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.jaylax.wiredshack.MyApp;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SharePref {

    private static SharePref sharePref = new SharePref();
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;


    public static final String PREF_TOKEN = "prefToken";
    public static final String PREF_USER = "prefUser";



    private SharePref() {
    } //prevent creating multiple instances by making the constructor private

    //The context passed into the getInstance should be application level context.
    public static SharePref getInstance(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
            editor = sharedPreferences.edit();
        }
        return sharePref;
    }


    public void save(@NotNull String key, @Nullable Object value) {
        if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean)value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer)value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float)value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long)value);
        } else if (value instanceof String) {
            editor.putString(key, (String)value);
        } else if (value instanceof Enum) {
            editor.putString(key, value.toString());
        } else if (value != null) {
        }

        editor.apply();
    }

    public Object get(@NotNull String key, Object defValue) {
        Object returnValue = sharedPreferences.getAll().get(key);
        Object var10000 = returnValue;
        if (returnValue == null) {
            var10000 = defValue;
        }

        return var10000;
    }

    //remove
    public  void delete(@NotNull String key) {
        if (sharedPreferences.contains(key)) {
            editor.remove(key).commit();
        }

    }

    private boolean has(String key) {
        return sharedPreferences.contains(key);
    }

    //clear all
    public void clearAll() {
        editor.clear();
        editor.commit();
    }

}


/*
public class SharedPref {
    private static SharedPreferences sharedPreferences = null;
    public static final String PREF_TOKEN = "prefToken";


    public void save(@NotNull String key, @Nullable Object value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean)value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer)value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float)value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long)value);
        } else if (value instanceof String) {
            editor.putString(key, (String)value);
        } else if (value instanceof Enum) {
            editor.putString(key, value.toString());
        } else if (value != null) {
        }

        editor.apply();
    }

    public  void delete(@NotNull String key) {
        if (sharedPreferences.contains(key)) {
            this.getEditor().remove(key).commit();
        }

    }

    public  void clearAll() {
        this.getEditor().clear().apply();
    }

    private SharedPreferences.Editor getEditor() {
        return sharedPreferences.edit();
    }

    public static Object get(@NotNull String key, Object defValue) {
        Object returnValue = sharedPreferences.getAll().get(key);
        Object var10000 = returnValue;
        if (returnValue == null) {
            var10000 = defValue;
        }

        return var10000;
    }

    private final boolean has(String key) {
        return sharedPreferences.contains(key);
    }

    public SharedPref() {
        sharedPreferences = (new MyApp()).instanceApp.getSharedPreferences("share_pref", Context.MODE_PRIVATE);
    }

    static {
        sharedPreferences = (new MyApp()).instanceApp.getSharedPreferences("share_pref", Context.MODE_PRIVATE);
    }
}*/
