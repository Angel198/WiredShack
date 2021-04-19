package com.jaylax.wiredshack.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.jaylax.wiredshack.MyApp;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SharedPref {
    private static SharedPreferences sharedPreferences = null;
    public static final String PREF_TOKEN = "prefToken";


    public static void save(@NotNull String key, @Nullable Object value) {
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

    public final void delete(@NotNull String key) {
        if (sharedPreferences.contains(key)) {
            this.getEditor().remove(key).commit();
        }

    }

    public final void clearAll() {
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

    private SharedPref() {
    }

    static {
        sharedPreferences = (new MyApp()).instanceApp.getSharedPreferences("share_pref", Context.MODE_PRIVATE);
    }
}