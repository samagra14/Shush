package com.mdg.droiders.samagra.shush.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.mdg.droiders.samagra.shush.AlarmScheduler;
import com.mdg.droiders.samagra.shush.R;
import com.mdg.droiders.samagra.shush.data.PlacesContract;
import com.mdg.droiders.samagra.shush.fragments.TimePickerDialogFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by rohan on 19/8/17.
 * Adapter that binds data to the view for time entries in the Shush app.
 */
public class TimeListAdapter extends RecyclerView.Adapter<TimeListAdapter.TimeRowHolder> {

    private static final String DIALOG_TIME_TAG = "time_picker_dialog_tag";
    // The format which is used to display date in each row
    private static final SimpleDateFormat DISPLAY_DATE_FORMAT =
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
        return new TimeRowHolder(row);
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

            // Retrieving the whole row from the database.
            int id = timeDataCursor.getInt(
                    timeDataCursor.getColumnIndexOrThrow(PlacesContract.TimeEntry._ID)
            );
            String startTime = timeDataCursor.getString(
                    timeDataCursor.getColumnIndexOrThrow(PlacesContract.TimeEntry.COLUMN_START_TIME)
            );
            String endTime = timeDataCursor.getString(
                    timeDataCursor.getColumnIndexOrThrow(PlacesContract.TimeEntry.COLUMN_END_TIME)
            );
            int monday = timeDataCursor.getInt(
                    timeDataCursor.getColumnIndexOrThrow(PlacesContract.TimeEntry.COLUMN_MONDAY)
            );
            int tuesday = timeDataCursor.getInt(
                    timeDataCursor.getColumnIndexOrThrow(PlacesContract.TimeEntry.COLUMN_TUESDAY)
            );
            int wednesday = timeDataCursor.getInt(
                    timeDataCursor.getColumnIndexOrThrow(PlacesContract.TimeEntry.COLUMN_WEDNESDAY)
            );
            int thursday = timeDataCursor.getInt(
                    timeDataCursor.getColumnIndexOrThrow(PlacesContract.TimeEntry.COLUMN_THURSDAY)
            );
            int friday = timeDataCursor.getInt(
                    timeDataCursor.getColumnIndexOrThrow(PlacesContract.TimeEntry.COLUMN_FRIDAY)
            );
            int saturday = timeDataCursor.getInt(
                    timeDataCursor.getColumnIndexOrThrow(PlacesContract.TimeEntry.COLUMN_SATURDAY)
            );
            int sunday = timeDataCursor.getInt(
                    timeDataCursor.getColumnIndexOrThrow(PlacesContract.TimeEntry.COLUMN_SUNDAY)
            );
            // Updating the holder
            holder.id = id;
            holder.startTime.setText(startTime);
            holder.endTime.setText(endTime);
            holder.monday.setChecked(monday == 1);
            holder.tuesday.setChecked(tuesday == 1);
            holder.wednesday.setChecked(wednesday == 1);
            holder.thursday.setChecked(thursday == 1);
            holder.friday.setChecked(friday == 1);
            holder.saturday.setChecked(saturday == 1);
            holder.sunday.setChecked(sunday == 1);

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

