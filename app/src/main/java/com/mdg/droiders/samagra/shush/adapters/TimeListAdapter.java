package com.mdg.droiders.samagra.shush.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mdg.droiders.samagra.shush.AlarmScheduler;
import com.mdg.droiders.samagra.shush.R;
import com.mdg.droiders.samagra.shush.data.PlacesContract;
import com.mdg.droiders.samagra.shush.interfaces.TimeAdapterNotifier;
import com.mdg.droiders.samagra.shush.viewholders.TimeRowHolder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by rohan on 19/8/17.
 * Adapter that binds data to the view for time entries in the Shush app.
 */
public class TimeListAdapter extends RecyclerView.Adapter<TimeRowHolder>
        implements TimeAdapterNotifier {

    // The format which is used to display date in each row
    public static final SimpleDateFormat DISPLAY_DATE_FORMAT =
            new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
    private static final String LOG_TAG = "Samagra/TLA/";

    private Context mContext;
    private Cursor timeDataCursor;
    private AlarmScheduler alarmScheduler;

    public TimeListAdapter(Context mContext) {
        this.mContext = mContext;
        refreshCursor();
        alarmScheduler = new AlarmScheduler(mContext);
    }

    @Override
    public TimeRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext)
                .inflate(R.layout.time_picker_row, parent, false);
        return new TimeRowHolder(row, this, mContext);
    }

    @Override
    public void onBindViewHolder(TimeRowHolder holder, int position) {
        if (position == timeDataCursor.getCount()) {
            holder.itemView.setVisibility(View.INVISIBLE);
            return;
        }
        // isBound set to false so that listeners do not react to set events in this method.
        holder.isBound = false;
        holder.itemView.setVisibility(View.VISIBLE);
        if (timeDataCursor.moveToPosition(position)) {

            // Retrieving the whole row from the database and Updating the holder
            holder.id = timeDataCursor.getInt(
                    timeDataCursor.getColumnIndexOrThrow(PlacesContract.TimeEntry._ID)
            );
            holder.startTime.setText(timeDataCursor.getString(
                    timeDataCursor.getColumnIndexOrThrow(PlacesContract.TimeEntry.COLUMN_START_TIME)
            ));
            holder.endTime.setText(timeDataCursor.getString(
                    timeDataCursor.getColumnIndexOrThrow(PlacesContract.TimeEntry.COLUMN_END_TIME)
            ));
            for (int i = 0; i < 7; i++) {
                int day = timeDataCursor.getInt(
                        timeDataCursor.getColumnIndexOrThrow(getColumnFromDay(i))
                );
                holder.days[i].setChecked(day == 1);
            }

            // All the set events have occurred, now we can again start listening.
            holder.isBound = true;
        }

    }

    @Override
    public int getItemCount() {
        // An extra item is to make space for the fab
        return timeDataCursor.getCount() + 1;
    }

    /**
     * Adds a new row in the recycler as well as in the db with some default values.
     */
    public void addItem() {
        Calendar calendar = Calendar.getInstance();
        ContentValues values = new ContentValues();
        values.put(PlacesContract.TimeEntry.COLUMN_START_TIME,
                DISPLAY_DATE_FORMAT.format(calendar.getTime()));
        values.put(PlacesContract.TimeEntry.COLUMN_END_TIME,
                DISPLAY_DATE_FORMAT.format(calendar.getTime()));
        values.put(PlacesContract.TimeEntry.COLUMN_MONDAY, 0);
        values.put(PlacesContract.TimeEntry.COLUMN_TUESDAY, 0);
        values.put(PlacesContract.TimeEntry.COLUMN_WEDNESDAY, 0);
        values.put(PlacesContract.TimeEntry.COLUMN_THURSDAY, 0);
        values.put(PlacesContract.TimeEntry.COLUMN_FRIDAY, 0);
        values.put(PlacesContract.TimeEntry.COLUMN_SATURDAY, 0);
        values.put(PlacesContract.TimeEntry.COLUMN_SUNDAY, 0);
        mContext.getContentResolver().insert(
                PlacesContract.TimeEntry.CONTENT_URI,
                values
        );
        closeCursor();
        refreshCursor();
        notifyItemInserted(timeDataCursor.getCount() - 1);
    }

    /**
     * Make a query to the database to get the updated result in the local cursor instance.
     */
    public void refreshCursor() {
        timeDataCursor = mContext.getContentResolver().query(
                PlacesContract.TimeEntry.CONTENT_URI, null, null, null, null);
    }

    /**
     * Close the cursor to release its resources.
     * The cursor should be closed in onStop() and refreshed in onResume().
     */
    public void closeCursor() {
        timeDataCursor.close();
    }

    @Override
    public void notifyTimeChanged(TimeRowHolder holder) {
        ContentValues values = new ContentValues();
        String startTimeString = holder.startTime.getText().toString();
        String endTimeString = holder.endTime.getText().toString();
        values.put(PlacesContract.TimeEntry.COLUMN_START_TIME, startTimeString);
        values.put(PlacesContract.TimeEntry.COLUMN_END_TIME, endTimeString);

        mContext.getContentResolver().update(
                Uri.withAppendedPath(PlacesContract.TimeEntry.CONTENT_URI,
                        String.valueOf(holder.id)), values, null, null
        );

        Calendar startTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();
        setTimesFromHolder(holder, startTime, endTime);
        closeCursor();
        refreshCursor();
        alarmScheduler.setWeeklyAlarm(startTime, endTime, getDayArr(holder), holder.id);
    }

    @Override
    public void notifyDayAlarmSet(TimeRowHolder holder, int day) {

        ContentValues values = new ContentValues();
        values.put(getColumnFromDay(day), true);
        mContext.getContentResolver().update(
                Uri.withAppendedPath(
                        PlacesContract.TimeEntry.CONTENT_URI, String.valueOf(holder.id)
                ), values, null, null
        );

        Calendar startTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();
        setTimesFromHolder(holder, startTime, endTime);
        alarmScheduler.setSingleDayAlarm(startTime, endTime, day, holder.id);
    }

    @Override
    public void notifyDayAlarmCancelled(TimeRowHolder holder, int day) {

        ContentValues values = new ContentValues();
        values.put(getColumnFromDay(day), false);
        mContext.getContentResolver().update(
                Uri.withAppendedPath(PlacesContract.TimeEntry.CONTENT_URI, String.valueOf(holder.id))
                , values, null, null
        );

        Calendar startTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();
        setTimesFromHolder(holder, startTime, endTime);
        alarmScheduler.cancelSingleAlarm(day, holder.id);
    }

    /**
     * Returns the corresponding column of the day in db.
     *
     * @param day The day for which the column is to be returned
     * @return The column corresponding to the given day
     */
    private String getColumnFromDay(int day) {
        switch (day) {
            case 0: {
                return PlacesContract.TimeEntry.COLUMN_MONDAY;
            }
            case 1: {
                return PlacesContract.TimeEntry.COLUMN_TUESDAY;
            }
            case 2: {
                return PlacesContract.TimeEntry.COLUMN_WEDNESDAY;
            }
            case 3: {
                return PlacesContract.TimeEntry.COLUMN_THURSDAY;
            }
            case 4: {
                return PlacesContract.TimeEntry.COLUMN_FRIDAY;
            }
            case 5: {
                return PlacesContract.TimeEntry.COLUMN_SATURDAY;
            }
            case 6: {
                return PlacesContract.TimeEntry.COLUMN_SUNDAY;
            }
            default: {
                return null;
            }
        }
    }

    private void setTimesFromHolder(TimeRowHolder holder, Calendar startTime, Calendar endTime) {
        String startTimeString = holder.startTime.getText().toString();
        String endTimeString = holder.endTime.getText().toString();
        try {
            startTime.setTime(DISPLAY_DATE_FORMAT.parse(startTimeString));
            endTime.setTime(DISPLAY_DATE_FORMAT.parse(endTimeString));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private boolean[] getDayArr(TimeRowHolder holder) {
        boolean[] isAlarmSetOnDay = new boolean[7];
        for (int i = 0; i < 7; i++) {
            isAlarmSetOnDay[i] = holder.days[i].isChecked();
        }
        return isAlarmSetOnDay;
    }

}
