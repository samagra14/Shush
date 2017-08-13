package com.mdg.droiders.samagra.shush;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.List;

/**
 * Created by rohan on 12/8/17.
 * <br>
 * This class schedules an alarm which changes ringer mode of the phone when it is triggered.
 */
public class AlarmScheduler {


    /**
     * Request code for scheduling an alarm
     * Will be taken from the database
     */
    private static final int REQUEST_CODE = 12;

    private AlarmManager alarmManager;
    private Context context;

    public AlarmScheduler(Context context) {
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        this.context = context;
    }

    /**
     * Set an alarm to silence the phone.
     *
     * @param startTimeInMillis The time at which phone is to be shushed
     * @param endTimeInMillis   The time at which phone is to be un-shushed
     */
    public void setAlarm(long startTimeInMillis, long endTimeInMillis) {
        if (startTimeInMillis >= endTimeInMillis) {
            return;
        }
        alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                startTimeInMillis,
                getDefaultPendingIntent(true)
        );
        alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                endTimeInMillis,
                getDefaultPendingIntent(false)
        );
    }

    /**
     * Sets multiple alarms at once.
     * <br><br>
     * <b>Uses</b> : {@link #setAlarm(long, long)} to set each alarm one by one.
     *
     * @param startTimesInMillis The list of start times at which phone is to be shushed
     * @param endTimesInMillis   The list of end times at which phone is to be un-shushed
     */
    public void setAlarms(List<Long> startTimesInMillis, List<Long> endTimesInMillis) {
        if (startTimesInMillis.size() != endTimesInMillis.size()) {
            return;
        }
        for (int i = 0; i < startTimesInMillis.size(); i++) {
            setAlarm(startTimesInMillis.get(i), endTimesInMillis.get(i));
        }
    }

    private PendingIntent getDefaultPendingIntent(boolean shouldSilence) {
        PendingIntent pendingIntent;

        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        intent.putExtra(context.getString(R.string.alarm_intent_extra_key), shouldSilence);
        pendingIntent = PendingIntent.getBroadcast(
                context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return pendingIntent;
    }
}
