package com.nadavbarlev.flickrgallery;

import android.os.AsyncTask;
import com.nadavbarlev.flickrgallery.Model.GalleryItem;
import com.nadavbarlev.flickrgallery.Utils.SharedPreferencesHelper;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/*
 *  Class - FetchGalleryTask.java
 *  Author - Nadav Bar Lev
 *  Run Fetch or Search actions on separate thread and retrieve list of Gallery Items
 */

public class FetchGalleryTask extends AsyncTask<Integer, Void, ArrayList<GalleryItem>> {

    // Logs
    private static final String TAG = "FetchGalleryTask";

    // Data Members
    private WeakReference<OnCompleteListener> mOnCompleteListenerRef;
    private boolean                           mIsSearchedOrCleared;

    // Constructor
    public FetchGalleryTask(OnCompleteListener onCompleteListener) {
        this.mOnCompleteListenerRef = new WeakReference<>(onCompleteListener);
    }

    protected ArrayList<GalleryItem> doInBackground(Integer... params) {

        String searchQuery =
                SharedPreferencesHelper.loadString(Flickr.PREF_SEARCH_QUERY);
        boolean shouldClear =
                SharedPreferencesHelper.loadBoolean(Flickr.PREF_SHOULD_CLEAR);

        // Clear Action
        if (shouldClear) {

            // Turn on action flag for callback
            mIsSearchedOrCleared = true;

            SharedPreferencesHelper.save(Flickr.PREF_SHOULD_CLEAR, false);

            // Retrieve new data (first page)
            return new Flickr().fetch(String.valueOf(params[0]));
        }

        // Search Action
        else if (!searchQuery.equals("")) {

            // Turn on action flag for callback
            mIsSearchedOrCleared = true;

            // Retrieve specific search
            return new Flickr().search(searchQuery);
        }

        // Fetch new page action
        else {

            // Turn off action flag for callback
            mIsSearchedOrCleared = false;

            // Retrieve specific page (param[0])
            return new Flickr().fetch(String.valueOf(params[0]));
        }
    }

    protected void onPostExecute(ArrayList<GalleryItem> galleryItems) {

        // Get a reference to the listener if it is still there
        OnCompleteListener onCompleteListener = mOnCompleteListenerRef.get();

        if (onCompleteListener != null) {
            onCompleteListener.getGalleryItems(galleryItems, mIsSearchedOrCleared);
        }
    }

    public interface OnCompleteListener {
        void getGalleryItems(ArrayList<GalleryItem> galleryItems, boolean isSearchedOrCleared);
    }
}
