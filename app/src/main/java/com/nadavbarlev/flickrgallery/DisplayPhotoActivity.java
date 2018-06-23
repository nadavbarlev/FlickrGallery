package com.nadavbarlev.flickrgallery;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

/*
 *  DisplayPhotoActivity.java
 *  Author - Nadav Bar Lev
 *  Activity which display a specific flickr photo web URL
 */

public class DisplayPhotoActivity extends AppCompatActivity {

    // Logs
    private static final String TAG = "DisplayPhotoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_photo);

        // Members
        String mURL = getIntent().getStringExtra("photoURL");

        // Controls
        WebView mWebView = (WebView)findViewById(R.id.webViewDisplay);

        // Config WebView
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(mURL);
    }
}
