package com.mdg.droiders.samagra.shush.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by samagra on 20/7/17.
 * A database helper class for entry and creation of the places databases.
 */

public class PlacesDbHelper extends SQLiteOpenHelper {

    //Database Name
    private static final String DATABASE_NAME = "shush.db";

    //increment this while changing the dtabase schema
    private static final int DATABASE_VERSION = 1;

    //Constructor
    public PlacesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String CREATE_PLACES_TABLE = "CREATE TABLE " + PlacesContract.PlaceEntry.TABLE_NAME + " ("+
        PlacesContract.PlaceEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PlacesContract.PlaceEntry.COLUMN_PLACE_ID + " TEXT NOT NULL, " +
                "UNIQUE (" + PlacesContract.PlaceEntry.COLUMN_PLACE_ID + ") ON CONFLICT REPLACE" +
                "); ";
        final String CREATE_TIME_TABLE = "CREATE TABLE " + PlacesContract.TimeEntry.TABLE_NAME + " ("+
                PlacesContract.TimeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PlacesContract.TimeEntry.COLUMN_START_TIME + " INTEGER NOT NULL, " +
                PlacesContract.TimeEntry.COLUMN_END_TIME + " INTEGER, " +
                PlacesContract.TimeEntry.COLUMN_TUESDAY + " INTEGER NOT NULL, " +
                PlacesContract.TimeEntry.COLUMN_WEDNESDAY + " INTEGER NOT NULL, " +
                PlacesContract.TimeEntry.COLUMN_THURSDAY + " INTEGER NOT NULL, " +
                PlacesContract.TimeEntry.COLUMN_FRIDAY + " INTEGER NOT NULL, " +
                PlacesContract.TimeEntry.COLUMN_SATURDAY + " INTEGER NOT NULL, " +
                PlacesContract.TimeEntry.COLUMN_SUNDAY + " INTEGER NOT NULL, " +
                PlacesContract.TimeEntry.COLUMN_MONDAY + " INTEGER NOT NULL " +
                "); ";

        sqLiteDatabase.execSQL(CREATE_TIME_TABLE);
        sqLiteDatabase.execSQL(CREATE_PLACES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //Simply drop the table and create a new table
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PlacesContract.PlaceEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PlacesContract.TimeEntry.TABLE_NAME);

        //create a new table
        onCreate(sqLiteDatabase);

    }
}
