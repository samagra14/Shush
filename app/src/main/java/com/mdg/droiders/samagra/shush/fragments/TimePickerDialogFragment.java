package com.mdg.droiders.samagra.shush.fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by rohan on 19/8/17.
 * A fragment that displays a time picker dialog window,
 * floating on top of its activity's window.
 */
public class TimePickerDialogFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    public interface TimeSetCallback {
        void onTimeSet(int hour, int minute);
    }

    private TimeSetCallback mCallback;
    private Integer hourOfDay;
    private Integer minutes;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Calendar rightNow = Calendar.getInstance();
        if (hourOfDay == null) {
            hourOfDay = rightNow.get(Calendar.HOUR_OF_DAY);
        }
        if (minutes == null) {
            minutes = rightNow.get(Calendar.MINUTE);
        }

        return new TimePickerDialog(getActivity(), this,
                hourOfDay,
                minutes,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        if (mCallback != null) {
            mCallback.onTimeSet(hourOfDay, minute);
        }
    }

    public void setTimeSetCallback(TimeSetCallback mCallback) {
        this.mCallback = mCallback;
    }

    public void setTime(Calendar displayTime) {
        hourOfDay = displayTime.get(Calendar.HOUR_OF_DAY);
        minutes = displayTime.get(Calendar.MINUTE);
    }

}
