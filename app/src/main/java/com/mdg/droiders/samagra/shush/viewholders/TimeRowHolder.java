package com.mdg.droiders.samagra.shush.viewholders;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.mdg.droiders.samagra.shush.R;
import com.mdg.droiders.samagra.shush.adapters.TimeListAdapter;
import com.mdg.droiders.samagra.shush.fragments.TimePickerDialogFragment;
import com.mdg.droiders.samagra.shush.interfaces.TimeAdapterNotifier;

import java.text.ParseException;
import java.util.Calendar;

/**
 * Created by rohan on 27/11/17.
 * The {@link RecyclerView.ViewHolder ViewHolder} for the row which
 * shows if a shush alarm is scheduled in the app.
 */
public class TimeRowHolder extends RecyclerView.ViewHolder {

    private static final String DIALOG_TIME_TAG = "time_picker_dialog_tag";

    public int id;
    public View itemView;
    public TextView startTime;
    public TextView endTime;
    public Switch enableSwitch;
    public CheckBox[] days;

    /**
     * Listeners attached to the holder will only react to events
     * if the a view is bound to the current holder.
     */
    public boolean isBound;

    private TimePickerDialogFragment timePickerDialog;

    public TimeRowHolder(View itemView, final TimeAdapterNotifier notifier,
                         final Context mContext) {
        super(itemView);
        this.itemView = itemView;
        id = -1;
        days = new CheckBox[]{
                itemView.findViewById(R.id.monday),
                itemView.findViewById(R.id.tuesday),
                itemView.findViewById(R.id.wednesday),
                itemView.findViewById(R.id.thursday),
                itemView.findViewById(R.id.friday),
                itemView.findViewById(R.id.saturday),
                itemView.findViewById(R.id.sunday)
        };
        startTime = itemView.findViewById(R.id.start_time);
        endTime = itemView.findViewById(R.id.end_time);
        enableSwitch = itemView.findViewById(R.id.enable_switch);
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
                        ((TextView) view).setText(
                                TimeListAdapter.DISPLAY_DATE_FORMAT.format(calendar.getTime()));
                        notifier.notifyTimeChanged(TimeRowHolder.this);
                    }
                });
                Calendar displayedTime = Calendar.getInstance();
                try {
                    displayedTime.setTime(
                            TimeListAdapter.DISPLAY_DATE_FORMAT.parse(((TextView) view).getText().toString()));
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
        for (int i = 0; i < 7; i++) {
            final int finalI = i;
            days[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    if (!isBound) {
                        return;
                    }
                    if (checked) {
                        notifier.notifyDayAlarmSet(TimeRowHolder.this, finalI);
                    } else {
                        notifier.notifyDayAlarmCancelled(TimeRowHolder.this, finalI);
                    }
                }
            });
        }
    }
}
