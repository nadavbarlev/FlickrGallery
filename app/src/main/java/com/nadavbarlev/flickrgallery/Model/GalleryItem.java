package com.nadavbarlev.flickrgallery.Model;

/*
 *  Class  - GalleryItem.java
 *  Author - Nadav Bar Lev
 *  Represent a Photo
 */
public class GalleryItem {

    // Data Members
    private String mCaption;
    private String mId;
    private String mUrl;
    private String mOwner;

    // Constructor
    public GalleryItem(String mCaption, String mId, String mUrl, String mOwner) {
        this.mCaption = mCaption;
        this.mId = mId;
        this.mUrl = mUrl;
        this.mOwner = mOwner;
    }

    // Getters
    public String getCaption() {
        return mCaption;
    }

    public String getId() {
        return mId;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getOwner() {
        return mOwner;
    }

    // Methods
    public String getPhotoPageUrl() {
        return "https://www.flickr.com/photos/" + getOwner() + "/" + getId();
    }

    public String toString() {
        return mCaption;
    }
}
