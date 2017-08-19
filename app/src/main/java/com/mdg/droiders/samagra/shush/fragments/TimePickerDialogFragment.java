package com.mdg.droiders.samagra.shush.fragments;

import android.app.Dialog;

import android.app.TimePickerDialog;
import android.os.Bundle;
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

    TimeSetCallback mCallback;

    @Override

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Calendar rightNow = Calendar.getInstance();

        return new TimePickerDialog(getActivity(), this,
                rightNow.get(Calendar.HOUR_OF_DAY),
                rightNow.get(Calendar.MINUTE),
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
}
