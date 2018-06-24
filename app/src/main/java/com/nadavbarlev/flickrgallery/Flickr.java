package com.nadavbarlev.flickrgallery;

import android.net.Uri;
import android.util.Log;
import com.nadavbarlev.flickrgallery.Model.GalleryItem;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import javax.net.ssl.HttpsURLConnection;

/*
 *  Class - Flickr.java
 *  Author - Nadav Bar Lev
 *  Manages the access to Flickr API
 */

public class Flickr {

    // Logs
    private static final String TAG = "Flickr";

    // Constants - Shared Preferences
    public static final String PREF_SEARCH_QUERY     = "searchQuery";
    public static final String PREF_LAST_RESULT_ID   = "lastResultId";
    public static final String PREF_IS_SEARCH_ACTIVE = "isSearchActive";
    public static final String PREF_SHOULD_CLEAR     = "shouldClear";

    // Constants - Local
    private static final String ENDPOINT           = "https://api.flickr.com/services/rest/";
    private static final String API_KEY            = "a9b8ab97cbc3a144ef8b4d3f4da2a528";
    private static final String METHOD_GET_RECENT  = "flickr.photos.getRecent";
    private static final String METHOD_SEARCH      = "flickr.photos.search";
    private static final String SMALL_URL          = "url_s";
    private static final String XML_TAG_PHOTO      = "photo";
    private static final String XML_TAG_ID         = "id";
    private static final String XML_TAG_TITLE      = "title";
    private static final String XML_TAG_OWNER      = "owner";

    /* Fetch all */
    public ArrayList<GalleryItem> fetch(String page) {

        // Create URL
        String url = Uri.parse(ENDPOINT).buildUpon()
                .appendQueryParameter("method", METHOD_GET_RECENT)
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("extras", SMALL_URL)
                .appendQueryParameter("page", page)
                .build().toString();

        return (getGallery(url));
    }

    /* Search query */
    public ArrayList<GalleryItem> search(String query) {

        // Create URL
        String url = Uri.parse(ENDPOINT).buildUpon()
                .appendQueryParameter("method", METHOD_SEARCH)
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("extras", SMALL_URL)
                .appendQueryParameter("text", query)
                .build().toString();

        return (getGallery(url));
    }

    /* Read the URL data and Return it as an ArrayList */
    public ArrayList<GalleryItem> getGallery(String url) {

        ArrayList<GalleryItem> galleryItems = null;

        try {

            // Gets data as String
            String xmlString = getUrl(url);

            if (xmlString == null)
                return null;

            galleryItems = new ArrayList<GalleryItem>();

            // Create a Xml Parser
            XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = xmlPullParserFactory.newPullParser();

            // Set data to parse
            xmlPullParser.setInput(new StringReader(xmlString));

            // Parse XML string into an ArrayList
            parseXmlToArray(galleryItems, xmlPullParser);

        } catch (XmlPullParserException xpe) {
            Log.e(TAG, "Parse failed", xpe);
        } catch (IOException ex) {
            Log.e(TAG, "Connection failed", ex);
        }

        return (galleryItems);
    }

    /* Download data from URL and return it as a String */
    String getUrl(String urlSpec) throws IOException {

        // Create an URL object
        URL url = new URL(urlSpec);

        // Open Connection
        HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();

        try {

            // Connection failed
            if (connection.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                return null;
            }

            // Gets data
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            InputStream inputStream = connection.getInputStream();
            int bytesRead = 0;
            byte[] buffer = new byte[1024];

            // Insert data to byteArrayOutputStream
            while ((bytesRead = inputStream.read(buffer)) > 0) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }

            // Close Stream
            byteArrayOutputStream.close();

            // Return data as String
            return (new String(byteArrayOutputStream.toByteArray()));

        } finally {

            connection.disconnect();
        }
    }

    /* Gets Xml string and Parse it to an ArrayList */
    void parseXmlToArray(ArrayList<GalleryItem> items, XmlPullParser parser)
            throws XmlPullParserException, IOException {

        int tagType = parser.next();

        // Pass over all Xml-String
        while (tagType != XmlPullParser.END_DOCUMENT) {

            // If this is the Start of GalleryItem Object
            if (tagType == XmlPullParser.START_TAG &&
                    XML_TAG_PHOTO.equals(parser.getName())) {
                String id = parser.getAttributeValue(null, XML_TAG_ID);
                String caption = parser.getAttributeValue(null, XML_TAG_TITLE);
                String smallUrl = parser.getAttributeValue(null, SMALL_URL);
                String owner = parser.getAttributeValue(null, XML_TAG_OWNER);

                // Add new GalleryItem to the list
                items.add(new GalleryItem(caption, id, smallUrl, owner));

            }

            tagType = parser.next();
        }
    }
}

