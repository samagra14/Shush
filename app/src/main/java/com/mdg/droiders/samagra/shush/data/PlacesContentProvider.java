package com.mdg.droiders.samagra.shush.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by samagra on 23/7/17.
 * A content provider to access the local data stored in our class.
 */

public class PlacesContentProvider extends ContentProvider {


    // Define final integer constants for the directory of places and a single item.
    // It's convention to use 100, 200, 300, etc for directories,
    // and related ints (101, 102, ..) for items in that directory.
    public static final int PLACES = 100;
    public static final int SINGLE_PLACE_WITH_ID = 101;

    //A member variable of the placesDbHelper to deal with the places initialised in the onCreate.
    private PlacesDbHelper mPlacesDbHelper;

    // Declare a static variable for the Uri matcher that you construct
    public static final UriMatcher sUriMatcher = buildUriMatcher();
    private final String LOG_TAG = this.toString();//log tag for logging messages

    /**
     * A static method to construct a UriMatcher
     * @return a {@link UriMatcher}
     */
    public static UriMatcher buildUriMatcher() {
        //initialise a uri matcher
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        //add uris for detection
        uriMatcher.addURI(PlacesContract.AUTHORITY, PlacesContract.PATH, PLACES);
        uriMatcher.addURI(PlacesContract.AUTHORITY, PlacesContract.PATH + "/#", SINGLE_PLACE_WITH_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mPlacesDbHelper = new PlacesDbHelper(getContext());
        return true;
    }

    /**
     * Handles requests to query data by uri.
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return A cursor object
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        //Get access to underlying database (read only for query)
        final SQLiteDatabase db = mPlacesDbHelper.getReadableDatabase();

        //Add a variable for uri matching and a cursor that can be returned
        int match = sUriMatcher.match(uri);
        Cursor retCursor;


        switch (match){
            //query for the places directory.
            case PLACES:
                retCursor = db.query(PlacesContract.PlaceEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            //default exception
            default:
                throw new UnsupportedOperationException("Cannot access Uri : " + uri);

                }

        //Set a notification uri to the cursor so that it gets updated
        // id there is any change in the uri and return it.
        retCursor.setNotificationUri(getContext().getContentResolver(),uri);

        return retCursor;
    }

    /**
     *
     * @param uri
     * @return
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    /**
     * Handles request to insert a single new row of data.
     * @param uri
     * @param contentValues
     * @return
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        //Get access to underlying database
        final SQLiteDatabase db = mPlacesDbHelper.getWritableDatabase();

        //Add a variable for uri matching and a uri for the new inserted row that can be returned
        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match){
            case PLACES:
                //insert new values to the database
                long id = db.insert(PlacesContract.PlaceEntry.TABLE_NAME,null,contentValues);
                if (id>0){
                    returnUri = ContentUris.withAppendedId(uri,id);
                }
                else {
                    throw new SQLException("Unable to insert row into " + uri);
                }
                break;
            //default case that throws exception
            default:
                throw  new UnsupportedOperationException("Unknown Uri : "+ uri);

        }

        // Notify the resolver if the uri has been changed, and return the newly inserted URI
        getContext().getContentResolver().notifyChange(uri,null);

        // Return constructed uri (this points to the newly inserted row of data)
        return returnUri;
    }

    /**
     * Deletes a single row of data.
     * @param uri
     * @param s
     * @param strings
     * @return
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {

        // Get access to the database and write URI matching code to recognize a single item
        final SQLiteDatabase db = mPlacesDbHelper.getWritableDatabase();
        int deletedRows;
        int match = sUriMatcher.match(uri);// Keep track of the number of deleted places
        switch (match){
            // Handle the single item case, recognized by the ID included in the URI path
            case SINGLE_PLACE_WITH_ID:
                // Get the place ID from the URI path
                String id = uri.getPathSegments().get(1);
                // Use selections/selectionArgs to filter for this ID
                deletedRows = db.delete(PlacesContract.PlaceEntry.TABLE_NAME,"id=?",new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Invalid uri: "+ uri);

        }
        // Notify the resolver of a change and return the number of items deleted
        if (deletedRows>0){
            // A place (or more) was deleted, set notification
            getContext().getContentResolver().notifyChange(uri,null);
        }
        // Return the number of places deleted
        return deletedRows;
    }

    /**
     * updates a single row of data.
     * @param uri
     * @param contentValues
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {

        // Get access to underlying database
        final SQLiteDatabase db = mPlacesDbHelper.getWritableDatabase();
        int affectedRows;        // Keep track of the number of updated places

        int match = sUriMatcher.match(uri);
        switch (match){
            case SINGLE_PLACE_WITH_ID:
                // Get the place ID from the URI path
                String id = uri.getPathSegments().get(1);
                // Use selections/selectionArgs to filter for this ID
                affectedRows = db.update(PlacesContract.PlaceEntry.TABLE_NAME,contentValues,"_id=?",new String[]{id});
                break;
            // Default exception
            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }
        // Notify the resolver of a change and return the number of items updated
        if (affectedRows>0)
            // A place (or more) was updated, set notification
            getContext().getContentResolver().notifyChange(uri,null);

        // Return the number of places deleted
        return affectedRows;
    }
}
