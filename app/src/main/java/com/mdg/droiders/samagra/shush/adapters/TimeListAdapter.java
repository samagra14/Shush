package com.mdg.droiders.samagra.shush.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.mdg.droiders.samagra.shush.R;
import com.mdg.droiders.samagra.shush.data.PlacesContract;
import com.mdg.droiders.samagra.shush.fragments.TimePickerDialogFragment;

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

    private Context mContext;
    private Cursor timeDataCursor;

    public TimeListAdapter(Context mContext, Cursor timeDataCursor) {
        this.mContext = mContext;
        this.timeDataCursor = timeDataCursor;
         /*=mContext.getContentResolver().query(
                PlacesContract.TimeEntry.CONTENT_URI,
                null,
                null,
                null,
                null);*/
    }

    @Override
    public TimeRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext)
                .inflate(R.layout.time_picker_row, parent, false);
        return new TimeRowHolder(row);
    }

    @Override
    public void onBindViewHolder(TimeRowHolder holder, int position) {
        if (timeDataCursor.getCount() == 0) {
            return;
        }
        if (position == timeDataCursor.getCount()) {
            holder.startTime.setVisibility(View.INVISIBLE);
            holder.endTime.setVisibility(View.INVISIBLE);
            holder.monday.setVisibility(View.INVISIBLE);
            holder.tuesday.setVisibility(View.INVISIBLE);
            holder.wednesday.setVisibility(View.INVISIBLE);
            holder.thursday.setVisibility(View.INVISIBLE);
            holder.friday.setVisibility(View.INVISIBLE);
            holder.saturday.setVisibility(View.INVISIBLE);
            holder.sunday.setVisibility(View.INVISIBLE);
            return;
        }
        if (timeDataCursor.moveToPosition(position)) {
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
            holder.id = timeDataCursor.getColumnIndexOrThrow(PlacesContract.TimeEntry._ID);
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

    private boolean refreshData(TimeRowHolder holder) {

        ContentValues values = new ContentValues();
        values.put(PlacesContract.TimeEntry.COLUMN_START_TIME,
                holder.startTime.getText().toString());
        values.put(PlacesContract.TimeEntry.COLUMN_END_TIME,
                holder.endTime.getText().toString());
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

        String selection = PlacesContract.TimeEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(holder.id)};
        int affectedRows = mContext.getContentResolver().update(
                PlacesContract.TimeEntry.CONTENT_URI, values, selection, selectionArgs
        );
        return affectedRows == 1;
    }

    class TimeRowHolder extends RecyclerView.ViewHolder {

        public int id;
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

        TimeRowHolder(View itemView) {
            super(itemView);
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
            View.OnClickListener timeChangeListener = new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    TimePickerDialogFragment timePickerDialog = new TimePickerDialogFragment();
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
                    timePickerDialog.show(
                            ((AppCompatActivity) mContext).getSupportFragmentManager(),
                            DIALOG_TIME_TAG);
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