    /*private boolean refreshData(TimeRowHolder holder) {

        ContentValues values = new ContentValues();
        String startTimeString = holder.startTime.getText().toString();
        String endTimeString = holder.endTime.getText().toString();
        values.put(PlacesContract.TimeEntry.COLUMN_START_TIME, startTimeString);
        values.put(PlacesContract.TimeEntry.COLUMN_END_TIME, endTimeString);
        values.put(PlacesContract.TimeEntry.COLUMN_MONDAY,
                holder.monday.isChecked() ? 1 : 0);
        values.put(PlacesContract.TimeEntry.COLUMN_TUESDAY,
                holder.tuesday.isChecked() ? 1 : 0);
        values.put(PlacesContract.TimeEntry.COLUMN_WEDNESDAY,
                holder.wednesday.isChecked() ? 1 : 0);
        values.put(PlacesContract.TimeEntry.COLUMN_THURSDAY,
                holder.thursday.isChecked() ? 1 : 0);
        values.put(PlacesContract.TimeEntry.COLUMN_FRIDAY,
                holder.friday.isChecked() ? 1 : 0);
        values.put(PlacesContract.TimeEntry.COLUMN_SATURDAY,
                holder.saturday.isChecked() ? 1 : 0);
        values.put(PlacesContract.TimeEntry.COLUMN_SUNDAY,
                holder.sunday.isChecked() ? 1 : 0);

        int affectedRows = mContext.getContentResolver().update(
                Uri.withAppendedPath(PlacesContract.TimeEntry.CONTENT_URI, String.valueOf(holder.id))
                , values, null, null
        );
        Calendar startTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();
        try {
            startTime.setTime(DISPLAY_DATE_FORMAT.parse(startTimeString));
            endTime.setTime(DISPLAY_DATE_FORMAT.parse(endTimeString));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        closeCursor();
        refreshCursor();
        alarmScheduler.setWeeklyAlarm(startTime, endTime, getDayArr(holder), holder.id);
        return affectedRows == 1;
    }*/

    /**
     * Updates db and reschedules alarm if startTime or endTime changes.
     *
     * @param holder The holder instance that is currently bound to the row whose
     *               time is changed.
     */
    private void notifyTimeChanged(TimeRowHolder holder) {
        ContentValues values = new ContentValues();
        String startTimeString = holder.startTime.getText().toString();
        String endTimeString = holder.endTime.getText().toString();
        values.put(PlacesContract.TimeEntry.COLUMN_START_TIME, startTimeString);
        values.put(PlacesContract.TimeEntry.COLUMN_END_TIME, endTimeString);

        mContext.getContentResolver().update(
                Uri.withAppendedPath(PlacesContract.TimeEntry.CONTENT_URI, String.valueOf(holder.id))
                , values, null, null
        );

        Calendar startTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();
        setTimesFromHolder(holder, startTime, endTime);
        closeCursor();
        refreshCursor();
        alarmScheduler.setWeeklyAlarm(startTime, endTime, getDayArr(holder), holder.id);
    }

    /**
     * Updates db and sets alarm for the corresponding day.
     *
     * @param holder The holder instance that is currently bound to the row whose
     *               alarm is set.
     * @param day    The day for which alarm is set. The day is zero indexed and starts from monday.
     */
    private void notifyDayAlarmSet(TimeRowHolder holder, int day) {

        ContentValues values = new ContentValues();
        values.put(getColumnFromDay(day), true);
        mContext.getContentResolver().update(
                Uri.withAppendedPath(PlacesContract.TimeEntry.CONTENT_URI, String.valueOf(holder.id))
                , values, null, null
        );

        Calendar startTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();
        setTimesFromHolder(holder, startTime, endTime);
        alarmScheduler.setSingleDayAlarm(startTime, endTime, day, holder.id);
    }

    /**
     * Updates db and cancels alarm for the corresponding day.
     *
     * @param holder The holder instance that is currently bound to the row whose
     *               alarm is cancelled.
     * @param day    The day for which alarm is cancelled. The day is zero indexed and starts from monday.
     */
    private void notifyDayAlarmCancelled(TimeRowHolder holder, int day) {

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
        return new boolean[]{
                holder.monday.isChecked(),
                holder.tuesday.isChecked(),
                holder.wednesday.isChecked(),
                holder.thursday.isChecked(),
                holder.friday.isChecked(),
                holder.saturday.isChecked(),
                holder.sunday.isChecked()
        };
    }

    class TimeRowHolder extends RecyclerView.ViewHolder {

        public int id;
        View itemView;
        TextView startTime;
        TextView endTime;
        Switch enableSwitch;
        CheckBox monday;
        CheckBox tuesday;
        CheckBox wednesday;
        CheckBox thursday;
        CheckBox friday;
        CheckBox saturday;
        CheckBox sunday;

