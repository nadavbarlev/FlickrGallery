package com.nadavbarlev.flickrgallery;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import com.nadavbarlev.flickrgallery.Model.GalleryItem;
import com.nadavbarlev.flickrgallery.Utils.SharedPreferencesHelper;
import java.util.ArrayList;

/*
 *  Class  - MainActivity.java
 *  Author - Nadav Bar Lev
 */

public class MainActivity extends AppCompatActivity implements FetchGalleryTask.OnCompleteListener {

    // Logs
    private static final String TAG = "MainActivity";

    // Data Members
    private GalleryItemAdapter     mGalleryItemAdapter;
    private ArrayList<GalleryItem> mGalleryItems;
    private int                    mNextPageToLoad;
    private boolean                mIsUserScrolled;

    // Controls
    private Toolbar   mToolbar;
    private ImageView mImageViewSearch;
    private EditText  mEditTextSearch;
    private GridView mGridViewGallery;
    private MenuItem  mMenuItemPolling;
    private MenuItem  mMenuItemClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Data Members
        mGalleryItems   = new ArrayList<GalleryItem>();
        mNextPageToLoad = 1;
        mIsUserScrolled = false;

        // Controls
        mToolbar         = (Toolbar)findViewById(R.id.toolbarMain);
        mImageViewSearch = (ImageView)findViewById(R.id.imageViewSearch);
        mEditTextSearch  = (EditText)findViewById(R.id.editTextSearch);
        mGridViewGallery = (GridView)findViewById(R.id.gridViewGallery);

        // Events
        mImageViewSearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Init recent page
                mNextPageToLoad = 1;

                // If search box is not empty
                if (!mEditTextSearch.getText().toString().equals("")) {

                    // Stop the previous Polling
                    PollService.setServiceAlarm(MainActivity.this, false);

                    // Set menuItem title to "Start Polling" and Enable item
                    mMenuItemPolling.setTitle(R.string.menu_start_polling);
                    mMenuItemPolling.setEnabled(true);

                    // Enable Clear Search
                    mMenuItemClear.setEnabled(true);

                    // Save the search query for the Flickr-Search
                    SharedPreferencesHelper.save(Flickr.PREF_SEARCH_QUERY, mEditTextSearch.getText().toString());

                    // Turn On ActiveSearch flag
                    SharedPreferencesHelper.save(Flickr.PREF_IS_SEARCH_ACTIVE, true);

                    new FetchGalleryTask(MainActivity.this).execute(mNextPageToLoad);
                }
            }
        });


       mGridViewGallery.setOnScrollListener(new AbsListView.OnScrollListener() {

           @Override
           public void onScrollStateChanged(AbsListView view, int scrollState) {

               if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                   mIsUserScrolled = false;
               } else if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING ||
                        scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                   mIsUserScrolled = true;
               }
           }

           @Override
           public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

               // If SearchActive flag off and the user scrolled to the bottom
               if (mIsUserScrolled &&
                   firstVisibleItem + visibleItemCount >= totalItemCount &&
                   !SharedPreferencesHelper.loadBoolean(Flickr.PREF_IS_SEARCH_ACTIVE)) {

                   // Get the next page
                   new FetchGalleryTask(MainActivity.this).execute(mNextPageToLoad);

                   // Turn of flag
                   mIsUserScrolled = false;
               }
           }
       });


        // Load the previous search query
        mEditTextSearch.setText(SharedPreferencesHelper.loadString(Flickr.PREF_SEARCH_QUERY));

        // setup Gallery
        new FetchGalleryTask(MainActivity.this).execute(mNextPageToLoad);

        // Define toolbar as the ActionBar
        setSupportActionBar(mToolbar);
    }


    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate menu
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);

        // Get polling item
        mMenuItemPolling = menu.findItem(R.id.action_toggle_polling);
        mMenuItemClear   = menu.findItem(R.id.action_clear_search);

        // Search is not active
        if (!SharedPreferencesHelper.loadBoolean(Flickr.PREF_IS_SEARCH_ACTIVE)) {

            // Set menuItem title to "Start Polling" and disable item
            mMenuItemPolling.setEnabled(false);
            mMenuItemPolling.setTitle(R.string.menu_start_polling);

            // Disable "Clear Search"
            mMenuItemClear.setEnabled(false);
        }
        else {

            // Search active and polling started
            if (PollService.isServiceAlarmOn()) {

                mMenuItemPolling.setTitle(R.string.menu_stop_polling);
                PollService.setServiceAlarm(this, true);

            } else {

                mMenuItemPolling.setTitle(R.string.menu_start_polling);
                PollService.setServiceAlarm(this, false);

            }
        }

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())  {

            case (R.id.action_clear_search):

                menuSearchAction();

                return (true);

            case (R.id.action_toggle_polling):

                menuTogglePollingAction(item);

                return (true);

            default:

                return super.onOptionsItemSelected(item);
        }
    }

    /*
     * This function is a Callback for FetchGalleryTask.
     * Gets the new GalleryItems, set them to the GridView and scroll to the correct position.
     * This is the only place to handle mNextPageToLoad
     */
    @Override
    public void getGalleryItems(ArrayList<GalleryItem> galleryItems, boolean isSearchedOrCleared) {

        // Fetch - New page
        if (!isSearchedOrCleared) {

            // Add new Gallery Items to the main list
            mGalleryItems.addAll(galleryItems);

            // Next page to retrieve
            mNextPageToLoad++;

            // Scroll to the correct position
            mGridViewGallery.setSelection((mNextPageToLoad - 2) * 100);
        }
        else {

            // Search or clear occurred - init the gallery
            mGalleryItems.clear();
            mGalleryItems.addAll(galleryItems);

            // Scroll to the correct position
            mGridViewGallery.setSelection((mNextPageToLoad - 1) * 100 + 1);
        }

         if (mGalleryItemAdapter == null) {

             // Config adapter
             mGalleryItemAdapter = new GalleryItemAdapter(this, R.layout.gallery_item, this.mGalleryItems);

             // Set gridView Adapter
             mGridViewGallery.setAdapter(mGalleryItemAdapter);
          }
          else {

             mGalleryItemAdapter.notifyDataSetChanged();
         }
    }

    private void menuSearchAction() {

        mNextPageToLoad = 1;

        // Clear previous Search query
        SharedPreferencesHelper.save(Flickr.PREF_SEARCH_QUERY, "");

        // Turn Off SearchActive flag
        SharedPreferencesHelper.save(Flickr.PREF_IS_SEARCH_ACTIVE, false);

        // Turn On ClearAction flag
        SharedPreferencesHelper.save(Flickr.PREF_SHOULD_CLEAR, true);

        // Clear editText Search
        mEditTextSearch.setText("");

        // Get new Gallery items
        new FetchGalleryTask(MainActivity.this).execute(mNextPageToLoad);

        // Disable Polling and change item to "Start Polling" for next search action
        mMenuItemPolling.setTitle(R.string.menu_start_polling);
        mMenuItemPolling.setEnabled(false);

        // Disable Clear Search
        mMenuItemClear.setEnabled(false);
    }

    private void menuTogglePollingAction(MenuItem item) {

        // Polling already active
        if (PollService.isServiceAlarmOn()) {

            // Turn Off Polling
            PollService.setServiceAlarm(this, false);

            // Item title - Start Polling
            item.setTitle(R.string.menu_start_polling);

        } else {

            // Turn On Polling
            PollService.setServiceAlarm(this, true);

            // Item title - Stop Polling
            item.setTitle(R.string.menu_stop_polling);
        }
    }
}
