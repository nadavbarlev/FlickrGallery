package com.nadavbarlev.flickrgallery.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/*
 *  Class  - SharedPreferencesHelper.java
 *  Author - Nadav Bar Lev
 *  Handler for SharedPreferences
 */

public class SharedPreferencesHelper {

    public static void save(String key, String value) {
        Context context = MyApp.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPreferences", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void save(String key, int value) {
        Context context = MyApp.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPreferences", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static void save(String key, boolean value) {
        Context context = MyApp.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPreferences", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static String loadString(String key, String defaultValue) {
        Context context = MyApp.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPreferences", context.MODE_PRIVATE);
        return sharedPreferences.getString(key, defaultValue);
    }

    public static String loadString(String key) {
        return loadString(key, "");
    }

    public static int loadInt(String key, int defaultValue) {
        Context context = MyApp.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPreferences", context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, defaultValue);
    }

    public static int loadInt(String key) {
        return loadInt(key, 0);
    }

    public static boolean loadBoolean(String key, boolean defaultValue) {
        Context context = MyApp.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPreferences", context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public static boolean loadBoolean(String key) {
        return (loadBoolean(key, false));
    }
}
