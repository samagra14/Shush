package com.mdg.droiders.samagra.shush.data;

import android.provider.BaseColumns;

/**
 * Created by samagra on 20/7/17.
 * A contract class for places selected by the user.
 */

public class PlacesContract {

    // Contract class for a single place entry
    public static final class PlaceEntry implements BaseColumns{
        public static final String TABLE_NAME = "places";
        public static final String COLUMN_PLACE_ID = "placeID";
    }
}
