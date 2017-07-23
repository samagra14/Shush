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

    public static final int PLACES = 100;
    public static final int SINGLE_PLACE_WITH_ID = 101;
    //A member variable of the placesDbHelper to deal with the places.
    private PlacesDbHelper mPlacesDbHelper;

    public static final UriMatcher sUriMatcher = buildUriMatcher();
    private final String LOG_TAG = this.toString();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PlacesContract.AUTHORITY, PlacesContract.PATH, PLACES);
        uriMatcher.addURI(PlacesContract.AUTHORITY, PlacesContract.PATH + "/#", SINGLE_PLACE_WITH_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mPlacesDbHelper = new PlacesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mPlacesDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor retCursor;
        switch (match){
            case PLACES:
                retCursor = db.query(PlacesContract.PlaceEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Cannot access Uri : " + uri);

                }
        retCursor.setNotificationUri(getContext().getContentResolver(),uri);

        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = mPlacesDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match){
            case PLACES:
                long id = db.insert(PlacesContract.PlaceEntry.TABLE_NAME,null,contentValues);
                if (id>0){
                    returnUri = ContentUris.withAppendedId(uri,id);
                }
                else {
                    throw new SQLException("Unable to insert row into " + uri);
                }
                break;
            default:
                throw  new UnsupportedOperationException("Unknown Uri : "+ uri);

        }
        getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        final SQLiteDatabase db = mPlacesDbHelper.getWritableDatabase();
        int deletedRows;
        int match = sUriMatcher.match(uri);
        switch (match){
            case SINGLE_PLACE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                deletedRows = db.delete(PlacesContract.PlaceEntry.TABLE_NAME,"id=?",new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Invalid uri: "+ uri);

        }
        if (deletedRows>0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return deletedRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mPlacesDbHelper.getWritableDatabase();
        int affectedRows;
        int match = sUriMatcher.match(uri);
        switch (match){
            case SINGLE_PLACE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                affectedRows = db.update(PlacesContract.PlaceEntry.TABLE_NAME,contentValues,"_id=?",new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }
        if (affectedRows>0)
            getContext().getContentResolver().notifyChange(uri,null);
        return affectedRows;
    }
}
