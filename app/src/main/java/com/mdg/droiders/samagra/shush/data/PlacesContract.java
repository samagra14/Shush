package com.mdg.droiders.samagra.shush.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by samagra on 20/7/17.
 * A contract class for places selected by the user.
 */

public class PlacesContract {

    //The authority which is how our code knows which content provider to access.
    public static final String AUTHORITY = "com.mdg.droiders.samagra.shush";

    //the base content uri.
    public static final Uri BASE_CONTENT_URI =Uri.parse("content://"+AUTHORITY);

    //Possible paths which can be accessed by this uri
    //This is the path for the places table.
    public static final String PATH = "places";

    // Contract class for a single place entry
    public static final class PlaceEntry implements BaseColumns{

        //Place entry content uri.
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();
        //Table constants.
        public static final String TABLE_NAME = "places";
        public static final String COLUMN_PLACE_ID = "placeID";
    }
}
