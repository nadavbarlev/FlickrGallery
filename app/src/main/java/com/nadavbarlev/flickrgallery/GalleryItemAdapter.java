package com.nadavbarlev.flickrgallery;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.nadavbarlev.flickrgallery.Model.GalleryItem;
import com.nadavbarlev.flickrgallery.Utils.SquareImageView;
import com.nadavbarlev.flickrgallery.Utils.UniversalImageLoader;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.util.ArrayList;

/*
 *  Class  - GalleryItemAdapter.java
 *  Author - Nadav Bar Lev
 *  Gallery adapter
 */

public class GalleryItemAdapter extends ArrayAdapter<GalleryItem> {

    // Logs
    private static final String TAG = "GalleryItemAdapter";

    // Data Members
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private int mResourceID;

    // Constructor
    public GalleryItemAdapter(Context context, int resource, ArrayList<GalleryItem> items) {
        super(context, resource, items);

        // Members
        this.mContext = context;
        this.mResourceID = resource;
        this.mLayoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        // Config UniversalImageLoader
        ImageLoader.getInstance().init(new UniversalImageLoader(context).getConfig());
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        RelativeLayout relativeLayoutMain = (RelativeLayout) mLayoutInflater.inflate(mResourceID, parent, false);

        // Controls
        ImageView imageView = (SquareImageView) relativeLayoutMain.findViewById(R.id.imageViewImage);
        ProgressBar progressBar = (ProgressBar) relativeLayoutMain.findViewById(R.id.progressBarImage);

        // Get current Item
        GalleryItem currGalleryItem = getItem(position);

        // Events
        imageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                // Display current photo on a full screen
                Intent intentDisplayPhotoGallery = new Intent(mContext, DisplayPhotoActivity.class);
                intentDisplayPhotoGallery.putExtra("photoURL", getItem(position).getPhotoPageUrl());
                mContext.startActivity(intentDisplayPhotoGallery);
            }
        });

        UniversalImageLoader.setImage(currGalleryItem.getUrl(),
                                      imageView,
                                      progressBar, "");

        return (relativeLayoutMain);

    }
}