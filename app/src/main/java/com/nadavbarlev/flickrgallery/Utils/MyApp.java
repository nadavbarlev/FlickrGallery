package com.nadavbarlev.flickrgallery.Utils;

import android.app.Application;
import android.content.Context;

/*
 *  Class  - MyApp.java
 *  Author - Nadav Bar Lev
 *  Application Context
 */

public class MyApp extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
