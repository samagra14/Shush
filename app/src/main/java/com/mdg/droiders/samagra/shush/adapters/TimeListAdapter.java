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
        holder.itemView.setVisibility(View.VISIBLE);
        if (timeDataCursor.moveToPosition(position)) {
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
        }

    }

    @Override
    public int getItemCount() {
        return timeDataCursor.getCount() + 1;
    }

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

    public void closeCursor() {
        timeDataCursor.close();
    }

    private boolean refreshData(TimeRowHolder holder) {

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
    }

    public void refreshCursor() {
        timeDataCursor = mContext.getContentResolver().query(
                PlacesContract.TimeEntry.CONTENT_URI, null, null, null, null);
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
                    timePickerDialog.setTimeSetCallback(new TimePickerDialogFragment.TimeSetCallback() {
                        @Override
                        public void onTimeSet(int hour, int minute) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.HOUR_OF_DAY, hour);
                            calendar.set(Calendar.MINUTE, minute);
                            ((TextView) view).setText(DISPLAY_DATE_FORMAT.format(calendar.getTime()));
                            refreshData(TimeRowHolder.this);
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
            CompoundButton.OnCheckedChangeListener dayCheckChangeListener
                    = new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    refreshData(TimeRowHolder.this);
                }
            };
            startTime.setOnClickListener(timeChangeListener);
            endTime.setOnClickListener(timeChangeListener);
            monday.setOnCheckedChangeListener(dayCheckChangeListener);
            tuesday.setOnCheckedChangeListener(dayCheckChangeListener);
            wednesday.setOnCheckedChangeListener(dayCheckChangeListener);
            thursday.setOnCheckedChangeListener(dayCheckChangeListener);
            friday.setOnCheckedChangeListener(dayCheckChangeListener);
            saturday.setOnCheckedChangeListener(dayCheckChangeListener);
            sunday.setOnCheckedChangeListener(dayCheckChangeListener);
        }
    }
}