        /**
         * Listeners attached to the holder will only react to events
         * if the a view is bound to the current holder.
         */
        boolean isBound;

        TimePickerDialogFragment timePickerDialog;

        TimeRowHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            id = -1;
            startTime = itemView.findViewById(R.id.start_time);
            endTime = itemView.findViewById(R.id.end_time);
            enableSwitch = itemView.findViewById(R.id.enable_switch);
            monday = itemView.findViewById(R.id.monday);
            tuesday = itemView.findViewById(R.id.tuesday);
            wednesday = itemView.findViewById(R.id.wednesday);
            thursday = itemView.findViewById(R.id.thursday);
            friday = itemView.findViewById(R.id.friday);
            saturday = itemView.findViewById(R.id.saturday);
            sunday = itemView.findViewById(R.id.sunday);
            timePickerDialog = new TimePickerDialogFragment();
            View.OnClickListener timeChangeListener = new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    if (!isBound) {
                        return;
                    }
                    timePickerDialog.setTimeSetCallback(new TimePickerDialogFragment.TimeSetCallback() {
                        @Override
                        public void onTimeSet(int hour, int minute) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.HOUR_OF_DAY, hour);
                            calendar.set(Calendar.MINUTE, minute);
                            ((TextView) view).setText(DISPLAY_DATE_FORMAT.format(calendar.getTime()));
                            notifyTimeChanged(TimeRowHolder.this);
                        }
                    });
                    Calendar displayedTime = Calendar.getInstance();
                    try {
                        displayedTime.setTime(
                                DISPLAY_DATE_FORMAT.parse(((TextView) view).getText().toString()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    timePickerDialog.setTime(displayedTime);
                    timePickerDialog.show(
                            ((AppCompatActivity) mContext).getSupportFragmentManager(),
                            DIALOG_TIME_TAG
                    );
                }
            };
            startTime.setOnClickListener(timeChangeListener);
            endTime.setOnClickListener(timeChangeListener);
            monday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    if (!isBound) {
                        return;
                    }
                    if (checked) {
                        notifyDayAlarmSet(TimeRowHolder.this, 0);
                    } else {
                        notifyDayAlarmCancelled(TimeRowHolder.this, 0);
                    }
                }
            });
            tuesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    if (!isBound) {
                        return;
                    }
                    if (checked) {
                        notifyDayAlarmSet(TimeRowHolder.this, 1);
                    } else {
                        notifyDayAlarmCancelled(TimeRowHolder.this, 1);
                    }
                }
            });
            wednesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    if (!isBound) {
                        return;
                    }
                    if (checked) {
                        notifyDayAlarmSet(TimeRowHolder.this, 2);
                    } else {
                        notifyDayAlarmCancelled(TimeRowHolder.this, 2);
                    }
                }
            });
            thursday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    if (!isBound) {
                        return;
                    }
                    if (checked) {
                        notifyDayAlarmSet(TimeRowHolder.this, 3);
                    } else {
                        notifyDayAlarmCancelled(TimeRowHolder.this, 3);
                    }
                }
            });
            friday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    if (!isBound) {
                        return;
                    }
                    if (checked) {
                        notifyDayAlarmSet(TimeRowHolder.this, 4);
                    } else {
                        notifyDayAlarmCancelled(TimeRowHolder.this, 4);
                    }
                }
            });
            saturday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    if (!isBound) {
                        return;
                    }
                    if (checked) {
                        notifyDayAlarmSet(TimeRowHolder.this, 5);
                    } else {
                        notifyDayAlarmCancelled(TimeRowHolder.this, 5);
                    }
                }
            });
            sunday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    if (!isBound) {
                        return;
                    }
                    if (checked) {
                        notifyDayAlarmSet(TimeRowHolder.this, 6);
                    } else {
                        notifyDayAlarmCancelled(TimeRowHolder.this, 6);
                    }
                }
            });
        }
    }
}
