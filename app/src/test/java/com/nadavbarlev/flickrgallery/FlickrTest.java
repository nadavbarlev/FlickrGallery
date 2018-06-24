package com.nadavbarlev.flickrgallery;

import com.nadavbarlev.flickrgallery.Model.GalleryItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import java.util.ArrayList;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class FlickrTest {

    /* Fetch Segment */

    @Test
    public void fetch_smallPage() {

        String pageToGet      = "1";
        int    outputExpected = 100;
        int    outputReceived;

        ArrayList<GalleryItem> galleryItems =
                new Flickr().fetch(pageToGet);
        outputReceived = galleryItems.size();

        assertEquals(outputExpected, outputReceived);
    }

    @Test
    public void fetch_bigPage() {

        String pageToGet      = "100000";
        int    outputExpected = 100;
        int    outputReceived;

        ArrayList<GalleryItem> galleryItems =
                new Flickr().fetch(pageToGet);
        outputReceived = galleryItems.size();

        assertEquals(outputExpected, outputReceived);
    }

    /* Search Segment */

    @Test
    public void search_existQuery() {

        String queryToSearch    = "Cat";
        int    outputUnexpected = 0;
        int    outputReceived;

        ArrayList<GalleryItem> galleryItems =
                new Flickr().search(queryToSearch);
        outputReceived = galleryItems.size();

        assertNotEquals(outputUnexpected, outputReceived);
    }

    @Test
    public void search_notExistQuery() {

        String queryToSearch  = "0";
        int    outputExpected = 0;
        int    outputReceived;

        ArrayList<GalleryItem> galleryItems =
                new Flickr().search(queryToSearch);
        outputReceived = galleryItems.size();

        assertEquals(outputExpected, outputReceived);
    }

    /* GetGallery Segment */

    @Test
    public void getGallery_legalURL() {

        String URL =
                "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=a9b8ab97cbc3a144ef8b4d3f4da2a528&extras=url_s&text=0";

        ArrayList<GalleryItem> galleryItems =
                new Flickr().getGallery(URL);

        assertNotNull(galleryItems);

    }

    @Test
    public void getGallery_illegalURL() {

        String URL            = "";
        int    outputExpected = 0;
        int    outputReceived;

        ArrayList<GalleryItem> galleryItems =
                new Flickr().getGallery(URL);

        outputReceived = galleryItems.size();

        assertEquals(outputExpected, outputReceived);

    }
}