package com.nadavbarlev.flickrgallery;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import com.nadavbarlev.flickrgallery.Model.GalleryItem;
import com.nadavbarlev.flickrgallery.Utils.MyApp;
import com.nadavbarlev.flickrgallery.Utils.SharedPreferencesHelper;
import java.util.ArrayList;

/*
 *  Class  - PollService.java
 *  Author - Nadav Bar Lev
 *  Handle PollService - Check every POLL_INTERVAL millisec if an image was added for the
 *  current search
 */

public class PollService extends IntentService {

    // Logs
    private static final String TAG = "PollService";

    // Constants
    public static final String PREF_IS_ALARM_ON = "isAlarmOn";
    public static final int    POLL_INTERVAL    = 1000 * 60 * 15; // 15 Min

    // Constructor
    public PollService() {
        super(TAG);

        // Init Service when app close - START_REDELIVER_INTENT
        setIntentRedelivery(true);
    }

    protected void onHandleIntent(Intent intent) {

        String searchQuery  = SharedPreferencesHelper.loadString(Flickr.PREF_SEARCH_QUERY);
        String lastResultID = SharedPreferencesHelper.loadString(Flickr.PREF_LAST_RESULT_ID);

        ArrayList<GalleryItem> items = new Flickr().search(searchQuery);

        if (items.size() == 0)
            return;

        // Get the ID of the first item
        String resultId = items.get(0).getId();

        // There is a new photo to display
        if (!resultId.equals(lastResultID)) {

            Intent intentMain = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intentMain, 0);

            // Create notification - Builder Design Pattern
            Notification notification = new NotificationCompat.Builder(this)
                    .setTicker(getResources().getString(R.string.notification_new_photo_title))
                    .setSmallIcon(R.drawable.ic_notification_new_photo)
                    .setContentTitle(getResources().getString(R.string.notification_new_photo_title))
                    .setContentText(getResources().getString(R.string.notification_new_photo_content))
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();

            NotificationManagerCompat.from(this).notify(1, notification);

        }

        SharedPreferencesHelper.save(Flickr.PREF_LAST_RESULT_ID, resultId);
    }

    public static void setServiceAlarm(Context context, boolean isOn) {

        // Set PollService in Pending Intent
        Intent        intent        = new Intent(context, PollService.class);
        PendingIntent pendingIntent = PendingIntent.getService(MyApp.getContext(), 0, intent, 0);

        // Get AlarmManager Service
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        if (isOn) {

            // Turn On AlarmManager Service for enable PollService
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), POLL_INTERVAL, pendingIntent);
        }
        else {

            // Turn Off AlarmManager and PollService
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }

        // Save search ON/OFF status
        SharedPreferencesHelper.save(PollService.PREF_IS_ALARM_ON, isOn);
    }

    public static boolean isServiceAlarmOn() {
        return (SharedPreferencesHelper.loadBoolean(PollService.PREF_IS_ALARM_ON));
    }
}
